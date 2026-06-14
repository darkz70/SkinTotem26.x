package com.darkz.skintotem.config.cache;

import com.mojang.serialization.Codec;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import lombok.*;
import com.darkz.skintotem.platform.PlatformHelper;
import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.utils.*;
import net.minecraft.core.UUIDUtil;
import org.slf4j.*;
import static com.mojang.serialization.codecs.RecordCodecBuilder.create;
import static com.darkz.skintotem.utils.CodecUtils.option;

@Setter
@Getter
public class KnownPlayerUUIDsConfig {

	public static final Codec<KnownPlayerUUIDsConfig> CODEC = create((instance) -> instance.group(
			option("cache", new HashMap<>(), Codec.unboundedMap(Codec.STRING, UUIDUtil.AUTHLIB_CODEC), KnownPlayerUUIDsConfig::getCache)
	).apply(instance, (map) -> {
		return new KnownPlayerUUIDsConfig(new ConcurrentHashMap<>(map));
	}));
	private static final File CONFIG_FILE = PlatformHelper.getConfigDir().resolve(SkinTotem.MOD_ID + "-known-player-uuids" + ".json5").toFile();
	private static final Logger LOGGER = LoggerFactory.getLogger(SkinTotem.MOD_NAME + "/KnownPlayerUUIDsConfig");
	private static KnownPlayerUUIDsConfig INSTANCE;
	private final Map<String, UUID> cache;
	private transient boolean dirty;

	public KnownPlayerUUIDsConfig(Map<String, UUID> cache) {
		this.cache = cache;
	}

	private KnownPlayerUUIDsConfig() {
		throw new IllegalArgumentException();
	}

	public static KnownPlayerUUIDsConfig getInstance() {
		return INSTANCE == null ? reload() : INSTANCE;
	}

	public static KnownPlayerUUIDsConfig reload() {
		return INSTANCE = read();
	}

	public static KnownPlayerUUIDsConfig getNewInstance() {
		return CodecUtils.parseNewInstanceHacky(CODEC);
	}

	private static KnownPlayerUUIDsConfig read() {
		return ConfigUtils.readConfig(CODEC, CONFIG_FILE, LOGGER);
	}

	public void save() {
		ConfigUtils.saveConfig(this, CODEC, CONFIG_FILE, LOGGER);
	}
}


