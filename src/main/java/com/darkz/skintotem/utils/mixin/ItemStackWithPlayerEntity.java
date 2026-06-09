package com.darkz.skintotem.utils.mixin;

import net.minecraft.client.player.AbstractClientPlayer;

public interface ItemStackWithPlayerEntity {

	void myTotemDoll$setPlayerEntity(AbstractClientPlayer player);

	AbstractClientPlayer myTotemDoll$getPlayerEntity();
}
