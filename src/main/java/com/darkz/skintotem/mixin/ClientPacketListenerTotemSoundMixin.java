package com.darkz.skintotem.mixin;

import com.darkz.skintotem.compat.EmoteSupport;
import com.darkz.skintotem.sound.SkinTotemSounds;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Собственный звук срабатывания тотема бессмертия.
 * <p>
 * Клиент проигрывает ванильный {@code SoundEvents.TOTEM_USE} в {@code handleEntityEvent}
 * (событие сущности 35). Перехватываем именно этот вызов: свой звук играем всегда,
 * ванильный — только если в конфиге не включена его замена.
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
	}

	/**
	 * {@code displayItemActivation} вызывается только для локального игрока, поэтому точка подходит
	 * для эмоции «меня спас тотем»: её проигрывает сам игрок, а кукла повторяет её зеркалированием.
	 */
	@Inject(
			method = "handleEntityEvent",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/player/LocalPlayer;displayItemActivation(Lnet/minecraft/world/item/ItemStack;)V"
			)
	)
	private void skintotem$playActivationEmote(ClientboundEntityEventPacket packet, CallbackInfo callbackInfo) {
		EmoteSupport.playActivationEmote();
	}
}
