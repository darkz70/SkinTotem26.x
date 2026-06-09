package com.darkz.skintotem.extension;

import com.darkz.skintotem.doll.data.TotemDollData;
import com.darkz.skintotem.doll.manager.*;
import com.darkz.skintotem.tag.manager.TagsManager;
import com.darkz.skintotem.utils.mixin.*;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemStackExtension {

	@Nullable
	public static Component getRealCustomName(ItemStack itemStack) {
		// components is accessible via AW in 26.1
		return itemStack.components.get(net.minecraft.core.component.DataComponents.CUSTOM_NAME);
	}

	public static TotemDollData getTotemDollData(ItemStack stack) {
		return getTotemDollData(stack, true);
	}

	public static TotemDollData getTotemDollData(ItemStack stack, boolean applyRenderProperties) {
		Component name = getRealCustomName(stack);

		if (name != null) {
			String o = TagsManager.getNicknameOrSkinProviderFromName(name.getString());
			TotemDollData data = TotemDollManager.getDoll(o);

			// refresh render properties
			data.refreshRenderProperties();

			String tags = TagsManager.getTagsFromName(name.getString());
			if (tags != null) {
				// Editing render properties here
				TagsManager.processTags(tags, data);
			}

			return applyRenderProperties ? data.applyRenderProperties() : data; // apply render properties
		}

		TotemDollData data = StandardTotemDollManager.getStandardDoll().refreshRenderProperties();
		return applyRenderProperties ? data.applyRenderProperties() : data;
	}

	public static void setModdedModel(ItemStack itemStack, boolean modded) {
		((ItemStackWithModdedBakedModel) (Object) itemStack).myTotemDoll$setModdedModel(modded);
	}

	public static boolean hasModdedModel(ItemStack itemStack) {
		return ((ItemStackWithModdedBakedModel) (Object) itemStack).myTotemDoll$isModdedModel();
	}

	public static void setPlayerEntity(ItemStack itemStack, AbstractClientPlayer playerEntity) {
		((ItemStackWithPlayerEntity) (Object) itemStack).myTotemDoll$setPlayerEntity(playerEntity);
	}

	public static AbstractClientPlayer getPlayerEntity(ItemStack itemStack) {
		return ((ItemStackWithPlayerEntity) (Object) itemStack).myTotemDoll$getPlayerEntity();
	}

}
