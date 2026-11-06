package com.darkz.skintotem.yacl.category;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.CyclingListControllerBuilder;
import java.util.*;
import lombok.experimental.ExtensionMethod;
import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.config.SkinTotemConfig;
import com.darkz.skintotem.config.totem.*;
import com.darkz.skintotem.doll.data.SkinTotemData;
import com.darkz.skintotem.doll.manager.SkinTotemManager;
import com.darkz.skintotem.extension.SimpleOptionExtension;
import com.darkz.skintotem.model.bb.manager.BlockBenchModelManager;
import com.darkz.skintotem.skin.CustomSkinLibrary;
import com.darkz.skintotem.skin.figura.FiguraAvatars;
import com.darkz.skintotem.yacl.custom.controller.totem.SkinTotemModelControllerBuilder;
import com.darkz.skintotem.yacl.custom.renderer.SkinTotemPreviewRenderer;
import com.darkz.skintotem.yacl.custom.simple.main.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

@ExtensionMethod(SimpleOptionExtension.class)
public class StandardDollCategory {

	public static ConfigCategory get(SkinTotemConfig defConfig, SkinTotemConfig config) {
		SkinTotemPreviewRenderer renderer = new SkinTotemPreviewRenderer();

		List<Option<?>> options = new ArrayList<>();

		Option<Boolean> useVanillaTotemModelOption = SimpleOption.<Boolean>startBuilder("use_vanilla_totem_model")
				.withCustomDescription(renderer)
				.withBinding(defConfig.isUseVanillaTotemModel(), config::isUseVanillaTotemModel, (value) -> {
					config.setUseVanillaTotemModel(value);
					for (Option<?> option : options) {
						option.setAvailable(!value);
					}
				}, true)
				.withController()
				.build();

		ConfigCategory standardDollCategory = SimpleCategory.startBuilder("standard_doll")
				.options(useVanillaTotemModelOption)
				.groups(getStandardDollSkinGroup(defConfig, config, renderer))
				.groups(getStandardDollModelGroup(defConfig, config, renderer))
				.build();

		for (OptionGroup group : standardDollCategory.groups()) {
			for (Option<?> option : group.options()) {
				if (option == useVanillaTotemModelOption) {
					continue;
				}
				option.setAvailable(!useVanillaTotemModelOption.pendingValue());
				options.add(option);
			}
		}

		return standardDollCategory;
	}

	private static OptionGroup getStandardDollSkinGroup(SkinTotemConfig defConfig, SkinTotemConfig config, SkinTotemPreviewRenderer renderer) {
		Option<String> standardDollSkinDataOption = SimpleOption.<String>startBuilder("standard_doll_skin_data")
				.withCustomDescription(renderer)
				.withBinding(defConfig.getStandardSkinTotemSkinValue(), config::getStandardSkinTotemSkinValue, (value) -> {
					config.setStandardSkinTotemSkinValue(value);
					renderer.updateDoll();
				}, true)
				.withController()
				.build();

		standardDollSkinDataOption.setAvailable(config.getStandardSkinTotemSkinType().isNeedData());

		Option<SkinTotemSkinType> standardDollSkinTypeOption = SimpleOption.<SkinTotemSkinType>startBuilder("standard_doll_skin_type")
				.withCustomDescription(renderer)
				.withBinding(defConfig.getStandardSkinTotemSkinType(), config::getStandardSkinTotemSkinType, (value) -> {
					config.setStandardSkinTotemSkinType(value);
					renderer.updateDoll();
					standardDollSkinDataOption.setAvailable(value.isNeedData());
				}, true)
				.withController(SkinTotemSkinType.class)
				.build();

		Option<String> customSkinFileOption = createCustomSkinFileOption(config, renderer, standardDollSkinDataOption);

		return SimpleGroup.startBuilder("standard_doll_skin")
				.withCustomDescription(renderer)
				.options(
						standardDollSkinTypeOption,
						standardDollSkinDataOption,
						customSkinFileOption
				).build();
	}

