package com.darkz.skintotem.doll.manager;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.InputStream;
import java.nio.file.*;
import java.util.concurrent.CompletableFuture;
import com.darkz.skintotem.SkinTotem;
import com.darkz.skintotem.atlas.manager.SkinTotemAtlasSpriteManager;
import com.darkz.skintotem.client.SkinTotemClient;
import com.darkz.skintotem.config.SkinTotemConfig;
import com.darkz.skintotem.config.totem.SkinTotemSkinType;
import com.darkz.skintotem.doll.data.*;
import com.darkz.skintotem.skin.provider.extended.MojangSkinProvider;
import com.darkz.skintotem.skin.provider.extended.TLauncherSkinProvider;
import com.darkz.skintotem.skin.provider.extended.ElyBySkinProvider;
import com.darkz.skintotem.utils.texture.*;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.*;

public class StandardSkinTotemManager {

	@Nullable
	private static SkinTotemData DEFAULT_DOLL;

	@NotNull
	public static SkinTotemData getStandardDoll() {
		if (DEFAULT_DOLL == null) {
			return initializeStandardDollData();
		}
		return DEFAULT_DOLL;
	}

	public static SkinTotemData initializeStandardDollData() {
		DEFAULT_DOLL = overrideWithConfigValues(loadStandardDoll());
		return DEFAULT_DOLL;
	}

	public static SkinTotemData updateDoll(boolean recreateModel) {
		SkinTotemData standardDoll = getStandardDoll();
		overrideWithConfigValues(standardDoll);
		standardDoll.setShouldRecreateStandardModel(recreateModel);
		return standardDoll.refreshAndApplyRenderProperties();
	}

	public static SkinTotemData overrideWithConfigValues(SkinTotemData data) {
		SkinTotemConfig config = SkinTotemConfig.getInstance();
		data.getStandardSprites().setStandardArmsType(config.getStandardSkinTotemArmsType());
		return data;
	}

	@NotNull
	public static SkinTotemData loadStandardDoll() {
		SkinTotemConfig config = SkinTotemConfig.getInstance();
		SkinTotemSkinType skinTotemSkin = config.getStandardSkinTotemSkinType();
		String data = config.getStandardSkinTotemSkinValue();

		if (skinTotemSkin == SkinTotemSkinType.STEVE || skinTotemSkin == SkinTotemSkinType.HOLDING_PLAYER || data == null || data.isEmpty()) {
			return getSteveDoll();
		}

		return switch (skinTotemSkin) {
			    case PLAYER -> loadPlayerSkin(data);
                case URL_SKIN -> loadUrlSkin(data);
                case FILE_SKIN -> loadFileSkin(data);
                case TLAUNCHER -> TLauncherSkinProvider.getInstance().getOrLoadDoll(data);
                case ELY_BY -> ElyBySkinProvider.getInstance().getOrLoadDoll(data);
                default -> getSteveDoll();
		};
	}

	public static @NotNull SkinTotemData getSteveDoll() {
		SkinTotemData skinTotemData = SkinTotemData.create(null);
		skinTotemData.getStandardSprites().setState(LoadingState.DOWNLOADED);
		return skinTotemData;
	}

	public static SkinTotemData loadFileSkin(@NotNull String data) {
		SkinTotemData skinTotemData = SkinTotemData.create(null);
		SkinTotemSprites textures = skinTotemData.getStandardSprites();
		textures.setState(LoadingState.DOWNLOADING);

		CompletableFuture.runAsync(() -> {
			Identifier id = SkinTotem.getDollTextureId("file/%s".formatted(Math.abs(data.hashCode())));

			try (InputStream inputStream = Files.newInputStream(Path.of(data))) {
				NativeImage nativeImage = NativeImage.read(inputStream);

				SkinTotemAtlasSpriteManager.registerSpecialSkinSprite(id, nativeImage, true, (sprite) -> {
					textures.setSkinSprite(sprite);
					textures.setState(LoadingState.DOWNLOADED);
				});

			} catch (NoSuchFileException e) {
				textures.setState(LoadingState.CRITICAL_ERROR);
			} catch (Exception e) {
				SkinTotemClient.LOGGER.error("Failed to load skin from file at \"{}\":", data, e);
				textures.setState(LoadingState.CRITICAL_ERROR);
			}
		});

		return skinTotemData;
	}

	public static SkinTotemData loadUrlSkin(@NotNull String data) {
		SkinTotemData skinTotemData = SkinTotemData.create(null);
		SkinTotemSprites textures = skinTotemData.getStandardSprites();
		textures.setState(LoadingState.DOWNLOADING);

		CompletableFuture.runAsync(() -> {
			Identifier id = SkinTotem.getDollTextureId("url/%s".formatted(Math.abs(data.hashCode())));

			FailedAction onFailed = (throwable) -> {
				textures.setState(LoadingState.CRITICAL_ERROR);
				SkinTotemClient.LOGGER.warn("Failed to download standard doll url skin:", throwable);
			};

			SuccessAction onSuccess = (sprite) -> {
				textures.setSkinSprite(sprite);
				textures.setState(LoadingState.DOWNLOADED);
			};

			PlayerSkinUtils.downloadSkin(data, id, onSuccess, onFailed, false);
		});

		return skinTotemData;
	}

	public static SkinTotemData loadPlayerSkin(@NotNull String data) {
		if (MojangSkinProvider.getInstance().canProcess(data)) {
				SkinTotemData skinTotemData = MojangSkinProvider.getInstance().createNewDoll(data);
				
				// Пытаемся загрузить с Mojang
				MojangSkinProvider.getInstance().loadDoll(data, true, skinTotemData);
				
				// Если скин не загрузился (например, пиратский ник), пробуем TLauncher и Ely.by
				CompletableFuture.runAsync(() -> {
					try {
						// Ждем немного, чтобы Mojang API успел ответить (обычно это быстро)
						Thread.sleep(1000); 
						if (skinTotemData.getStandardSprites().getState() != LoadingState.DOWNLOADED) {
							SkinTotemClient.LOGGER.info("[SkinTotem] Mojang skin not found for {}, trying TLauncher...", data);
							TLauncherSkinProvider.getInstance().loadDoll(data, true, skinTotemData);
							
							Thread.sleep(1000);
							if (skinTotemData.getStandardSprites().getState() != LoadingState.DOWNLOADED) {
								SkinTotemClient.LOGGER.info("[SkinTotem] TLauncher skin not found for {}, trying Ely.by...", data);
								ElyBySkinProvider.getInstance().loadDoll(data, true, skinTotemData);
							}
						}
					} catch (Exception ignored) {}
				});
				
				return skinTotemData;
			}
			return getSteveDoll();
	}
}
