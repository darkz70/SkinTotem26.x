package com.darkz.skintotem.sound.custom;

import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.client.SkinTotemClient;
import com.darkz.skintotem.config.SkinTotemConfig;
import com.mojang.blaze3d.audio.SoundBuffer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import com.darkz.skintotem.loader.SkinTotemLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * Пользовательские звуки из папки {@code config/skintotem/sounds}.
 * <p>
 * Файл с именем звука подменяет встроенный звук мода: игра запрашивает буфер по пути ресурса
 * {@code skintotem:sounds/<имя>.ogg}, а мод отдаёт свой, уже раскодированный. Ресурс-паки при этом
 * продолжают работать как обычно — папка просто удобнее, в неё можно положить файл любого формата.
 * <p>
 * Несколько файлов для одного звука становятся вариантами: при каждом воспроизведении берётся
 * случайный. Варианты задаются либо суффиксом ({@code skin_change_1.ogg}), либо вложенной папкой
 * ({@code skin_change/любое_имя.ogg}).
 */
public final class CustomSoundPack {

	/** Звуки мода, которые можно заменить. */
	public static final List<String> SOUND_NAMES = List.of("totem_activate", "doll_summon", "skin_change", "skin_error");

	private static final Path FOLDER = SkinTotemLoader.getConfigDir().resolve(SkinTotem.MOD_ID).resolve("sounds");
	private static final Path CONVERSION_CACHE = FOLDER.resolve(".converted");
	private static final String README_NAME = "README.txt";

	/** Раскодированные звуки: путь ресурса → варианты. Переживают перезагрузку звукового движка. */
	private static final Map<Identifier, List<DecodedSound>> SOUNDS = new HashMap<>();
	/** Буферы OpenAL, созданные для вариантов. Сбрасываются, когда игра чистит свои буферы. */
	private static final Map<DecodedSound, CompletableFuture<SoundBuffer>> LIVE = new IdentityHashMap<>();
	/** Человекочитаемый отчёт о последнем сканировании для {@code /skintotem sounds list}. */
	private static final List<String> REPORT = new ArrayList<>();

	private static volatile boolean scanning = false;

	private CustomSoundPack() {
	}

	public static Path folder() {
		return FOLDER;
	}

	/** Создаёт папку с подсказкой и запускает первое сканирование. */
	public static void init() {
		try {
			Files.createDirectories(FOLDER);
			writeReadme();
		} catch (IOException exception) {
			SkinTotemClient.LOGGER.warn("Could not prepare custom sounds folder {}: {}", FOLDER, exception.toString());
		}
		scanAsync();
	}

	/** Перечитывает папку в фоне, чтобы не задерживать игровой поток. */
	public static CompletableFuture<Void> scanAsync() {
		if (scanning) {
			return CompletableFuture.completedFuture(null);
		}
		scanning = true;
		return CompletableFuture.runAsync(() -> {
			try {
				scan();
			} catch (Throwable throwable) {
				SkinTotemClient.LOGGER.error("Failed to load custom sounds: ", throwable);
			} finally {
				scanning = false;
			}
		});
	}

	/**
	 * Отдаёт буфер пользовательского звука или {@code null}, если для этого звука ничего не положили
	 * и нужно играть встроенный.
	 */
	@Nullable
	public static synchronized CompletableFuture<SoundBuffer> getBuffer(Identifier soundPath) {
		if (SOUNDS.isEmpty() || !isEnabled()) {
			return null;
		}

		List<DecodedSound> variants = SOUNDS.get(soundPath);
		if (variants == null || variants.isEmpty()) {
			return null;
		}

		DecodedSound sound = variants.size() == 1
				? variants.get(0)
				: variants.get(ThreadLocalRandom.current().nextInt(variants.size()));

		return LIVE.computeIfAbsent(sound, decoded -> CompletableFuture.completedFuture(
				new SoundBuffer(decoded.data(), decoded.format())
		));
	}

