package com.darkz.skintotem.client.command;

import com.darkz.skintotem.client.SkinTotemClient;
import net.minecraft.client.Minecraft;
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
		if (client.gui != null) {
			client.gui.hud.getChat().addClientSystemMessage(text);
			return;
		}
		SkinTotemClient.LOGGER.info("{}", text.getString());
	}
}
