package com.AntonSibgatulin.shop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.AntonSibgatulin.Players.Player;
import com.AntonSibgatulin.user.User;

public class ShopModel {
	public HashMap<String, ShopPlayerModel> shopPlayerModelMap = new HashMap<>();
	public HashMap<String, ShopMoneyModel> shopMoneyModelMap = new HashMap<>();
	public HashMap<String, ShopMagicKeyModel> shopMagicKeyModelMap = new HashMap<>();
	public HashMap<String, ShopBoxModel> shopBoxModelMap = new HashMap<>();
	public String path = null;
	public String json = null;

	public ShopModel(String path) {
		this.path = path;
		if (path == null)
			return;
		File[] files = new File(path).listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory())
				continue;
			BufferedReader bufferedReader = null;
			try {
				bufferedReader = new BufferedReader(new FileReader(file));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String string = null;
			String all = "";
			try {
				while ((string = bufferedReader.readLine()) != null) {
					all += string;
				}
				JSONObject jsonObjectMain = new JSONObject(all);
				json = jsonObjectMain.toString();
				if (jsonObjectMain.has("players")) {
					JSONArray jsonArray = jsonObjectMain.getJSONArray("players");
					for (int j = 0; j < jsonArray.length(); j++) {
						JSONObject jsonObject = jsonArray.getJSONObject(j);
						if (jsonObject.has("none") && jsonObject.getBoolean("none")) {
							continue;

						}
						String cards = jsonObject.getString("cards");
						int price = jsonObject.getInt("price");
						int score = jsonObject.getInt("score");
						int count_of_cards = jsonObject.getInt("count_of_cards");
						int magicKey = jsonObject.getInt("magicKey");

						ShopPlayerModel shopPlayerModel = new ShopPlayerModel("players_" + j, price, score, magicKey,
								cards, count_of_cards);
						shopPlayerModelMap.put(shopPlayerModel.id_, shopPlayerModel);

					}
				}

				if (jsonObjectMain.has("money")) {
					JSONArray jsonArray = jsonObjectMain.getJSONArray("money");
					for (int j = 0; j < jsonArray.length(); j++) {
						JSONObject jsonObject = jsonArray.getJSONObject(j);
						if (jsonObject.has("none") && jsonObject.getBoolean("none")) {
							continue;

						}
						int price = jsonObject.getInt("price");
						int count = jsonObject.getInt("count");
						int magicKey = jsonObject.getInt("magicKey");
						ShopMoneyModel shopMoneyModel = new ShopMoneyModel("money_" + j, price, count, magicKey);
						// shopMoneyModelMap.put(shopPlayerModel.id_,
						// shopPlayerModel);
						shopMoneyModelMap.put(shopMoneyModel.id, shopMoneyModel);

					}
				}

				if (jsonObjectMain.has("box")) {
					JSONArray jsonArray = jsonObjectMain.getJSONArray("box");
					for (int j = 0; j < jsonArray.length(); j++) {
						JSONObject jsonObject = jsonArray.getJSONObject(j);
						int price_coin = jsonObject.getInt("price_coin");
						if (jsonObject.has("none") && jsonObject.getBoolean("none")) {
							continue;

						}
						int price = jsonObject.getInt("price");
						int count = jsonObject.getInt("count");
						int magicKey = jsonObject.getInt("magicKey");
						ShopBoxModel shopBoxModel = new ShopBoxModel("box_" + j, count, price, price_coin, magicKey);
						// shopMoneyModelMap.put(shopPlayerModel.id_,
						// shopPlayerModel);
						shopBoxModelMap.put(shopBoxModel.id, shopBoxModel);

					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public String getId(String product_id) {
		ShopPlayerModel shopPlayerModel = shopPlayerModelMap.get(product_id);
		return shopPlayerModel.cards;

	}

	public void buy(User user, String id, String type, Player player) {
		if (user == null)
			return;

		ShopPlayerModel shopPlayerModel = shopPlayerModelMap.get(id);
		// System.out.println("Datas " + shopPlayerModel.cards);
		// System.out.println(isHasPlayer(user, shopPlayerModel.cards));
		if (isHasPlayer(user, shopPlayerModel.cards) == false) {

			if (type.equals("money")) {
				if (user.money - shopPlayerModel.price >= 0) {
					user.money -= shopPlayerModel.price;
					user.send_score_money();
					JSONObject jsonObject = user.createObject(shopPlayerModel.cards, player);
					JSONObject jsonObject2 = new JSONObject(user.info.players);
					jsonObject2.getJSONArray("players").put(jsonObject);

					user.info.setPlayers(jsonObject2.toString());
					user.sendBought(id);

				} else {
					user.send("lobby;shop;money_not_enough");
				}
			}
		}
	}

	public boolean isHasPlayer(User user, String id) {

		JSONObject jsonObject = new JSONObject(user.info.players);
		JSONArray jsonArray = jsonObject.getJSONArray("players");
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject2 = jsonArray.getJSONObject(i);
			String idString = jsonObject2.getString("id");
			if (idString.equals(id)) {
				return true;
			}
		}
		return false;

	}

}
