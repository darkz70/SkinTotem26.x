package com.darkz.skintotem.modmenu;

// import com.terraformersmc.modmenu.api.*;
import net.fabricmc.loader.api.*;
import com.darkz.skintotem.MyTotemDoll;
import com.darkz.skintotem.client.MyTotemDollClient;
import com.darkz.skintotem.yacl.YACLConfigurationScreen;

@SuppressWarnings("rawtypes")
public class ModMenuIntegration /* implements ModMenuApi */ {

	// @Override
	public Object getModConfigScreenFactory() {
		FabricLoader fabricLoader = FabricLoader.getInstance();
		if (fabricLoader.isModLoaded("yet_another_config_lib_v3")) {
			ModContainer modContainer = fabricLoader.getModContainer("yet_another_config_lib_v3").orElseThrow();
			Version version = modContainer.getMetadata().getVersion();
			try {
				Version requestsVersion = Version.parse(MyTotemDoll.YACL_DEPEND_VERSION);
				if (version.compareTo(requestsVersion) >= 0) {
					return YACLConfigurationScreen::createScreen;
				}
			} catch (VersionParsingException e) {
				MyTotemDollClient.LOGGER.error("Failed to compare YACL version, tell mod author about this error: ", e);
			}
			return parent -> NoConfigLibraryScreen.createScreenAboutOldVersion(parent, version.getFriendlyString());
		}
		return NoConfigLibraryScreen::createScreen;
	}
}
