package com.AntonSibgatulin.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.hibernate.Session;
import org.json.JSONObject;

import com.AntonSibgatulin.Players.BreathLoader;
import com.AntonSibgatulin.Players.HouseLoader;
import com.AntonSibgatulin.Players.PlayersLoader;
import com.AntonSibgatulin.Players.stuff.StuffLoader;
import com.AntonSibgatulin.location.LocationLoader;
import com.AntonSibgatulin.location.TaskManager;
import com.AntonSibgatulin.server.Server;
import com.AntonSibgatulin.shop.ShopModel;

import com.AntonSibgatulin.database.HibernateUtil;

public class Main {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws UnknownHostException {
		JSONObject config = null;
		// load configure from json file
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("config/socket.cfg")));
			String all = "";
			String piece = "";
			while ((piece = reader.readLine()) != null) {
				all += piece;

			}
			config = new JSONObject(all);

		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException();
		}

		StuffLoader stuffLoader = new StuffLoader("stuff");
		ShopModel shopModel = new ShopModel("shop");

		HouseLoader houseLoader = new HouseLoader("houses");
		PlayersLoader playersLoader = new PlayersLoader("players");

		LocationLoader locationLoader = new LocationLoader("location", houseLoader);

		BreathLoader breathLoader = new BreathLoader("breath");
		TaskManager taskManager = new TaskManager("tasks");
		InetAddress IP = InetAddress.getLocalHost();

		// run some server on websocket
		String host = config.getString("host");

		boolean debug = false;
		if (!debug) {
			host = IP.getHostAddress();
		}
		Session session = HibernateUtil.getSessionFactory().openSession();

		Server server = new Server(host, config.getInt("port"), playersLoader, breathLoader, locationLoader,
				taskManager, houseLoader, stuffLoader, shopModel);
		server.start();
		// run 1st connection to db
		// you even neednt has Session session = you can clear it

	}
}