	/**
	 * Вызывается, когда игра очищает свои звуковые буферы (перезагрузка ресурсов, смена устройства).
	 * Буферы OpenAL становятся недействительными, поэтому их нужно удалить и создать заново,
	 * а заодно подхватить файлы, которые пользователь добавил во время игры.
	 */
	public static void onSoundEngineReload() {
		discardBuffers();
		scanAsync();
	}

	public static synchronized void discardBuffers() {
		for (CompletableFuture<SoundBuffer> future : LIVE.values()) {
			SoundBuffer buffer = future.getNow(null);
			if (buffer != null) {
				buffer.discardAlBuffer();
			}
		}
		LIVE.clear();
	}

	/** Отчёт о загруженных файлах для команды. */
	public static synchronized List<String> report() {
		return List.copyOf(REPORT);
	}

	public static boolean isEnabled() {
		SkinTotemConfig config = SkinTotemConfig.getInstance();
		return config != null && config.isModEnabled() && config.isSoundsEnabled() && config.isCustomSoundsEnabled();
	}

	private static boolean isConversionAllowed() {
		SkinTotemConfig config = SkinTotemConfig.getInstance();
		return config == null || config.isCustomSoundsAutoConvert();
	}

	private static void scan() {
		Map<Identifier, List<DecodedSound>> found = new HashMap<>();
		List<String> report = new ArrayList<>();

		if (Files.isDirectory(FOLDER)) {
			Path cache = isConversionAllowed() ? CONVERSION_CACHE : null;

			for (String name : SOUND_NAMES) {
				List<DecodedSound> variants = new ArrayList<>();

				for (Path file : candidates(name)) {
					try {
						DecodedSound sound = AudioFiles.decode(file, cache);
						variants.add(sound);
						report.add(name + " <- " + FOLDER.relativize(file) + " (" + sound.describe() + ")");
					} catch (Throwable throwable) {
						report.add(name + " <- " + FOLDER.relativize(file) + " (failed: " + describeError(file, throwable) + ")");
						SkinTotemClient.LOGGER.warn("Could not load custom sound {}: {}", file, describeError(file, throwable));
					}
				}

				if (!variants.isEmpty()) {
					found.put(SkinTotem.id("sounds/" + name + ".ogg"), variants);
				}
			}
		}

		synchronized (CustomSoundPack.class) {
			SOUNDS.clear();
			SOUNDS.putAll(found);
			REPORT.clear();
			REPORT.addAll(report);
		}

		// Буферы OpenAL нельзя удалять из рабочего потока — это делает поток игры.
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft != null) {
			minecraft.execute(CustomSoundPack::discardBuffers);
		}

