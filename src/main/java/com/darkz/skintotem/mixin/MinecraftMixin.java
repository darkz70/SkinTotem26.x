package com.darkz.skintotem.mixin;

import java.util.List;
import java.util.function.Function;
//? if fabric {
import net.fabricmc.loader.api.FabricLoader;
//?} else {
/*import net.neoforged.fml.loading.FMLLoader;
*///?}
import com.darkz.skintotem.config.SkinTotemConfig;
import com.darkz.skintotem.gui.screen.WelcomeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	@Inject(at = @At("HEAD"), method = "addInitialScreens")
	private void addSTHelloScreen(List<Function<Runnable, Screen>> list, CallbackInfoReturnable<Boolean> ci) {
		SkinTotemConfig config = SkinTotemConfig.getInstance();
		if (config.isFirstRun() || config.isFirstRunTemp()) {
			list.add(WelcomeScreen::new);
			//? if fabric {
			if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
				config.setFirstRun(false);
				config.setFirstRunTemp(false);
			}
			//?} else {
			/*if (!FMLLoader.isProduction()) {
				// dev env - don't reset flags
			} else {
				config.setFirstRun(false);
				config.setFirstRunTemp(false);
			}
			*///?}
			config.save();
		}
	}

}
