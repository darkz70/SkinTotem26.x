package com.darkz.skintotem.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import com.darkz.skintotem.MyTotemDoll;
import com.darkz.skintotem.cache.KnownPlayerUUIDsConfigManager;
import com.darkz.skintotem.client.command.MyTotemDollCommandManager;
import com.darkz.skintotem.client.event.MyTotemDollEvents;
import com.darkz.skintotem.config.MyTotemDollConfig;
import com.darkz.skintotem.pack.MyTotemDollReloadListener;
import com.darkz.skintotem.tag.manager.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.*;

public class MyTotemDollClient implements ClientModInitializer {

	public static Logger LOGGER = LoggerFactory.getLogger(MyTotemDoll.MOD_NAME + "/Client");

	public static boolean canProcess(@Nullable ItemStack stack) {
		return stack != null && MyTotemDollConfig.getInstance().isModEnabled() && isProbablyTotem(stack);
	}

	@SuppressWarnings("deprecation")
		private static boolean isProbablyTotem(ItemStack stack) {
			boolean bl = stack.is(Items.TOTEM_OF_UNDYING);
			return bl || (MyTotemDollConfig.getInstance().isSupportOtherModsTotems() && BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains("totem"));
		}

	@Override
	public void onInitializeClient() {
		LOGGER.info("{} Client Initialized", MyTotemDoll.MOD_NAME);
		TagsManager.register();
		TagsSkinProviders.register();
		MyTotemDollCommandManager.register();
		MyTotemDollEvents.register();
		MyTotemDollReloadListener.register();
		KnownPlayerUUIDsConfigManager.start();
		PictureInPictureRendererRegistry.register(context -> new com.darkz.skintotem.doll.renderer.special.ItemGuiElementRenderer(context.bufferSource()));
	}
}
