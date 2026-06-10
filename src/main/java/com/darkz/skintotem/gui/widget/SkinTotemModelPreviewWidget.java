package com.darkz.skintotem.gui.widget;

import lombok.*;
import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.doll.data.SkinTotemData;
import com.darkz.skintotem.doll.manager.StandardSkinTotemManager;
import com.darkz.skintotem.doll.renderer.SkinTotemRenderer;
import com.darkz.skintotem.model.base.MModel;
import com.darkz.skintotem.model.bb.manager.BlockBenchModelManager;
import com.darkz.skintotem.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

@Getter
@Setter
public class SkinTotemModelPreviewWidget extends AbstractWidget {

	private final float size;

	private SkinTotemData data;

	private boolean loading;
	private int failedLoadingStatusCode = 0;

	public SkinTotemModelPreviewWidget(int x, int y, float size) {
		super(x, y, (int) size, (int) size, Component.nullToEmpty(""));
		this.size = size;
		this.data = StandardSkinTotemManager.getStandardDoll().copy();
	}

	@Override
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		graphics.enableScissor(this.getX(), this.getY(), (this.getX() + this.getWidth()), this.getY() + this.getHeight());
		if (this.loading) {
			this.renderLoadingText(graphics);
		} else {
			this.renderPreview(graphics);
		}
		graphics.disableScissor();
	}

	protected void renderLoadingText(GuiGraphicsExtractor context) {
		int halfOfSize = (int) this.size / 2;
		Font textRenderer = Minecraft.getInstance().font;
		//context.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + 1, -1);
		DrawUtils.drawCenteredText(context, this.getLoadingText(Util.getMillis()), this.getX(), this.getY() + halfOfSize - (textRenderer.lineHeight / 2), (int) this.size);
	}

	protected void renderPreview(GuiGraphicsExtractor context) {
		SkinTotemRenderer.renderPreview(context, this.getX(), this.getY(), (int) this.getSize(), (int) this.getSize(), this.getSize() / 1.5F, this.getData().refreshAndApplyRenderProperties());
	}

	public void updateModel(Identifier id) {
		this.loading                 = true;
		this.failedLoadingStatusCode = 0;
		BlockBenchModelManager.getModelAsyncAsResponse(id, (response) -> {
			MModel value = response.value();
			if (value != null) {
				this.updateModel(value);
				this.loading = false;
			} else {
				this.failedLoadingStatusCode = response.statusCode();
			}
		});
	}

	public void updateModel(MModel model) {
		this.data.setStandardMModel(model);
	}

	private Component getLoadingText(long tick) {
		if (this.failedLoadingStatusCode == 100) {
			return SkinTotem.text("text.loading.failed.to_load");
		} else if (this.failedLoadingStatusCode == 102) {
			return SkinTotem.text("text.loading.failed.unsupported_format");
		} else if (this.failedLoadingStatusCode > 101 && this.failedLoadingStatusCode < 104) {
			return SkinTotem.text("text.loading.failed.wrong_metadata");
		}

		int i = (int) (tick / 300L % 4L);
		return SkinTotem.text("text.loading.%s".formatted(i));
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput builder) {

	}
}
