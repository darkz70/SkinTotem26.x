package com.darkz.skintotem.utils.mixin;

import com.darkz.skintotem.gui.widget.tag.*;
import org.jetbrains.annotations.Nullable;

public interface STAnvilScreen {

	@Nullable
	TagButtonWidget mySkinTotem$getTagButtonWidget();

	@Nullable
	TagMenuWidget mySkinTotem$getTagMenuWidget();

	default void mySkinTotem$setImageWidth(int width) {}

}
