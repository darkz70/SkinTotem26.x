package com.darkz.skintotem.doll.renderer.special;

import com.darkz.skintotem.doll.data.SkinTotemData;
import com.darkz.skintotem.doll.renderer.DollRenderContext;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public record SkinTotemRenderState(
		@Nullable
		SkinTotemData data,
		@Nullable
		ItemStack stack,
		int x,
		int y,
		int width,
		int height,
		float size,
		DollRenderContext renderContext,
		Matrix3x2f matrices,
		@Nullable ScreenRectangle scissorArea,
		@Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState {

	public static SkinTotemRenderState getGui(ItemStack stack, int x, int y, Matrix3x2f matrices, @Nullable ScreenRectangle scissorArea) {
		return new SkinTotemRenderState(null, stack, x, y, 16, 16, 16, DollRenderContext.D_GUI, matrices, scissorArea, PictureInPictureRenderState.getBounds(x, y, x + 16, y + 16, scissorArea));
	}

	public static SkinTotemRenderState getPreview(SkinTotemData data, int x, int y, int width, int height, float size, @Nullable ScreenRectangle scissorArea) {
		return new SkinTotemRenderState(data, null, x, y, width, height, size, DollRenderContext.D_PREVIEW, null, scissorArea, PictureInPictureRenderState.getBounds(x, y, x + width, y + height, scissorArea));
	}

	@Override
	public int x0() {
		return this.x();
	}

	@Override
	public int y0() {
		return this.y();
	}

	@Override
	public int x1() {
		return this.x0() + this.width();
	}

	@Override
	public int y1() {
		return this.y0() + this.height();
	}

	@Override
	public float scale() {
		return 1.0F;
	}

	@Override
	public Matrix3x2f pose() {
		if (this.matrices == null) {
			return PictureInPictureRenderState.super.pose();
		}
		return this.matrices;
	}
}