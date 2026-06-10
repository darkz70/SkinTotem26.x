package com.darkz.skintotem.yacl.custom.category.better;

import dev.isxander.yacl3.api.ConfigCategory;
import com.darkz.skintotem.utils.mixin.yacl.BetterYACLCategoryBuilder;

public interface BetterConfigCategory extends ConfigCategory {

	static Builder createBuilder() {
		return ((BetterYACLCategoryBuilder) ConfigCategory.createBuilder()).mySkinTotem$enableBetter();
	}
}
