package com.darkz.skintotem.yacl.custom.screen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.*;
import java.util.*;
import java.util.Map.Entry;
import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.config.SkinTotemConfig;
import com.darkz.skintotem.doll.model.SkinTotemModel;
import com.darkz.skintotem.gui.BackgroundRenderer;
import com.darkz.skintotem.gui.widget.SkinTotemModelPreviewWidget;
import com.darkz.skintotem.gui.widget.button.*;
import com.darkz.skintotem.pack.SkinTotemModelFinder;
import com.darkz.skintotem.utils.DrawUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.*;

public class SkinTotemModelSelectionScreen extends Screen {

	private final Option<Identifier> option;
	private final Screen parent;
	private final List<Dimension<Integer>> dimensions = new ArrayList<>();
	@SuppressWarnings("all")
	private MutableDimension<Integer> modelPanelDimension, listPanelDimension, modelPathDimension, titleDimension, listTitleDimension;
	private SkinTotemModelPreviewWidget skinTotemModelPreviewWidget;
	private ButtonListWidget listWidget;

	@Nullable
	private Identifier selectedModelId;
	@Nullable
	private Component selectedModelName;
	@Nullable
	private Component selectedModel;

	public SkinTotemModelSelectionScreen(Screen parent, Option<Identifier> option) {
		super(SkinTotem.text("standard_model_selection_screen.title"));
		this.option = option;
		this.parent = parent;
	}

	private static @NotNull String getModelName(String path) {
		int i = path.lastIndexOf('/');
		if (i != -1) {
			return path.substring(i + 1);
		}
		return path;
	}

	@Override
	protected void init() {
		int o = 10;
		int h = 20;

		this.modelPanelDimension = this.getModelPanelDimension(o);
		this.listTitleDimension  = this.getListTitleDimension(o, h);
		this.listPanelDimension  = this.getListPanelDimension(this.listTitleDimension, o, h);
		this.modelPathDimension  = this.getModelPathDimension(this.modelPanelDimension, this.listPanelDimension, o, h);
		this.titleDimension      = this.getTitleDimension(o, h, this.modelPanelDimension);
		MutableDimension<Integer> textFieldDimension = this.getTextFieldDimension(h, o);
		MutableDimension<Integer> buttonPanelDimension = this.getButtonPanelDimension(o, h, this.modelPathDimension, textFieldDimension);

		this.listWidget = this.addRenderableWidget(new ButtonListWidget(this.listPanelDimension.x(), this.listPanelDimension.y() + 2, this.listPanelDimension.width(), this.listPanelDimension.height(), 20));

		EditBox textFieldWidget = this.addRenderableWidget(new EditBox(Minecraft.getInstance().font, textFieldDimension.x(), textFieldDimension.y(), textFieldDimension.width(), textFieldDimension.height(), Component.nullToEmpty("")));
		textFieldWidget.setResponder(this.listWidget::search);
		textFieldWidget.setHint(SkinTotem.text("placeholder.search"));

		this.addRenderableWidget(
				Button.builder(SkinTotem.text("button.close"), (b) -> this.close(false))
						.bounds(buttonPanelDimension.x(), buttonPanelDimension.y(), buttonPanelDimension.width(), buttonPanelDimension.height())
						.build()
		);
		buttonPanelDimension.move(0, h + o);
		this.addRenderableWidget(
				Button.builder(SkinTotem.text("button.select_current"), (b) -> this.close(true))
						.bounds(buttonPanelDimension.x(), buttonPanelDimension.y(), buttonPanelDimension.width(), buttonPanelDimension.height())
						.build()
		);

		Dimension<Integer> modelPreviewDimension = this.getModelPreviewDimension(this.modelPanelDimension);

		this.skinTotemModelPreviewWidget = new SkinTotemModelPreviewWidget(
				modelPreviewDimension.x(), modelPreviewDimension.y(),
				Math.min(modelPreviewDimension.width(), modelPreviewDimension.height())
		);

		Identifier standardModelId = SkinTotemConfig.getInstance().getStandardSkinTotemModelValue();

		Set<Entry<String, Set<Identifier>>> entries = new HashSet<>(SkinTotemModelFinder.getFoundedTotemModels().entrySet());
		entries.add(Map.entry(SkinTotem.MOD_ID, SkinTotemModelFinder.getBuiltinTotemModels()));

		for (Entry<String, Set<Identifier>> entry : entries) {
			for (Identifier id : entry.getValue()) {
				String pack = entry.getKey();
				String modelName = getModelName(id.getPath());

				OnPress pressAction = (widget) -> this.setSelectedModel(id, pack, modelName);

				ButtonListEntryWidget button = new ButtonListEntryWidget(Component.nullToEmpty(modelName), pressAction);

				if (id.equals(standardModelId)) {
					pressAction.onPress(button.getWidget());
				}

				listWidget.addEntry(button);
			}
		}

		this.dimensions.clear();
		this.dimensions.add(this.modelPanelDimension);
		this.dimensions.add(this.listPanelDimension);
		this.dimensions.add(this.modelPathDimension);
		this.dimensions.add(this.titleDimension);
		this.dimensions.add(this.listTitleDimension);
	}

