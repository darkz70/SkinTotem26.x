package com.darkz.skintotem.utils;

import org.jetbrains.annotations.Nullable;

/**
 * Сравнение версий без API загрузчика: одинаково работает на Fabric и NeoForge.
 * <p>
 * Понимает строки вида {@code 3.9.7+26.3-fabric}, {@code 0.6.0+mc1.21.1}, {@code 4.10}:
 * метаданные после «+» и «-» отбрасываются, числовые части сравниваются как числа
 * (поэтому {@code 4.10} больше, чем {@code 4.9}).
 */
public final class VersionUtils {

	private VersionUtils() {
	}

	/**
	 * @return отрицательное число, если {@code first} старее {@code second}, 0 при равенстве, положительное — если новее.
	 */
	public static int compare(@Nullable String first, @Nullable String second) {
		String[] a = split(first);
		String[] b = split(second);

		int length = Math.max(a.length, b.length);
		for (int i = 0; i < length; i++) {
			int x = i < a.length ? toNumber(a[i]) : 0;
			int y = i < b.length ? toNumber(b[i]) : 0;
			if (x != y) {
				return Integer.compare(x, y);
			}
		}
		return 0;
	}

	/** @return {@code true}, если версия {@code version} старее, чем {@code than} (или неизвестна). */
	public static boolean isOlderThan(@Nullable String version, String than) {
		if (version == null) {
			return false;
		}
		return compare(version, than) < 0;
	}

	/** @return {@code true}, если версия {@code version} не старее, чем {@code than}. */
	public static boolean isAtLeast(@Nullable String version, String than) {
		if (version == null) {
			return false;
		}
		return compare(version, than) >= 0;
	}

	private static String[] split(@Nullable String version) {
		if (version == null || version.isBlank()) {
			return new String[0];
		}

		String core = version.trim();
		int plus = core.indexOf('+');
		if (plus >= 0) {
			core = core.substring(0, plus);
		}
		int dash = core.indexOf('-');
		if (dash >= 0) {
			core = core.substring(0, dash);
		}
		return core.split("\\.");
	}

	private static int toNumber(String part) {
		int end = 0;
		while (end < part.length() && Character.isDigit(part.charAt(end))) {
			end++;
		}
		if (end == 0) {
			return 0;
		}
		try {
			return Integer.parseInt(part.substring(0, end));
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
