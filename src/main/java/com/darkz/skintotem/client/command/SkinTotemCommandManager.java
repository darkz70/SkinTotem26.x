package com.darkz.skintotem.client.command;

//? if fabric {
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;
//?} else {
/*import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import static net.minecraft.commands.Commands.literal;
*///?}
import com.darkz.skintotem.client.command.refresh.RefreshCommand;

public class SkinTotemCommandManager {

    public static void register() {
        //? if fabric {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("skintotem")
                    .then(RefreshCommand.getInstance())
                    .then(SkinTotemCommand.getInfoCommand())
                    .then(SkinTotemCommand.getCreditsCommand())
                    .then(SkinTotemCommand.getTlCommand())
                    .then(SkinTotemCommand.getElyCommand())
                    .then(SkinTotemCommand.getUrlCommand())
                    .executes(SkinTotemCommand.getHelpExecutor())
            );
        });
        //?} else {
        /*NeoForge.EVENT_BUS.addListener((RegisterClientCommandsEvent event) -> {
            event.getDispatcher().register(literal("skintotem")
                    .then(RefreshCommand.getInstanceNeo())
                    .then(SkinTotemCommand.getInfoCommandNeo())
                    .then(SkinTotemCommand.getCreditsCommandNeo())
                    .then(SkinTotemCommand.getTlCommandNeo())
                    .then(SkinTotemCommand.getElyCommandNeo())
                    .then(SkinTotemCommand.getUrlCommandNeo())
                    .executes(SkinTotemCommand.getHelpExecutorNeo())
            );
        });
        *///?}
    }

}
