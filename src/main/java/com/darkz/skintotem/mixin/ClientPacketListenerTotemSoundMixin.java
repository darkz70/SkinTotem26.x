package com.darkz.skintotem.mixin;

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
 * Собственный звук срабатывания тотема бессмертия.
 * <p>
 * Клиент проигрывает ванильный {@code SoundEvents.TOTEM_USE} в {@code handleEntityEvent}
 * (событие сущности 35) через {@code ClientLevel.playLocalSound}. Перехватываем именно этот
 * вызов: свой звук играем всегда, ванильный — только если в конфиге не включена его замена.
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
		if (sound != SoundEvents.TOTEM_USE) {
			original.call(level, x, y, z, sound, source, volume, pitch, distanceDelay);
			return;
		}
		boolean suppressVanilla = SkinTotemSounds.onTotemActivated(x, y, z);
		if (!suppressVanilla) {
			original.call(level, x, y, z, sound, source, volume, pitch, distanceDelay);
		}
	}
}
