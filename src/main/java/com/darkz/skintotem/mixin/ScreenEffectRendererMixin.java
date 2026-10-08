package com.darkz.skintotem.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.darkz.skintotem.doll.renderer.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {

	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"
			),
			method = "renderItemActivationAnimation"
	)
	private void renderFloatingDoll(ItemStackRenderState instance, PoseStack matrices, SubmitNodeCollector orderedRenderCommandQueue, int light, int uv, int i, Operation<Void> original, @Local(argsOnly = true) PlayerRenderState playerRenderState) {
		PlayerRenderState.@Nullable ItemActivationRenderState itemActivation = playerRenderState.itemActivation;
		@Nullable ItemStack itemActivationItem = itemActivation == null ? null : itemActivation.item;

		if (!SkinTotemRenderer.sentRenderRequest(matrices, itemActivationItem, DollRenderContext.D_FLOATING, light, uv, 0, orderedRenderCommandQueue)) {
			original.call(instance, matrices, orderedRenderCommandQueue, light, uv, i);
		}
	}

}
