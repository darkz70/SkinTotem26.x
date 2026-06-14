package com.darkz.skintotem.compat;

//? if fabric {
import net.fabricmc.loader.api.FabricLoader;
//?} else {
/*import net.neoforged.fml.ModList;
*///?}

public class LoadedMods {

	//? if fabric {
	public static final boolean EARS_LOADED = FabricLoader.getInstance().isModLoaded("ears");
	//?} else {
	/*public static final boolean EARS_LOADED = ModList.get().isLoaded("ears");
	*///?}

}
