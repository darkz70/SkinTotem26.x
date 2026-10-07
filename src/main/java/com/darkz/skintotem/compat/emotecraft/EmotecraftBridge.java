package com.darkz.skintotem.compat.emotecraft;

import java.util.ArrayList;
import java.util.List;
import com.darkz.skintotem.doll.model.SkinTotemModel;
import com.darkz.skintotem.model.base.MModel;
import com.darkz.skintotem.model.base.MModelCollection;
import com.zigythebird.playeranim.accessors.IAnimatedAvatar;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
import com.zigythebird.playeranim.util.RenderUtil;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import io.github.kosmx.emotes.api.events.client.ClientEmoteAPI;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.player.AbstractClientPlayer;
import org.jetbrains.annotations.Nullable;

/**
 * Код, который напрямую обращается к Emotecraft и Player Animation Library.
 * <p>
 * Загружается только через {@link com.darkz.skintotem.compat.EmoteSupport}, когда моды установлены.
 * <p>
 * Поза берётся не из данных эмоции, а из уже посчитанного PAL состояния игрока
 * ({@code AvatarAnimManager}) — так кукла автоматически повторяет сглаживание, переходы
 * и любые другие анимации, которые библиотека накладывает на игрока.
 */
public final class EmotecraftBridge {

	/** Имена костей такие же, как у ванильной модели игрока в PAL. */
	private static final String BONE_HEAD = "head";
	private static final String BONE_TORSO = "torso";
	private static final String BONE_RIGHT_ARM = "right_arm";
	private static final String BONE_LEFT_ARM = "left_arm";
	private static final String BONE_RIGHT_LEG = "right_leg";
	private static final String BONE_LEFT_LEG = "left_leg";

	private static final List<ModelPart> POSED_PARTS = new ArrayList<>();
	private static final List<PartPose> SAVED_POSES = new ArrayList<>();

	private EmotecraftBridge() {
	}

	/**
	 * @return {@code true}, если игрок сейчас проигрывает анимацию и поза куклы была изменена
	 */
	public static boolean applyEmotePose(AbstractClientPlayer holder, SkinTotemModel model) {
		if (!(holder instanceof IAnimatedAvatar animated)) {
			return false;
		}

		AvatarAnimManager manager = animated.playerAnimLib$getAnimManager();
		if (manager == null || !manager.isActive()) {
			return false;
		}

		boolean slim = model.isSlim();

		applyBone(manager, model.getHead(), BONE_HEAD);
		applyBone(manager, model.getBody(), BONE_TORSO);
		applyBone(manager, slim ? model.getRightArmSlim() : model.getRightArmWide(), BONE_RIGHT_ARM);
		applyBone(manager, slim ? model.getLeftArmSlim() : model.getLeftArmWide(), BONE_LEFT_ARM);
		applyBone(manager, model.getRightLeg(), BONE_RIGHT_LEG);
		applyBone(manager, model.getLeftLeg(), BONE_LEFT_LEG);

		return !POSED_PARTS.isEmpty();
	}

	/** Возвращает позы, сохранённые в {@link #applyEmotePose}. */
	public static void restorePose() {
		for (int i = POSED_PARTS.size() - 1; i >= 0; i--) {
			POSED_PARTS.get(i).loadPose(SAVED_POSES.get(i));
		}
		POSED_PARTS.clear();
		SAVED_POSES.clear();
	}

	/** Находит эмоцию по названию среди загруженных в Emotecraft и проигрывает её. */
	public static boolean playEmoteByName(String name) {
		Animation found = null;

		for (Animation animation : ClientEmoteAPI.clientEmoteList()) {
			String animationName = animation.getNameOrId();
			if (animationName != null && animationName.equalsIgnoreCase(name)) {
				found = animation;
				break;
			}
		}

		return found != null && ClientEmoteAPI.playEmote(found);
	}

	private static void applyBone(AvatarAnimManager manager, @Nullable MModelCollection collection, String boneName) {
		if (collection == null || collection.isEmpty()) {
			return;
		}

		for (MModel model : collection.getModels()) {
			if (model == null) {
				continue;
			}

			ModelPart part = model.asModelPart();
			POSED_PARTS.add(part);
			SAVED_POSES.add(part.storePose());

			PlayerAnimBone bone = new PlayerAnimBone(boneName);
			RenderUtil.copyVanillaPart(part, bone);
			manager.updatePart(part, bone);
		}
	}
}
