package com.darkz.skintotem.config.totem;

import com.mojang.serialization.Codec;
import lombok.Getter;
import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.config.other.EnumWithText;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

@Getter
public enum SkinTotemArmsType implements StringRepresentable, EnumWithText {

	WIDE,
	SLIM;

	public static final Codec<SkinTotemArmsType> CODEC = StringRepresentable.fromEnum(SkinTotemArmsType::values);

	public static SkinTotemArmsType of(boolean slim) {
		return slim ? SLIM : WIDE;
	}

	public static SkinTotemArmsType of(@Nullable String s) {
		if (s == null) {
			return WIDE;
		}
		return s.equals("slim") ? SLIM : WIDE;
	}

	public Component getText() {
		return SkinTotem.text("modmenu.option.standard_doll_model_arms_type.%s".formatted(this.getSerializedName()));
	}

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase();
	}

	public boolean isSlim() {
		return this == SLIM;
	}
}
