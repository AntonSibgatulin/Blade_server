package com.AntonSibgatulin.Players.stuff;

public class StuffModel implements Cloneable {

	public String id = null;
	public boolean isCollapse = false;
	public int mass = 0;
	public boolean keep = false;
	public int price = 0;
	public int power = 0;
	public int w = 0;
	public int h = 0;

	public StuffModel(String id, boolean isCollapse, int mass, boolean keep, int price, int power, int w, int h) {

		this.id = id;
		this.isCollapse = isCollapse;
		this.mass = mass;
		this.keep = keep;
		this.price = price;
		this.power = power;
		this.h = h;
		this.w = w;

	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
