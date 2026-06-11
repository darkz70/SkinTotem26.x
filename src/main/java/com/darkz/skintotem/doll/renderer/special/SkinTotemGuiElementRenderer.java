package com.darkz.skintotem.doll.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import lombok.experimental.ExtensionMethod;
import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.client.SkinTotemClient;
import com.darkz.skintotem.config.SkinTotemConfig;
import com.darkz.skintotem.doll.data.SkinTotemRenderProperties;
import com.darkz.skintotem.doll.renderer.*;
import com.darkz.skintotem.extension.ItemStackExtension;
import com.darkz.skintotem.utils.LightningUtils;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jetbrains.annotations.NotNull;

@ExtensionMethod(ItemStackExtension.class)
public class SkinTotemGuiElementRenderer extends PictureInPictureRenderer<SkinTotemRenderState> {

	public static final Map<SkinTotemRenderProperties, SkinTotemGuiElementRenderer> PROPERTIES_RENDERERS = new HashMap<>();

	private boolean active;

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public SkinTotemGuiElementRenderer(BufferSource vertexConsumers) {
		super(vertexConsumers);
	}

	@NotNull
	public static SkinTotemGuiElementRenderer getRenderer(SkinTotemRenderProperties renderProperties, BufferSource immediate) {
		SkinTotemGuiElementRenderer renderer = PROPERTIES_RENDERERS.get(renderProperties.copy());
		if (renderer == null) {
			SkinTotemGuiElementRenderer createdRenderer = new SkinTotemGuiElementRenderer(immediate);
			PROPERTIES_RENDERERS.put(renderProperties, createdRenderer);
			return createdRenderer;
		}

		return renderer;
	}

	public static void closeTotemRenderers() {
		PROPERTIES_RENDERERS.values().forEach(SkinTotemGuiElementRenderer::close);
	}

	public static void clearUnusedRenderers() {
		int all = PROPERTIES_RENDERERS.size();
		PROPERTIES_RENDERERS.entrySet().removeIf((entry) -> {
			SkinTotemGuiElementRenderer renderer = entry.getValue();
			if (!renderer.isActive()) {
				renderer.close();
				return true;
			}
			renderer.setActive(false);
			return false;
		});
		int cleared = all - PROPERTIES_RENDERERS.size();
		if (SkinTotemConfig.getInstance().isDebugLogEnabled() && cleared != 0) {
			SkinTotemClient.LOGGER.info("Removed Inactive Totem Doll Renderers: {}", cleared);
		}
	}

	@Override
	protected void renderToTexture(SkinTotemRenderState state, PoseStack matrices) {
		if (state.renderContext() == DollRenderContext.D_PREVIEW && state.data() != null) {
			SkinTotemRenderer.renderDataPreview(matrices, this.bufferSource, this.bufferSource::endBatch, state.size() + 1, state.data());
		} else if (state.stack() != null) {
			LightningUtils.disable3dLighting();
			matrices.pushPose();
			matrices.scale(16F, -16F, -16F);
			SkinTotemRenderer.renderDoll(matrices, state.stack(), state.renderContext(), this.bufferSource, 15728880, OverlayTexture.NO_OVERLAY);
			matrices.popPose();
			this.bufferSource.endBatch();
			LightningUtils.enable3dLighting();

			if (state.stack().hasModdedModel()) {
				state.stack().setModdedModel(false);
			}
		Override
	public Class<SkinTotemRenderState> getRenderStateClass() {
		return SkinTotemRenderState.class;
	}

	@Override
	protected String getTextureLabel() {
		return "%s-doll-special-gui-renderer".formatted(SkinTotem.MOD_ID);
	}

	@Override
	protected float getTranslateY(int height, int windowScaleFactor) {
		return height / 2F;
		}
	}
