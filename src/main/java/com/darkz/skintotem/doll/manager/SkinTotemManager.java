package com.darkz.skintotem.doll.manager;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import com.darkz.skintotem.atlas.manager.*;
import com.darkz.skintotem.doll.data.SkinTotemData;
import com.darkz.skintotem.skin.provider.SkinProvider;
import com.darkz.skintotem.skin.provider.extended.MojangSkinProvider;
import com.darkz.skintotem.tag.manager.TagsSkinProviders;
import org.jetbrains.annotations.Nullable;

public class SkinTotemManager {

	public static SkinTotemData getDoll(String nickname) {
		if (TagsSkinProviders.isProvider(nickname)) {
			return TagsSkinProviders.loadDollFromProvider(nickname);
		}
		return MojangSkinProvider.getInstance().getOrLoadDoll(nickname);
	}

	public static Set<String> getAllLoadedKeys() {
		Set<String> loaded = new HashSet<>();

		for (SkinProvider value : TagsSkinProviders.getSkinProvidersIds().values()) {
			loaded.addAll(value.getLoadedKeys());
		}

		loaded.addAll(MojangSkinProvider.getInstance().getLoadedKeys());

		return loaded;
	}

	public static Set<SkinTotemData> getAllLoadedDolls() {
		Set<SkinTotemData> loaded = new HashSet<>();

		for (SkinProvider value : TagsSkinProviders.getSkinProvidersIds().values()) {
			loaded.addAll(value.getLoadedDolls());
		}

		loaded.addAll(MojangSkinProvider.getInstance().getLoadedDolls());

		return loaded;
	}

	public static CompletableFuture<Float> reloadData(Consumer<Float> action) {
		List<SkinProvider> providers = new ArrayList<>(TagsSkinProviders.getSkinProvidersIds().values());
		providers.add(MojangSkinProvider.getInstance());

		Set<CompletableFuture<?>> list = new HashSet<>();
		long startMs = System.currentTimeMillis();

		for (SkinProvider provider : providers) {
			list.add(provider.reloadAll());
		}

		return CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).thenApply((__) -> {
			SkinTotemAtlasManager.stitchAndUpdate(SkinTotemAtlasSpriteManager.getSprites(), null);
			action.accept((System.currentTimeMillis() - startMs) / 1000F);
			return null;
		});
	}

	@Nullable
	public static CompletableFuture<Float> reloadData(String value, Consumer<Float> action) {
		long startMs = System.currentTimeMillis();

		SkinProvider skinProvider = TagsSkinProviders.getProviderFor(value);

		CompletableFuture<Void> completableFuture =
				skinProvider == null
						?
						MojangSkinProvider.getInstance().reloadOne(value)
						:
						skinProvider.reloadOne(value);

		if (completableFuture == null) {
			return null;
		}

		return completableFuture.thenApply((__) -> {
			SkinTotemAtlasManager.stitchAndUpdate(SkinTotemAtlasSpriteManager.getSprites(), null);
			action.accept((System.currentTimeMillis() - startMs) / 1000F);
			return null;
		});
	}
}
