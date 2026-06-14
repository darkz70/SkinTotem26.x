package com.darkz.skintotem.config;

import com.mojang.datafixers.util.Pair;
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

    private static final Codec<Pair<Pair<Boolean, Boolean>, Pair<RenderingConfig, String>>> GROUP_1 = RecordCodecBuilder.create(instance -> instance.group(
            option("mod_enabled", true, Codec.BOOL, p -> p.getFirst().getFirst()),
            option("debug_log_enabled", false, Codec.BOOL, p -> p.getFirst().getSecond()),
            option("rendering_config", RenderingConfig.getNewInstance(), RenderingConfig.CODEC, p -> p.getSecond().getFirst()),
            option("standard_doll_skin_data", "", Codec.STRING, p -> p.getSecond().getSecond())
    ).apply(instance, (a, b, c, d) -> Pair.of(Pair.of(a, b), Pair.of(c, d))));

    private static final Codec<Pair<Pair<SkinTotemSkinType, Identifier>, Pair<Identifier, SkinTotemArmsType>>> GROUP_2 = RecordCodecBuilder.create(instance -> instance.group(
            option("standard_doll_skin_type", SkinTotemSkinType.STEVE, SkinTotemSkinType.CODEC, p -> p.getFirst().getFirst()),
            option("selected_standard_doll_model_data", SkinTotemModel.NONE, Identifier.CODEC, p -> p.getFirst().getSecond()),
            option("standard_doll_model_data", SkinTotemModel.TWO_D_MODEL_ID, Identifier.CODEC, p -> p.getSecond().getFirst()),
            option("standard_doll_model_arms_type", SkinTotemArmsType.WIDE, SkinTotemArmsType.CODEC, p -> p.getSecond().getSecond())
    ).apply(instance, (a, b, c, d) -> Pair.of(Pair.of(a, b), Pair.of(c, d))));

    private static final Codec<Pair<Pair<Vec2i, Boolean>, Pair<Integer, Float>>> GROUP_3 = RecordCodecBuilder.create(instance -> instance.group(
            option("tag_button_pos", new Vec2i(155, 48), Vec2i.CODEC, p -> p.getFirst().getFirst()),
            option("use_vanilla_totem_model", false, Codec.BOOL, p -> p.getFirst().getSecond()),
            Codec.INT.optionalFieldOf("better_tag_menu_tooltip_size")
                    .xmap(o -> o.orElse(60), Optional::of)
                    .forGetter(p -> p.getSecond().getFirst()),
            option("tag_menu_tooltip_model_scale", 1.0F, Codec.FLOAT, p -> p.getSecond().getSecond())
    ).apply(instance, (a, b, c, d) -> Pair.of(Pair.of(a, b), Pair.of(c, d))));

    private static final Codec<Pair<Pair<Integer, Boolean>, Pair<Boolean, Boolean>>> GROUP_4 = RecordCodecBuilder.create(instance -> instance.group(
            option("executor_threads_count", 6, Codec.INT, p -> p.getFirst().getFirst()),
            option("first_run", true, Codec.BOOL, p -> p.getFirst().getSecond()),
            option("first_run_temp", true, Codec.BOOL, p -> p.getSecond().getFirst()),
            option("support_other_mods_totems", true, Codec.BOOL, p -> p.getSecond().getSecond())
    ).apply(instance, (a, b, c, d) -> Pair.of(Pair.of(a, b), Pair.of(c, d))));

    private static final Codec<Pair<Boolean, Integer>> GROUP_5 = RecordCodecBuilder.create(instance -> instance.group(
            option("auto_refresh_enabled", false, Codec.BOOL, Pair::getFirst),
            option("auto_refresh_interval_minutes", 5, Codec.INT, Pair::getSecond)
    ).apply(instance, Pair::of));

    public static final Codec<SkinTotemConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GROUP_1.forGetter(c -> Pair.of(Pair.of(c.modEnabled, c.debugLogEnabled), Pair.of(c.renderingConfig, c.standardSkinTotemSkinValue))),
            GROUP_2.forGetter(c -> Pair.of(Pair.of(c.standardSkinTotemSkinType, c.selectedStandardSkinTotemModelValue), Pair.of(c.standardSkinTotemModelValue, c.standardSkinTotemArmsType))),
            GROUP_3.forGetter(c -> Pair.of(Pair.of(c.tagButtonPos, c.useVanillaTotemModel), Pair.of(c.betterTagMenuTooltipSize, c.tagMenuTooltipModelScale))),
            GROUP_4.forGetter(c -> Pair.of(Pair.of(c.parallelTasksCount, c.firstRun), Pair.of(c.firstRunTemp, c.supportOtherModsTotems))),
            GROUP_5.forGetter(c -> Pair.of(c.autoRefreshEnabled, c.autoRefreshIntervalMinutes))
    ).apply(instance, (g1, g2, g3, g4, g5) -> new SkinTotemConfig(
            g1.getFirst().getFirst(), g1.getFirst().getSecond(), g1.getSecond().getFirst(), g1.getSecond().getSecond(),
            g2.getFirst().getFirst(), g2.getFirst().getSecond(), g2.getSecond().getFirst(), g2.getSecond().getSecond(),
            g3.getFirst().getFirst(), g3.getFirst().getSecond(), g3.getSecond().getFirst(), g3.getSecond().getSecond(),
            g4.getFirst().getFirst(), g4.getFirst().getSecond(), g4.getSecond().getFirst(), g4.getSecond().getSecond(),
            g5.getFirst(), g5.getSecond()
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
