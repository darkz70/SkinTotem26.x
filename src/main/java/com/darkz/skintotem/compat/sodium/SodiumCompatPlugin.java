package com.darkz.skintotem.compat.sodium;

import com.darkz.skintotem.compat.CompatPlugin;
import com.darkz.skintotem.loader.SkinTotemLoader;
import com.darkz.skintotem.utils.VersionUtils;
import org.spongepowered.asm.service.MixinService;

public class SodiumCompatPlugin extends CompatPlugin {

	/** С этой версии Sodium рендер кубиков переехал в другой миксин. */
	private static final String HOT_SODIUM_VERSION = "0.6.0+mc1.21.1";

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
		// <0.6.0 (currentOlder == true)  -> ModelPartMixinMixin
		// >=0.6.0 (currentOlder == false) -> CubeMixinMixin
		String currentVersion = SkinTotemLoader.getModVersion(this.getCompatModId());
		boolean bl = VersionUtils.isOlderThan(currentVersion, HOT_SODIUM_VERSION);
		MixinService.getService().getLogger("[SkinTotem: SodiumCompatPlugin]").info("[{}] Detected Sodium, current version older than hot: {}", mixinName, bl);
		return bl;
	}
}
