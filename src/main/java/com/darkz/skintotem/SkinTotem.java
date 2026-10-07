package com.darkz.skintotem;

import java.util.regex.Pattern;

import net.fabricmc.api.ModInitializer;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;
import org.slf4j.*;

public class SkinTotem implements ModInitializer {

	public static final String MOD_NAME = /*$ mod_name*/ "SkinTotem";
	public static final String MOD_ID = /*$ mod_id*/ "skintotem";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
	public static final String YACL_DEPEND_VERSION = /*$ yacl*/ "3.9.0+26.1-fabric";
	private static final Pattern FORMATTING_CODE = Pattern.compile("&(?=[0-9A-Fa-fK-Ok-oRr])");

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static Identifier getDollTextureId(String path) {
		return id("doll/textures/" + path);
	}

	public static Identifier getDollModelId(String path) {
		return id("dolls/%s.bbmodel".formatted(path));
	}

	public static MutableComponent text(String path, Object... args) {
		return Component.literal(colorize(Component.translatable(String.format("%s.%s", MOD_ID, path), args).getString()));
	}

	/**
	 * Превращает «&»-коды форматирования в «§»-коды.
	 * Одиночный «&» (например, в «Sounds & Emotes») остаётся обычным символом:
	 * раньше он превращался в «§ » и шрифт съедал его вместе со следующим знаком.
	 */
	public static String colorize(String text) {
		return text.indexOf('&') < 0 ? text : FORMATTING_CODE.matcher(text).replaceAll("§");
	}

	public static Identifier spriteId(String path) {
		return id(path);
	}

	@Override
	public void onInitialize() {
		LOGGER.info("{} Initialized", MOD_NAME);
	}
}
