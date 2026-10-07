package com.darkz.skintotem.sound;

import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.config.SkinTotemConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * Собственные звуки мода.
 * <p>
 * Мод клиентский, поэтому события звука НЕ регистрируются в {@code BuiltInRegistries.SOUND_EVENT}
 * (это сдвинуло бы сетевые id и сломало совместимость с ванильными серверами). Вместо этого
 * создаются отдельные {@link SoundEvent} и проигрываются напрямую через {@code SoundManager},
 * который находит файлы по {@code assets/skintotem/sounds.json}.
 */
public final class SkinTotemSounds {

	public static final SoundEvent TOTEM_ACTIVATE = create("totem_activate");
	public static final SoundEvent DOLL_SUMMON = create("doll_summon");
	public static final SoundEvent SKIN_CHANGE = create("skin_change");
	public static final SoundEvent SKIN_ERROR = create("skin_error");

	private SkinTotemSounds() {
	}

	private static SoundEvent create(String path) {
		return SoundEvent.createVariableRangeEvent(SkinTotem.id(path));
	}

	public static boolean isEnabled() {
		SkinTotemConfig config = SkinTotemConfig.getInstance();
		return config != null && config.isModEnabled() && config.isSoundsEnabled();
	}

	private static float masterVolume() {
		SkinTotemConfig config = SkinTotemConfig.getInstance();
		return config == null ? 1.0F : config.getSoundsVolume();
	}

	/** Проигрывает звук в точке мира. */
	public static void playAt(SoundEvent sound, double x, double y, double z, float volume, float pitch) {
		if (!isEnabled()) {
			return;
		}
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null) {
			return;
		}
		minecraft.getSoundManager().play(new SimpleSoundInstance(
				sound,
				SoundSource.PLAYERS,
				volume * masterVolume(),
				pitch,
				minecraft.level.getRandom(),
				x, y, z
		));
	}

	/** Проигрывает звук у сущности (или у камеры, если сущности нет). */
	public static void playAtEntity(SoundEvent sound, @Nullable Entity entity, float volume, float pitch) {
		if (!isEnabled()) {
			return;
		}
		if (entity == null) {
			playUi(sound, pitch, volume);
			return;
		}
		playAt(sound, entity.getX(), entity.getY(), entity.getZ(), volume, pitch);
	}

	/** Проигрывает звук «в голове» игрока — для интерфейсных событий. */
	public static void playUi(SoundEvent sound, float pitch, float volume) {
		if (!isEnabled()) {
			return;
		}
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, volume * masterVolume()));
	}

	/**
	 * Вызывается из миксина при срабатывании тотема бессмертия.
	 *
	 * @return {@code true}, если ванильный звук нужно подавить
	 */
	public static boolean onTotemActivated(double x, double y, double z) {
		if (!isEnabled()) {
			return false;
		}
		playAt(TOTEM_ACTIVATE, x, y, z, 1.0F, 1.0F);
		return SkinTotemConfig.getInstance().isReplaceVanillaTotemSound();
	}

	/** Кукла с новым скином впервые появилась в руке локального игрока. */
	public static void onDollSummoned(@Nullable Entity holder) {
		playAtEntity(DOLL_SUMMON, holder, 0.6F, 1.0F);
	}

	private static final Map<String, Long> LAST_RENDERED = new HashMap<>();
	/** Пауза, после которой кукла считается «появившейся заново», а не отрисованной в соседнем кадре. */
	private static final long APPEAR_GAP_NANOS = 500_000_000L;
	private static final long FORGET_NANOS = 60_000_000_000L;

	/**
	 * Вызывается на каждый кадр отрисовки куклы в руке. Звук играет только в тот кадр,
	 * когда кукла появилась после перерыва, иначе он звучал бы каждый кадр.
	 */
	public static void onDollRendered(@Nullable String key, @Nullable Entity holder) {
		if (!isEnabled() || holder == null || holder != Minecraft.getInstance().player) {
			return;
		}

		long now = System.nanoTime();
		Long last = LAST_RENDERED.put(key == null ? "" : key, now);
		if (last == null || now - last > APPEAR_GAP_NANOS) {
			onDollSummoned(holder);
		}

		if (LAST_RENDERED.size() > 128) {
			LAST_RENDERED.entrySet().removeIf(entry -> now - entry.getValue() > FORGET_NANOS);
		}
	}

	/** Скин успешно обновлён командой. */
	public static void onSkinChanged() {
		playUi(SKIN_CHANGE, 1.0F, 0.7F);
	}

	/** Не удалось загрузить скин. */
	public static void onSkinError() {
		playUi(SKIN_ERROR, 1.0F, 0.7F);
	}
}
