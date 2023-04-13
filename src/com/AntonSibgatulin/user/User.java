package com.AntonSibgatulin.user;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONObject;

import com.AntonSibgatulin.Players.Information;
import com.AntonSibgatulin.Players.Player;
import com.AntonSibgatulin.location.PlayerController;
import com.AntonSibgatulin.services.Anticheat;

@Entity
@Table(name = "users", schema = "public")
public class User implements Serializable {
	public static final long MAX_TIME_BETWEEN_CREATE_MAP = 1000*60*5;
	public static final int MAX_CREATE_MAP = 3;

	public void send_score_money() {
		// TODO Auto-generated method stub
		send("lobby;score_money;" + score + ";" + money + ";" + magicKey);
	}

	public static String[] usersRang = { "student", "undergrad", "younger hunter", "hunter", "older hunter",
			"assistant manager", "younger hashira", "hashira", "hashira hunter for the deamon", "pillar" };
	public Information info = null;
	public int width = 0;
	public int height = 0;

	public ArrayList<Player> players = new ArrayList<>();

	public PlayerController playerController = null;
	public int id;
	public String login;
	public String password;
	public Integer rang;
	public Integer money;
	public Player player = null;
	public int score = 0;
	public int magicKey = 0;
	public int box = 0;
	public String magicCards = null;
	public String locale = null;
	public Anticheat anticheat = null;
	public long timeBan = 0;
	public int ban = 0;

	public int userType = 0;

	@Column(name = "userType")
	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public void setBan(int ban) {
		this.ban = ban;
	}

	@Column(name="ban")
	public int getBan(){
		return ban;
	}

	public void setTimeBan(long timeBan) {
		this.timeBan = timeBan;
	}

	@Column(name="timeBan")
	public long getTimeBan() {
		return timeBan;
	}

	@Column(name = "magicCards")
	public String getMagicCards() {
		return magicCards;
	}

	@Column(name = "locale")
	public String getLocale() {
		return locale;
	}

	@Column(name = "magicKey")
	public int getMagicKey() {
		return magicKey;

	}

	@Column(name = "box")
	public int getBox() {
		return magicKey;

	}

	public void setBox(int str) {
		this.box = str;
	}

	public void setLocale(String str) {
		this.locale = str;
	}

	public void setMagicKey(int str) {
		this.magicKey = str;
	}

	public void setMagicCards(String magicCards) {
		this.magicCards = magicCards;
	}

	public void exit() {
		playerController.t.stop();

		playerController = null;

	}

	public String language = "ru";

	public User() {
	}

	public User(Player player) {
		this.player = player;
	}

	public User(String login, String password) {

		setLogin(login);
		setPassword(password);
		setUserType(0);


	}

	public boolean isCreated = false;
	public WebSocket connection = null;

	public void send(String message) {

		if (connection != null && connection.isClosed() == false && connection.isClosing() == false)
			connection.send(message);
	}

	public String getUserRang() {
		return usersRang[getRang()];

	}

	@Id
	@Column(name = "id")
	public int getId() {
		return id;
	}

	@Column(name = "score")
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setId(int userId) {
		this.id = userId;
	}

	@Column(name = "rang")
	public int getRang() {
		return rang;
	}

	public void setRang(int rang) {
		this.rang = rang;
	}

	@Column(name = "money")
	public int getMoney() {
		return money;
	}

	public void setmoney(int money) {
		this.money = money;
	}

	@Column(name = "login")
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Column(name = "password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void sendEnergy(double energy) {

		send("battle;set_energy_me;" + (energy * 100 / player.energy));

	}

	public JSONObject createObject(String id, Player player) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("is", false);
		jsonObject.put("location_end", new JSONArray());
		jsonObject.put("id", id);
		jsonObject.put("type", 0);
		jsonObject.put("score", 0);
		jsonObject.put("tasks", new JSONArray());
		JSONArray breath = new JSONArray();
		JSONObject jsonObject1 = new JSONObject();
		jsonObject1.put("is", true);
		System.out.println(player.styles.size() + " is loading");
		jsonObject1.put("id", player.styles.get(0).toString());

		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < 12; i++) {
			jsonArray.put(i);
		}
		jsonObject1.put("styles", jsonArray);

		breath.put(jsonObject1);
		jsonObject.put("breath", breath);
		return jsonObject;
	}

	public void sendBought(String id2) {
		send("lobby;shop_bought;" + id2);

	}

}
