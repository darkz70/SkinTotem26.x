package com.darkz.skintotem.client.command;

import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.loader.SkinTotemLoader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.network.chat.Component;

public class SkinTotemCommand {

    private static final String P = "§6[SkinTotem]§r ";

    /** Версия берётся из метаданных мода, чтобы не расходиться с gradle.properties. */
    private static final String VERSION = getModVersion();

    private static final String SOURCE_URL = "https://github.com/darkz70/SkinTotem26.x";

    private static String getModVersion() {
        String version = SkinTotemLoader.getModVersion(SkinTotem.MOD_ID);
        return version != null ? version : "unknown";
    }

    public static <S> LiteralArgumentBuilder<S> getInfoCommand() {
        return LiteralArgumentBuilder.<S>literal("info").executes(ctx -> {
            CommandFeedback.send(Component.literal(
                P + "§bv" + VERSION + " §8| §bAuthor: §fDarkz"
            ));
            return 1;
        });
    }

    public static <S> LiteralArgumentBuilder<S> getCreditsCommand() {
        return LiteralArgumentBuilder.<S>literal("credits").executes(ctx -> {
            CommandFeedback.send(Component.literal(
                "\n§6§lSkinTotem §fv" + VERSION + "\n" +
                "§7Author: §fDarkz\n" +
                "§7Source: §f" + SOURCE_URL + "\n"
            ));
            return 1;
        });
    }

    public static <S> LiteralArgumentBuilder<S> getTlCommand() {
        return LiteralArgumentBuilder.<S>literal("tl").executes(ctx -> {
            CommandFeedback.send(
                Component.literal(P + "§aUsing TLauncher skin source")
            );
            return 1;
        });
    }

    public static <S> LiteralArgumentBuilder<S> getElyCommand() {
        return LiteralArgumentBuilder.<S>literal("ely").executes(ctx -> {
            CommandFeedback.send(
                Component.literal(P + "§aUsing Ely.by skin source")
            );
            return 1;
        });
    }

    public static <S> LiteralArgumentBuilder<S> getUrlCommand() {
        return LiteralArgumentBuilder.<S>literal("url")
            .then(RequiredArgumentBuilder.<S, String>argument("url", StringArgumentType.greedyString())
                .executes(ctx -> {
                    String url = StringArgumentType.getString(ctx, "url");
                    CommandFeedback.send(
                        Component.literal(P + "§aCustom skin URL:\n§f" + url)
                    );
                    return 1;
                })
            );
    }

    public static <S> com.mojang.brigadier.Command<S> getHelpExecutor() {
        return ctx -> {
            CommandFeedback.send(Component.literal(
                P + "§7Commands:\n" +
                "  §f/skintotem info\n" +
                "  §f/skintotem refresh\n" +
                "  §f/skintotem tl\n" +
                "  §f/skintotem ely\n" +
                "  §f/skintotem url <url>\n" +
                "  §f/skintotem sounds [reload|list]\n" +
                "  §f/skintotem credits"
            ));
            return 1;
        };
    }
}
