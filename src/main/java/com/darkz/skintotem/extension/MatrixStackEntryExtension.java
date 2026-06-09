package com.darkz.skintotem.extension;

import com.mojang.blaze3d.vertex.PoseStack.Pose;

public class MatrixStackEntryExtension {

	public static void copyFrom(Pose entry, Pose anotherEntry) {
		entry.pose().set(anotherEntry.pose());
		entry.normal().set(anotherEntry.normal());
		// trustedNormals is accessible via AW in 26.1
		entry.trustedNormals = anotherEntry.trustedNormals;
	}

}
