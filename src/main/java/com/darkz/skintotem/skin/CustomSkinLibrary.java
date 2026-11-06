package com.darkz.skintotem.skin;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.skin.figura.FiguraAvatars;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Библиотека пользовательских скинов в стиле Figura: все PNG-файлы из папки
 * {@code config/skintotem/skins/} плюс скины аватаров Figura ({@code figura/avatars/<имя>/avatar.png})
 * можно выбрать для куклы тотема из выпадающего списка, не вводя путь вручную.
 */
public final class CustomSkinLibrary {

	private CustomSkinLibrary() {
	}

	/** Папка, куда можно положить свои скины (создаётся при первом обращении). */
	public static Path getSkinsDir() {
		return FabricLoader.getInstance().getConfigDir().resolve(SkinTotem.MOD_ID).resolve("skins");
	}

	/**
	 * @return отсортированный список абсолютных путей ко всем PNG-скинам:
	 * из {@code config/skintotem/skins/} и из аватаров Figura
	 */
	public static List<String> listSkinFiles() {
		List<String> files = new ArrayList<>(listOwnSkinFiles());
		for (FiguraAvatars.Avatar avatar : FiguraAvatars.list()) {
			if (avatar.skin() != null) {
				files.add(avatar.skin().toAbsolutePath().toString());
			}
		}
		files.sort(String.CASE_INSENSITIVE_ORDER);
		return files;
	}

	private static List<String> listOwnSkinFiles() {
		Path dir = getSkinsDir();
		try {
			Files.createDirectories(dir);
		} catch (IOException ignored) {
		}

		List<String> files = new ArrayList<>();
		try (Stream<Path> stream = Files.list(dir)) {
			stream.filter(Files::isRegularFile)
					.filter(path -> {
						String name = path.getFileName().toString().toLowerCase();
						return name.endsWith(".png");
					})
					.map(Path::toAbsolutePath)
					.map(Path::toString)
					.forEach(files::add);
		} catch (IOException ignored) {
		}
		return files;
	}

	/** Имя для выпадающего списка: для аватаров Figura — «Figura · <имя>», иначе имя файла. */
	public static String displayName(String path) {
		if (path == null || path.isEmpty()) {
			return path;
		}
		if (FiguraAvatars.isFiguraAvatarSkin(path)) {
			return FiguraAvatars.figuraLabel(path);
		}
		String name = Path.of(path).getFileName().toString();
		return name.isEmpty() ? path : name;
	}
}
