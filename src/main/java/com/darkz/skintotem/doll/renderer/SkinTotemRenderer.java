package com.darkz.skintotem.doll.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import lombok.experimental.ExtensionMethod;
import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.atlas.AtlasSprite;
import com.darkz.skintotem.client.SkinTotemClient;
import com.darkz.skintotem.config.SkinTotemConfig;
import com.darkz.skintotem.config.rendering.*;
import com.darkz.skintotem.config.totem.SkinTotemSkinType;
import com.darkz.skintotem.doll.data.*;
import com.darkz.skintotem.doll.manager.StandardSkinTotemManager;
import com.darkz.skintotem.doll.model.SkinTotemModel;
import com.darkz.skintotem.doll.model.SkinTotemModel.Drawer;
import com.darkz.skintotem.extension.*;
import com.darkz.skintotem.optimization.SkinTotemRenderRequestsCollector;
import com.darkz.skintotem.thing.ThingMarks;
import com.darkz.skintotem.utils.*;
import com.darkz.skintotem.utils.plugin.SkinTotemPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.AbstractClientPlayer;
import com.mojang.blaze3d.vertex.MultiBufferSource;
import com.mojang.blaze3d.vertex.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.*;


@ExtensionMethod({ItemStackExtension.class, DrawContextExtension.class})
public class SkinTotemRenderer {

	public static boolean sentRenderRequest(PoseStack matrices, ItemStack stack, DollRenderContext context, int light, int overlay, int outlineColor, @Nullable MultiBufferSource provider) {
		if (canRender(stack)) {
			SkinTotemData skinTotemData = stack.getSkinTotemData(false);
			SkinTotemRenderRequestsCollector.getInstance().requestRender(matrices, skinTotemData, stack.getPlayerEntity(), context, light, overlay, outlineColor, provider);
			if (!ThingMarks.WORLD_RENDERING.get().isMarked()) {
				SkinTotemRenderRequestsCollector.getInstance().renderStates();
			}
			return true;
		}
		return false;
	}

	public static void renderDoll(PoseStack matrices, ItemStack stack, DollRenderContext context, MultiBufferSource vertexConsumers, int light, int overlay) {
		renderDoll(matrices, stack.getSkinTotemData(), stack.getPlayerEntity(), context, vertexConsumers, light, overlay);
	}

	public static void renderDoll(PoseStack matrices, SkinTotemData skinTotemData, AbstractClientPlayer holdingPlayer, DollRenderContext context, MultiBufferSource vertexConsumers, int light, int overlay) {
		DollRenderContext renderContext = context == DollRenderContext.D_NONE ? DollRenderContext.D_GUI : context;
		beforeDollRendered(renderContext, holdingPlayer, skinTotemData);
		matrices.pushPose();

		renderContext.apply(skinTotemData.getModelToRender().getMain(), matrices);
		skinTotemData.getRenderProperties().setRenderContext(renderContext);
		matrices.translate(-0.5F, -1.0F, -0.5F);

		switch (renderContext) {
			case D_FIRST_PERSON_LEFT_HAND,
			     D_FIRST_PERSON_RIGHT_HAND -> SkinTotemRenderer.renderInHand(renderContext.isLeftHanded(), true, matrices, vertexConsumers, light, overlay, skinTotemData);
			case D_THIRD_PERSON_LEFT_HAND,
			     D_THIRD_PERSON_RIGHT_HAND -> SkinTotemRenderer.renderInHand(renderContext.isLeftHanded(), false, matrices, vertexConsumers, light, overlay, skinTotemData);
			default -> SkinTotemRenderer.render(matrices, vertexConsumers, light, overlay, skinTotemData);
		}

		afterDollRenderer();
		matrices.popPose();
	}

	public static void renderPreview(GuiGraphicsExtractor context, int x, int y, int width, int height, float size, @Nullable SkinTotemData data) {
		renderPreview(context, x, y, width, height, size, data, DollRenderContext.D_PREVIEW);
	}

