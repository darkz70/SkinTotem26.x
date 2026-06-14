package com.darkz.skintotem.neoforge;

//? if neoforge {
/*import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.atlas.manager.SkinTotemAtlasManager;
import com.darkz.skintotem.atlas.manager.SkinTotemAtlasSpriteManager;
import com.darkz.skintotem.doll.data.*;
import com.darkz.skintotem.gui.tooltip.combined.*;
import com.darkz.skintotem.gui.tooltip.info.*;
import com.darkz.skintotem.gui.tooltip.preview.*;
import com.darkz.skintotem.gui.tooltip.state.LoadingStateTooltipData;
import com.darkz.skintotem.gui.tooltip.tags.*;
import com.darkz.skintotem.gui.tooltip.wrapped.*;
import com.darkz.skintotem.pack.SkinTotemReloadListener;
import com.darkz.skintotem.thread.SkinTotemTaskExecutor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import net.neoforged.neoforge.common.NeoForge;

@EventBusSubscriber(modid = SkinTotem.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SkinTotemNeoForgeEvents {

    @SubscribeEvent
    public static void onRegisterReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(SkinTotem.id("reload-listener"), new SkinTotemReloadListener());
    }

    @SubscribeEvent
    public static void onRegisterTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(TagsTooltipData.class, data -> new TagsTooltipComponent(data.tags()));
        event.register(InfoTooltipData.class, data -> new InfoTooltipComponent(data.key(), data.color()));
        event.register(LoadingStateTooltipData.class, data ->
            ClientTooltipComponent.create(SkinTotem.text("text.status").append(data.state().getText()).getVisualOrderText()));
        event.register(CombinedTooltipData.class, data -> new CombinedTooltipComponent(data.list()));
        event.register(SkinTotemPreviewTooltipData.class, data -> new SkinTotemPreviewTooltipComponent(data.data(), data.model()));
        event.register(WrappedTextTooltipData.class, data -> new WrappedTextTooltipComponent(data.text()));
    }
}
*///?}

//? if fabric {
// NeoForge-only file — empty on Fabric build
public class SkinTotemNeoForgeEvents {}
//?}