	private MutableDimension<Integer> getTextFieldDimension(int h, int o) {
		return this.listPanelDimension.clone().setHeight(h).setY(this.listPanelDimension.yLimit() + (o / 2));
	}

	private void close(boolean applyCurrent) {
		if (applyCurrent && this.selectedModelId != null) {
			if (this.skinTotemModelPreviewWidget.getFailedLoadingStatusCode() != 0) {
				this.option.requestSet(SkinTotemModel.THREE_D_MODEL_id);
			} else {
				this.option.requestSet(this.selectedModelId);
			}
		}

		this.onClose();
	}

	private MutableDimension<Integer> getButtonPanelDimension(int o, int h, MutableDimension<Integer> modelPathDimension, MutableDimension<Integer> textFieldDimension) {
		return modelPathDimension.withX(modelPathDimension.xLimit() + o).withY(textFieldDimension.yLimit() + o).withWidth(textFieldDimension.width()).withHeight(h).clone();
	}



	@Override
	public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
		super.extractBackground(context, mouseX, mouseY, delta);

		for (Dimension<Integer> dimension : this.dimensions) {
			BackgroundRenderer.drawTransparencyBackground(context, dimension.x(), dimension.y(), dimension.width(), dimension.height(), true);
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
		super.extractRenderState(context, mouseX, mouseY, delta);
		Font textRenderer = Minecraft.getInstance().font;

		// Title
		DrawUtils.drawCenteredText(context, this.getTitle(), this.titleDimension.x() + 2, this.titleDimension.y(), this.titleDimension.width() - 2, this.titleDimension.height());

		// List Title
		DrawUtils.drawCenteredText(context, SkinTotem.text("text.found_models", this.listWidget.getItemCount()), this.listTitleDimension.x() + 2, this.listTitleDimension.y(), this.listTitleDimension.width() - 2, this.listTitleDimension.height());

		// "Full Model Path" text
		MutableComponent fullModelPathText = SkinTotem.text("text.full_model_path");
		int a = textRenderer.width(fullModelPathText);
		int offset = 10;

		if (this.modelPathDimension.x() + a + offset > this.modelPathDimension.xLimit() - offset) {
			DrawUtils.drawText(context, fullModelPathText, this.modelPathDimension.x() + offset, this.modelPathDimension.y() + offset, this.modelPathDimension.width() - offset, textRenderer.lineHeight + offset);
		} else {
			context.text(textRenderer, fullModelPathText, this.modelPathDimension.x() + offset, this.modelPathDimension.y() + offset, -1, true);
		}

		// Model Path Text
		context.enableScissor(this.modelPathDimension.x(), this.modelPathDimension.y(), this.modelPathDimension.xLimit() - offset, this.modelPathDimension.yLimit());

		Component text = this.selectedModel == null ? Component.literal("...").withStyle(ChatFormatting.GRAY) : this.selectedModel;
		int width = textRenderer.width(text);
		if (this.modelPathDimension.x() + width + offset > this.modelPathDimension.xLimit() - offset) {
			DrawUtils.drawText(context, text, this.modelPathDimension.x() + offset, this.modelPathDimension.yLimit() - textRenderer.lineHeight - offset, this.modelPathDimension.width() - offset, textRenderer.lineHeight);
		} else {
			context.text(textRenderer, text, this.modelPathDimension.x() + offset, this.modelPathDimension.yLimit() - textRenderer.lineHeight - offset, -1, true);
		}

		context.disableScissor();

		// Model Name Text
		context.enableScissor(this.modelPanelDimension.x(), this.modelPanelDimension.y(), this.modelPanelDimension.xLimit(), this.modelPanelDimension.yLimit());

		Component selectedModelNameText = this.selectedModelName == null ? SkinTotem.text("text.standard_doll") : this.selectedModelName;
		context.text(textRenderer, selectedModelNameText, this.modelPanelDimension.x() + offset, this.modelPanelDimension.y() + offset, -1, true);

		// Underline for this text
		context.fill(this.modelPanelDimension.x() + offset, this.modelPanelDimension.y() + offset + textRenderer.lineHeight + 3, this.modelPanelDimension.x() + offset + Math.min((textRenderer.width(selectedModelNameText) + 5), this.modelPanelDimension.width() - (offset * 2)), this.modelPanelDimension.y() + offset + textRenderer.lineHeight + 4, -1);
		context.disableScissor();

		// Model Preview
		this.skinTotemModelPreviewWidget.extractRenderState(context, mouseX, mouseY, delta);
	}