	public static void renderPreview(GuiGraphicsExtractor context, int x, int y, int width, int height, float size, @Nullable SkinTotemData data, DollRenderContext renderContext) {
		if (data == null) {
			long currentTime = Util.getMillis();
			float rotationSpeed = 0.05f;
			float rotation = (currentTime * rotationSpeed) % 360;
			context.guiRenderState.addPicturesInPictureState(new com.darkz.skintotem.doll.renderer.special.ItemGuiRenderState(Items.TOTEM_OF_UNDYING.getDefaultInstance(), x, y, width, height, size, Axis.YP.rotationDegrees(rotation), context.scissorStack.peek()));
		} else {
			data.getRenderProperties().setRenderContext(renderContext);
			context.guiRenderState.addPicturesInPictureState(com.darkz.skintotem.doll.renderer.special.SkinTotemRenderState.getPreview(data, x, y, width, height, size, context.scissorStack.peek()));
		}
	}

	public static void renderDataPreview(PoseStack matrices, BufferSource consumers, Runnable draw, float size, @NotNull SkinTotemData data) {
		float i = (size / 2F);

		long currentTime = Util.getMillis();
		float rotationSpeed = 0.05f;

		float rotation = (currentTime * rotationSpeed) % 360;

		LightningUtils.disable3dLighting();
		matrices.pushPose();
		matrices.scale(-i, -i, i);
		matrices.mulPose(Axis.YP.rotationDegrees(rotation));
		matrices.translate(-0.5F, -1.0F, -0.5F);
		SkinTotemRenderer.render(matrices, consumers, 15728880, OverlayTexture.NO_OVERLAY, data);
		matrices.popPose();
		draw.run();
		LightningUtils.enable3dLighting();
	}

	public static void renderInHand(boolean leftHanded, boolean firstPerson, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, SkinTotemData skinTotemData) {
		matrices.pushPose();

		if (firstPerson) {
			SkinTotemConfig config = SkinTotemConfig.getInstance();
			RenderingConfig renderingConfig = config.getRenderingConfig();
			HandRenderingConfig handRenderingConfig = leftHanded ? renderingConfig.getLeftHandConfig() : renderingConfig.getRightHandConfig();

			matrices.translate((handRenderingConfig.getOffsetZ() / 100F) * (leftHanded ? 1 : -1), handRenderingConfig.getOffsetY() / 100F, handRenderingConfig.getOffsetX() / 100F);

			matrices.translate(0.5F, 0.5F, 0.5F);

			double scale = handRenderingConfig.getScale();
			matrices.scale((float) scale, (float) scale, (float) scale);
			matrices.mulPose(Axis.XP.rotationDegrees((float) handRenderingConfig.getRotationX()));
			matrices.mulPose(Axis.YP.rotationDegrees((float) handRenderingConfig.getRotationY() * (leftHanded ? -1 : 1)));
			matrices.mulPose(Axis.ZP.rotationDegrees((float) handRenderingConfig.getRotationZ() * (leftHanded ? -1 : 1)));

			matrices.translate(-0.5F, -0.5F, -0.5F);
		}

		SkinTotemRenderer.render(matrices, vertexConsumers, light, overlay, skinTotemData);
		matrices.popPose();
	}

	public static void render(PoseStack matrices, MultiBufferSource provider, int light, int overlay, SkinTotemData skinTotemData) {
		SkinTotemSprites textures = skinTotemData.getSpritesToRender();
		AtlasSprite skinSprite = textures.getSkinSprite();
		AtlasSprite capeSprite = textures.getCapeSprite();
		AtlasSprite elytraSprite = textures.getElytraSprite();
		SkinTotemModel model = skinTotemData.getModelToRender();

		String nickname = skinTotemData.getNickname();

		if (nickname != null && (nickname.equalsIgnoreCase("dinnerbone") || nickname.equalsIgnoreCase("grumm"))) {
			matrices.translate(0.5F, 1.0F, 0.5F);
			matrices.mulPose(Axis.ZP.rotationDegrees(180));
			matrices.translate(-0.5F, -1.0F, -0.5F);
		}

		matrices.pushPose();
		matrices.translate(0.5F, 0.5F, 0.5F);
		matrices.scale(-1.0F, -1.0F, 1.0F); // - - 0
		matrices.translate(-0.5F, -0.5F, -0.5F);

		Drawer drawer = model.getDrawer();

		if (nickname != null && nickname.equals("deadmau5")) {
			drawer.requestDrawingPartWithSprite("ears", skinSprite);
		}

		if (capeSprite != null && capeSprite.isUploaded()) {
			drawer.requestDrawingPartWithSprite("cape", capeSprite);
		}

		if (elytraSprite.isUploaded()) {
			drawer.requestDrawingPartWithSprite("elytra", elytraSprite);
		}

		applyHeadLookAtCursor(skinTotemData, model);
		drawer.draw(matrices, provider, skinSprite, light, overlay, -1);
		restoreHeadRotation(skinTotemData, model);

		matrices.popPose();
	}

