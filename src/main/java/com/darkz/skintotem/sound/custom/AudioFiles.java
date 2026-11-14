package com.darkz.skintotem.sound.custom;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import net.minecraft.client.sounds.JOrbisAudioStream;
import org.jetbrains.annotations.Nullable;

/**
 * Чтение звуковых файлов из папки пользователя.
 * <p>
 * Звук попадает в OpenAL ровно таким, каким его дал пользователь: частота дискретизации
 * не меняется, громкость не трогается, никакой дополнительной обработки нет. Приводится только то,
 * без чего OpenAL не умеет работать — разрядность (к 16 битам) и количество каналов (не больше двух).
 * <p>
 * Что читается напрямую, без единой внешней программы:
 * <ul>
 *     <li>{@code .ogg} — тем же декодером, что использует сама игра;</li>
 *     <li>{@code .wav}, {@code .aiff}, {@code .au} — средствами JVM;</li>
 *     <li>{@code .mp3} — декодером JLayer, который лежит внутри jar мода.</li>
 * </ul>
 * Редкие форматы ({@code .m4a}, {@code .flac}, {@code .opus} и т.д.) конвертируются автоматически
 * через ffmpeg, если он есть в системе; результат кэшируется, поэтому конвертация выполняется
 * один раз на файл.
 */
public final class AudioFiles {

	/** Форматы, которые читаются без внешних программ. */
	public static final List<String> NATIVE_EXTENSIONS = List.of("ogg", "oga", "wav", "wave", "aif", "aiff", "aifc", "au", "snd", "mp3", "mp2", "mp1");

	/** Расширения, которые вообще имеет смысл пробовать как звук. */
	public static final List<String> KNOWN_EXTENSIONS = List.of(
			"ogg", "oga", "wav", "wave", "aif", "aiff", "aifc", "au", "snd",
			"mp3", "m4a", "aac", "flac", "opus", "wma", "mp4", "webm", "mka", "mov", "3gp", "amr", "ac3"
	);

	/** Форматы, которые читаются без внешних программ (по содержимому файла). */
	private static final List<String> NATIVE_KINDS = List.of("ogg", "wav", "aiff", "au", "mpeg");

	/** Сколько повреждённых кадров mp3 можно молча пропустить, не теряя весь звук. */
	private static final int MAX_BROKEN_FRAMES = 128;

	private static final long CONVERT_TIMEOUT_SECONDS = 180L;
	private static final int MAX_DECODED_BYTES = 64 * 1024 * 1024;

	private AudioFiles() {
	}

	public static String extension(Path file) {
		String name = file.getFileName().toString();
		int dot = name.lastIndexOf('.');
		return dot < 0 ? "" : name.substring(dot + 1).toLowerCase(Locale.ROOT);
	}

	public static String baseName(Path file) {
		String name = file.getFileName().toString();
		int dot = name.lastIndexOf('.');
		return dot < 0 ? name : name.substring(0, dot);
	}

	public static boolean isNative(Path file) {
		return NATIVE_EXTENSIONS.contains(extension(file)) || NATIVE_KINDS.contains(sniff(file));
	}

	/**
	 * Определяет формат по первым байтам файла, а не по расширению: файл можно назвать как угодно
	 * (или ошибиться с расширением), звук всё равно должен заиграть.
	 *
	 * @return {@code ogg}, {@code wav}, {@code aiff}, {@code au}, {@code mpeg}, {@code flac},
	 *         {@code mp4}, {@code matroska} или пустая строка, если формат не опознан
	 */
	public static String sniff(Path file) {
		byte[] head = new byte[16];
		int read;
		try (InputStream input = Files.newInputStream(file)) {
			read = input.readNBytes(head, 0, head.length);
		} catch (IOException exception) {
			return "";
		}
		if (read < 4) {
			return "";
		}

		if (starts(head, 0, "OggS")) {
			return "ogg";
		}
		if (read >= 12 && starts(head, 0, "RIFF") && starts(head, 8, "WAVE")) {
			return "wav";
		}
		if (read >= 12 && starts(head, 0, "FORM") && (starts(head, 8, "AIFF") || starts(head, 8, "AIFC"))) {
			return "aiff";
		}
		if (starts(head, 0, ".snd")) {
			return "au";
		}
		if (starts(head, 0, "fLaC")) {
			return "flac";
		}
		if (starts(head, 0, "ID3") || ((head[0] & 0xFF) == 0xFF && (head[1] & 0xE0) == 0xE0)) {
			return "mpeg";
		}
		if (read >= 12 && starts(head, 4, "ftyp")) {
			return "mp4";
		}
		if ((head[0] & 0xFF) == 0x1A && (head[1] & 0xFF) == 0x45 && (head[2] & 0xFF) == 0xDF && (head[3] & 0xFF) == 0xA3) {
			return "matroska";
		}
		return "";
	}

