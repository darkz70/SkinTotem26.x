package com.darkz.skintotem.yacl.custom.renderer;

import dev.isxander.yacl3.gui.image.ImageRenderer;
import lombok.experimental.ExtensionMethod;
import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.config.SkinTotemConfig;
import com.darkz.skintotem.config.totem.SkinTotemSkinType;
import com.darkz.skintotem.doll.data.SkinTotemData;
import com.darkz.skintotem.doll.manager.StandardSkinTotemManager;
import com.darkz.skintotem.doll.renderer.SkinTotemRenderer;
import com.darkz.skintotem.extension.DrawContextExtension;
import com.darkz.skintotem.gui.BackgroundRenderer;
import com.darkz.skintotem.utils.*;
import com.darkz.skintotem.utils.plugin.SkinTotemPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.MultiLineLabel;
import org.jetbrains.annotations.Nullable;

@ExtensionMethod(DrawContextExtension.class)
public class SkinTotemPreviewRenderer implements ImageRenderer {

	private static final int STANDARD_SUGGESTION_TEXT_COLOR = ColorUtils.getArgb(255, 79, 64);
	private static final int HOLDING_PLAYER_COLOR = ColorUtils.getArgb(212, 120, 28);

	private SkinTotemData data;
	@Nullable
	private MultiLineLabel suggestionText;
	@Nullable
	private SkinTotemSkinType suggestionSkinType;

	private int lastRenderWidth;

	public SkinTotemPreviewRenderer() {
		this.data = StandardSkinTotemManager.getStandardDoll();
	}

	@Override
	public int render(GuiGraphicsExtractor context, int x, int y, int renderWidth, float tickDelta) {
		int offset = 5;
		int width = renderWidth - (offset * 2);

		this.renderDollStatus(context, x + offset, y + offset, width);
		this.updateSuggestion(width, this.lastRenderWidth != renderWidth);
		this.lastRenderWidth = renderWidth;

		int i = this.renderSuggestionText(context, x + offset, y + offset + 30 + 10, width);
		return (this.renderDoll(context, x + offset, i, width) + offset) - y;
	}

	private void updateSuggestion(int width, boolean resized) {
		Font textRenderer = Minecraft.getInstance().font;
		SkinTotemConfig config = SkinTotemConfig.getInstance();
		SkinTotemSkinType skinType = config.getStandardSkinTotemSkinType();
		String skinValue = config.getStandardSkinTotemSkinValue();

		SkinTotemSkinType type = this.suggestionSkinType;

		if ((skinType.isNeedData() && skinValue.isEmpty()) && skinType != SkinTotemSkinType.STEVE || skinType == SkinTotemSkinType.HOLDING_PLAYER) {
			this.suggestionSkinType = skinType;
		} else {
			this.suggestionSkinType = null;
			this.suggestionText     = null;
		}

		if (this.suggestionSkinType != null && (type != this.suggestionSkinType || resized)) {
			this.suggestionText = MultiLineLabel.create(textRenderer, this.suggestionSkinType.getSuggestionText().withColor(this.getSuggestionColors()), width - 5);
		}
	}

	private int renderSuggestionText(GuiGraphicsExtractor context, int x, int y, int width) {
		int suggestionColor = this.getSuggestionColors();

		if (this.suggestionText == null) {
			return y;
		}

		context.push();
		context.translate(0, 0, 10);
		int i = this.suggestionText.visitLines(TextAlignment.LEFT, x + 5, y + 5, 10, context.textRenderer());
		context.translate(0, 0, -5);
		BackgroundRenderer.drawTransparencyWidgetBackground(context, x, y, width, i - y + 5, true, suggestionColor);

		context.pop();

		return i + 5 + 10;
	}

	private int getSuggestionColors() {
		if (this.suggestionSkinType == SkinTotemSkinType.HOLDING_PLAYER) {
			return HOLDING_PLAYER_COLOR;
		}
		return STANDARD_SUGGESTION_TEXT_COLOR;
	}

	private void renderDollStatus(GuiGraphicsExtractor context, int x, int y, int width) {
		BackgroundRenderer.drawTransparencyWidgetBackground(context, x, y, width, 30, true, true);

		DrawUtils.drawCenteredText(context, SkinTotem.text("text.status").append(this.data.getStandardSprites().getState().getText()), x + 2, y, width - 4, 30);
	}

	private int renderDoll(GuiGraphicsExtractor context, int x, int y, int size) {
		SkinTotemConfig config = SkinTotemConfig.getInstance();

		BackgroundRenderer.drawTransparencyWidgetBackground(context, x, y, size, size, true, true);

		SkinTotemRenderer.renderPreview(context, x, y, size, size, size / 1.5F, config.isUseVanillaTotemModel() || SkinTotemPlugin.isGoodStick(config.getStandardSkinTotemSkinValue()) ? null : this.data.refreshAndApplyRenderProperties());

		return y + size + 2;
	}

	@Override
	public void close() {

	}

	public void updateDoll() {
		this.data = StandardSkinTotemManager.initializeStandardDollData();
	}

	public void updateDollState(boolean recreateModel) {
		this.data = StandardSkinTotemManager.updateDoll(recreateModel);
	}
}
