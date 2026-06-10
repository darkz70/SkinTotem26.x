package com.darkz.skintotem.yacl.category;

import dev.isxander.yacl3.api.*;
import java.util.*;
import lombok.experimental.ExtensionMethod;
import com.darkz.skintotem.config.SkinTotemConfig;
import com.darkz.skintotem.config.totem.*;
import com.darkz.skintotem.doll.data.SkinTotemData;
import com.darkz.skintotem.doll.manager.SkinTotemManager;
import com.darkz.skintotem.extension.SimpleOptionExtension;
import com.darkz.skintotem.yacl.custom.controller.totem.SkinTotemModelControllerBuilder;
import com.darkz.skintotem.yacl.custom.renderer.SkinTotemPreviewRenderer;
import com.darkz.skintotem.yacl.custom.simple.main.*;
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

		return SimpleGroup.startBuilder("standard_doll_skin")
				.withCustomDescription(renderer)
				.options(
						standardDollSkinTypeOption,
						standardDollSkinDataOption
				).build();
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

		return SimpleGroup.startBuilder("standard_doll_model")
				.withCustomDescription(renderer)
				.options(
						standardDollModelPathOption,
						standardDollModelArmsTypeOption
				).build();
	}
}
