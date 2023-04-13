package com.AntonSibgatulin.location.CP;

import com.AntonSibgatulin.location.PlayerController;
import com.AntonSibgatulin.location.Vector2d;

public class PointModel {
	public Vector2d position = null;
	public Vector2d size = null;
	public long time = 10000;
	public PlayerController player = null;
	public PointModel() {
		// TODO Auto-generated constructor stub
	}
	public PointModel(Vector2d pos,Vector2d size){
		
		this.position = pos;
		this.size = size;
		
	}
	

}
