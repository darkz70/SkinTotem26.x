package com.darkz.skintotem.pack;

import java.util.concurrent.*;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.atlas.manager.*;
import com.darkz.skintotem.model.bb.manager.BlockBenchModelManager;
import com.darkz.skintotem.tag.manager.TagsManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.*;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.*;

public class SkinTotemReloadListener implements PreparableReloadListener {

	public static void register() {
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(getFabricId(), new SkinTotemReloadListener());
	}

	public static Identifier getFabricId() {
		return SkinTotem.id("%s-reload-listener".formatted(SkinTotem.MOD_ID));
	}

	@Override
	public CompletableFuture<Void> reload(SharedState store, Executor prepareExecutor, PreparationBarrier synchronizer, Executor applyExecutor) {
		return synchronizer.wait(Unit.INSTANCE).thenRunAsync(() -> {
			ProfilerFiller profiler = Profiler.get();
			profiler.push("listener");
			this.reloadStuff(synchronizer, store.resourceManager(), prepareExecutor, applyExecutor);
			profiler.pop();
		}, applyExecutor);
	}

	private void reloadStuff(PreparationBarrier synchronizer, ResourceManager resourceManager, Executor prepareExecutor, Executor applyExecutor) {
		this.reloadAtlas(synchronizer, prepareExecutor, applyExecutor);
		BlockBenchModelManager.reload();
		SkinTotemModelFinder.reload(resourceManager);
		TagsManager.reloadCustomModelIdsTags();
	}

	private void reloadAtlas(PreparationBarrier synchronizer, Executor prepareExecutor, Executor applyExecutor) {
		SkinTotemAtlasSpriteManager.reload();
		SkinTotemAtlasManager.stitchAndUpdate(SkinTotemAtlasSpriteManager.getSprites(), synchronizer, prepareExecutor, applyExecutor, null);
	}
}
