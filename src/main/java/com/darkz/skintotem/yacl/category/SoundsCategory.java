package com.darkz.skintotem.yacl.category;

import dev.isxander.yacl3.api.*;
import lombok.experimental.ExtensionMethod;
import com.darkz.skintotem.config.SkinTotemConfig;
import com.darkz.skintotem.extension.SimpleOptionExtension;
import com.darkz.skintotem.yacl.custom.simple.main.*;
import com.darkz.skintotem.yacl.custom.simple.utils.SimpleContent;

/**
 * Категория «Звуки и эмоции»: собственные звуки мода и интеграция с Emotecraft.
 */
@ExtensionMethod(SimpleOptionExtension.class)
public class SoundsCategory {

	public static ConfigCategory get(SkinTotemConfig defConfig, SkinTotemConfig config) {
		return SimpleCategory.startBuilder("sounds")
				.groups(getSoundsGroup(defConfig, config))
				.groups(getEmotesGroup(defConfig, config))
				.build();
	}

	private static OptionGroup getSoundsGroup(SkinTotemConfig defConfig, SkinTotemConfig config) {
		return SimpleGroup.startBuilder("sounds")
				.options(
						SimpleOption.<Boolean>startBuilder("sounds_enabled")
								.withBinding(defConfig.isSoundsEnabled(), config::isSoundsEnabled, config::setSoundsEnabled, true)
								.withDescription(SimpleContent.NONE)
								.withController()
								.build(),
						SimpleOption.<Float>startBuilder("sounds_volume")
								.withBinding(defConfig.getSoundsVolume(), config::getSoundsVolume, config::setSoundsVolume, true)
								.withDescription(SimpleContent.NONE)
								.withController(0.0F, 2.0F, 0.05F)
								.build(),
						SimpleOption.<Boolean>startBuilder("replace_vanilla_totem_sound")
								.withBinding(defConfig.isReplaceVanillaTotemSound(), config::isReplaceVanillaTotemSound, config::setReplaceVanillaTotemSound, true)
								.withDescription(SimpleContent.NONE)
								.withController()
								.build(),
						SimpleOption.<Boolean>startBuilder("custom_sounds_enabled")
								.withBinding(defConfig.isCustomSoundsEnabled(), config::isCustomSoundsEnabled, config::setCustomSoundsEnabled, true)
								.withDescription(SimpleContent.NONE)
								.withController()
								.build(),
						SimpleOption.<Boolean>startBuilder("custom_sounds_auto_convert")
								.withBinding(defConfig.isCustomSoundsAutoConvert(), config::isCustomSoundsAutoConvert, config::setCustomSoundsAutoConvert, true)
								.withDescription(SimpleContent.NONE)
								.withController()
								.build()
				)
				.build();
	}

	private static OptionGroup getEmotesGroup(SkinTotemConfig defConfig, SkinTotemConfig config) {
		return SimpleGroup.startBuilder("emotes")
				.options(
						SimpleOption.<Boolean>startBuilder("emote_mirroring_enabled")
								.withBinding(defConfig.isEmoteMirroringEnabled(), config::isEmoteMirroringEnabled, config::setEmoteMirroringEnabled, true)
								.withDescription(SimpleContent.NONE)
								.withController()
								.build(),
						SimpleOption.<String>startBuilder("activation_emote")
								.withBinding(defConfig.getActivationEmote(), config::getActivationEmote, config::setActivationEmote, true)
								.withDescription(SimpleContent.NONE)
								.withController()
								.build()
				)
				.build();
	}
}
