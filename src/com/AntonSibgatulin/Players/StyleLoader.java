package com.AntonSibgatulin.Players;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class StyleLoader {
	public JSONArray json = null;
	public ArrayList<StyleModel> styles = new ArrayList<>();
	public HashMap<Integer, StyleModel> hashMap = new HashMap<>();

	public StyleLoader(JSONArray json, EBreath style) {
		this.json = json;
		for (int i = 0; i < json.length(); i++) {
			JSONObject jsonObject = json.getJSONObject(i);
			String description = jsonObject.getString("des");
			boolean has = false;
			if (jsonObject.has("none") && jsonObject.getBoolean("none")) {
				// continue;
				has = jsonObject.getBoolean("none");
			}
			String name = jsonObject.getString("name");
			int maxTime = jsonObject.getInt("maxTime");
			int minTime = jsonObject.getInt("minTime");
			int power = jsonObject.getInt("power");
			int target = jsonObject.getInt("target");
			double energy = jsonObject.getDouble("energy");
			int length = 0;
			boolean contr = false;
			if (jsonObject.has("length")) {
				length = jsonObject.getInt("length");

			}
			if (jsonObject.has("contr")) {
				contr = jsonObject.getBoolean("contr");

			}
			System.out.println(style.toString() + " " + description + " is loading");
			StyleModel styleModel = new StyleModel(style, length, contr, name, maxTime, minTime, power, energy, target,
					style.toString(), i, jsonObject, has);
			styles.add(styleModel);
			hashMap.put(i, styleModel);

		}
	}

}