	private static boolean starts(byte[] bytes, int offset, String text) {
		if (bytes.length < offset + text.length()) {
			return false;
		}
		for (int i = 0; i < text.length(); i++) {
			if (bytes[offset + i] != (byte) text.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	/** Формат по расширению — запасной вариант, когда содержимое ни на что не похоже. */
	private static String kindByExtension(String extension) {
		return switch (extension) {
			case "ogg", "oga" -> "ogg";
			case "wav", "wave" -> "wav";
			case "aif", "aiff", "aifc" -> "aiff";
			case "au", "snd" -> "au";
			case "mp3", "mp2", "mp1" -> "mpeg";
			case "flac" -> "flac";
			default -> "";
		};
	}

	/**
	 * Читает файл и готовит PCM-данные для OpenAL.
	 *
	 * @param conversionCache куда складывать сконвертированные файлы, {@code null} — конвертация запрещена
	 */
	public static DecodedSound decode(Path file, @Nullable Path conversionCache) throws Exception {
		String kind = sniff(file);
		if (kind.isEmpty()) {
			kind = kindByExtension(extension(file));
		}

		if (kind.equals("ogg")) {
			return decodeOgg(file);
		}
		if (kind.equals("mpeg")) {
			try {
				return decodeMpeg(file);
			} catch (UnsupportedAudioFileException broken) {
				// Совсем нечитаемый mp3: если в системе есть ffmpeg, пробуем через него.
				if (conversionCache == null) {
					throw broken;
				}
				return decodePcm(convert(file, conversionCache, null), file);
			}
		}

		try {
			return decodePcm(file, file);
		} catch (UnsupportedAudioFileException unsupported) {
			if (conversionCache == null) {
				throw unsupported;
			}

			// Формат не читается напрямую: конвертируем во временный wav и пробуем ещё раз.
			Path converted = convert(file, conversionCache, null);
			try {
				return decodePcm(converted, file);
			} catch (UnsupportedAudioFileException stillUnsupported) {
				// Многоканальный звук (5.1 и подобные) — сводим к стерео.
				return decodePcm(convert(file, conversionCache, 2), file);
			}
		}
	}

	/** OGG читается тем же декодером, что и ванильные звуки, поэтому качество полностью сохраняется. */
	private static DecodedSound decodeOgg(Path file) throws IOException {
		try (InputStream input = new BufferedInputStream(Files.newInputStream(file));
			 JOrbisAudioStream stream = new JOrbisAudioStream(input)) {
			ByteBuffer data = stream.readAll();
			checkSize(data.limit(), file);
			return new DecodedSound(file, data, stream.getFormat());
		}
	}

	/**
	 * MP3 раскодирован встроенным JLayer, поэтому mp3-файл работает сразу: ни ffmpeg, ни кодеки
	 * системы не нужны. Частота и количество каналов берутся из самого файла и не меняются.
	 */
	private static DecodedSound decodeMpeg(Path file) throws IOException, UnsupportedAudioFileException {
		try (InputStream input = new BufferedInputStream(Files.newInputStream(file))) {
			Bitstream bitstream = new Bitstream(input);
			Decoder decoder = new Decoder();
			ByteArrayOutputStream pcm = new ByteArrayOutputStream(1 << 16);

			int channels = 0;
			float sampleRate = 0.0F;
			byte[] chunk = new byte[0];

			int decodedFrames = 0;
			int brokenFrames = 0;
			try {
				while (true) {
					Header header;
					try {
						header = bitstream.readFrame();
					} catch (Exception unreadable) {
						// Битый кусок внутри файла: пропускаем его и ищем следующий кадр.
						if (++brokenFrames > MAX_BROKEN_FRAMES) {
							break;
						}
						bitstream.closeFrame();
						continue;
					}
					if (header == null) {
						break;
					}

					try {
						SampleBuffer samples = (SampleBuffer) decoder.decodeFrame(header, bitstream);
						channels = samples.getChannelCount();
						sampleRate = samples.getSampleFrequency();

						short[] values = samples.getBuffer();
						int length = samples.getBufferLength();
						if (chunk.length < length * 2) {
							chunk = new byte[length * 2];
						}

						// OpenAL ждёт 16 бит со знаком в порядке little-endian.
						for (int i = 0; i < length; i++) {
							short value = values[i];
							chunk[i * 2] = (byte) (value & 0xFF);
							chunk[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
						}

						pcm.write(chunk, 0, length * 2);
						decodedFrames++;
					} catch (Exception undecodable) {
						if (++brokenFrames > MAX_BROKEN_FRAMES) {
							break;
						}
					} finally {
						bitstream.closeFrame();
					}

					if (pcm.size() > MAX_DECODED_BYTES) {
						throw new IOException("Sound is too long: " + file.getFileName());
					}
				}
			} finally {
				try {
					bitstream.close();
				} catch (Exception ignored) {
					// поток уже закрыт — ничего делать не нужно
				}
			}

			if (decodedFrames == 0) {
				throw new UnsupportedAudioFileException("JLayer found no playable MPEG frames in " + file.getFileName());
			}
			if (channels < 1 || channels > 2 || sampleRate <= 0.0F) {
				throw new UnsupportedAudioFileException("Unsupported MP3 layout: " + channels + " channels at " + sampleRate + " Hz");
			}

			byte[] bytes = pcm.toByteArray();
			checkSize(bytes.length, file);

			ByteBuffer data = ByteBuffer.allocateDirect(bytes.length);
			data.put(bytes);
			data.flip();

			AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, 16, channels, channels * 2, sampleRate, false);
			return new DecodedSound(file, data, format);
		}
	}

	private static DecodedSound decodePcm(Path file, Path originalFile) throws IOException, UnsupportedAudioFileException {
		try (InputStream input = new BufferedInputStream(Files.newInputStream(file));
			 AudioInputStream source = AudioSystem.getAudioInputStream(input)) {

			AudioFormat sourceFormat = source.getFormat();
			if (sourceFormat.getChannels() > 2 || sourceFormat.getChannels() < 1) {
				throw new UnsupportedAudioFileException("OpenAL supports mono and stereo only, got " + sourceFormat.getChannels() + " channels");
			}

			AudioFormat targetFormat = openAlFormat(sourceFormat);
			byte[] bytes;
			if (sourceFormat.matches(targetFormat)) {
				bytes = source.readAllBytes();
			} else {
				if (!AudioSystem.isConversionSupported(targetFormat, sourceFormat)) {
					throw new UnsupportedAudioFileException("Cannot convert " + sourceFormat + " to " + targetFormat);
				}
				try (AudioInputStream converted = AudioSystem.getAudioInputStream(targetFormat, source)) {
					bytes = converted.readAllBytes();
				}
			}

			checkSize(bytes.length, file);
			ByteBuffer data = ByteBuffer.allocateDirect(bytes.length);
			data.put(bytes);
			data.flip();
			return new DecodedSound(originalFile, data, targetFormat);
		}
	}

	/**
	 * OpenAL принимает только 8-битный беззнаковый и 16-битный знаковый PCM с одним или двумя каналами.
	 * Частоту дискретизации не меняем — OpenAL воспроизводит буфер на его собственной частоте.
	 */
	private static AudioFormat openAlFormat(AudioFormat format) {
		boolean signed16 = format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)
				&& format.getSampleSizeInBits() == 16
				&& !format.isBigEndian();
		boolean unsigned8 = format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)
				&& format.getSampleSizeInBits() == 8;

		if (signed16 || unsigned8) {
			return format;
		}

		float sampleRate = format.getSampleRate() > 0 ? format.getSampleRate() : 44100.0F;
		int channels = Math.max(1, Math.min(2, format.getChannels()));
		return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, 16, channels, channels * 2, sampleRate, false);
	}

