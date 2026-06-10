package com.darkz.skintotem.utils;

import java.util.function.Function;
import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.yacl.custom.simple.utils.SimpleContent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class ModMenuUtils {

	private ModMenuUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static String getOptionKey(String optionId) {
		return String.format("modmenu.option.%s", optionId);
	}

	public static String getCategoryKey(String categoryId) {
		return String.format("modmenu.category.%s", categoryId);
	}

	public static String getGroupKey(String groupId) {
		return String.format("modmenu.group.%s", groupId);
	}

	public static Component getName(String key) {
		return SkinTotem.text(key + ".name");
	}

	public static Component getDescription(String key) {
		return SkinTotem.text(key + ".description");
	}

	public static Identifier getContentId(SimpleContent content, String contentId) {
		return SkinTotem.id(String.format("textures/config/%s.%s", contentId, content.getFileExtension()));
	}

	public static Component getModTitle() {
		return SkinTotem.text("modmenu.title");
	}

	public static Function<Boolean, Component> getEnabledOrDisabledFormatter() {
		return state -> SkinTotem.text("modmenu.formatter.enabled_or_disabled." + state);
	}

	public static Component getNoConfigScreenMessage() {
		return SkinTotem.text("modmenu.no_config_library_screen.message");
	}

	public static Component getOldConfigScreenMessage(String version) {
		return SkinTotem.text("modmenu.old_config_library_screen.message", version, SkinTotem.YACL_DEPEND_VERSION);
	}
}
