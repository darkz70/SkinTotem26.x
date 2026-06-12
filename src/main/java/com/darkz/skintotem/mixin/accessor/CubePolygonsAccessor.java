package com.darkz.skintotem.mixin.accessor;

import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelPart.Cube.class)
public interface CubePolygonsAccessor {
    @Accessor("polygons")
    void setPolygons(ModelPart.Polygon[] polygons);
}
