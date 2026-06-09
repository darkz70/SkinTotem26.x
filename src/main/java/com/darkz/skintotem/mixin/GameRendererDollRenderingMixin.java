package com.darkz.skintotem.mixin;

import com.darkz.skintotem.optimization.TotemDollRenderRequestsCollector;
import com.darkz.skintotem.thing.ThingMarks;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class GameRendererDollRenderingMixin {

	@Inject(at = @At("HEAD"), method = "extractVisibleEntities")
	private void beforeDollRendering(CallbackInfo ci) {
		ThingMarks.WORLD_RENDERING.get().setMarked(true);
	}

	@Inject(at = @At("TAIL"), method = "extractVisibleEntities")
	private void afterDollRendering(CallbackInfo ci) {
		TotemDollRenderRequestsCollector.getInstance().renderStates();
		ThingMarks.WORLD_RENDERING.get().setMarked(false);
	}

}
