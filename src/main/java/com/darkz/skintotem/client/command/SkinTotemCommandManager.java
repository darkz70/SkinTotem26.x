package com.darkz.skintotem.client.command;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import com.darkz.skintotem.client.command.refresh.RefreshCommand;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class SkinTotemCommandManager {

	public static void register() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(literal("skintotem")
					.then(RefreshCommand.getInstance()));
		});
	}
}
