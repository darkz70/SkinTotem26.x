package com.darkz.skintotem.client.command.sounds;

import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.client.command.CommandFeedback;
import com.darkz.skintotem.client.command.builder.CommandTextBuilder;
import com.darkz.skintotem.sound.custom.CustomSoundPack;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/**
 * {@code /skintotem sounds} — показывает и перечитывает пользовательские звуки из папки конфигурации.
 */
public class SoundsCommand {

	public static <S> LiteralArgumentBuilder<S> getInstance() {
		return LiteralArgumentBuilder.<S>literal("sounds")
				.then(LiteralArgumentBuilder.<S>literal("reload").executes(context -> {
					CommandFeedback.send(CommandTextBuilder.startBuilder("command.sounds.reloading").build());

					// Чтение файлов идёт в фоне, отчёт показываем в игровом потоке, когда он готов.
					CustomSoundPack.scanAsync().thenRun(() -> Minecraft.getInstance().execute(SoundsCommand::sendReport));
					return 1;
				}))
				.then(LiteralArgumentBuilder.<S>literal("list").executes(context -> {
					sendReport();
					return 1;
				}))
				.executes(context -> {
					sendReport();
					return 1;
				});
	}

	private static void sendReport() {
		CommandFeedback.send(CommandTextBuilder.startBuilder("command.sounds.folder", CustomSoundPack.folder()).build());

		List<String> report = CustomSoundPack.report();
		if (report.isEmpty()) {
			CommandFeedback.send(SkinTotem.text("command.sounds.empty"));
			return;
		}

		CommandFeedback.send(SkinTotem.text("command.sounds.loaded", report.size()));
		for (String line : report) {
			boolean failed = line.contains("(failed:");
			CommandFeedback.send(Component.literal(failed ? "§c  • " + line : "§a  • §f" + line));
		}
	}
}
