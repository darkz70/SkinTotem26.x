package com.darkz.skintotem.compat;

import com.darkz.skintotem.client.SkinTotemClient;
import com.darkz.skintotem.compat.emotecraft.EmotecraftBridge;
import com.darkz.skintotem.config.SkinTotemConfig;
import com.darkz.skintotem.doll.model.SkinTotemModel;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.player.AbstractClientPlayer;
import org.jetbrains.annotations.Nullable;

/**
 * Безопасный фасад интеграции с Emotecraft.
 * <p>
 * Классы Emotecraft и Player Animation Library упоминаются только в {@link EmotecraftBridge},
 * который загружается лениво — при первом вызове и только когда нужные моды реально установлены.
 * Любая ошибка интеграции выключает её до перезапуска игры, чтобы не спамить исключениями в рендере.
 */
public final class EmoteSupport {

	/** Анимации кукле отдаёт эта библиотека (обязательная зависимость Emotecraft). */
	private static final boolean ANIMATION_LIB_LOADED = FabricLoader.getInstance().isModLoaded("player_animation_library");
	private static final boolean EMOTECRAFT_LOADED = FabricLoader.getInstance().isModLoaded("emotecraft");

	private static boolean failed = false;
	private static boolean restoreFailed = false;

	private EmoteSupport() {
	}

	public static boolean isEmotecraftLoaded() {
		return EMOTECRAFT_LOADED;
	}

	private static boolean canMirror() {
		if (!ANIMATION_LIB_LOADED || failed) {
			return false;
		}
		SkinTotemConfig config = SkinTotemConfig.getInstance();
		return config != null && config.isModEnabled() && config.isEmoteMirroringEnabled();
	}

	/** Переносит позу текущей эмоции игрока на куклу. Вызывать перед отрисовкой. */
	public static void applyEmotePose(@Nullable AbstractClientPlayer holder, @Nullable SkinTotemModel model) {
		if (!canMirror() || holder == null || model == null) {
			return;
		}
		try {
			EmotecraftBridge.applyEmotePose(holder, model);
		} catch (Throwable throwable) {
			disable("apply doll emote pose", throwable);
		}
	}

	/**
	 * Возвращает частям куклы исходную позу. Вызывать после отрисовки.
	 * <p>
	 * Намеренно не смотрит на {@code failed}: если поза успела примениться, а потом интеграция
	 * сломалась, её всё равно нужно снять — иначе кукла навсегда застынет в позе эмоции.
	 */
	public static void restorePose() {
		if (!ANIMATION_LIB_LOADED || restoreFailed) {
			return;
		}
		try {
			EmotecraftBridge.restorePose();
		} catch (Throwable throwable) {
			restoreFailed = true;
			disable("restore doll pose", throwable);
		}
	}

	/** Проигрывает выбранную в конфиге эмоцию при срабатывании тотема. */
	public static void playActivationEmote() {
		if (!EMOTECRAFT_LOADED || failed) {
			return;
		}

		SkinTotemConfig config = SkinTotemConfig.getInstance();
		if (config == null || !config.isModEnabled()) {
			return;
		}

		String emoteName = config.getActivationEmote();
		if (emoteName == null || emoteName.isBlank()) {
			return;
		}

		try {
			if (!EmotecraftBridge.playEmoteByName(emoteName.trim())) {
				SkinTotemClient.LOGGER.warn("Emote \"{}\" not found in Emotecraft, activation emote skipped", emoteName);
			}
		} catch (Throwable throwable) {
			disable("play activation emote", throwable);
		}
	}

	private static void disable(String action, Throwable throwable) {
		failed = true;
		SkinTotemClient.LOGGER.error("Emotecraft integration disabled, failed to {}: ", action, throwable);
	}
}
