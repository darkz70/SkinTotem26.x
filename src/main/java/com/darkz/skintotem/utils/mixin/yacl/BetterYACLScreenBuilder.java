package com.darkz.skintotem.utils.mixin.yacl;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.YetAnotherConfigLib.Builder;

public interface BetterYACLScreenBuilder {

	static Builder startBuilder() {
		return ((BetterYACLScreenBuilder) YetAnotherConfigLib.createBuilder()).myTotemDoll$enable();
	}

	Builder myTotemDoll$enable();
}
