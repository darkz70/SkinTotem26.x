package com.darkz.skintotem.mixin;

import lombok.experimental.ExtensionMethod;
import com.darkz.skintotem.doll.data.SkinTotemData;
import com.darkz.skintotem.doll.renderer.special.*;
import com.darkz.skintotem.extension.ItemStackExtension;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ExtensionMethod(ItemStackExtension.class)
@Mixin(GuiRenderer.class)
public class GuiRendererMixin {


	@Shadow
	@Final
	private FeatureRenderDispatcher featureRenderDispatcher;

	@Shadow @Final private GuiRenderState renderState;

	@Inject(at = @At("HEAD"), method = "preparePictureInPictureState", cancellable = true)
	private void renderDoll(PictureInPictureRenderState elementState, int windowScaleFactor, CallbackInfo ci) {
		if (!(elementState instanceof SkinTotemRenderState skinTotemRenderState)) {
			return;
		}

		SkinTotemData data = skinTotemRenderState.data() == null ?
				skinTotemRenderState.stack() == null ?
						null
						:
						skinTotemRenderState.stack().getSkinTotemData()
				:
				skinTotemRenderState.data();

		if (data == null) {
			return;
		}

		SkinTotemGuiElementRenderer guiRenderer = data.getGuiRenderer();
		guiRenderer.setActive(true);
		guiRenderer.prepare(skinTotemRenderState, this.renderState, this.featureRenderDispatcher, windowScaleFactor);
		ci.cancel();
	}

	@Inject(at = @At(value = "TAIL"), method = "preparePictureInPicture")
	private void clearUnusedRenderers(CallbackInfo ci) {
		SkinTotemGuiElementRenderer.clearUnusedRenderers();
	}

	@Inject(at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"), method = "close")
	private void closeSkinTotemRenderers(CallbackInfo ci) {
		SkinTotemGuiElementRenderer.closeTotemRenderers();
	}

}
