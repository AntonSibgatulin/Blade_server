package com.AntonSibgatulin.location.deamons.lowmoon.StuffModel;

import java.awt.Rectangle;
import java.awt.geom.Line2D;

import org.json.JSONObject;

import com.AntonSibgatulin.location.Square;
import com.AntonSibgatulin.location.Vector2d;

public class LineModel {
	public static long TIME_IS_USING_LINE = 2500;
	public long time_start = 0;
	public Line2D line2d = null;
	// geometry
	public int angle = 0;// angle of slope
	public int length = 0;// length of line
	public int lengthMax = 0;
	public int speedLength = 7;

	public int lineWidth = 1;

	public Vector2d pointStart = new Vector2d(0, 0);
	public String id = null;

	public LineModel(String id, Vector2d start, int angle) {
		this.id = id;

		this.angle = angle;
		this.pointStart = start;

	}

	public boolean CheckCollision(Square square) {

		Rectangle rectangle = new Rectangle((int) square.x, (int) square.y, (int) square.w, (int) square.h);
		Line2D line2d = new Line2D.Double((int) pointStart.x, (int) pointStart.y,
				(int) (pointStart.x + length * Math.cos((double) (angle) / 180 * Math.PI)),
				(int) (pointStart.y - length * Math.sin((double) (angle) / 180 * Math.PI)));

		return rectangle.intersectsLine(line2d);

	}

	public JSONObject getJSONData() {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", id);
		jsonObject.put("length", length);
		jsonObject.put("x", pointStart.x);
		jsonObject.put("y", pointStart.y);
		jsonObject.put("lineWidth", lineWidth);
		jsonObject.put("angle", angle);
		jsonObject.put("endx", (pointStart.x + (length * Math.cos((double) (angle) / 180 * Math.PI))));
		jsonObject.put("endy", (pointStart.y - (length * Math.sin((double) (angle) / 180 * Math.PI))));

		return jsonObject;
	}

}
