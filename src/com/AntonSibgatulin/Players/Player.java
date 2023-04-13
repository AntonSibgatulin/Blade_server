package com.AntonSibgatulin.Players;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class Player implements Cloneable {
	public JSONObject jsonObject = null;
	public HashMap<String, Integer> players_info = new HashMap<>();
	public ArrayList<EBreath> styles = null;
	public double speed = 0;
	public double jump = 0;
	public double go = 0;
	public double speed_shot = 0;
	public double breath = 0;
	public int run = 0;
	public long health = 0;
	public int type = 0;
	public String name = null;
	public boolean npc = false;
	public int id = 0;
	public double energy = 0;
	public double energy_recovery = 0;
	public int power = 0;
	public boolean hashiro = false;
	public JSONArray just_untill = null;
	public String info = null;
	public int heart = 3;
	public ArrayList<IBreath> arrayList = new ArrayList<>();
	public HashMap<String, IBreath> hashMap = new HashMap<>();
	public IBreath ibreath = null;
	public Integer maxPlayers = 0;
	public int size = 1;
	public int radius = 3;

	public boolean isHasStyle(String style) {
		for (int i = 0; i < this.styles.size(); i++) {
			if (EBreath.valueOf(style).equals(this.styles.get(i)))
				return true;
		}
		return false;
	}

	public Player(Integer id, ArrayList<EBreath> styles, double speed, double jump, double go, double speed_shot,
			double breath, int run, long health, double energy, double energy_recovery, int power, int type,
			String name, JSONObject jsonObject, boolean hashiro, JSONArray just_untill, String info) {
		this.id = id;
		this.jsonObject = jsonObject;
		this.styles = styles;
		this.speed = speed;
		this.jump = jump;
		this.go = go;
		this.speed_shot = speed_shot;
		this.breath = breath;
		this.run = run;
		this.health = health;
		this.type = type;
		this.name = name;
		this.energy = energy;
		this.energy_recovery = energy_recovery;
		this.power = power;
		this.hashiro = hashiro;
		this.info = info;
		this.just_untill = just_untill;

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
