package net.lopymine.mtd.skin.provider.extended;

import java.util.*;
import java.util.stream.Collectors;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.api.*;
import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.skin.data.ParsedSkinData;
import net.lopymine.mtd.skin.provider.StandardSkinProvider;
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
    public TotemDollData createNewDoll(String value) {
        return TotemDollData.create(value);
    }

    @Override
    public Set<String> getLoadedKeys() {
        return this.getCache().values().stream().map(TotemDollData::getNickname).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    protected Identifier getId(String value, String type) {
        return MyTotemDoll.getDollTextureId("tlauncher_api/%s/%s".formatted(type, value.toLowerCase()));
    }

    @Override
    public boolean canProcess(String value) {
        return value != null && value.length() >= 2 && value.length() <= 16;
    }
}
