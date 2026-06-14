package com.darkz.skintotem.client;

//? if fabric {
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
//?} else {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
*///?}
import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.cache.KnownPlayerUUIDsConfigManager;
import com.darkz.skintotem.client.command.SkinTotemCommandManager;
import com.darkz.skintotem.client.event.SkinTotemEvents;
import com.darkz.skintotem.config.SkinTotemConfig;
import com.darkz.skintotem.doll.renderer.special.ItemGuiElementRenderer;
import com.darkz.skintotem.doll.renderer.special.SkinTotemGuiElementRenderer;
import com.darkz.skintotem.pack.SkinTotemReloadListener;
import com.darkz.skintotem.tag.manager.*;
import com.darkz.skintotem.refresh.SkinAutoRefresher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.*;

//? if neoforge {
/*@Mod(value = SkinTotem.MOD_ID, dist = Dist.CLIENT)
*///?}
public class SkinTotemClient
//? if fabric {
        implements ClientModInitializer
//?}
{

	public static Logger LOGGER = LoggerFactory.getLogger(SkinTotem.MOD_NAME + "/Client");

	public static boolean canProcess(@Nullable ItemStack stack) {
		return stack != null && SkinTotemConfig.getInstance().isModEnabled() && isProbablyTotem(stack);
	}

	@SuppressWarnings("deprecation")
	private static boolean isProbablyTotem(ItemStack stack) {
		boolean bl = stack.is(Items.TOTEM_OF_UNDYING);
		return bl || (SkinTotemConfig.getInstance().isSupportOtherModsTotems() && BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains("totem"));
	}

	//? if neoforge {
	/*public SkinTotemClient(IEventBus modEventBus) {
		init();
	}
	*///?}

	//? if fabric {
	@Override
	public void onInitializeClient() {
		init();
	}
	//?}

	private static void init() {
		LOGGER.info("{} Client Initialized", SkinTotem.MOD_NAME);
		TagsManager.register();
		TagsSkinProviders.register();
		SkinTotemCommandManager.register();
		SkinTotemEvents.register();
		SkinTotemReloadListener.register();
		KnownPlayerUUIDsConfigManager.start();
		SkinAutoRefresher.start();
		//? if fabric {
		PictureInPictureRendererRegistry.register(context -> new ItemGuiElementRenderer(context.bufferSource()));
		PictureInPictureRendererRegistry.register(context -> new SkinTotemGuiElementRenderer(context.bufferSource()));
		//?}
	}
}
