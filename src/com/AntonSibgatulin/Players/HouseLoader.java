package com.AntonSibgatulin.Players;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import com.AntonSibgatulin.location.house.HouseModel;

public class HouseLoader {
	public ArrayList<HouseModel> houseModels = new ArrayList<>();
	public HashMap<String, HouseModel> hashMap = new HashMap<>();

	public HouseLoader(String path) {
		File[] files = new File(path).listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			String all = "";
			String pString = null;

			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				while ((pString = bufferedReader.readLine()) != null) {
					all += pString;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONObject jsonObject = new JSONObject(all);
			HouseModel houseModel = new HouseModel(jsonObject.getString("id"), 0, 0, jsonObject.getInt("width"),
					jsonObject.getInt("height"));

			houseModels.add(houseModel);
			hashMap.put(jsonObject.getString("id"), houseModel);

		}
	}

}
