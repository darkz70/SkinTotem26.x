package com.darkz.skintotem.client.command;

import com.darkz.skintotem.client.SkinTotemClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

/**
 * Ответы клиентских команд. Пишем прямо в чат клиента, поэтому код команд
 * не зависит от типа источника команды (у Fabric и NeoForge он разный).
 */
public final class CommandFeedback {

	private CommandFeedback() {
	}

	public static void send(Component text) {
		Minecraft client = Minecraft.getInstance();
		LocalPlayer player = client.player;
		if (player != null) {
			player.displayClientMessage(text, false);
			return;
		}
		SkinTotemClient.LOGGER.info("{}", text.getString());
	}
}
