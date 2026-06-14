package com.darkz.skintotem.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
//? if fabric {
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
//?} else {
/*import net.minecraft.commands.CommandSourceStack;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.Commands.argument;
*///?}
import net.minecraft.network.chat.Component;

public class SkinTotemCommand {

    private static final String P = "§6[SkinTotem]§r ";

    //? if fabric {
    public static LiteralArgumentBuilder<FabricClientCommandSource> getInfoCommand() {
        return literal("info").executes(ctx -> {
            ctx.getSource().sendFeedback(Component.literal(
                P + "§bv1.0.0 §8| §bAuthor: §fDarkz §8| §fLopyMine §8| KlashRaick §8| §fK-TEAM"
            ));
            return 1;
        });
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> getCreditsCommand() {
        return literal("credits").executes(ctx -> {
            ctx.getSource().sendFeedback(Component.literal(
                "\n§6╔══════════════════════════════════════════════════╗\n" +
                "§6║  §bSkinTotem §fv1.0.0                                     §6║\n" +
                "§6║  §7Author:       §fDarkz      §fKlashRaisk   §fLopyMine    §6║\n" +
                "§6║  §7Team:         §fK-TEAM                                 §6║\n" +
                "§6╚════════════════════════════════════════════════════╝\n"
            ));
            return 1;
        });
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> getTlCommand() {
        return literal("tl").executes(ctx -> {
            ctx.getSource().sendFeedback(Component.literal(P + "§aUsing TLauncher skin source"));
            return 1;
        });
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> getElyCommand() {
        return literal("ely").executes(ctx -> {
            ctx.getSource().sendFeedback(Component.literal(P + "§aUsing Ely.by skin source"));
            return 1;
        });
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> getUrlCommand() {
        return literal("url")
            .then(argument("url", StringArgumentType.greedyString())
                .executes(ctx -> {
                    String url = StringArgumentType.getString(ctx, "url");
                    ctx.getSource().sendFeedback(Component.literal(P + "§aCustom skin URL:\n§f" + url));
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
    //?} else {
    /*public static LiteralArgumentBuilder<CommandSourceStack> getInfoCommandNeo() {
        return literal("info").executes(ctx -> {
            ctx.getSource().sendSuccess(() -> Component.literal(
                P + "§bv1.0.0 §8| §bAuthor: §fDarkz §8| §fLopyMine §8| KlashRaick §8| §fK-TEAM"
            ), false);
            return 1;
        });
    }

    public static LiteralArgumentBuilder<CommandSourceStack> getCreditsCommandNeo() {
        return literal("credits").executes(ctx -> {
            ctx.getSource().sendSuccess(() -> Component.literal(
                "\n§6╔══════════════════════════════════════════════════╗\n" +
                "§6║  §bSkinTotem §fv1.0.0                                     §6║\n" +
                "§6║  §7Author:       §fDarkz      §fKlashRaisk   §fLopyMine    §6║\n" +
                "§6║  §7Team:         §fK-TEAM                                 §6║\n" +
                "§6╚════════════════════════════════════════════════════╝\n"
            ), false);
            return 1;
        });
    }

    public static LiteralArgumentBuilder<CommandSourceStack> getTlCommandNeo() {
        return literal("tl").executes(ctx -> {
            ctx.getSource().sendSuccess(() -> Component.literal(P + "§aUsing TLauncher skin source"), false);
            return 1;
        });
    }

    public static LiteralArgumentBuilder<CommandSourceStack> getElyCommandNeo() {
        return literal("ely").executes(ctx -> {
            ctx.getSource().sendSuccess(() -> Component.literal(P + "§aUsing Ely.by skin source"), false);
            return 1;
        });
    }

    public static LiteralArgumentBuilder<CommandSourceStack> getUrlCommandNeo() {
        return literal("url")
            .then(argument("url", StringArgumentType.greedyString())
                .executes(ctx -> {
                    String url = StringArgumentType.getString(ctx, "url");
                    ctx.getSource().sendSuccess(() -> Component.literal(P + "§aCustom skin URL:\n§f" + url), false);
                    return 1;
                })
            );
    }

    public static com.mojang.brigadier.Command<CommandSourceStack> getHelpExecutorNeo() {
        return ctx -> {
            ctx.getSource().sendSuccess(() -> Component.literal(
                P + "§7Commands:\n" +
                "  §f/skintotem info\n" +
                "  §f/skintotem refresh\n" +
                "  §f/skintotem tl\n" +
                "  §f/skintotem ely\n" +
                "  §f/skintotem url <url>\n" +
                "  §f/skintotem credits"
            ), false);
            return 1;
        };
    }
    *///?}
}
