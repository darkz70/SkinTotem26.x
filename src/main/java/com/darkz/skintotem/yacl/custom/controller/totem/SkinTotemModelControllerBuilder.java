package com.darkz.skintotem.yacl.custom.controller.totem;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import net.minecraft.resources.Identifier;

public interface SkinTotemModelControllerBuilder extends ControllerBuilder<Identifier> {

	static SkinTotemModelControllerBuilder create(Option<Identifier> option) {
		return new SkinTotemModelControllerBuilderImpl(option);
	}
}
