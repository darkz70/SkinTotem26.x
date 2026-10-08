package com.darkz.skintotem.client.event;

import java.util.List;
import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.atlas.manager.*;
import com.darkz.skintotem.doll.data.*;
import com.darkz.skintotem.gui.tooltip.combined.*;
import com.darkz.skintotem.gui.tooltip.info.*;
import com.darkz.skintotem.gui.tooltip.preview.*;
import com.darkz.skintotem.gui.tooltip.state.LoadingStateTooltipData;
import com.darkz.skintotem.gui.tooltip.tags.*;
import com.darkz.skintotem.gui.tooltip.wrapped.*;
import com.darkz.skintotem.loader.SkinTotemLoader;
import com.darkz.skintotem.thread.SkinTotemTaskExecutor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.Nullable;

public class SkinTotemEvents {

	public static void register() {
		SkinTotemLoader.registerTooltipComponents();
		SkinTotemLoader.registerClientStopping(SkinTotemEvents::onClientStopping);
	}

	/** Единая для Fabric и NeoForge фабрика компонентов подсказок. */
	@Nullable
	public static ClientTooltipComponent createTooltipComponent(TooltipComponent data) {
		{
			if (data instanceof TagsTooltipData tooltipData) {
				return new TagsTooltipComponent(tooltipData.tags());
			}
			if (data instanceof InfoTooltipData tooltipData) {
				return new InfoTooltipComponent(tooltipData.key(), tooltipData.color());
			}
			if (data instanceof LoadingStateTooltipData tooltipData) {
				return ClientTooltipComponent.create(SkinTotem.text("text.status").append(tooltipData.state().getText()).getVisualOrderText());
			}
			if (data instanceof CombinedTooltipData tooltipData) {
				return new CombinedTooltipComponent(tooltipData.list());
			}
			if (data instanceof SkinTotemPreviewTooltipData tooltipData) {
				return new SkinTotemPreviewTooltipComponent(tooltipData.data(), tooltipData.model());
			}
			if (data instanceof WrappedTextTooltipData tooltipData) {
				return new WrappedTextTooltipComponent(tooltipData.text());
			}
			return null;
		}
	}

	private static void onClientStopping() {
		SkinTotemTaskExecutor.stop();
		SkinTotemAtlasManager.close();
		SkinTotemAtlasSpriteManager.close();
	}
}
