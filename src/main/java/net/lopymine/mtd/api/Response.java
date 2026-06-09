package net.lopymine.mtd.api;

import org.jetbrains.annotations.Nullable;

public record Response<T>(int statusCode, @Nullable T value) {

	public static <T> Response<T> empty(int statusCode) {
		return new Response<>(statusCode, null);
	}

	public static <T> Response<T> of(int statusCode, T value) {
		return new Response<>(statusCode, value);
	}

	public boolean isEmpty() {
		return this.value == null;
	}
}
