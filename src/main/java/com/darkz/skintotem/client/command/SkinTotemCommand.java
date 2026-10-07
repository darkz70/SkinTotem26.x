package com.darkz.skintotem.client.command;

import com.darkz.skintotem.SkinTotem;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;

public class SkinTotemCommand {

    private static final String P = "§6[SkinTotem]§r ";

    /** Версия берётся из метаданных мода, чтобы не расходиться с gradle.properties. */
    private static final String VERSION = FabricLoader.getInstance()
            .getModContainer(SkinTotem.MOD_ID)
            .map(container -> container.getMetadata().getVersion().getFriendlyString())
            .orElse("unknown");

    private static final String SOURCE_URL = "https://github.com/darkz70/SkinTotem26.x";

    public static LiteralArgumentBuilder<FabricClientCommandSource> getInfoCommand() {
        return literal("info").executes(ctx -> {
            ctx.getSource().sendFeedback(Component.literal(
                P + "§bv" + VERSION + " §8| §bAuthor: §fDarkz"
            ));
            return 1;
        });
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> getCreditsCommand() {
        return literal("credits").executes(ctx -> {
            ctx.getSource().sendFeedback(Component.literal(
                "\n§6§lSkinTotem §fv" + VERSION + "\n" +
                "§7Author: §fDarkz\n" +
                "§7Source: §f" + SOURCE_URL + "\n"
            ));
            return 1;
        });
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> getTlCommand() {
        return literal("tl").executes(ctx -> {
            ctx.getSource().sendFeedback(
                Component.literal(P + "§aUsing TLauncher skin source")
            );
            return 1;
        });
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> getElyCommand() {
        return literal("ely").executes(ctx -> {
            ctx.getSource().sendFeedback(
                Component.literal(P + "§aUsing Ely.by skin source")
            );
            return 1;
        });
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> getUrlCommand() {
        return literal("url")
            .then(argument("url", StringArgumentType.greedyString())
                .executes(ctx -> {
                    String url = StringArgumentType.getString(ctx, "url");
                    ctx.getSource().sendFeedback(
                        Component.literal(P + "§aCustom skin URL:\n§f" + url)
                    );
                    return 1;
                })
            );
    }

    public static com.mojang.brigadier.Command<FabricClientCommandSource> getHelpExecutor() {
        return ctx -> {
            ctx.getSource().sendFeedback(Component.literal(
                P + "§7Commands:\n" +
                "  §f/skintotem info\n" +
                "  §f/skintotem refresh\n" +
                "  §f/skintotem tl\n" +
                "  §f/skintotem ely\n" +
                "  §f/skintotem url <url>\n" +
                "  §f/skintotem credits"
            ));
            return 1;
        };
    }
}