		if (!found.isEmpty()) {
			int total = found.values().stream().mapToInt(List::size).sum();
			SkinTotemClient.LOGGER.info("Loaded {} custom sound file(s) from {}", total, FOLDER);
		}
	}

	/**
	 * Файлы, относящиеся к звуку: {@code имя.ext}, {@code имя_что-угодно.ext} и всё внутри папки {@code имя/}.
	 */
	private static List<Path> candidates(String name) {
		List<Path> files = new ArrayList<>();

		try (Stream<Path> top = Files.list(FOLDER)) {
			top.filter(Files::isRegularFile)
					.filter(path -> {
						String base = AudioFiles.baseName(path).toLowerCase(Locale.ROOT);
						return base.equals(name) || base.startsWith(name + "_");
					})
					.filter(CustomSoundPack::looksLikeAudio)
					.sorted(Comparator.comparing(path -> path.getFileName().toString()))
					.forEach(files::add);
		} catch (IOException exception) {
			SkinTotemClient.LOGGER.warn("Could not read {}: {}", FOLDER, exception.toString());
		}

		Path variantFolder = FOLDER.resolve(name);
		if (Files.isDirectory(variantFolder)) {
			try (Stream<Path> nested = Files.list(variantFolder)) {
				nested.filter(Files::isRegularFile)
						.filter(CustomSoundPack::looksLikeAudio)
						.sorted(Comparator.comparing(path -> path.getFileName().toString()))
						.forEach(files::add);
			} catch (IOException exception) {
				SkinTotemClient.LOGGER.warn("Could not read {}: {}", variantFolder, exception.toString());
			}
		}

		return files;
	}

	/**
	 * Берём файл, если расширение знакомое ИЛИ если его содержимое похоже на звук:
	 * пользователю не нужно думать про расширения — достаточно положить файл в папку.
	 */
	private static boolean looksLikeAudio(Path file) {
		String fileName = file.getFileName().toString();
		if (fileName.startsWith(".") || fileName.equalsIgnoreCase(README_NAME)) {
			return false;
		}
		if (AudioFiles.KNOWN_EXTENSIONS.contains(AudioFiles.extension(file))) {
			return true;
		}
		return !AudioFiles.sniff(file).isEmpty();
	}

	private static String describeError(Path file, Throwable throwable) {
		String message = throwable.getMessage() == null ? throwable.getClass().getSimpleName() : throwable.getMessage();
		if (message.contains("ffmpeg")) {
			return message + " — save the file as .ogg, .mp3 or .wav (these need nothing installed), or install ffmpeg";
		}
		return message;
	}

	private static void writeReadme() throws IOException {
		Path readme = FOLDER.resolve(README_NAME);
		String text = String.join("\n",
				"SkinTotem — custom sounds / свои звуки",
				"======================================",
				"",
				"[EN]",
				"Put your own sound files here to replace the mod's sounds. File name = sound name:",
				"",
				"  totem_activate   - the totem saves you",
				"  doll_summon      - a doll appears in your hand",
				"  skin_change      - a skin was loaded successfully",
				"  skin_error       - a skin could not be loaded",
				"",
				"Example: totem_activate.ogg, doll_summon.wav, skin_error.mp3",
				"",
				"Formats: .ogg, .mp3, .wav, .aiff and .au work out of the box — nothing to install. The audio",
				"is used as it is: no resampling, no volume changes, no quality loss. Rare formats (.m4a,",
				".flac, .opus, ...) are converted automatically if ffmpeg is installed on your system; the",
				"result is cached in the .converted folder. Without ffmpeg the mod keeps its built-in sound",
				"and writes a hint to the log and to /skintotem sounds list.",
				"",
				"Random variants: add a suffix (skin_change_1.ogg, skin_change_2.ogg) or create a folder",
				"named after the sound (skin_change/anything.ogg). One of the variants is picked per play.",
				"",
				"Mono files are positioned in 3D space, stereo files always play at full volume.",
				"",
				"Changes are picked up on resource reload (F3+T) or with /skintotem sounds reload.",
				"",
				"[RU]",
				"Положите сюда свои звуковые файлы, чтобы заменить звуки мода. Имя файла = имя звука:",
				"",
				"  totem_activate   - тотем спас игрока",
				"  doll_summon      - кукла появилась в руке",
				"  skin_change      - скин успешно загружен",
				"  skin_error       - скин загрузить не удалось",
				"",
				"Пример: totem_activate.ogg, doll_summon.wav, skin_error.mp3",
				"",
				"Форматы: .ogg, .mp3, .wav, .aiff и .au работают сразу — ставить ничего не нужно. Звук берётся",
				"как есть: без передискретизации, без изменения громкости и без потери качества. Редкие форматы",
				"(.m4a, .flac, .opus, ...) конвертируются автоматически, если в системе установлен ffmpeg;",
				"результат кэшируется в папке .converted. Без ffmpeg мод оставит встроенный звук и напишет",
				"подсказку в лог и в /skintotem sounds list.",
				"",
				"Случайные варианты: добавьте суффикс (skin_change_1.ogg, skin_change_2.ogg) или создайте",
				"папку с именем звука (skin_change/любое_имя.ogg). При каждом проигрывании берётся один.",
				"",
				"Моно-файлы звучат позиционно, стерео — всегда на полной громкости.",
				"",
				"Изменения подхватываются при перезагрузке ресурсов (F3+T) или командой /skintotem sounds reload.",
				""
		);
		Files.write(readme, text.getBytes(StandardCharsets.UTF_8));
	}
}
