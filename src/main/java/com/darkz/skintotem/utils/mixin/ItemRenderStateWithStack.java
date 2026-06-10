package com.darkz.skintotem.utils.mixin;

import net.minecraft.world.item.ItemStack;

public interface ItemRenderStateWithStack {

	void mySkinTotem$setStack(ItemStack stack);

	void mySkinTotem$shouldClear(boolean bl);

	void mySkinTotem$reset();

}
