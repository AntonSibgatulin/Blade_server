package com.AntonSibgatulin.Players.stuff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class StuffLoader {
	public ArrayList<StuffModel> stuffModels = new ArrayList<>();
	public HashMap<String, StuffModel> stuffModelsHashMap = new HashMap<>();

	public StuffLoader(String path) {
		File[] files = new File(path).listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory())
				continue;

			try {
				JSONObject jsonObject = (JSONObject) new JSONParser().parse(new FileReader(file));
				String id = jsonObject.getString("id");
				boolean isCollapse = jsonObject.getBoolean("isCollapse");
				int mass = jsonObject.getInt("mass");
				boolean keep = jsonObject.getBoolean("keep");
				int price = jsonObject.getInt("price");
				int power = jsonObject.getInt("power");
				int w = jsonObject.getInt("w");
				int h = jsonObject.getInt("h");
				StuffModel stuffModel = new StuffModel(id, isCollapse, mass, keep, price, power, w, h);
				stuffModelsHashMap.put(id, stuffModel);
				stuffModels.add(stuffModel);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
