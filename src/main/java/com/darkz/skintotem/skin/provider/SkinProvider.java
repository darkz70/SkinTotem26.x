package com.darkz.skintotem.skin.provider;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import com.darkz.skintotem.doll.data.TotemDollData;
import org.jetbrains.annotations.NotNull;

public interface SkinProvider {

	@NotNull
	TotemDollData getOrLoadDoll(String value);

	Set<String> getLoadedKeys();

	Collection<TotemDollData> getLoadedDolls();

	CompletableFuture<Void> reloadAll();

	CompletableFuture<Void> reloadOne(String value);

	boolean canProcess(String value);
}
