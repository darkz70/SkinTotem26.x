package com.darkz.skintotem.thread;

import java.util.List;
import java.util.concurrent.*;
import com.darkz.skintotem.config.SkinTotemConfig;

public class SkinTotemTaskExecutor {

	public static ExecutorService MAIN_EXECUTOR = Executors.newFixedThreadPool(SkinTotemConfig.getInstance().getParallelTasksCount());

	public static void reload() {
		int threadsCount = SkinTotemConfig.getInstance().getParallelTasksCount();
		List<Runnable> runnables = MAIN_EXECUTOR.shutdownNow();
		MAIN_EXECUTOR = Executors.newFixedThreadPool(threadsCount);
		for (Runnable runnable : runnables) {
			MAIN_EXECUTOR.submit(runnable);
		}
	}

	public static void stop() {
		MAIN_EXECUTOR.shutdown();
	}

	public static CompletableFuture<Void> execute(Runnable runnable) {
		return CompletableFuture.runAsync(runnable, MAIN_EXECUTOR);
	}
}
