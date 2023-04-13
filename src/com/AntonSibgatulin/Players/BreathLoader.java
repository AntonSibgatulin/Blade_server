package com.AntonSibgatulin.Players;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.AntonSibgatulin.Players.beast.BeastModel;
import com.AntonSibgatulin.Players.flame.FlameModel;
import com.AntonSibgatulin.Players.insect.InsectModel;
import com.AntonSibgatulin.Players.thunder.ThunderModel;
import com.AntonSibgatulin.Players.water.WaterModel;

public class BreathLoader {
	public HashMap<String, IBreath> breaths = new HashMap<>();
	public ArrayList<IBreath> arrayList = new ArrayList<>();
	public ArrayList<JSONObject> datas = new ArrayList<>();

	public BreathLoader(String path) {
		File file = new File(path);
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file2 = files[i];
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file2));
				String string = null;
				String all = "";
				while ((string = bufferedReader.readLine()) != null) {
					all += string;
				}
				// System.out.println(files[i].getName());
				JSONObject jsonObject = new JSONObject(all);
				String id = jsonObject.getString("id");
				JSONArray styles = jsonObject.getJSONArray("styles");
				if (id.equals("WATER")) {
					WaterModel model = new WaterModel(jsonObject);
					breaths.put("WATER", model);
					arrayList.add(model);
					System.out.println("Breath " + id + " is loading");

				}

				if (id.equals("THUNDER")) {
					ThunderModel model = new ThunderModel(jsonObject);
					breaths.put(id, model);
					arrayList.add(model);
					System.out.println("Thunder " + id + " is loading");

				}
				if (id.equals("FLAME")) {
					FlameModel model = new FlameModel(jsonObject);

					breaths.put(id, model);
					arrayList.add(model);
					System.out.println("Flame " + id + " is loading");

				}

				if (id.equals("INSECT")) {
					InsectModel model = new InsectModel(jsonObject);

					breaths.put(id, model);
					arrayList.add(model);
					System.out.println("Flame " + id + " is loading");

				}

				if (id.equals("BEAST")) {
					BeastModel model = new BeastModel(jsonObject);

					breaths.put(id, model);
					arrayList.add(model);
					System.out.println("Flame " + id + " is loading");

				}

				datas.add(jsonObject);
				bufferedReader.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		// TODO Auto-generated constructor stub
	}

}
