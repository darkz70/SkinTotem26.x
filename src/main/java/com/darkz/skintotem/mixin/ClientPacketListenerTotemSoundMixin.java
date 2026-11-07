package com.darkz.skintotem.mixin;

import com.darkz.skintotem.compat.EmoteSupport;
import com.darkz.skintotem.sound.SkinTotemSounds;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Собственный звук срабатывания тотема бессмертия и эмоция при спасении.
 * <p>
 * Клиент проигрывает ванильный {@code SoundEvents.TOTEM_USE} в {@code handleEntityEvent}
 * (событие сущности 35) через {@code ClientLevel.playLocalSound}. Перехватываем именно этот
 * вызов: свой звук играем всегда, ванильный — только если в конфиге не включена его замена.
 * <p>
 * В 26.2 из этого метода больше нет прямого вызова {@code LocalPlayer.displayItemActivation},
 * поэтому эмоцию «меня спас тотем» вызываем здесь же, по факту звука TOTEM_USE — он зовётся
 * только для локального игрока.
 */
@Mixin(ClientPacketListener.class)
public class ClientPacketListenerTotemSoundMixin {

	@WrapOperation(
			method = "handleEntityEvent",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
			)
	)
	private void skintotem$totemActivationSound(ClientLevel level, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay, Operation<Void> original) {
		boolean suppressVanilla = SkinTotemSounds.onTotemActivated(x, y, z);
		if (!suppressVanilla) {
			original.call(level, x, y, z, sound, source, volume, pitch, distanceDelay);
		}
		if (sound == SoundEvents.TOTEM_USE) {
			EmoteSupport.playActivationEmote();
		}
	}
}
