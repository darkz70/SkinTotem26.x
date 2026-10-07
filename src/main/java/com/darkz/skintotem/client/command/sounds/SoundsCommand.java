package com.darkz.skintotem.client.command.sounds;

import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.client.command.builder.CommandTextBuilder;
import com.darkz.skintotem.sound.custom.CustomSoundPack;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

/**
 * {@code /skintotem sounds} — показывает и перечитывает пользовательские звуки из папки конфигурации.
 */
public class SoundsCommand {

	public static LiteralArgumentBuilder<FabricClientCommandSource> getInstance() {
		return literal("sounds")
				.then(literal("reload").executes(context -> {
					FabricClientCommandSource source = context.getSource();
					source.sendFeedback(CommandTextBuilder.startBuilder("command.sounds.reloading").build());

					// Чтение файлов идёт в фоне, отчёт показываем в игровом потоке, когда он готов.
					CustomSoundPack.scanAsync().thenRun(() -> Minecraft.getInstance().execute(() -> sendReport(source)));
					return 1;
				}))
				.then(literal("list").executes(context -> {
					sendReport(context.getSource());
					return 1;
				}))
				.executes(context -> {
					sendReport(context.getSource());
					return 1;
				});
	}

	private static void sendReport(FabricClientCommandSource source) {
		source.sendFeedback(CommandTextBuilder.startBuilder("command.sounds.folder", CustomSoundPack.folder()).build());

		List<String> report = CustomSoundPack.report();
		if (report.isEmpty()) {
			source.sendFeedback(SkinTotem.text("command.sounds.empty"));
			return;
		}

		source.sendFeedback(SkinTotem.text("command.sounds.loaded", report.size()));
		for (String line : report) {
			boolean failed = line.contains("(failed:");
			source.sendFeedback(Component.literal(failed ? "§c  • " + line : "§a  • §f" + line));
		}
	}
}
