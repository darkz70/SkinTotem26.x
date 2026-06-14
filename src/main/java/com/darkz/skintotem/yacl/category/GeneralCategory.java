package com.darkz.skintotem.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.*;
import net.fabricmc.loader.api.FabricLoader;
import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.config.other.vector.Vec2i;
import com.darkz.skintotem.config.rendering.RenderingConfig;
import com.darkz.skintotem.config.totem.*;
import com.darkz.skintotem.doll.model.SkinTotemModel;
import com.darkz.skintotem.utils.*;
import net.minecraft.resources.Identifier;
import org.slf4j.*;
import static com.darkz.skintotem.utils.CodecUtils.option;

@Getter
@Setter
@AllArgsConstructor
public class SkinTotemConfig {
    public boolean isModEnabled() { return modEnabled; }
    public boolean isDebugLogEnabled() { return debugLogEnabled; }
    public boolean isSupportOtherModsTotems() { return supportOtherModsTotems; }

	public static final Codec<SkinTotemConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			option("mod_enabled", true, Codec.BOOL, SkinTotemConfig::isModEnabled),
			option("debug_log_enabled", false, Codec.BOOL, SkinTotemConfig::isDebugLogEnabled),
			option("rendering_config", RenderingConfig.getNewInstance(), RenderingConfig.CODEC, SkinTotemConfig::getRenderingConfig),
			option("standard_doll_skin_data", "", Codec.STRING, SkinTotemConfig::getStandardSkinTotemSkinValue),
			option("standard_doll_skin_type", SkinTotemSkinType.STEVE, SkinTotemSkinType.CODEC, SkinTotemConfig::getStandardSkinTotemSkinType),
			option("selected_standard_doll_model_data", SkinTotemModel.NONE, Identifier.CODEC, SkinTotemConfig::getSelectedStandardSkinTotemModelValue),
			option("standard_doll_model_data", SkinTotemModel.TWO_D_MODEL_ID, Identifier.CODEC, SkinTotemConfig::getStandardSkinTotemModelValue),
			option("standard_doll_model_arms_type", SkinTotemArmsType.WIDE, SkinTotemArmsType.CODEC, SkinTotemConfig::getStandardSkinTotemArmsType),
			option("tag_button_pos", new Vec2i(155, 48), Vec2i.CODEC, SkinTotemConfig::getTagButtonPos),
			option("use_vanilla_totem_model", false, Codec.BOOL, SkinTotemConfig::isUseVanillaTotemModel),
			Codec.INT.optionalFieldOf("better_tag_menu_tooltip_size")
					.xmap(o -> o.orElse(60), Optional::of)
					.forGetter(SkinTotemConfig::getBetterTagMenuTooltipSize),
			option("tag_menu_tooltip_model_scale", 1.0F, Codec.FLOAT, SkinTotemConfig::getTagMenuTooltipModelScale),
			option("executor_threads_count", 6, Codec.INT, SkinTotemConfig::getParallelTasksCount),
			option("first_run", true, Codec.BOOL, SkinTotemConfig::isFirstRun),
			option("first_run_temp", true, Codec.BOOL, SkinTotemConfig::isFirstRunTemp),
			option("support_other_mods_totems", true, Codec.BOOL, SkinTotemConfig::isSupportOtherModsTotems),
			option("auto_refresh_enabled", false, Codec.BOOL, SkinTotemConfig::isAutoRefreshEnabled),
			option("auto_refresh_interval_minutes", 5, Codec.INT, SkinTotemConfig::getAutoRefreshIntervalMinutes)
	).apply(instance, SkinTotemConfig::new));

	private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve(SkinTotem.MOD_ID + ".json5").toFile();
	private static final Logger LOGGER = LoggerFactory.getLogger(SkinTotem.MOD_NAME + "/Config");
	private static SkinTotemConfig INSTANCE;

	private boolean modEnabled;
	private boolean debugLogEnabled;
	private RenderingConfig renderingConfig;
	private String standardSkinTotemSkinValue;
	private SkinTotemSkinType standardSkinTotemSkinType;
	private Identifier selectedStandardSkinTotemModelValue;
	private Identifier standardSkinTotemModelValue;
	private SkinTotemArmsType standardSkinTotemArmsType;
	private Vec2i tagButtonPos;
	private boolean useVanillaTotemModel;
	private int betterTagMenuTooltipSize;
	private float tagMenuTooltipModelScale;
	private int parallelTasksCount;
	private boolean firstRun;
	private boolean firstRunTemp;
	private boolean supportOtherModsTotems;
	private boolean autoRefreshEnabled;
	private int autoRefreshIntervalMinutes;

	public Identifier getSelectedStandardSkinTotemModelValue() {
		return this.selectedStandardSkinTotemModelValue == SkinTotemModel.NONE ? this.selectedStandardSkinTotemModelValue = this.standardSkinTotemModelValue : this.selectedStandardSkinTotemModelValue;
	}

	private SkinTotemConfig() {
		throw new IllegalArgumentException();
	}

	public static SkinTotemConfig getInstance() {
		return INSTANCE == null ? reload() : INSTANCE;
	}

	public static SkinTotemConfig reload() {
		return INSTANCE = SkinTotemConfig.read();
	}

	public static SkinTotemConfig getNewInstance() {
		return CodecUtils.parseNewInstanceHacky(CODEC);
	}

	private static SkinTotemConfig read() {
		return ConfigUtils.readConfig(CODEC, CONFIG_FILE, LOGGER);
	}

	public void saveAsync() {
		CompletableFuture.runAsync(this::save);
	}

	public void save() {
		ConfigUtils.saveConfig(this, CODEC, CONFIG_FILE, LOGGER);
	}
}
