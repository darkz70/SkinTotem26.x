package com.darkz.skintotem.mixin.yacl;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.yacl3.gui.YACLScreen;
import com.darkz.skintotem.yacl.YACLConfigurationScreen;
import com.darkz.skintotem.yacl.custom.TransparencySprites;
import com.darkz.skintotem.yacl.custom.category.rendering.RenderingCategoryTab;
import com.darkz.skintotem.yacl.custom.screen.MyTotemDollYACLScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Screen.class)
public abstract class ScreenMixin {

	@WrapWithCondition(method = "extractBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractBlurredBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"))
	public boolean disableBlur(Screen instance, GuiGraphicsExtractor context) {
		Screen screen = Minecraft.getInstance().screen;
		if (YACLConfigurationScreen.notOpen(screen)) {
			return true;
		}
		if (!(screen instanceof YACLScreen yaclScreen)) {
			return true;
		}
		return !(yaclScreen.tabManager.getCurrentTab() instanceof RenderingCategoryTab);
	}

	@ModifyArg(method = "extractMenuBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractMenuBackgroundTexture(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/resources/Identifier;IIFFII)V"), index = 1)
	private Identifier swapBackgroundTexture(Identifier original) {
		if (YACLConfigurationScreen.notOpen(((Screen) (Object) this))) {
			return original;
		}
		return TransparencySprites.getMenuBackgroundTexture();
	}

}
