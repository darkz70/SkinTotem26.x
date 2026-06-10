package com.darkz.skintotem.yacl.custom.controller.totem;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.utils.DrawUtils;
import com.darkz.skintotem.yacl.custom.screen.SkinTotemModelSelectionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class SkinTotemModelControllerElement extends ControllerWidget<SkinTotemModelController> {

	private final SkinTotemModelController controller;
	private final Component selectText;

	public SkinTotemModelControllerElement(SkinTotemModelController controller, YACLScreen screen, Dimension<Integer> dim) {
		super(controller, screen, dim);
		this.controller = controller;
		this.selectText = SkinTotem.text("text.select_text");
	}

	@Override
	protected int getHoveredControlWidth() {
		return this.getUnhoveredControlWidth();
	}

	@Override
	protected Component getValueText() {
		if (this.hovered && this.isAvailable()) {
			return this.selectText;
		}
		return super.getValueText();
	}

	@Override
	protected void extractValueText(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		Font textRenderer = Minecraft.getInstance().font;
		Component valueText = this.getValueText();

		int width = textRenderer.width(valueText);
		if (this.getDimension().x() + this.getXPadding() + width > this.getDimension().xLimit() - this.getXPadding()) {
			DrawUtils.drawText(graphics, valueText, this.getDimension().x() + this.getXPadding(), this.getDimension().y(), this.getDimension().width() - this.getXPadding(), this.getDimension().height());
			return;
		}
		super.extractValueText(graphics, mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
		if (this.isAvailable() && this.isMouseOver(click.x(), click.y()) && this.getDimension().isPointInside((int) click.x(), (int) click.y())) {
			this.playDownSound();
			Minecraft.getInstance().setScreen(new SkinTotemModelSelectionScreen(this.screen, this.controller.option()));
			return true;
		}
		return false;
	}
}
