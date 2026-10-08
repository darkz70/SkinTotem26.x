package com.darkz.skintotem.client.command.refresh;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Map;
import java.util.concurrent.*;
import com.darkz.skintotem.api.MojangAPI;
import com.darkz.skintotem.client.SkinTotemClient;
import com.darkz.skintotem.client.command.CommandFeedback;
import com.darkz.skintotem.client.command.builder.CommandTextBuilder;
import com.darkz.skintotem.doll.manager.SkinTotemManager;
import com.darkz.skintotem.sound.SkinTotemSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class RefreshCommand {

	private static final Map<String, CompletableFuture<Float>> RELOADING_FUTURES = new ConcurrentHashMap<>();
	@Nullable
	private static CompletableFuture<Float> RELOADING_ALL_FUTURE = null;

	public static <S> LiteralArgumentBuilder<S> getInstance() {
		return LiteralArgumentBuilder.<S>literal("refresh")
				.then(LiteralArgumentBuilder.<S>literal("all")
						.executes(RefreshCommand::reloadAll))
				.then(LiteralArgumentBuilder.<S>literal("player")
						.then(RequiredArgumentBuilder.<S, String>argument("nickname", StringArgumentType.word())
								.suggests((context, builder) ->
										SharedSuggestionProvider.suggest(SkinTotemManager.getAllLoadedKeys(), builder))
								.executes(RefreshCommand::reloadForPlayer)
						));
	}

	private static <S> int reloadAll(CommandContext<S> context) {
		if (RELOADING_ALL_FUTURE != null) {
			return 0;
		}

		Component startFeedback = CommandTextBuilder.startBuilder("command.refresh.all.start").build();
		CommandFeedback.send(startFeedback);

		RELOADING_ALL_FUTURE = SkinTotemManager.reloadData((seconds) -> {
			Component endFeedback = CommandTextBuilder.startBuilder("command.refresh.all.end", seconds).build();
			Minecraft.getInstance().execute(() -> {
				CommandFeedback.send(endFeedback);
				SkinTotemSounds.onSkinChanged();
			});
		}).whenComplete((r, e) -> {
			RELOADING_ALL_FUTURE = null;
			if (e != null) {
				SkinTotemClient.LOGGER.error("Failed to refresh all doll data: ", e);
				Minecraft.getInstance().execute(SkinTotemSounds::onSkinError);
			}
		});

		MojangAPI.useFallbackAPI = false;

		return Command.SINGLE_SUCCESS;
	}

	private static <S> int reloadForPlayer(CommandContext<S> context) {
		String nickname = StringArgumentType.getString(context, "nickname");

		CompletableFuture<Float> future = RELOADING_FUTURES.get(nickname);
		if (future != null) {
			return 0;
		}

		Component startFeedback = CommandTextBuilder.startBuilder("command.refresh.player.start", nickname).build();
		CommandFeedback.send(startFeedback);

		CompletableFuture<Float> f = SkinTotemManager.reloadData(nickname, (seconds) -> {
			Component endFeedback = CommandTextBuilder.startBuilder("command.refresh.player.end", nickname, seconds).build();
			Minecraft.getInstance().execute(() -> {
				CommandFeedback.send(endFeedback);
				SkinTotemSounds.onSkinChanged();
			});
		});

		if (f != null) {
			CompletableFuture<Float> fc = f.whenComplete((r, e) -> {
				RELOADING_FUTURES.remove(nickname);
				if (e != null) {
					SkinTotemClient.LOGGER.error("Failed to refresh doll data for \"{}\": ", nickname, e);
					Minecraft.getInstance().execute(SkinTotemSounds::onSkinError);
				}
			});
			RELOADING_FUTURES.put(nickname, fc);
		}

		return Command.SINGLE_SUCCESS;
	}
}
