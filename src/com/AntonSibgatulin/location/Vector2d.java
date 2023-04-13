package com.AntonSibgatulin.location;

public class Vector2d {
	public double x = 0.0;
	public double y = 0.0;

	public double rot = 0.0D;

	public Vector2d(double x, double y) {
		this.x = x;
		this.y = y;

	}

	public double distanceToWithoutZ(Vector2d to) {
		return Math.sqrt(this.pow2((double) (this.x - to.x)) + this.pow2((double) (this.y - to.y)));
	}

	private double pow2(double value) {
		return Math.pow(value, 2.0D);
	}
}
