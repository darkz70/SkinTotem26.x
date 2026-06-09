package com.darkz.skintotem.utils.mixin;

import com.darkz.skintotem.gui.widget.tag.*;
import org.jetbrains.annotations.Nullable;

public interface MTDAnvilScreen {

	@Nullable
	TagButtonWidget myTotemDoll$getTagButtonWidget();

	@Nullable
	TagMenuWidget myTotemDoll$getTagMenuWidget();

}
