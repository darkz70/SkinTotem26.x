package com.darkz.skintotem.config.totem;

import com.mojang.serialization.Codec;
import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.config.other.EnumWithText;
import net.minecraft.network.chat.*;
import net.minecraft.util.StringRepresentable;

public enum SkinTotemSkinType implements StringRepresentable, EnumWithText {

	STEVE(false),
	PLAYER(true),
	HOLDING_PLAYER(false),
	URL_SKIN(true),
	FILE_SKIN(true),
	TLAUNCHER(true),
	ELY_BY(true);

	public static final Codec<SkinTotemSkinType> CODEC = StringRepresentable.fromEnum(SkinTotemSkinType::values);

	private final boolean needData;

	SkinTotemSkinType(boolean needData) {
		this.needData = needData;
	}

	public boolean isNeedData() {
		return needData;
	}

	public MutableComponent getText() {
		return SkinTotem.text("modmenu.option.standard_doll_skin_type.%s".formatted(this.getSerializedName()));
	}

	public MutableComponent getSuggestionText() {
		return SkinTotem.text("modmenu.option.standard_doll_skin_type.%s.suggestion".formatted(this.getSerializedName()));
	}

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase();
	}
}