	private static final java.util.List<net.minecraft.client.model.geom.PartPose> savedHeadPoses = new java.util.ArrayList<>();

	private static void applyHeadLookAtCursor(SkinTotemData skinTotemData, SkinTotemModel model) {
		savedHeadPoses.clear();
		DollRenderContext ctx = skinTotemData.getRenderProperties().getRenderContext();
		if (ctx != DollRenderContext.D_GUI && ctx != DollRenderContext.D_TOOLTIP) return;

		net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
		if (mc.screen == null) return;

		com.mojang.blaze3d.platform.Window window = mc.getWindow();
		double scaleFactor = window.getGuiScale();
		double mouseX = mc.mouseHandler.xpos() / scaleFactor;
		double mouseY = mc.mouseHandler.ypos() / scaleFactor;
		double centerX = window.getGuiScaledWidth() / 2.0;
		double centerY = window.getGuiScaledHeight() / 2.0;

		float maxYaw   = 30.0F;
		float maxPitch = 20.0F;
		float yaw   = (float) net.minecraft.util.Mth.clamp((mouseX - centerX) / centerX * maxYaw,   -maxYaw,   maxYaw);
		float pitch = (float) net.minecraft.util.Mth.clamp((mouseY - centerY) / centerY * maxPitch, -maxPitch, maxPitch);

		for (com.darkz.skintotem.model.base.MModel headModel : model.getHead().getModels()) {
			net.minecraft.client.model.geom.ModelPart mp = headModel.getModelPart();
			savedHeadPoses.add(mp.storePose());
			mp.xRot += (float) Math.toRadians(pitch);
			mp.yRot += (float) Math.toRadians(yaw);
		}
	}

	private static void restoreHeadRotation(SkinTotemData skinTotemData, SkinTotemModel model) {
		if (savedHeadPoses.isEmpty()) return;
		java.util.List<com.darkz.skintotem.model.base.MModel> headModels = model.getHead().getModels();
		for (int i = 0; i < Math.min(headModels.size(), savedHeadPoses.size()); i++) {
			headModels.get(i).getModelPart().loadPose(savedHeadPoses.get(i));
		}
		savedHeadPoses.clear();
	}

	private static void beforeDollRendered(@Nullable DollRenderContext context, AbstractClientPlayer playerEntity, SkinTotemData skinTotemData) {
		ProfilerFiller profiler = ProfilerUtils.getProfiler();
		profiler.popPush(SkinTotem.MOD_ID);

		if (context == DollRenderContext.D_GUI && SkinTotemConfig.getInstance().getStandardSkinTotemSkinType() == SkinTotemSkinType.HOLDING_PLAYER) {
			playerEntity = Minecraft.getInstance().player;
		}

		if (StandardSkinTotemManager.getStandardDoll().equals(skinTotemData)) {
			SkinTotemRenderer.prepareStandardDollForRendering(playerEntity, skinTotemData);
		}
	}

	private static void prepareStandardDollForRendering(AbstractClientPlayer playerEntity, SkinTotemData skinTotemData) {
		if (playerEntity != null && SkinTotemConfig.getInstance().getStandardSkinTotemSkinType() == SkinTotemSkinType.HOLDING_PLAYER) {
			if (!playerEntity.equals(Minecraft.getInstance().player) && playerEntity.isInvisibleTo(Minecraft.getInstance().player)) {
				return;
			}
			skinTotemData.setFrameSprites(playerEntity);
		}
	}

	private static void afterDollRenderer() {
		ProfilerFiller profiler = ProfilerUtils.getProfiler();
		profiler.pop();
	}

	public static boolean canRender(@Nullable ItemStack stack) {
		if (!SkinTotemClient.canProcess(stack)) {
			return false;
		}
		if (stack.hasModdedModel()) {
			return false;
		}
		Component realCustomName = stack.getRealCustomName();
		boolean standardDollWithoutName = realCustomName == null;
		if (standardDollWithoutName && SkinTotemConfig.getInstance().isUseVanillaTotemModel()) {
			return false;
		}
		return !SkinTotemPlugin.work(realCustomName);
	}
}
