package com.darkz.skintotem.skin.provider.extended;

import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.api.*;
import com.darkz.skintotem.doll.data.SkinTotemData;
import com.darkz.skintotem.skin.data.ParsedSkinData;
import com.darkz.skintotem.skin.provider.StandardSkinProvider;
import net.minecraft.resources.Identifier;

public class NameMCSkinProvider extends StandardSkinProvider {

	private static final NameMCSkinProvider INSTANCE = new NameMCSkinProvider();

	private NameMCSkinProvider() {
		super(false);
	}

	public static NameMCSkinProvider getInstance() {
		return NameMCSkinProvider.INSTANCE;
	}

	@Override
	protected Response<ParsedSkinData> loadDollFromAPI(String value) {
		return NameMCAPI.getSkinData(value);
	}

	@Override
	public SkinTotemData createNewDoll(String value) {
		return SkinTotemData.create("NameMC");
	}

	@Override
	protected Identifier getId(String value, String type) {
		return SkinTotem.getDollTextureId("name_mc/%s/%s".formatted(type, value.toLowerCase()));
	}

	@Override
	public boolean canProcess(String value) {
		if (value == null || value.length() != 16) {
			return false;
		}

		for (int i = 0; i < 16; i++) {
			char c = value.charAt(i);
			if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
				continue;
			}
			return false;
		}

		return true;
	}
}
