package com.AntonSibgatulin.location.deamons.lowmoon.StuffModel;

import org.json.JSONObject;

import com.AntonSibgatulin.location.Square;
import com.AntonSibgatulin.location.Vector2d;
import com.AntonSibgatulin.location.deamons.npc.NPCDeamon;
import com.AntonSibgatulin.location.generation.MapGeneration;

public class BallsModel {
	public String id = null;
	public NPCDeamon npcDeamon = null;
	public int angle = 0;
	public boolean active = true;

	public int maxLength = 10 * MapGeneration.SIZE;
	public int length = 0;
	public int speed = 5;
	public Vector2d vec = null;

	public Square pos = null;
	public Square lastPos = null;

	public Vector2d getPosition() {
		return new Vector2d(pos.x, pos.y);
	}

	public BallsModel(String id, int angle, Vector2d position, Vector2d size) {
		this.id = id;
		this.pos = new Square(position.x, position.y, size.x, size.y);
		this.angle = angle;
		this.vec = position;

	}

	public void update() {
		length += speed;
		pos.x += (length * Math.cos((double) (angle) / 180 * Math.PI));
		pos.y -= (length * Math.sin((double) (angle) / 180 * Math.PI));
		if (getPosition().distanceToWithoutZ(vec) >= maxLength) {
			active = false;

		}
	}

	public JSONObject getJSONObject() {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", id);
		jsonObject.put("length", length);
		jsonObject.put("x", pos.x);
		jsonObject.put("y", pos.y);
		jsonObject.put("sx", pos.w);
		jsonObject.put("sy", pos.h);

		jsonObject.put("angle", angle);

		return jsonObject;
	}

	public boolean isAnotherPosition() {

		if (lastPos == null || pos.x != lastPos.x || pos.y != lastPos.y) {
			if (lastPos == null)
				lastPos = new Square(pos.x, pos.y, pos.w, pos.y);
			else {
				lastPos.x = pos.x;
				lastPos.y = pos.y;
			}
			return true;
		} else {
			return false;
		}

	}

}
