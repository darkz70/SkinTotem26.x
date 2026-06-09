package com.darkz.skintotem.utils.tooltip;

import org.jetbrains.annotations.Nullable;

public interface IRequestableTooltipScreen {

	void myTotemDoll$requestTooltip(@Nullable TooltipRequest tooltipRequest);

	@Nullable
	TooltipRequest myTotemDoll$getCurrentRequest();

}
