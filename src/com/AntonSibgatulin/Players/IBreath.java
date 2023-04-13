package com.AntonSibgatulin.Players;

import org.json.JSONObject;

import com.AntonSibgatulin.location.PlayerController;

public interface IBreath {

	public void run();

	public void setSetting(String str);

	public int getNumberofStyle();

	public int posAttack();

	public void fire();

	public void go();

	public void length();

	public void addStyle(StyleModel style);

	public void setStyle(int style);

	public JSONObject getStyles();

	public JSONObject getJSONInfo();

	public EBreath getBreath();

	public Object cl();

	public void cleanStyles();

	public void setPlayerController(PlayerController playerController);

	public int getPowerOfStrike();

	public double getEnergy();

	public void sendFire();
}