	/**
	 * Свой скин из папки {@code config/skintotem/skins/} (как в Figura): выпадающий список
	 * всех PNG-файлов; выбор файла включает тип скина «File Skin» и подставляет путь.
	 */
	private static Option<String> createCustomSkinFileOption(SkinTotemConfig config, SkinTotemPreviewRenderer renderer, Option<String> standardDollSkinDataOption) {
		SimpleOption.Builder<String> builder = SimpleOption.<String>startBuilder("custom_skin_file")
				.withCustomDescription(renderer)
				.withBinding(
						"",
						() -> config.getStandardSkinTotemSkinType() == SkinTotemSkinType.FILE_SKIN ? config.getStandardSkinTotemSkinValue() : "",
						(value) -> {
							config.setStandardSkinTotemSkinType(SkinTotemSkinType.FILE_SKIN);
							config.setStandardSkinTotemSkinValue(value);
							standardDollSkinDataOption.setAvailable(true);
							renderer.updateDoll();
						}, true);

		List<String> skinFiles = CustomSkinLibrary.listSkinFiles();
		if (!skinFiles.isEmpty()) {
			String current = config.getStandardSkinTotemSkinValue();
			if (config.getStandardSkinTotemSkinType() == SkinTotemSkinType.FILE_SKIN && current != null && !current.isEmpty() && !skinFiles.contains(current)) {
				skinFiles.add(current);
			}
			List<String> choices = List.copyOf(skinFiles);
			builder.getOptionBuilder().controller((option) -> CyclingListControllerBuilder.create(option)
					.values(choices)
					.formatValue((path) -> Component.literal(CustomSkinLibrary.displayName(path))));
		} else {
			builder.withController();
		}

		return builder.build();
	}

	private static OptionGroup getStandardDollModelGroup(SkinTotemConfig defConfig, SkinTotemConfig config, SkinTotemPreviewRenderer renderer) {
		Option<Identifier> standardDollModelPathOption = SimpleOption.<Identifier>startBuilder("standard_doll_model_path")
				.withCustomDescription(renderer)
				.withBinding(config.getSelectedStandardSkinTotemModelValue(), config::getStandardSkinTotemModelValue, (value) -> {
					config.setStandardSkinTotemModelValue(value);
					renderer.updateDollState(true);
					for (SkinTotemData data : SkinTotemManager.getAllLoadedDolls()) {
						data.setShouldRecreateStandardModel(true);
					}
				}, true)
				.getOptionBuilder()
				.controller(SkinTotemModelControllerBuilder::create)
				.build();
		Option<SkinTotemArmsType> standardDollModelArmsTypeOption = SimpleOption.<SkinTotemArmsType>startBuilder("standard_doll_model_arms_type")
				.withCustomDescription(renderer)
				.withBinding(defConfig.getStandardSkinTotemArmsType(), config::getStandardSkinTotemArmsType, (value) -> {
					config.setStandardSkinTotemArmsType(value);
					renderer.updateDollState(false);
				}, true)
				.withController(SkinTotemArmsType.class)
				.build();

		standardDollModelPathOption.setAvailable(!config.isUseVanillaTotemModel());
		standardDollModelArmsTypeOption.setAvailable(!config.isUseVanillaTotemModel());

		Option<String> figuraAvatarOption = createFiguraAvatarOption(config, renderer);

		return SimpleGroup.startBuilder("standard_doll_model")
				.withCustomDescription(renderer)
				.options(
						standardDollModelPathOption,
						standardDollModelArmsTypeOption,
						figuraAvatarOption
				).build();
	}

	/**
	 * Выбор аватара Figura ({@code figura/avatars/<имя>/}): моделька куклы берётся из
	 * {@code .bbmodel} аватара, а скин — из {@code avatar.png} (slim-руки определяются по PNG).
	 */
	private static Option<String> createFiguraAvatarOption(SkinTotemConfig config, SkinTotemPreviewRenderer renderer) {
		SimpleOption.Builder<String> builder = SimpleOption.<String>startBuilder("figura_avatar")
				.withCustomDescription(renderer)
				.withBinding("", () -> "", (value) -> {
					if (value == null || value.isEmpty()) {
						return;
					}
					for (FiguraAvatars.Avatar avatar : FiguraAvatars.list()) {
						if (!avatar.name().equals(value)) {
							continue;
						}
						if (avatar.model() != null) {
							Identifier modelId = BlockBenchModelManager.registerFileModel(avatar.model());
							config.setStandardSkinTotemModelValue(modelId);
							for (SkinTotemData data : SkinTotemManager.getAllLoadedDolls()) {
								data.setShouldRecreateStandardModel(true);
							}
						}
						if (avatar.skin() != null) {
							config.setStandardSkinTotemSkinType(SkinTotemSkinType.FILE_SKIN);
							config.setStandardSkinTotemSkinValue(avatar.skin().toAbsolutePath().toString());
						}
						renderer.updateDollState(true);
						break;
					}
				}, true);

		List<String> avatars = new ArrayList<>();
		avatars.add("");
		FiguraAvatars.list().forEach((avatar) -> avatars.add(avatar.name()));

		if (avatars.size() > 1) {
			List<String> choices = List.copyOf(avatars);
			builder.getOptionBuilder().controller((option) -> CyclingListControllerBuilder.create(option)
					.values(choices)
					.formatValue((name) -> name == null || name.isEmpty()
							? SkinTotem.text("modmenu.option.activation_emote.none")
							: Component.literal("Figura · " + name)));
		} else {
			builder.withController();
		}

		return builder.build();
	}
}
