package com.darkz.skintotem.yacl.custom.simple.main;

import dev.isxander.yacl3.api.*;
import com.darkz.skintotem.utils.ModMenuUtils;
import com.darkz.skintotem.yacl.custom.renderer.SkinTotemPreviewRenderer;
import net.minecraft.network.chat.Component;

public class SimpleGroup {

	private final OptionGroup.Builder groupBuilder;
	private OptionDescription builtDescription = null;

	public SimpleGroup(String groupId) {
		String groupKey = ModMenuUtils.getGroupKey(groupId);
		Component groupName = ModMenuUtils.getName(groupKey);

		this.groupBuilder = OptionGroup.createBuilder().name(groupName);
	}

	public static SimpleGroup startBuilder(String groupId) {
		return new SimpleGroup(groupId);
	}

	public SimpleGroup options(Option<?>... options) {
		for (Option<?> option : options) {
			if (option == null) {
				continue;
			}
			this.groupBuilder.option(option);
		}
		return this;
	}

	public SimpleGroup withCustomDescription(SkinTotemPreviewRenderer renderer) {
		this.builtDescription = OptionDescription.createBuilder().customImage(renderer).build();
		return this;
	}

	public OptionGroup build() {
		if (this.builtDescription != null) {
			this.groupBuilder.description(this.builtDescription);
		}
		return this.groupBuilder.build();
	}
}
