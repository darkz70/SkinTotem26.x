package com.darkz.skintotem.yacl.custom;

import com.darkz.skintotem.SkinTotem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

@SuppressWarnings("unused")
public class TransparencySprites {

	public static final SpriteTextures TAB_BUTTON_SPRITES = new SpriteTextures(
			SkinTotem.spriteId("transparency/tab/tab_selected"), // enabled
			SkinTotem.spriteId("transparency/tab/tab"), // disabled
			SkinTotem.spriteId("transparency/tab/tab_selected_highlighted"), // enabled and hovered
			SkinTotem.spriteId("transparency/tab/tab_highlighted") // disabled and hovered
	);

	public static final Identifier SCROLLER_SPRITE = SkinTotem.spriteId("transparency/scroller/scroller");
	public static final Identifier SCROLLER_BACKGROUND_SPRITE = SkinTotem.spriteId("transparency/scroller/scroller_background");

	public static final Identifier DARKER_MENU_BACKGROUND_TEXTURE = SkinTotem.id("textures/gui/transparency/darker_menu_background.png");
	private static final Identifier MENU_BACKGROUND_TEXTURE = SkinTotem.id("textures/gui/transparency/menu_background.png");
	private static final Identifier MENU_LIST_BACKGROUND_TEXTURE = SkinTotem.id("textures/gui/transparency/menu_background.png");
	private static final Identifier IN_WORLD_MENU_BACKGROUND_TEXTURE = SkinTotem.id("textures/gui/transparency/inworld_menu_background.png");
	private static final Identifier IN_WORLD_MENU_LIST_BACKGROUND_TEXTURE = SkinTotem.id("textures/gui/transparency/inworld_menu_list_background.png");

	private static final Identifier MENU_SEPARATOR_TEXTURE = SkinTotem.id("textures/gui/transparency/menu_separator.png");
	private static final Identifier IN_WORLD_MENU_SEPARATOR_TEXTURE = SkinTotem.id("textures/gui/transparency/inworld_menu_separator.png");

	public static Identifier getMenuBackgroundTexture() {
		return Minecraft.getInstance().level == null ? MENU_BACKGROUND_TEXTURE : IN_WORLD_MENU_BACKGROUND_TEXTURE;
	}

	public static Identifier getMenuListBackgroundTexture() {
		return Minecraft.getInstance().level == null ? MENU_LIST_BACKGROUND_TEXTURE : IN_WORLD_MENU_LIST_BACKGROUND_TEXTURE;
	}

	public static Identifier getMenuSeparatorTexture() {
		return Minecraft.getInstance().level == null ? MENU_SEPARATOR_TEXTURE : IN_WORLD_MENU_SEPARATOR_TEXTURE;
	}

	public record SpriteTextures(Identifier enabled, Identifier disabled, Identifier enabledFocused,
	                             Identifier disabledFocused) {

		public Identifier get(boolean enabled, boolean hovered) {
			return enabled ?
					(!hovered ? this.enabled : this.enabledFocused)
					:
					(!hovered ? this.disabled : this.disabledFocused);
		}

	}
}
