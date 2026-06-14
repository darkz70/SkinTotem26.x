package com.darkz.skintotem.client.event;

import java.util.List;
//? if fabric {
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
//?} else {
/*import net.neoforged.neoforge.client.event.ClientTooltipColorEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
*///?}
import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.atlas.manager.*;
import com.darkz.skintotem.doll.data.*;
import com.darkz.skintotem.gui.tooltip.combined.*;
import com.darkz.skintotem.gui.tooltip.info.*;
import com.darkz.skintotem.gui.tooltip.preview.*;
import com.darkz.skintotem.gui.tooltip.state.LoadingStateTooltipData;
import com.darkz.skintotem.gui.tooltip.tags.*;
import com.darkz.skintotem.gui.tooltip.wrapped.*;
import com.darkz.skintotem.thread.SkinTotemTaskExecutor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class SkinTotemEvents {

	public static void register() {
		registerTooltipCallbacks();
		registerLifecycleEvents();
	}

	private static void registerTooltipCallbacks() {
		//? if fabric {
		ClientTooltipComponentCallback.EVENT.register((data) -> {
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
		});
		//?}
		// NeoForge: tooltip components are registered via RegisterClientTooltipComponentFactoriesEvent
		// in a separate @EventBusSubscriber class — see SkinTotemNeoForgeEvents
	}

	private static void registerLifecycleEvents() {
		//? if fabric {
		ClientLifecycleEvents.CLIENT_STOPPING.register((client) -> {
			SkinTotemTaskExecutor.stop();
			SkinTotemAtlasManager.close();
			SkinTotemAtlasSpriteManager.close();
		});
		//?} else {
		/*NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.GameShuttingDownEvent e) -> {
			SkinTotemTaskExecutor.stop();
			SkinTotemAtlasManager.close();
			SkinTotemAtlasSpriteManager.close();
		});
		*///?}
	}
}
