package com.darkz.skintotem.neoforge;

//? if neoforge {
/*import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.client.SkinTotemClient;
import com.darkz.skintotem.loader.SkinTotemLoader;
import com.darkz.skintotem.modmenu.NoConfigLibraryScreen;
import com.darkz.skintotem.utils.VersionUtils;
import com.darkz.skintotem.yacl.YACLConfigurationScreen;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = SkinTotem.MOD_ID, dist = Dist.CLIENT)
public class SkinTotemNeoForgeClient {

	public SkinTotemNeoForgeClient(ModContainer container) {
		SkinTotem.LOGGER.info("{} Initialized", SkinTotem.MOD_NAME);
		SkinTotemClient.init();
		container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> createConfigScreen(parent));
	}

	private static Screen createConfigScreen(Screen parent) {
		String yaclVersion = SkinTotemLoader.getModVersion("yet_another_config_lib_v3");
		if (yaclVersion == null) {
			return NoConfigLibraryScreen.createScreen(parent);
		}
		if (VersionUtils.isAtLeast(yaclVersion, SkinTotem.YACL_DEPEND_VERSION)) {
			return YACLConfigurationScreen.createScreen(parent);
		}
		return NoConfigLibraryScreen.createScreenAboutOldVersion(parent, yaclVersion);
	}
}
*///?}
