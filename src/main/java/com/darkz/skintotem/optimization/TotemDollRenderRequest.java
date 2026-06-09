package com.darkz.skintotem.optimization;

import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.darkz.skintotem.doll.data.*;
import com.darkz.skintotem.doll.renderer.DollRenderContext;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.Nullable;

public record TotemDollRenderRequest(
		Pose copyPeek,
		TotemDollData data,
		TotemDollRenderProperties renderProperties,
		AbstractClientPlayer holdingPlayer,
		DollRenderContext context,
		int light,
		int overlay,
		int outlineColor,
		@Nullable MultiBufferSource provider) {

}
