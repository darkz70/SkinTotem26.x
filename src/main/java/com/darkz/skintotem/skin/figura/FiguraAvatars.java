package com.darkz.skintotem.skin.figura;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

/**
 * Аватары Figura: папки вида {@code <gameDir>/figura/avatars/<ИмяАватара>/} с {@code avatar.json}.
 * Скин аватара — {@code avatar.png} (или первый попавшийся PNG), модель — первый {@code .bbmodel}.
 * Именно так Figura подхватывает аватары пользователя, поэтому кукла тотема может использовать
 * те же файлы, что и Figura.
 */
public final class FiguraAvatars {

	public record Avatar(String name, Path folder, @Nullable Path skin, @Nullable Path model) {
	}

	private FiguraAvatars() {
	}

	/** Папка аватаров Figura ({@code <gameDir>/figura/avatars}). */
	public static Path getAvatarsDir() {
		return FabricLoader.getInstance().getGameDir().resolve("figura").resolve("avatars");
	}

	public static boolean isAvailable() {
		return Files.isDirectory(getAvatarsDir());
	}

	/** @return все найденные аватары Figura, отсортированные по имени */
	public static List<Avatar> list() {
		Path dir = getAvatarsDir();
		List<Avatar> avatars = new ArrayList<>();
		if (!Files.isDirectory(dir)) {
			return avatars;
		}

		try (Stream<Path> stream = Files.list(dir)) {
			stream.filter(Files::isDirectory).forEach(folder -> {
				String name = folder.getFileName().toString();
				Path skin = existing(folder.resolve("avatar.png"));
				Path model = null;
				try (Stream<Path> files = Files.list(folder)) {
					if (skin == null) {
						skin = files.filter(Files::isRegularFile)
								.filter(p -> p.getFileName().toString().toLowerCase().endsWith(".png"))
								.findFirst().orElse(null);
					}
				} catch (IOException ignored) {
				}
				try (Stream<Path> files = Files.list(folder)) {
					model = files.filter(Files::isRegularFile)
							.filter(p -> p.getFileName().toString().toLowerCase().endsWith(".bbmodel"))
							.findFirst().orElse(null);
				} catch (IOException ignored) {
				}
				avatars.add(new Avatar(name, folder, skin, model));
			});
		} catch (IOException ignored) {
		}

		avatars.sort(Comparator.comparing(Avatar::name, String.CASE_INSENSITIVE_ORDER));
		return avatars;
	}

	@Nullable
	private static Path existing(Path path) {
		return Files.isRegularFile(path) ? path : null;
	}

	/** @return true, если путь скина лежит внутри папки аватаров Figura */
	public static boolean isFiguraAvatarSkin(String path) {
		if (path == null || path.isEmpty()) {
			return false;
		}
		Path p = Path.of(path).toAbsolutePath();
		Path avatarsDir = getAvatarsDir().toAbsolutePath();
		return p.startsWith(avatarsDir);
	}

	/** Метка для выпадающего списка: «Figura · <имя аватара>». */
	public static String figuraLabel(String skinPath) {
		Path p = Path.of(skinPath).toAbsolutePath();
		Path avatarsDir = getAvatarsDir().toAbsolutePath();
		if (p.startsWith(avatarsDir)) {
			Path relative = avatarsDir.relativize(p);
			if (relative.getNameCount() >= 2) {
				return "Figura · " + relative.getName(0);
			}
		}
		return p.getFileName().toString();
	}
}
