package com.darkz.skintotem.skin.provider.extended;

import java.util.*;
import java.util.stream.Collectors;
import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.api.*;
import com.darkz.skintotem.doll.data.SkinTotemData;
import com.darkz.skintotem.skin.data.ParsedSkinData;
import com.darkz.skintotem.skin.provider.StandardSkinProvider;
import net.minecraft.resources.Identifier;

public class TLauncherSkinProvider extends StandardSkinProvider {
    private static final TLauncherSkinProvider INSTANCE = new TLauncherSkinProvider();
    private TLauncherSkinProvider() { super(true); }
    public static TLauncherSkinProvider getInstance() { return INSTANCE; }

    @Override
    protected Response<ParsedSkinData> loadDollFromAPI(String value) {
        return TLauncherAPI.getSkinData(value);
    }

    @Override
    public SkinTotemData createNewDoll(String value) {
        return SkinTotemData.create(value);
    }

    @Override
    public Set<String> getLoadedKeys() {
        return this.getCache().values().stream().map(SkinTotemData::getNickname).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    protected Identifier getId(String value, String type) {
        return SkinTotem.getDollTextureId("tlauncher_api/%s/%s".formatted(type, value.toLowerCase()));
    }

    @Override
    public boolean canProcess(String value) {
        return value != null && value.length() >= 2 && value.length() <= 16;
    }
}
