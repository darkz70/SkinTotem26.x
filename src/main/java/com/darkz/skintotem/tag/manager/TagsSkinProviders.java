package com.darkz.skintotem.tag.manager;

import java.util.*;
import com.darkz.skintotem.doll.data.SkinTotemData;
import com.darkz.skintotem.doll.manager.StandardSkinTotemManager;
import com.darkz.skintotem.skin.provider.SkinProvider;
import com.darkz.skintotem.skin.provider.extended.NameMCSkinProvider;
import com.darkz.skintotem.skin.provider.extended.TLauncherSkinProvider;
import com.darkz.skintotem.skin.provider.extended.ElyBySkinProvider;
import org.jetbrains.annotations.Nullable;


public class TagsSkinProviders {

	private static final Map<String, SkinProvider> SKIN_PROVIDERS_IDS = new HashMap<>();

	public static Map<String, SkinProvider> getSkinProvidersIds() {
		return SKIN_PROVIDERS_IDS;
	}

	public static void register() {
		registerProvider("NameMC", NameMCSkinProvider.getInstance());
		registerProvider("TLauncher", TLauncherSkinProvider.getInstance());
		registerProvider("ElyBy", ElyBySkinProvider.getInstance());
	}

	public static void registerProvider(String id, SkinProvider provider) {
		SKIN_PROVIDERS_IDS.put(id, provider);
	}

	public static boolean isProvider(String o) {
		int b = o.lastIndexOf("|");
		if (b == -1) {
			return SKIN_PROVIDERS_IDS.containsKey(o);
		}
		String[] split = o.substring(0, b).split("\\|");
		String id = split[0].trim();
		return SKIN_PROVIDERS_IDS.containsKey(id);
	}

	@Nullable
	public static SkinProvider getProviderFor(String o) {
		return SKIN_PROVIDERS_IDS.get(o);
	}

	public static SkinTotemData loadDollFromProvider(String o) {
		if (!o.contains("|")) {
			return StandardSkinTotemManager.getStandardDoll();
		}
		String[] split = o.split("\\|");
		String id = split[0].trim();
		SkinProvider skinProvider = SKIN_PROVIDERS_IDS.get(id);
		if (skinProvider == null || split.length < 2) {
			return StandardSkinTotemManager.getStandardDoll();
		}
		return skinProvider.getOrLoadDoll(split[1].trim());
	}
}