	private void setSelectedModel(Identifier modelId, String pack, String modelName) {
		String packName = SkinTotem.MOD_ID.equals(pack) ? SkinTotem.MOD_NAME.replace(" ", "") : pack;
		this.selectedModel     = SkinTotem.text("text.nice_id", packName, modelId.getPath());
		this.selectedModelId   = modelId;
		this.selectedModelName = Component.nullToEmpty(modelName);
		this.skinTotemModelPreviewWidget.updateModel(modelId);
	}

	private MutableDimension<Integer> getListTitleDimension(int o, int h) {
		int w = this.width / 5;
		return Dimension.ofInt(this.width - o - w, (o * 2) + h, w, h);
	}

	private MutableDimension<Integer> getTitleDimension(int o, int h, MutableDimension<Integer> modelPanelDimension) {
		return Dimension.ofInt(modelPanelDimension.xLimit() + o, o, this.width - modelPanelDimension.xLimit() - (o * 2), h);
	}

	private MutableDimension<Integer> getModelPreviewDimension(MutableDimension<Integer> modelPanelDimension) {
		int v = Math.min(modelPanelDimension.width(), modelPanelDimension.height());

		return Dimension.ofInt(modelPanelDimension.x() + ((modelPanelDimension.width() - v) / 2), modelPanelDimension.y() + ((modelPanelDimension.height() - v) / 2), v, v);
	}

	private MutableDimension<Integer> getModelPathDimension(MutableDimension<Integer> modelPanelDimension, MutableDimension<Integer> listPanelDimension, int o, int h) {
		int y = (h * 2) + (o * 2);
		modelPanelDimension.expand(-y, -y);
		return Dimension.ofInt(modelPanelDimension.x(), modelPanelDimension.yLimit() + o, (this.width - listPanelDimension.width() - (o * 3)), (h * 2) + o);
	}

	private MutableDimension<Integer> getListPanelDimension(MutableDimension<Integer> listTitleDimension, int o, int h) {
		int w = this.height - (o * 6 + h * 5);
		return listTitleDimension.clone().setHeight(w).setY(listTitleDimension.yLimit() + (o / 2));
	}

	private MutableDimension<Integer> getModelPanelDimension(int o) {
		int a = this.height - (o * 2);

		return Dimension.ofInt(o, o, a, a);
	}

	@Override
	public void onClose() {
		Minecraft.getInstance().gui.setScreen(this.parent);
	}
}
