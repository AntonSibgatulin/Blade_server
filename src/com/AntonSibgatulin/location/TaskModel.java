package com.AntonSibgatulin.location;

import java.util.ArrayList;

import org.json.JSONObject;

public class TaskModel implements Cloneable {
	public boolean isDoing = false;
	public String text = null;
	public String text_en = null;
	public String text_es = null;
	public String text_ja = null;
	public String id = null;

	public double distanceit = 0;

	public int time = 0;
	public int minscore = 100000;

	public int addscore = 0;
	public int addmoney = 0;

	public int minscore_to_the_use_it = 0;

	public boolean running = false;
	public int distance = 100000;

	public int addrun = 0;
	public double addspeed = 0.0;

	public boolean exercise = false;
	public ArrayList<TaskModel> list = new ArrayList<>();
	public JSONObject jsonObject = null;

	public TaskModel(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
		init();
	}

	public void init() {

		id = this.jsonObject.getString("id");
		if (id.split("_")[0].equals("running")) {
			running = true;

			distance = jsonObject.getInt("distance");

			addrun = jsonObject.getInt("addrun");
			addspeed = jsonObject.getDouble("addspeed");

		}
		if (jsonObject.has("time")) {
			time = jsonObject.getInt("time");
		}
		minscore = jsonObject.getInt("minscore");

		addmoney = jsonObject.getInt("addmoney");
		addscore = jsonObject.getInt("addscore");
		text = jsonObject.getString("text");
		text_en = jsonObject.getString("text_en");
		text_es = jsonObject.getString("text_es");
		text_ja = jsonObject.getString("text_ja");

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
}
