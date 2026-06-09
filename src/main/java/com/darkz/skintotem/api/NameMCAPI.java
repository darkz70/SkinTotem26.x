package com.darkz.skintotem.api;

import com.darkz.skintotem.skin.data.ParsedSkinData;

public class NameMCAPI {

	public static Response<ParsedSkinData> getSkinData(String skin) {
		String skinUrl = NameMCAPI.getSkinUrl(skin);
		return new Response<>(200, new ParsedSkinData(skinUrl, null, null, false));
	}

	private static String getSkinUrl(String id) {
		return String.format("https://s.namemc.com/i/%s.png", id);
	}

}
