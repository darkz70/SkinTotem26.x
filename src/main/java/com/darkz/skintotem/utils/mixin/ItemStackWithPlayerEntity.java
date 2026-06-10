package com.darkz.skintotem.utils.mixin;

import net.minecraft.client.player.AbstractClientPlayer;

public interface ItemStackWithPlayerEntity {

	void mySkinTotem$setPlayerEntity(AbstractClientPlayer player);

	AbstractClientPlayer mySkinTotem$getPlayerEntity();
}
