package com.darkz.skintotem.platform;

import java.nio.file.Path;
//? if fabric {
import net.fabricmc.loader.api.FabricLoader;
//?} else {
/*import net.neoforged.fml.loading.FMLPaths;
*///?}

public class PlatformHelper {

	public static Path getConfigDir() {
		//? if fabric {
		return FabricLoader.getInstance().getConfigDir();
		//?} else {
		/*return FMLPaths.CONFIGDIR.get();
		*///?}
	}

	public static boolean isDevelopmentEnvironment() {
		//? if fabric {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
		//?} else {
		/*return !net.neoforged.fml.loading.FMLLoader.isProduction();
		*///?}
	}

	public static boolean isModLoaded(String modId) {
		//? if fabric {
		return FabricLoader.getInstance().isModLoaded(modId);
		//?} else {
		/*return net.neoforged.fml.ModList.get().isLoaded(modId);
		*///?}
	}
}
