package com.darkz.skintotem.atlas.stitch;

import com.darkz.skintotem.atlas.AtlasSprite;

public interface OnSpriteUploaded {

	void onUploaded(AtlasSprite sprite);

	default OnSpriteUploaded then(OnSpriteUploaded then) {
		return (sprite) -> {
			this.onUploaded(sprite);
			then.onUploaded(sprite);
		};
	}
}