	private static void checkSize(int bytes, Path file) throws IOException {
		if (bytes <= 0) {
			throw new IOException("Decoded to an empty sound: " + file.getFileName());
		}
		if (bytes > MAX_DECODED_BYTES) {
			throw new IOException("Sound is too long (" + bytes / 1024 / 1024 + " MiB decoded): " + file.getFileName());
		}
	}

	/** Конвертирует файл в wav через ffmpeg; результат кэшируется и переиспользуется. */
	private static Path convert(Path file, Path cacheFolder, @Nullable Integer channels) throws IOException, InterruptedException {
		Files.createDirectories(cacheFolder);
		Path target = cacheFolder.resolve(cacheName(file, channels));
		if (Files.isRegularFile(target) && Files.size(target) > 0L) {
			return target;
		}

		List<String> command = new ArrayList<>(List.of(
				"ffmpeg", "-y", "-hide_banner", "-loglevel", "error",
				"-i", file.toAbsolutePath().toString(),
				"-vn", "-map_metadata", "-1"
		));
		if (channels != null) {
			command.add("-ac");
			command.add(String.valueOf(channels));
		}
		command.add("-acodec");
		command.add("pcm_s16le");
		command.add(target.toAbsolutePath().toString());

		Process process;
		try {
			process = new ProcessBuilder(command).redirectErrorStream(true).start();
		} catch (IOException notFound) {
			throw new IOException("ffmpeg is not installed or not in PATH", notFound);
		}

		String output;
		try (InputStream stream = process.getInputStream()) {
			output = new String(stream.readAllBytes(), StandardCharsets.UTF_8).trim();
		}

		if (!process.waitFor(CONVERT_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
			process.destroyForcibly();
			throw new IOException("ffmpeg timed out while converting " + file.getFileName());
		}
		if (process.exitValue() != 0) {
			Files.deleteIfExists(target);
			throw new IOException("ffmpeg failed (" + process.exitValue() + "): " + output);
		}

		return target;
	}

	/** Имя кэша зависит от размера и времени изменения, поэтому изменённый файл конвертируется заново. */
	private static String cacheName(Path file, @Nullable Integer channels) throws IOException {
		long size = Files.size(file);
		long modified = Files.getLastModifiedTime(file).toMillis();
		String stamp = Long.toHexString(size * 31L ^ modified);
		String suffix = channels == null ? "" : "-" + channels + "ch";
		return sanitize(baseName(file)) + "-" + stamp + suffix + ".wav";
	}

	private static String sanitize(String name) {
		return name.replaceAll("[^a-zA-Z0-9_.-]", "_");
	}
}
