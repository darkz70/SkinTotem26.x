package com.darkz.skintotem.optimization;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import lombok.experimental.ExtensionMethod;
import com.darkz.skintotem.atlas.LockableAtlasTexture;
import com.darkz.skintotem.atlas.manager.SkinTotemAtlasManager;
import com.darkz.skintotem.client.SkinTotemClient;
import com.darkz.skintotem.doll.data.*;
import com.darkz.skintotem.doll.model.SkinTotemModel;
import com.darkz.skintotem.doll.renderer.*;
import com.darkz.skintotem.sound.SkinTotemSounds;
import com.darkz.skintotem.extension.MatrixStackEntryExtension;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.*;
import org.jetbrains.annotations.Nullable;

@ExtensionMethod(MatrixStackEntryExtension.class)
public class SkinTotemRenderRequestsCollector {

	private static final SkinTotemRenderRequestsCollector INSTANCE = new SkinTotemRenderRequestsCollector();
	private final PoseStack matrices = new PoseStack();
	private final List<SkinTotemRenderRequest> requests = new ArrayList<>();
	private final SkinTotemRenderProperties tempProperties = new SkinTotemRenderProperties();

	private SkinTotemRenderRequestsCollector() {

	}

	public static SkinTotemRenderRequestsCollector getInstance() {
		return INSTANCE;
	}

	public void requestRender(PoseStack matrices, SkinTotemData data, AbstractClientPlayer holdingPlayer, DollRenderContext context, int light, int overlay, int outlineColor, @Nullable SubmitNodeCollector provider) {
		PoseStack.Pose entry = matrices.last();
		this.requests.add(new SkinTotemRenderRequest(entry.copy(), data, data.getRenderProperties().copy(), holdingPlayer, context, light, overlay, outlineColor, provider));
	}

	public void renderStates() {
		LockableAtlasTexture atlasTexture = SkinTotemAtlasManager.getNullableAtlasTexture();
		if (atlasTexture == null) {
			SkinTotemClient.LOGGER.error("Game tried to render doll model requests, but atlas not initialized yet!");
			return;
		}
		atlasTexture.setLocked(true);

		for (SkinTotemRenderRequest request : this.requests) {
			if (request.provider() != null) {
				this.renderRequest(request, request.provider());
			}
		}

		this.requests.clear();
		// We should draw this before unlocking, to make sure that atlas won't be changed earlier than the draw call
		atlasTexture.setLocked(false);
	}

	private void renderRequest(SkinTotemRenderRequest request, SubmitNodeCollector mainProvider) {
		this.matrices.pushPose();
		this.matrices.last().copyFrom(request.copyPeek());

		SkinTotemData data = request.data();
		this.tempProperties.copyFrom(data.getRenderProperties());

		data.getRenderProperties().copyFrom(request.renderProperties());
		data.clearFrameModel();
		SkinTotemModel modelToRender = data.getModelToRender();
		modelToRender.resetPartsVisibility();
		data.getRenderProperties().applyToModel(modelToRender);

		if (request.context().isHand()) {
			SkinTotemSounds.onDollRendered(data.getNickname(), request.holdingPlayer());
		}

		// Обводка (свечение) снова работает: outlineColor уходит вторым проходом в outline-слой атласа.
		SkinTotemRenderer.renderDoll(this.matrices, data, request.holdingPlayer(), request.context(), mainProvider, request.light(), request.overlay(), request.outlineColor());

		data.getRenderProperties().copyFrom(this.tempProperties);

		this.matrices.popPose();
	}

}
