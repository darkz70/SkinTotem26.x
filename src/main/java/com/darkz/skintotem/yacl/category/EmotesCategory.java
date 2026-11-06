package com.darkz.skintotem.yacl.category;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.CyclingListControllerBuilder;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.ExtensionMethod;
import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.compat.EmoteSupport;
import com.darkz.skintotem.config.SkinTotemConfig;
import com.darkz.skintotem.extension.SimpleOptionExtension;
import com.darkz.skintotem.yacl.custom.simple.main.*;
import com.darkz.skintotem.yacl.custom.simple.utils.SimpleContent;
import net.minecraft.network.chat.Component;

/**
 * Категория интеграции с Emotecraft: кукла повторяет эмоции держащего тотем игрока,
 * а при срабатывании тотема проигрывается выбранная эмоция (с выбором из списка загруженных).
 */
@ExtensionMethod(SimpleOptionExtension.class)
public class EmotesCategory {

	public static ConfigCategory get(SkinTotemConfig defConfig, SkinTotemConfig config) {
		return SimpleCategory.startBuilder("emotes")
				.groups(getEmotesGroup(defConfig, config))
				.build();
	}

	private static OptionGroup getEmotesGroup(SkinTotemConfig defConfig, SkinTotemConfig config) {
		Option<Boolean> emoteMirroringOption = SimpleOption.<Boolean>startBuilder("emote_mirroring_enabled")
				.withBinding(defConfig.isEmoteMirroringEnabled(), config::isEmoteMirroringEnabled, config::setEmoteMirroringEnabled, true)
				.withDescription(SimpleContent.NONE)
				.withController()
				.build();
		emoteMirroringOption.setAvailable(EmoteSupport.isEmotecraftLoaded());

		Option<String> activationEmoteOption = createActivationEmoteOption(defConfig, config);
		activationEmoteOption.setAvailable(EmoteSupport.isEmotecraftLoaded());

		return SimpleGroup.startBuilder("emotes")
				.options(emoteMirroringOption, activationEmoteOption)
				.build();
	}

	/**
	 * Эмоция при срабатывании тотема. Если Emotecraft установлен — предлагаем выбор
	 * из списка загруженных эмоций; иначе остаётся обычное текстовое поле.
	 */
	private static Option<String> createActivationEmoteOption(SkinTotemConfig defConfig, SkinTotemConfig config) {
		SimpleOption.Builder<String> builder = SimpleOption.<String>startBuilder("activation_emote")
				.withBinding(defConfig.getActivationEmote(), config::getActivationEmote, config::setActivationEmote, true)
				.withDescription(SimpleContent.NONE);

		List<String> choices = new ArrayList<>();
		choices.add("");
		choices.addAll(EmoteSupport.getAvailableEmoteNames());
		String current = config.getActivationEmote();
		if (current != null && !current.isBlank() && !choices.contains(current)) {
			choices.add(current);
		}

		if (EmoteSupport.isEmotecraftLoaded() && choices.size() > 1) {
			List<String> immutableChoices = List.copyOf(choices);
			builder.getOptionBuilder().controller((option) -> CyclingListControllerBuilder.create(option)
					.values(immutableChoices)
					.formatValue((name) -> name == null || name.isEmpty()
							? SkinTotem.text("modmenu.option.activation_emote.none")
							: Component.literal(name)));
		} else {
			builder.withController();
		}

		return builder.build();
	}
}
