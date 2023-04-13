package com.AntonSibgatulin.Players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.AntonSibgatulin.location.PlayerController;
import com.AntonSibgatulin.location.Square;
import com.AntonSibgatulin.location.Timer;

public abstract class EntityBreath {
	public int w0 = 0;
	public int h0 = 0;
	public Square fight = null;

	public StyleModel mainStyle = null;
	public Timer mainTimer = null;

	public ArrayList<StyleModel> styles = new ArrayList<>();
	public HashMap<Integer, StyleModel> hashStyles = new HashMap<>();

	public PlayerController player = null;
	public JSONObject setting = null;
	public JSONArray stylesSetting = null;

	public StyleLoader styleLoader = null;
	public JSONObject jsonObject = null;

	public void sendFire() {
		// System.out.println("Send fire");
		if (player.locationModel == null) {
			player.send("battle;fire");
		} else {
			player.locationModel.sendEverybody("battle;fire;" + player.idPlayer + ";" + getNumberofStyle());
		}

	}

	public double getEnergy() {
		StyleModel styleModel = hashStyles.get(getNumberofStyle());
		if (styleModel == null) {
			return 0;
		} else {
			double target = styleModel.energy * (player.player.id + 1);

			return target;
		}
	}

	public int getPowerOfStrike() {
		StyleModel styleModel = hashStyles.get(getNumberofStyle());
		if (styleModel == null) {
			return 0;
		} else {
			int target = styleModel.target * ((player.user.player.id+1) * 100 / (player.user.player.maxPlayers)) / 100;
			// System.out.println("Target " + target);
			return random(target / 2, target);
		}
	}

	public int random(int min, int max) {
		return min + new Random().nextInt(max - min);
	}

	public int getNumberofStyle() {
		// TODO Auto-generated method stub
		if (mainStyle != null)
			return mainStyle.type;
		else
			return -1;
	}

	public void speedGo() {
		player.speed = Math.ceil(player.player.go);
	}

	public void speedHack(double i) {
		player.speed = i * Math.ceil(player.player.speed);
	}

	public void setPlayerController(PlayerController playerController) {
		this.player = playerController;

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

	public void setStyle(int style) {
		if (this.hashStyles.get(style) != null && !this.hashStyles.get(style).has) {
			mainStyle = this.hashStyles.get(style);
			player.user.send("battle;setStyle;" + style);
		}

	}

	public JSONObject getJSONInfo() {
		// TODO Auto-generated method stub
		return jsonObject;
	}

	public Object cl() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void cleanStyles() {
		player.fights = new ArrayList<>();
		if (player.locationModel == null)
			player.send("battle;my_fight_clean");
		else {
			player.locationModel.sendEverybody("battle;my_fight_clean;" + player.idPlayer);
		}

	}

	public void addStyle(StyleModel style) {
		// TODO Auto-generated method stub
		this.hashStyles.put(style.type, style);
		this.styles.add(style);

	}
}