package com.darkz.skintotem.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpriteContents.class)
public class SpriteContentsMixin {

	@Unique
	private static final String TEXT = "Wait! This crash was caused by the \"skintotem\" mod SPECIFICALLY to prevent a crash via drivers. This crash was made to make debugging this unexpected error easier. Someone (maybe \"skintotem\") just pushed closed sprite to upload and this shouldn't happen! Please report this crash-report to \"skintotem\" issue tracker: https://github.com/darkz70/SkinTotem26.x/issues";

	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/renderpearl/api/commands/CommandEncoder;writeToTexture(Lcom/mojang/renderpearl/api/textures/GpuTexture;Lcom/mojang/blaze3d/platform/NativeImage;IIII)V"),
			method = "uploadFirstFrame"
	)
	private void validateImageBeforeUpload(com.mojang.renderpearl.api.commands.CommandEncoder instance, com.mojang.renderpearl.api.textures.GpuTexture target, NativeImage source, int mipLevel, int x, int y, int z, Operation<Void> original) {
		try {
			// getPixelBytes() will fail if the underlying native image has already been closed/freed.
			source.getPixelBytes();
		} catch (Exception e) {
			throw new IllegalArgumentException(TEXT, e);
		}
		original.call(instance, target, source, mipLevel, x, y, z);
	}

}
