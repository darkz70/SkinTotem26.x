package com.darkz.skintotem.yacl.custom.category.rendering;

import dev.isxander.yacl3.api.ConfigCategory;
import com.darkz.skintotem.utils.mixin.yacl.BetterYACLCategoryBuilder;

public interface RenderingConfigCategory extends ConfigCategory {

	static Builder createBuilder() {
		return ((BetterYACLCategoryBuilder) ConfigCategory.createBuilder()).myTotemDoll$enableRendering();
	}
}
