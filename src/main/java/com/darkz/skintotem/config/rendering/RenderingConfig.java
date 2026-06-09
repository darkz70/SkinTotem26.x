package com.darkz.skintotem.config.rendering;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import lombok.*;
import com.darkz.skintotem.utils.CodecUtils;
import static com.darkz.skintotem.utils.CodecUtils.option;

@Getter
@Setter
public class RenderingConfig {

	public static final Codec<RenderingConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			option("right_hand", HandRenderingConfig.getNewInstance(), HandRenderingConfig.CODEC, RenderingConfig::getRightHandConfig),
			option("left_hand", HandRenderingConfig.getNewInstance(), HandRenderingConfig.CODEC, RenderingConfig::getLeftHandConfig)
	).apply(instance, RenderingConfig::new));

	private HandRenderingConfig rightHandConfig;
	private HandRenderingConfig leftHandConfig;

	public RenderingConfig(HandRenderingConfig rightHandConfig, HandRenderingConfig leftHandConfig) {
		this.rightHandConfig = rightHandConfig;
		this.leftHandConfig  = leftHandConfig;
	}

	public static Supplier<RenderingConfig> getNewInstance() {
		return () -> CodecUtils.parseNewInstanceHacky(CODEC);
	}

}
