package com.darkz.skintotem.compat.sodium;

//? if fabric {
import net.fabricmc.loader.api.*;
//?} else {
/*import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import java.util.Optional;
*///?}
import com.darkz.skintotem.compat.CompatPlugin;
import org.spongepowered.asm.service.MixinService;

public class SodiumCompatPlugin extends CompatPlugin {

	@Override
	protected String getCompatModId() {
		return "sodium";
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (!super.shouldApplyMixin(targetClassName, mixinClassName)) {
			return false;
		}

		boolean oldMixin = mixinClassName.equals("com.darkz.skintotem.mixin.sodium.ModelPartMixinMixin");
		boolean hotMixin = mixinClassName.equals("com.darkz.skintotem.mixin.sodium.CubeMixinMixin");

		if (hotMixin) {
			return !this.isCurrentVersionOlderThanHot(mixinClassName);
		}
		if (oldMixin) {
			return this.isCurrentVersionOlderThanHot(mixinClassName);
		}
		return true;
	}

	private boolean isCurrentVersionOlderThanHot(String mixinName) {
		//? if fabric {
		FabricLoader fabricLoader = FabricLoader.getInstance();
		ModContainer modContainer = fabricLoader.getModContainer(this.getCompatModId()).orElseThrow();
		Version currentVersion = modContainer.getMetadata().getVersion();
		try {
			Version hotVersion = Version.parse("0.6.0+mc1.21.1");
			boolean bl = currentVersion.compareTo(hotVersion) < 0;
			MixinService.getService().getLogger("[SkinTotem: SodiumCompatPlugin]").info("[{}] Detected Sodium, current version older than hot: {}", mixinName, bl);
			return bl;
		} catch (VersionParsingException e) {
			throw new RuntimeException(e);
		}
		//?} else {
		/*// NeoForge: use ModList to get Sodium version string
		String versionStr = ModList.get().getModContainerById(this.getCompatModId())
				.map(c -> c.getModInfo().getVersion().toString())
				.orElse("0.0.0");
		// Simple string comparison: "0.6.0" prefix check
		boolean bl = versionStr.compareTo("0.6.0") < 0;
		MixinService.getService().getLogger("[SkinTotem: SodiumCompatPlugin]").info("[{}] Detected Sodium, current version older than hot: {}", mixinName, bl);
		return bl;
		*///?}
	}
}
