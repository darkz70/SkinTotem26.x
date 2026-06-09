package com.darkz.skintotem.model.base;

import java.util.Set;
import lombok.Getter;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.*;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

@Getter
public class MCuboid extends ModelPart.Cube {

	private static final Set<Direction> EMPTY_SET = Set.of();
	private final CubeDeformation dilation;

	public MCuboid(Vector3f pos, Vector3f size, Polygon[] quads, CubeDeformation dilation) {
		super(0, 0, pos.x(), pos.y(), pos.z(), size.x(), size.y(), size.z(), 0, 0, 0, false, 0, 0, EMPTY_SET);
		// this.polygons = quads; // In 26.1 polygons is final and set in super constructor
		// We might need a different approach if we really need to override polygons, 
		// but for now let's see if the super constructor is enough.
		// If not, we might need to use a Mixin to make it non-final or use Reflection.
		this.dilation = dilation;
	}

	public Cube asCuboid() {
		return this;
	}
}
