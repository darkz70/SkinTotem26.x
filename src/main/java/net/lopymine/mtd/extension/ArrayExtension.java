package net.lopymine.mtd.extension;

public class ArrayExtension {

	public static <T> T[] shift(T[] array, int shift) {
		T[] shifted = array.clone();
		for (int i = 0; i < array.length; i++) {
			int n = (i + shift) % array.length;
			shifted[n] = array[i];
		}
		return shifted;
	}

}
