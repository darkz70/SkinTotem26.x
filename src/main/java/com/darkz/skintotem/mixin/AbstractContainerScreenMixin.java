package com.darkz.skintotem.mixin;

import com.darkz.skintotem.gui.widget.tag.*;
import com.darkz.skintotem.utils.mixin.STAnvilScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

	@Inject(at = @At("HEAD"), method = "mouseDragged", cancellable = true)
	private void mouseDragged(MouseButtonEvent click, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
		TagButtonWidget tagButtonWidget = this.getTagButtonWidget();
		if (tagButtonWidget == null) {
			return;
		}
		if (tagButtonWidget.mouseDragged(click, deltaX, deltaY)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(at = @At("HEAD"), method = "mouseReleased", cancellable = true)
	private void mouseReleased(MouseButtonEvent click, CallbackInfoReturnable<Boolean> cir) {
		TagButtonWidget tagButtonWidget = this.getTagButtonWidget();
		if (tagButtonWidget == null) {
			return;
		}
		if (tagButtonWidget.mouseReleased(click)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(at = @At("HEAD"), method = "mouseScrolled", cancellable = true)
	private void onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
		TagMenuWidget tagMenuWidget = this.getTagMenuWidget();
		if (tagMenuWidget == null) {
			return;
		}
		if (tagMenuWidget.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
			cir.setReturnValue(true);
		}
	}

	@Unique
	private @Nullable TagMenuWidget getTagMenuWidget() {
		if (!(this instanceof STAnvilScreen anvilScreen)) {
			return null;
		}
		return anvilScreen.mySkinTotem$getTagMenuWidget();
	}

	@Unique
	private @Nullable TagButtonWidget getTagButtonWidget() {
		if (!(this instanceof STAnvilScreen anvilScreen)) {
			return null;
		}
		return anvilScreen.mySkinTotem$getTagButtonWidget();
	}
}
