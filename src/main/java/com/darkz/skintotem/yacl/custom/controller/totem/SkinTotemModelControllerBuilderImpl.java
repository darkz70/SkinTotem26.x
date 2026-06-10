package com.darkz.skintotem.yacl.custom.controller.totem;

import dev.isxander.yacl3.api.*;
import net.minecraft.resources.Identifier;

public class SkinTotemModelControllerBuilderImpl implements SkinTotemModelControllerBuilder {

	private final Option<Identifier> option;

	public SkinTotemModelControllerBuilderImpl(Option<Identifier> option) {
		this.option = option;
	}

	@Override
	public Controller<Identifier> build() {
		return new SkinTotemModelController(this.option);
	}
}
