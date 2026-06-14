package com.darkz.skintotem.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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

    private static class DataGroup1 {
        boolean modEnabled;
        boolean debugLogEnabled;
        RenderingConfig renderingConfig;
        String standardSkinTotemSkinValue;
        SkinTotemSkinType standardSkinTotemSkinType;
        Identifier selectedStandardSkinTotemModelValue;
        Identifier standardSkinTotemModelValue;
        SkinTotemArmsType standardSkinTotemArmsType;
        Vec2i tagButtonPos;

        DataGroup1(boolean modEnabled, boolean debugLogEnabled, RenderingConfig renderingConfig, String standardSkinTotemSkinValue, SkinTotemSkinType standardSkinTotemSkinType, Identifier selectedStandardSkinTotemModelValue, Identifier standardSkinTotemModelValue, SkinTotemArmsType standardSkinTotemArmsType, Vec2i tagButtonPos) {
            this.modEnabled = modEnabled;
            this.debugLogEnabled = debugLogEnabled;
            this.renderingConfig = renderingConfig;
            this.standardSkinTotemSkinValue = standardSkinTotemSkinValue;
            this.standardSkinTotemSkinType = standardSkinTotemSkinType;
            this.selectedStandardSkinTotemModelValue = selectedStandardSkinTotemModelValue;
            this.standardSkinTotemModelValue = standardSkinTotemModelValue;
            this.standardSkinTotemArmsType = standardSkinTotemArmsType;
            this.tagButtonPos = tagButtonPos;
        }

        static final MapCodec<DataGroup1> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                option("mod_enabled", true, Codec.BOOL, d -> d.modEnabled),
                option("debug_log_enabled", false, Codec.BOOL, d -> d.debugLogEnabled),
                option("rendering_config", RenderingConfig.getNewInstance(), RenderingConfig.CODEC, d -> d.renderingConfig),
                option("standard_doll_skin_data", "", Codec.STRING, d -> d.standardSkinTotemSkinValue),
                option("standard_doll_skin_type", SkinTotemSkinType.STEVE, SkinTotemSkinType.CODEC, d -> d.standardSkinTotemSkinType),
                option("selected_standard_doll_model_data", SkinTotemModel.NONE, Identifier.CODEC, d -> d.selectedStandardSkinTotemModelValue),
                option("standard_doll_model_data", SkinTotemModel.TWO_D_MODEL_ID, Identifier.CODEC, d -> d.standardSkinTotemModelValue),
                option("standard_doll_model_arms_type", SkinTotemArmsType.WIDE, SkinTotemArmsType.CODEC, d -> d.standardSkinTotemArmsType),
                option("tag_button_pos", new Vec2i(155, 48), Vec2i.CODEC, d -> d.tagButtonPos)
        ).apply(instance, DataGroup1::new));
    }

    private static class DataGroup2 {
        boolean useVanillaTotemModel;
        int betterTagMenuTooltipSize;
        float tagMenuTooltipModelScale;
        int parallelTasksCount;
        boolean firstRun;
        boolean firstRunTemp;
        boolean supportOtherModsTotems;
        boolean autoRefreshEnabled;
        int autoRefreshIntervalMinutes;

        DataGroup2(boolean useVanillaTotemModel, int betterTagMenuTooltipSize, float tagMenuTooltipModelScale, int parallelTasksCount, boolean firstRun, boolean firstRunTemp, boolean supportOtherModsTotems, boolean autoRefreshEnabled, int autoRefreshIntervalMinutes) {
            this.useVanillaTotemModel = useVanillaTotemModel;
            this.betterTagMenuTooltipSize = betterTagMenuTooltipSize;
            this.tagMenuTooltipModelScale = tagMenuTooltipModelScale;
            this.parallelTasksCount = parallelTasksCount;
            this.firstRun = firstRun;
            this.firstRunTemp = firstRunTemp;
            this.supportOtherModsTotems = supportOtherModsTotems;
            this.autoRefreshEnabled = autoRefreshEnabled;
            this.autoRefreshIntervalMinutes = autoRefreshIntervalMinutes;
        }

        static final MapCodec<DataGroup2> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                option("use_vanilla_totem_model", false, Codec.BOOL, d -> d.useVanillaTotemModel),
                Codec.INT.optionalFieldOf("better_tag_menu_tooltip_size")
                        .xmap(o -> o.orElse(60), Optional::of)
                        .forGetter(d -> d.betterTagMenuTooltipSize),
                option("tag_menu_tooltip_model_scale", 1.0F, Codec.FLOAT, d -> d.tagMenuTooltipModelScale),
                option("executor_threads_count", 6, Codec.INT, d -> d.parallelTasksCount),
                option("first_run", true, Codec.BOOL, d -> d.firstRun),
                option("first_run_temp", true, Codec.BOOL, d -> d.firstRunTemp),
                option("support_other_mods_totems", true, Codec.BOOL, d -> d.supportOtherModsTotems),
                option("auto_refresh_enabled", false, Codec.BOOL, d -> d.autoRefreshEnabled),
                option("auto_refresh_interval_minutes", 5, Codec.INT, d -> d.autoRefreshIntervalMinutes)
        ).apply(instance, DataGroup2::new));
    }

    public static final Codec<SkinTotemConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DataGroup1.CODEC.forGetter(c -> new DataGroup1(c.modEnabled, c.debugLogEnabled, c.renderingConfig, c.standardSkinTotemSkinValue, c.standardSkinTotemSkinType, c.selectedStandardSkinTotemModelValue, c.standardSkinTotemModelValue, c.standardSkinTotemArmsType, c.tagButtonPos)),
            DataGroup2.CODEC.forGetter(c -> new DataGroup2(c.useVanillaTotemModel, c.betterTagMenuTooltipSize, c.tagMenuTooltipModelScale, c.parallelTasksCount, c.firstRun, c.firstRunTemp, c.supportOtherModsTotems, c.autoRefreshEnabled, c.autoRefreshIntervalMinutes))
    ).apply(instance, (dg1, dg2) -> new SkinTotemConfig(
            dg1.modEnabled, dg1.debugLogEnabled, dg1.renderingConfig, dg1.standardSkinTotemSkinValue, dg1.standardSkinTotemSkinType, dg1.selectedStandardSkinTotemModelValue, dg1.standardSkinTotemModelValue, dg1.standardSkinTotemArmsType, dg1.tagButtonPos,
            dg2.useVanillaTotemModel, dg2.betterTagMenuTooltipSize, dg2.tagMenuTooltipModelScale, dg2.parallelTasksCount, dg2.firstRun, dg2.firstRunTemp, dg2.supportOtherModsTotems, dg2.autoRefreshEnabled, dg2.autoRefreshIntervalMinutes
    )));

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
