package com.darkz.skintotem.doll.data;

import java.util.Optional;
import lombok.*;
import com.darkz.skintotem.doll.model.SkinTotemModel;
import com.darkz.skintotem.doll.renderer.special.SkinTotemGuiElementRenderer;
import com.darkz.skintotem.model.base.MModel;
import com.darkz.skintotem.model.bb.manager.BlockBenchModelManager;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset.Texture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.*;
import org.jetbrains.annotations.*;

@Getter
@Setter
public class SkinTotemData {

	private boolean shouldRecreateStandardModel;

	@Nullable
	private SkinTotemModel standardModel;
	@Nullable
	private SkinTotemModel frameModel;

	@NotNull
	private SkinTotemRenderProperties renderProperties = new SkinTotemRenderProperties();

	public SkinTotemData(@Nullable String nickname, @NotNull SkinTotemSprites sprites) {
		this.renderProperties.refresh(sprites);
		this.renderProperties.setNickname(nickname);
	}

	public SkinTotemData(@NotNull SkinTotemRenderProperties properties) {
		this.renderProperties.copyFrom(properties);
	}

	public static SkinTotemData create(@Nullable String nickname) {
		return new SkinTotemData(nickname, SkinTotemSprites.create());
	}

	public SkinTotemSprites getStandardSprites() {
		return this.renderProperties.getStandardSprites();
	}

	@Nullable
	public String getNickname() {
		return this.renderProperties.getNickname();
	}

	public void setStandardMModel(@NotNull Identifier modelId) {
		BlockBenchModelManager.consumeModelById(modelId, this::setStandardMModel);
	}

	public void setStandardMModel(@Nullable MModel model) {
		this.renderProperties.setStandardMModel(model);
		if (model == null) {
			return;
		}
		this.standardModel = this.renderProperties.createStandardModel();
	}

	public void setFrameMModel(@NotNull Identifier id) {
		this.renderProperties.consumeFrameMModel(id, this::setFrameMModel);
	}

	public void setFrameMModel(@Nullable MModel frameMModel) {
		this.renderProperties.setFrameMModel(frameMModel);
	}

	@Nullable
	private SkinTotemModel getFrameModelBasedOnFrameMModel() {
		if (this.renderProperties.getFrameMModel() != null) {
			if (this.frameModel == null || !this.frameModel.getMain().equals(this.renderProperties.getFrameMModel())) {
				return this.frameModel = this.renderProperties.createFrameModel();
			}
			return this.frameModel;
		}
		return null;
	}

	public void clearAllFrameModelsCompletely() {
		this.clearFrameModel();
		this.renderProperties.clearCachedFrameMModels();
	}

	public void clearFrameModel() {
		if (this.frameModel != null) {
			this.frameModel.resetPartsVisibility();
			this.frameModel = null;
		}
	}

	public void clearFrameSprites() {
		this.renderProperties.setFrameSprites(null);
	}

	@NotNull
	public SkinTotemModel getModelToRender() {
		SkinTotemModel tempModel = this.getFrameModelBasedOnFrameMModel();
		if (tempModel != null) {
			return tempModel;
		}

		if (this.standardModel != null && !this.shouldRecreateStandardModel) {
			return this.standardModel;
		}

		this.setStandardMModel(SkinTotemModel.createDollModel());

		if (this.shouldRecreateStandardModel) {
			this.shouldRecreateStandardModel = false;
		}

		return this.standardModel;
	}

	@NotNull
	public SkinTotemSprites getSpritesToRender() {
		return this.renderProperties.getFrameSprites() == null ? this.renderProperties.getStandardSprites() : this.renderProperties.getFrameSprites();
	}

	public void setSprites(@NotNull SkinTotemSprites sprites) {
		this.renderProperties.setStandardSprites(sprites);
	}

	@SuppressWarnings("unused")
	public void setFrameSprites(@Nullable SkinTotemSprites frameSprites) {
		this.renderProperties.setFrameSprites(frameSprites);
	}

	public void setFrameSprites(@Nullable AbstractClientPlayer playerEntity) {
		if (playerEntity == null) {
			return;
		}

		PlayerSkin skinTextures = playerEntity.getSkin();
		Identifier skinTexture = skinTextures.body().texturePath();
		Identifier capeTexture = Optional.of(skinTextures).map(PlayerSkin::cape).map(Texture::texturePath).orElse(null);
		Identifier elytraTexture = Optional.of(skinTextures).map(PlayerSkin::cape).map(Texture::texturePath).orElse(null);
		boolean slim = skinTextures.model() == PlayerModelType.SLIM;

		this.renderProperties.setFrameSprites(skinTexture, capeTexture, elytraTexture, slim, true);
	}

	@NotNull
	public SkinTotemData copy() {
		return new SkinTotemData(this.renderProperties);
	}

	@NotNull
	public SkinTotemData refreshAndApplyRenderProperties() {
		return this.refreshRenderProperties().applyRenderProperties();
	}

	@NotNull
	public SkinTotemData refreshRenderProperties() {
		// Make sure it's cleared
		this.clearFrameModel();
		this.clearFrameSprites();
		this.getModelToRender().resetPartsVisibility();
		this.renderProperties.refresh();
		return this;
	}

	@NotNull
	public SkinTotemData applyRenderProperties() {
		this.renderProperties.applyToModel(this.getModelToRender());
		return this;
	}


	@NotNull
	public SkinTotemGuiElementRenderer getGuiRenderer() {
		return SkinTotemGuiElementRenderer.getRenderer(this.renderProperties);
	}

}
