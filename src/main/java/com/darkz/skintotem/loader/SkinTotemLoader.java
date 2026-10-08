package com.darkz.skintotem.loader;

//? if fabric {
import java.nio.file.Path;
import com.darkz.skintotem.client.command.SkinTotemCommandManager;
import com.darkz.skintotem.client.event.SkinTotemEvents;
import com.darkz.skintotem.doll.renderer.special.*;
import com.darkz.skintotem.pack.SkinTotemReloadListener;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.Nullable;

/**
 * Прослойка между модом и загрузчиком: Fabric и NeoForge регистрируют одно и то же по-разному.
 * Весь остальной код мода работает только с этим классом и не знает, на чём он запущен.
 */
public final class SkinTotemLoader {

	private SkinTotemLoader() {
	}

	public static boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Nullable
	public static String getModVersion(String modId) {
		return FabricLoader.getInstance()
				.getModContainer(modId)
				.map((container) -> container.getMetadata().getVersion().getFriendlyString())
				.orElse(null);
	}

	public static Path getConfigDir() {
		return FabricLoader.getInstance().getConfigDir();
	}

	public static boolean isDevelopmentEnvironment() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	public static void registerClientCommands() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> SkinTotemCommandManager.registerTo(dispatcher));
	}

	public static void registerReloadListener() {
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(SkinTotemReloadListener.getListenerId(), new SkinTotemReloadListener());
	}

	public static void registerTooltipComponents() {
		ClientTooltipComponentCallback.EVENT.register(SkinTotemEvents::createTooltipComponent);
	}

	public static void registerPictureInPictureRenderers() {
		PictureInPictureRendererRegistry.register((context) -> new ItemGuiElementRenderer());
		PictureInPictureRendererRegistry.register((context) -> new SkinTotemGuiElementRenderer());
	}

	public static void registerClientStopping(Runnable runnable) {
		ClientLifecycleEvents.CLIENT_STOPPING.register((client) -> runnable.run());
	}
}
//?} elif neoforge {
/*import java.nio.file.Path;
import com.darkz.skintotem.client.command.SkinTotemCommandManager;
import com.darkz.skintotem.client.event.SkinTotemEvents;
import com.darkz.skintotem.doll.renderer.special.*;
import com.darkz.skintotem.gui.tooltip.combined.CombinedTooltipData;
import com.darkz.skintotem.gui.tooltip.info.InfoTooltipData;
import com.darkz.skintotem.gui.tooltip.preview.SkinTotemPreviewTooltipData;
import com.darkz.skintotem.gui.tooltip.state.LoadingStateTooltipData;
import com.darkz.skintotem.gui.tooltip.tags.TagsTooltipData;
import com.darkz.skintotem.gui.tooltip.wrapped.WrappedTextTooltipData;
import com.darkz.skintotem.pack.SkinTotemReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.*;
import net.neoforged.fml.loading.*;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.event.lifecycle.ClientStoppingEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

public final class SkinTotemLoader {

	private SkinTotemLoader() {
	}

	public static boolean isModLoaded(String modId) {
		ModList list = ModList.get();
		if (list != null) {
			return list.isLoaded(modId);
		}
		return FMLLoader.getCurrent().getLoadingModList().getModFileById(modId) != null;
	}

	@Nullable
	public static String getModVersion(String modId) {
		ModList list = ModList.get();
		if (list == null) {
			return null;
		}
		return list.getModContainerById(modId)
				.map((container) -> container.getModInfo().getVersion().toString())
				.orElse(null);
	}

	public static Path getConfigDir() {
		return FMLPaths.CONFIGDIR.get();
	}

	public static boolean isDevelopmentEnvironment() {
		return !FMLLoader.getCurrent().isProduction();
	}

	public static void registerClientCommands() {
		NeoForge.EVENT_BUS.addListener(RegisterClientCommandsEvent.class, (event) -> SkinTotemCommandManager.registerTo(event.getDispatcher()));
	}

	public static void registerReloadListener() {
		getModBus().addListener(AddClientReloadListenersEvent.class, (event) -> {
			event.addListener(SkinTotemReloadListener.getListenerId(), new SkinTotemReloadListener());
		});
	}

	public static void registerTooltipComponents() {
		getModBus().addListener(RegisterClientTooltipComponentFactoriesEvent.class, (event) -> {
			event.register(TagsTooltipData.class, SkinTotemEvents::createTooltipComponent);
			event.register(InfoTooltipData.class, SkinTotemEvents::createTooltipComponent);
			event.register(LoadingStateTooltipData.class, SkinTotemEvents::createTooltipComponent);
			event.register(CombinedTooltipData.class, SkinTotemEvents::createTooltipComponent);
			event.register(SkinTotemPreviewTooltipData.class, SkinTotemEvents::createTooltipComponent);
			event.register(WrappedTextTooltipData.class, SkinTotemEvents::createTooltipComponent);
		});
	}

	public static void registerPictureInPictureRenderers() {
		getModBus().addListener(RegisterPictureInPictureRenderersEvent.class, (event) -> {
			event.register(ItemGuiRenderState.class, ItemGuiElementRenderer::new);
			event.register(SkinTotemRenderState.class, SkinTotemGuiElementRenderer::new);
		});
	}

	public static void registerClientStopping(Runnable runnable) {
		NeoForge.EVENT_BUS.addListener(ClientStoppingEvent.class, (event) -> runnable.run());
	}

	private static IEventBus getModBus() {
		IEventBus bus = ModLoadingContext.get().getActiveContainer().getEventBus();
		if (bus == null) {
			throw new IllegalStateException("Failed to get SkinTotem mod event bus!");
		}
		return bus;
	}
}
*///?}
