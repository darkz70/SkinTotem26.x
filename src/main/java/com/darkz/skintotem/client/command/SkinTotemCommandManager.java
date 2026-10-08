package com.darkz.skintotem.client.command;

import com.darkz.skintotem.client.command.refresh.RefreshCommand;
import com.darkz.skintotem.client.command.sounds.SoundsCommand;
import com.darkz.skintotem.loader.SkinTotemLoader;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class SkinTotemCommandManager {

    public static void register() {
        SkinTotemLoader.registerClientCommands();
    }

    /**
     * Дерево команды собирается обобщённо: тип источника команды у Fabric и NeoForge разный,
     * а само дерево одинаковое.
     */
    public static <S> void registerTo(CommandDispatcher<S> dispatcher) {
        dispatcher.register(SkinTotemCommandManager.buildRoot());
    }

    private static <S> LiteralArgumentBuilder<S> buildRoot() {
        return LiteralArgumentBuilder.<S>literal("skintotem")
                .then(RefreshCommand.getInstance())
                .then(SkinTotemCommand.getInfoCommand())
                .then(SkinTotemCommand.getCreditsCommand())
                .then(SkinTotemCommand.getTlCommand())
                .then(SkinTotemCommand.getElyCommand())
                .then(SkinTotemCommand.getUrlCommand())
                .then(SoundsCommand.getInstance())
                .executes(SkinTotemCommand.getHelpExecutor());
    }

}
