package com.darkz.skintotem.utils;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OS;

public class ScreenUtils {

	private static Boolean IS_MAC = null;

	public static boolean hasShiftDown() {
		return InputConstants.isKeyDown(340) || InputConstants.isKeyDown(344);
	}

	public static boolean hasControlDown() {
		if (IS_MAC == null) {
			IS_MAC = Util.getPlatform() == OS.OSX;
		}
		if (IS_MAC) {
			return InputConstants.isKeyDown(343) || InputConstants.isKeyDown(347);
		} else {
			return InputConstants.isKeyDown(341) || InputConstants.isKeyDown(345);
		}
	}

}
