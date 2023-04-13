package com.AntonSibgatulin.location;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.AntonSibgatulin.Players.HouseLoader;
import com.AntonSibgatulin.location.generation.MapGeneration;

public class LocationLoader {
	public static final String[] types = { "train", "way_of_slayer" };
	public HashMap<String, MapGeneration> maps = new HashMap<>();
	public ArrayList<MapGeneration> arrayList = new ArrayList<>();
	public HashMap<String, MapGeneration> hashMap = new HashMap<>();

	public HouseLoader houseLoader = null;

	public LocationLoader(String path, HouseLoader houseLoader) {
		this.houseLoader = houseLoader;
		load(path);
	}

	public void load(String path) {
		File file = new java.io.File(path);
		File[] files = file.listFiles();
		for (int ii = 0; ii < files.length; ii++) {
			File iFile = files[ii];
			if (iFile.isDirectory() == false) {
				JSONObject jsonObject = null;
				try {
					System.out.println(iFile.getName() + " is loading");

					BufferedReader reader = new BufferedReader(new FileReader(iFile));
					String all = "";
					String tString = null;
					while ((tString = reader.readLine()) != null) {
						all += tString;
					}
					jsonObject = new JSONObject(all);
					String id = jsonObject.getString("id");
					int idint = jsonObject.getInt("idint");
					int i = jsonObject.getInt("width");
					int j = jsonObject.getInt("height");
					boolean down_b = jsonObject.getBoolean("down_b");
					int down = jsonObject.getInt("down");
					int rand = jsonObject.getInt("rand");
					int sma = jsonObject.getInt("sma");
					int min = jsonObject.getInt("min");
					int max = jsonObject.getInt("max");

					int px = jsonObject.getInt("min");
					int py = jsonObject.getInt("max");
					int money = jsonObject.getInt("money");
					int score = jsonObject.getInt("score");
					String description = jsonObject.getString("description");
					String description_en = jsonObject.getString("description_en");
					String description_es = jsonObject.getString("description_es");
					String description_jp = jsonObject.getString("description_ja");
					String description_zh = jsonObject.getString("description_zh");
					
					String category = jsonObject.getString("category");
					int random_power = 70;// default 70
					int random_amor = 60;// default 60
					int random_health = 90;// default 90
					int random_nitro = 30;// default 30
					int iteration_of_deamons = 1;// default 1
					int MAX_FAST_DEAMON = 3;// default 10
					double fastParametre = 3.1;// default 2.1
					int MAX_AMOR_DEAMON = 3;// default 10

					JSONArray jsonArray = null;
					if (jsonObject.has("highmoons")) {
						jsonArray = jsonObject.getJSONArray("highmoons");

					}
					if (jsonObject.has("MAX_AMOR_DEAMON")) {
						MAX_AMOR_DEAMON = jsonObject.getInt("MAX_AMOR_DEAMON");

					}
					if (jsonObject.has("MAX_FAST_DEAMON")) {
						MAX_FAST_DEAMON = jsonObject.getInt("MAX_FAST_DEAMON");
					}
					if (jsonObject.has("fastParametre")) {
						fastParametre = jsonObject.getDouble("fastParametre");

					}
					if (jsonObject.has("iteration_of_deamons")) {
						iteration_of_deamons = jsonObject.getInt("iteration_of_deamons");

					}
					if (jsonObject.has("rand_amor")) {
						random_amor = jsonObject.getInt("rand_amor");
					}

					if (jsonObject.has("rand_health")) {
						random_health = jsonObject.getInt("rand_health");
					}

					if (jsonObject.has("rand_power")) {
						random_power = jsonObject.getInt("rand_power");
					}

					if (jsonObject.has("rand_nitro")) {
						random_nitro = jsonObject.getInt("rand_nitro");
					}
					long seed = jsonObject.getLong("seed");// ("sma");

					MapGeneration mapGeneration = new MapGeneration(idint, id, i, j, seed, down_b, down, min, max, rand,
							sma, new Point(px, py), houseLoader, money, score, description, description_en,
							description_es, description_jp, description_zh,category, random_amor, random_power, random_nitro,
							random_health, iteration_of_deamons, jsonArray, MAX_FAST_DEAMON, fastParametre,
							MAX_AMOR_DEAMON);

					if (jsonObject.has("killAllDeamons")) {
						mapGeneration.killAllDeamons = jsonObject.getBoolean("killAllDeamons");

					}

					if (jsonObject.has("minLevelNpc")) {
						mapGeneration.minLevelNpc = jsonObject.getInt("minLevelNpc");

					}
					if (jsonObject.has("maxLevelNpc")) {
						mapGeneration.maxLevelNpc = jsonObject.getInt("maxLevelNpc");

					}

					System.err.println("Money " + mapGeneration.money);
					mapGeneration.generation();

					this.maps.put(id, mapGeneration);
					this.hashMap.put(category + "_" + idint, mapGeneration);
					arrayList.add(mapGeneration);
					System.out.println(id + " is loaded");

					reader.close();
				} catch (Exception e) {

					e.printStackTrace();
				}
			} else {
				load(path + "/" + iFile.getName());
			}
		}

	}

}
