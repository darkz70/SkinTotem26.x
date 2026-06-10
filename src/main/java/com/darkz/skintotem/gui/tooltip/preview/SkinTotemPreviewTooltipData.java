package com.darkz.skintotem.gui.tooltip.preview;

import com.darkz.skintotem.doll.data.SkinTotemData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record SkinTotemPreviewTooltipData(SkinTotemData data, Identifier model) implements TooltipComponent {

}
