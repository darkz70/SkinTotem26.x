package com.darkz.skintotem.utils.tooltip;

import org.jetbrains.annotations.Nullable;

public interface IRequestableTooltipScreen {

	void mySkinTotem$requestTooltip(@Nullable TooltipRequest tooltipRequest);

	@Nullable
	TooltipRequest mySkinTotem$getCurrentRequest();

}
