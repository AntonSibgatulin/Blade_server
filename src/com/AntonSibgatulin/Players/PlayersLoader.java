package com.AntonSibgatulin.Players;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class PlayersLoader {
	public HashMap<String, Player> players = new HashMap<>();
	public HashMap<String, Integer> players_info = new HashMap<>();
	public ArrayList<Player> arrayList = new ArrayList<>();
	public ArrayList<JSONObject> datas = new ArrayList<>();

	public PlayersLoader(String path) {
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
				JSONObject jsonObject = new JSONObject(all);
				String name = jsonObject.getString("id");
				String id = name;
				JSONArray styles = jsonObject.getJSONArray("styles");
				ArrayList<EBreath> styles2 = new ArrayList<>();

				for (int j = 0; j < styles.length(); j++) {
					String str = styles.getString(j);

					if (str.equals(EBreath.FLAME.toString()) || str.equals(EBreath.MOON.toString())
							|| str.equals(EBreath.SOUND.toString()) || str.equals(EBreath.SUN.toString())
							|| str.equals(EBreath.THUNDER.toString()) || str.equals(EBreath.BEAST.toString())
							|| str.equals(EBreath.WATER.toString()) || str.equals(EBreath.INSECT.toString())) {
						styles2.add(EBreath.valueOf(str));

					}

				}
				JSONArray levels = jsonObject.getJSONArray("levels");
				String info = "";
				if (jsonObject.has("info")) {
					info = jsonObject.getString("info");
				}
				JSONArray just_untill = null;
				if (jsonObject.has("just_untill")) {
					just_untill = jsonObject.getJSONArray("just_untill");
				}
				for (int j = 0; j < levels.length(); j++) {
					JSONObject jsonObject2 = levels.getJSONObject(j);
					boolean hashiro = false;
					if (jsonObject2.has("hashiro")) {
						hashiro = jsonObject2.getBoolean("hashiro");

					}

					int price = jsonObject2.getInt("price");
					double speed = jsonObject2.getDouble("speed");
					double speed_shot = jsonObject2.getDouble("speed_shot");
					double go = jsonObject2.getDouble("go");
					long health = jsonObject2.getLong("health");
					double jump = jsonObject2.getDouble("jump");
					int run = jsonObject2.getInt("run");
					double breath = jsonObject2.getDouble("breath");
					double energy = jsonObject2.getDouble("energy");
					double energy_recovery = jsonObject2.getDouble("energy_recovery");
					int power = jsonObject2.getInt("power");
					System.out.println(id + "_" + j + " is loading");
					Player player = new Player(j, styles2, speed, jump, go, speed_shot, breath, run, health, energy,
							energy_recovery, power, j, name, jsonObject, hashiro, just_untill, info);
					if (jsonObject2.has("npc") && jsonObject2.getBoolean("npc")) {
						player.npc = true;
					}
					this.players.put(id + "_" + j, player);
					this.players_info.put(name, j);
					this.arrayList.add(player);
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

	}

}
