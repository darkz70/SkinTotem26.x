package com.darkz.skintotem.client.event;

import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
import com.darkz.skintotem.MyTotemDoll;
import com.darkz.skintotem.atlas.manager.*;
import com.darkz.skintotem.doll.data.*;
import com.darkz.skintotem.gui.tooltip.combined.*;
import com.darkz.skintotem.gui.tooltip.info.*;
import com.darkz.skintotem.gui.tooltip.preview.*;
import com.darkz.skintotem.gui.tooltip.state.LoadingStateTooltipData;
import com.darkz.skintotem.gui.tooltip.tags.*;
import com.darkz.skintotem.gui.tooltip.wrapped.*;
import com.darkz.skintotem.thread.MyTotemDollTaskExecutor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class MyTotemDollEvents {

	public static void register() {
		registerTooltipCallbacks();
		registerLifecycleEvents();
	}

	private static void registerTooltipCallbacks() {
		ClientTooltipComponentCallback.EVENT.register((data) -> {
			if (data instanceof TagsTooltipData tooltipData) {
				return new TagsTooltipComponent(tooltipData.tags());
			}
			if (data instanceof InfoTooltipData tooltipData) {
				return new InfoTooltipComponent(tooltipData.key(), tooltipData.color());
			}
			if (data instanceof LoadingStateTooltipData tooltipData) {
				return ClientTooltipComponent.create(MyTotemDoll.text("text.status").append(tooltipData.state().getText()).getVisualOrderText());
			}
			if (data instanceof CombinedTooltipData tooltipData) {
				return new CombinedTooltipComponent(tooltipData.list());
			}
			if (data instanceof TotemDollPreviewTooltipData tooltipData) {
				return new TotemDollPreviewTooltipComponent(tooltipData.data(), tooltipData.model());
			}
			if (data instanceof WrappedTextTooltipData tooltipData) {
				return new WrappedTextTooltipComponent(tooltipData.text());
			}
			return null;
		});
	}

	private static void registerLifecycleEvents() {
		ClientLifecycleEvents.CLIENT_STOPPING.register((client) -> {
			MyTotemDollTaskExecutor.stop();
			MyTotemDollAtlasManager.close();
			MyTotemDollAtlasSpriteManager.close();
		});
	}
}
