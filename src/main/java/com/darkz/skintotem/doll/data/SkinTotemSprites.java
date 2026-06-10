package com.darkz.skintotem.doll.data;

import lombok.*;
import com.darkz.skintotem.atlas.*;
import com.darkz.skintotem.atlas.manager.*;
import com.darkz.skintotem.config.totem.SkinTotemArmsType;
import net.minecraft.core.ClientAsset.Texture;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.*;
import static com.darkz.skintotem.atlas.manager.SkinTotemAtlasSpriteManager.ELYTRA_SPRITE;
import static com.darkz.skintotem.atlas.manager.SkinTotemAtlasSpriteManager.STEVE_SKIN_SPRITE;


@Getter
@Setter
@AllArgsConstructor
public class SkinTotemSprites {

	@NotNull
	private LoadingState state = LoadingState.NOT_DOWNLOADED;

	@Nullable
	private AtlasSprite skinSprite;
	@Nullable
	private AtlasSprite capeSprite;
	@Nullable
	private AtlasSprite elytraSprite;

	private SkinTotemArmsType standardArmsType;
	private SkinTotemArmsType armsType;

	public SkinTotemSprites(@Nullable AtlasSprite skinSprite, @Nullable AtlasSprite capeSprite, @Nullable AtlasSprite elytraSprite, SkinTotemArmsType armsType) {
		this.skinSprite   = skinSprite;
		this.capeSprite   = capeSprite;
		this.elytraSprite = elytraSprite;
		this.armsType     = armsType;
	}

	public static SkinTotemSprites create() {
		return new SkinTotemSprites(null, null, null, SkinTotemArmsType.WIDE);
	}

	public static SkinTotemSprites of(net.minecraft.client.player.AbstractClientPlayer player) {
		return of(player.getSkin());
	}

	public static SkinTotemSprites of(net.minecraft.world.entity.player.PlayerSkin skinTextures) {
		Texture cape = skinTextures.cape();
		Texture elytra = skinTextures.elytra();
		return of(skinTextures.body().texturePath(), cape == null ? null : cape.texturePath(), elytra == null ? null : elytra.texturePath(), skinTextures.model() == net.minecraft.world.entity.player.PlayerModelType.SLIM, true);
	}


	public static SkinTotemSprites of(Identifier skinTexture, Identifier capeTexture, Identifier elytraTexture, boolean slim, boolean remapCape) {
		SkinTotemSprites skinTotemSprites = new SkinTotemSprites(null, null, null, SkinTotemArmsType.of(slim));

		if (skinTexture != null) {
			SkinTotemAtlasSpriteManager.registerSpecialSkinSprite(skinTexture, false, skinTotemSprites::setSkinSprite);
		}

		if (capeTexture != null) {
			if (remapCape) {
				RemappedAtlasSprite capeSprite = RemappedAtlasSprite.ofResource(capeTexture);
				SkinTotemAtlasSpriteManager.registerSpecialRemappedSprite(capeSprite);
				skinTotemSprites.setCapeSprite(capeSprite);
			} else {
				SkinTotemAtlasSpriteManager.registerSpecialSkinSprite(capeTexture, false, skinTotemSprites::setCapeSprite);
			}
		}

		if (elytraTexture != null) {
			SkinTotemAtlasSpriteManager.registerSpecialSkinSprite(elytraTexture, false, skinTotemSprites::setElytraSprite);
		}

		SkinTotemAtlasManager.stitchAndUpdate(SkinTotemAtlasSpriteManager.getSprites(), () -> {
			skinTotemSprites.setState(LoadingState.DOWNLOADED);
		});

		return skinTotemSprites;
	}

	public void setStandardArmsType(SkinTotemArmsType standardArmsType) {
		this.armsType         = standardArmsType;
		this.standardArmsType = standardArmsType;
	}

	public SkinTotemArmsType getArmsType() {
		return this.armsType == null ?
				this.standardArmsType == null ?
						SkinTotemArmsType.WIDE
						:
						this.standardArmsType
				:
				this.armsType;
	}

	public AtlasSprite getSkinSprite() {
		return this.skinSprite == null || !this.skinSprite.isUploaded() || this.state != LoadingState.DOWNLOADED ? STEVE_SKIN_SPRITE : this.skinSprite;
	}

	public AtlasSprite getElytraSprite() {
		AtlasSprite capeSprite = this.getCapeSprite();
		if (capeSprite != null && capeSprite.isUploaded()) {
			return capeSprite;
		}
		AtlasSprite elytraSprite = this.elytraSprite;
		if (elytraSprite != null && elytraSprite.isUploaded()) {
			return elytraSprite;
		}
		return ELYTRA_SPRITE;
	}

	public void destroy() {
		this.setState(LoadingState.DESTROYED);

		AtlasSprite skinSprite = this.skinSprite;
		AtlasSprite capeSprite = this.capeSprite;
		AtlasSprite elytraSprite = this.elytraSprite;

		this.skinSprite   = null;
		this.capeSprite   = null;
		this.elytraSprite = null;

		if (skinSprite != null) {
			skinSprite.closeAndUnregisterAnyway();
		}

		if (capeSprite != null) {
			capeSprite.close();
		}

		if (elytraSprite != null) {
			elytraSprite.close();
		}
	}

	public boolean canStartDownloading() {
		return this.state == LoadingState.ERROR || this.state == LoadingState.NOT_DOWNLOADED;
	}

	public SkinTotemSprites copy() {
		SkinTotemSprites skinTotemSprites = new SkinTotemSprites(this.skinSprite, this.capeSprite, this.elytraSprite, this.armsType);
		skinTotemSprites.setState(this.state);
		return skinTotemSprites;
	}
}
