package com.darkz.skintotem.gui.tooltip.state;

import com.darkz.skintotem.doll.data.LoadingState;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record LoadingStateTooltipData(LoadingState state) implements TooltipComponent {

}
