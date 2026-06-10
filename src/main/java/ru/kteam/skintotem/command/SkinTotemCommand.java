package ru.kteam.skintotem.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import com.darkz.skintotem.SkinTotem;

@Environment(EnvType.CLIENT)
public class SkinTotemCommand {

    private static final String P = "§6[SkinTotem]§r ";

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {

        dispatcher.register(
            ClientCommandManager.literal("skintotem")

                // /skintotem info
                .then(ClientCommandManager.literal("info").executes(ctx -> {
                    ctx.getSource().sendFeedback(Component.literal(
                        P + "§bv1.0.0 §8| §bAuthor: §fDarkz §8| §fLopyMine §8| KlashRaick §8| §fK-TEAM"
                    ));
                    return 1;
                }))

                // /skintotem refresh
                .then(ClientCommandManager.literal("refresh")
                    .executes(ctx -> {
                        ctx.getSource().sendFeedback(
                            Component.literal(P + "§aRefresh not implemented in this version")
                        );
                        return 1;
                    }))

                // /skintotem credits
                .then(ClientCommandManager.literal("credits").executes(ctx -> {
                    ctx.getSource().sendFeedback(Component.literal(
                        "\n§6╔══════════════════════════════════════════════════╗\n" +
                        "§6║  §bSkinTotem §fv1.0.0                                     §6║\n" +
                        "§6║  §7Author:       §fDarkz      §fKlashRaisk   §fLopyMine    §6║\n" +
                        "§6║  §7Team:         §fK-TEAM                                 §6║\n" +
                        "§6╚════════════════════════════════════════════════════╝\n"
                    ));
                    return 1;
                }))

                // /skintotem tl
                .then(ClientCommandManager.literal("tl").executes(ctx -> {
                    ctx.getSource().sendFeedback(
                        Component.literal(P + "§aUsing TLauncher skin source")
                    );
                    return 1;
                }))

                // /skintotem ely
                .then(ClientCommandManager.literal("ely").executes(ctx -> {
                    ctx.getSource().sendFeedback(
                        Component.literal(P + "§aUsing Ely.by skin source")
                    );
                    return 1;
                }))

                // /skintotem url <url>
                .then(ClientCommandManager.literal("url")
                    .then(ClientCommandManager.argument("url", StringArgumentType.greedyString())
                        .executes(ctx -> {

                            String url = StringArgumentType.getString(ctx, "url");

                            ctx.getSource().sendFeedback(
                                Component.literal(P + "§aCustom skin URL:\n§f" + url)
                            );

                            return 1;
                        })
                    )
                )

                // help
                .executes(ctx -> {
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
                })
        );

        SkinTotem.LOGGER.info("[SkinTotem] Commands registered");
    }
}
