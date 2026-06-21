package com.darkz.skintotem.doll.renderer.special;

import com.mojang.blaze3d.platform.Lighting.Entry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.darkz.skintotem.SkinTotem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ItemGuiElementRenderer extends PictureInPictureRenderer<ItemGuiRenderState> {

	private final ItemStackRenderState itemRenderState = new ItemStackRenderState();

	public ItemGuiElementRenderer() {
		super();
	}

	@Override
	public Class<ItemGuiRenderState> getRenderStateClass() {
		return ItemGuiRenderState.class;
	}

	@Override
	protected void renderToTexture(ItemGuiRenderState state, PoseStack matrices, SubmitNodeCollector submitNodeCollector) {
		Minecraft client = Minecraft.getInstance();

		client.gameRenderer.lighting().setupFor(Entry.ITEMS_FLAT);
		matrices.mulPose(state.rotation());
		float size = state.size();
		matrices.scale(-size, -size, size);
		this.renderItem(
				state.stack(),
				ItemDisplayContext.FIXED,
				15728880,
				OverlayTexture.NO_OVERLAY,
				matrices,
				submitNodeCollector,
				client.level,
				0
		);
	}

	@Override
	protected float getTranslateY(int height, int windowScaleFactor) {
		return height / 2F;
	}

	@Override
	protected String getTextureLabel() {
		return "%s-item-special-gui-renderer".formatted(SkinTotem.MOD_ID);
	}

	public void renderItem(ItemStack stack, ItemDisplayContext displayContext, int light, int overlay, PoseStack matrices, SubmitNodeCollector submitNodeCollector, @Nullable Level world, int seed) {
		this.renderItem(null, stack, displayContext, matrices, submitNodeCollector, world, light, overlay, seed);
	}

	public void renderItem(@Nullable LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext, PoseStack matrices, SubmitNodeCollector submitNodeCollector, @Nullable Level world, int light, int overlay, int seed) {
		Minecraft.getInstance().getItemModelResolver().updateForTopItem(this.itemRenderState, stack, displayContext, world, entity, seed);
		this.itemRenderState.submit(matrices, submitNodeCollector, light, overlay, 0);
	}
}
