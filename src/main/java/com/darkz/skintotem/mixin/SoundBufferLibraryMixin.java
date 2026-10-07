package com.darkz.skintotem.mixin;

import com.darkz.skintotem.sound.custom.CustomSoundPack;
import com.mojang.blaze3d.audio.SoundBuffer;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Подменяет звуки мода файлами из {@code config/skintotem/sounds}.
 * <p>
 * Это единственное место, где игра берёт готовый звуковой буфер, поэтому пользовательский файл
 * работает везде одинаково и не мешает ни ресурс-пакам, ни чужим звукам: для всех остальных
 * идентификаторов {@link CustomSoundPack#getBuffer} возвращает {@code null} и игра идёт своим путём.
 */
@Mixin(SoundBufferLibrary.class)
public class SoundBufferLibraryMixin {

	@Inject(method = "getCompleteBuffer", at = @At("HEAD"), cancellable = true)
	private void skintotem$useCustomSound(Identifier location, CallbackInfoReturnable<CompletableFuture<SoundBuffer>> callback) {
		CompletableFuture<SoundBuffer> custom = CustomSoundPack.getBuffer(location);
		if (custom != null) {
			callback.setReturnValue(custom);
		}
	}

	/** Игра очищает свои буферы — наши тоже становятся недействительными. */
	@Inject(method = "clear", at = @At("HEAD"), require = 0)
	private void skintotem$onClear(CallbackInfo callback) {
		CustomSoundPack.onSoundEngineReload();
	}
}
