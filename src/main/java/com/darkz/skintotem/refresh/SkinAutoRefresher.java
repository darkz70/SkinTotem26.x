package com.darkz.skintotem.refresh;

import com.darkz.skintotem.client.SkinTotemClient;
import com.darkz.skintotem.config.SkinTotemConfig;
import com.darkz.skintotem.doll.manager.StandardSkinTotemManager;
import java.util.concurrent.*;

public class SkinAutoRefresher {

	private static ScheduledExecutorService scheduler;

	public static void start() {
		stop();
		SkinTotemConfig config = SkinTotemConfig.getInstance();
		if (!config.isAutoRefreshEnabled()) {
			return;
		}
		int intervalMinutes = Math.max(1, config.getAutoRefreshIntervalMinutes());
		scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread t = new Thread(r, "SkinTotem-AutoRefresh");
			t.setDaemon(true);
			return t;
		});
		scheduler.scheduleAtFixedRate(() -> {
			try {
				SkinTotemClient.LOGGER.info("[SkinTotem] Auto-refreshing skin...");
				StandardSkinTotemManager.initializeStandardDollData();
			} catch (Exception e) {
				SkinTotemClient.LOGGER.error("[SkinTotem] Auto-refresh failed:", e);
			}
		}, intervalMinutes, intervalMinutes, TimeUnit.MINUTES);
		SkinTotemClient.LOGGER.info("[SkinTotem] Auto-refresh started, interval: {} min", intervalMinutes);
	}

	public static void stop() {
		if (scheduler != null && !scheduler.isShutdown()) {
			scheduler.shutdownNow();
			scheduler = null;
		}
	}

	public static void restart() {
		start();
	}
}
