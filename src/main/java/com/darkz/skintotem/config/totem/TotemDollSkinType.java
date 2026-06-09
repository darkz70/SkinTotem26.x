package com.darkz.skintotem.config.totem;

import com.mojang.serialization.Codec;
import lombok.Getter;
import com.darkz.skintotem.MyTotemDoll;
import com.darkz.skintotem.config.other.EnumWithText;
import net.minecraft.network.chat.*;
import net.minecraft.util.StringRepresentable;

@Getter
public enum TotemDollSkinType implements StringRepresentable, EnumWithText {

	STEVE(false),
	PLAYER(true),
	HOLDING_PLAYER(false),
	URL_SKIN(true),
	FILE_SKIN(true);

	public static final Codec<TotemDollSkinType> CODEC = StringRepresentable.fromEnum(TotemDollSkinType::values);

	private final boolean needData;

	TotemDollSkinType(boolean needData) {
		this.needData = needData;
	}

	public MutableComponent getText() {
		return MyTotemDoll.text("modmenu.option.standard_doll_skin_type.%s".formatted(this.getSerializedName()));
	}

	public MutableComponent getSuggestionText() {
		return MyTotemDoll.text("modmenu.option.standard_doll_skin_type.%s.suggestion".formatted(this.getSerializedName()));
	}

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase();
	}
}
