package net.lopymine.mtd.utils;

public class ColorUtils {

	public static int getArgb(int r, int g, int b) {
		return getArgb(255, r, g, b);
	}

	public static int getArgb(int a, int r, int g, int b) {
		return a << 24 | r << 16 | g << 8 | b;
	}

	public static int getRgb(int r, int g, int b) {
		return r << 16 | g << 8 | b;
	}

}
