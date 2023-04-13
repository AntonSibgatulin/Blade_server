package com.AntonSibgatulin.location;

public class Square implements Cloneable {

	@Override
	public String toString() {
		return x + " " + y + " " + w + " " + h;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;

		}

	}

	public double x, y, w, h;

	public Square() {
	}

	public Square(double _x, double _y, double _w, double _h) {
		x = _x;
		y = _y;
		w = _w;
		h = _h;
	}

	public void setPosSquare(Square square) {
		this.x = square.x;
		this.y = square.y;

	}

	// пересечение квадратов
	public static boolean isIntersect(Square a, Square b) {
		return ((a.x < (b.x + b.w)) && (b.x < (a.x + a.w)) && (a.y < (b.y + b.h)) && (b.y < (a.y + a.h)));
	}

	// пересечение квадратов с выталкиванием
	public static boolean isLockIntersect(Square a, Square b) {
		if (!isIntersect(a, b))
			return false;
		double x0 = b.x - (a.x - b.w);
		double y0 = b.y - (a.y - b.h);
		double x1 = (a.x + a.w) - b.x;
		double y1 = (a.y + a.h) - b.y;
		if (x1 < x0)
			x0 = -x1;
		if (y1 < y0)
			y0 = -y1;

		if (Math.abs(x0) < Math.abs(y0))
			a.x += x0;
		else if (Math.abs(x0) > Math.abs(y0))
			a.y += y0;
		else {
			a.x += x0;
			a.y += y0;
		}
		return true;
	}

};