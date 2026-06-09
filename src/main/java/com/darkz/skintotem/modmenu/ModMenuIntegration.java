package com.darkz.skintotem.modmenu;

/*? if dep.modmenu != "unknown" {*/
import com.terraformersmc.modmenu.api.*;
import net.fabricmc.loader.api.*;
import com.darkz.skintotem.MyTotemDoll;
import com.darkz.skintotem.client.MyTotemDollClient;
import com.darkz.skintotem.yacl.YACLConfigurationScreen;

public class ModMenuIntegration implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		FabricLoader fabricLoader = FabricLoader.getInstance();
		if (fabricLoader.isModLoaded("yet_another_config_lib_v3")) {
			ModContainer modContainer = fabricLoader.getModContainer("yet_another_config_lib_v3").orElseThrow();
			Version version = modContainer.getMetadata().getVersion();
			try {
				Version requestsVersion = Version.parse(MyTotemDoll.YACL_DEPEND_VERSION);
				if (version.compareTo(requestsVersion) >= 0) {
					return (com.terraformersmc.modmenu.api.ConfigScreenFactory<?>) YACLConfigurationScreen::createScreen;
				}
			} catch (VersionParsingException e) {
				MyTotemDollClient.LOGGER.error("Failed to compare YACL version, tell mod author about this error: ", e);
			}
			return (com.terraformersmc.modmenu.api.ConfigScreenFactory<?>) parent -> NoConfigLibraryScreen.createScreenAboutOldVersion(parent, version.getFriendlyString());
		}
		return (com.terraformersmc.modmenu.api.ConfigScreenFactory<?>) NoConfigLibraryScreen::createScreen;
	}
}
/*?} else {*/
/*
public class ModMenuIntegration {}
*/
/*?}*/
