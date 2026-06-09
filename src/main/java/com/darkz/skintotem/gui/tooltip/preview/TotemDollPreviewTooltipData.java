package com.darkz.skintotem.gui.tooltip.preview;

import com.darkz.skintotem.doll.data.TotemDollData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record TotemDollPreviewTooltipData(TotemDollData data, Identifier model) implements TooltipComponent {

}
