package com.darkz.skintotem.mixin.yacl.widget;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import dev.isxander.yacl3.gui.AbstractWidget;
import com.darkz.skintotem.gui.BackgroundRenderer;
import com.darkz.skintotem.yacl.YACLConfigurationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractWidget.class)
public class AbstractWidgetMixin {

	@WrapOperation(method = "drawButtonRect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
	private void renderTransparencyWidget(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier location, int x, int y, int width, int height, Operation<Void> original, @Local(argsOnly = true, ordinal = 0) boolean hovered, @Local(argsOnly = true, ordinal = 1) boolean enabled) {
		if (YACLConfigurationScreen.notOpen(Minecraft.getInstance().gui.screen())) {
			original.call(instance, renderPipeline, location, x, y, width, height);
			return;
		}
		BackgroundRenderer.drawTransparencyWidgetBackground(instance, x, y, width, height, enabled, hovered);
	}
}