package com.AntonSibgatulin.location.house;

import java.util.ArrayList;
import java.util.HashMap;

import com.AntonSibgatulin.chat.MessageModel;
import com.AntonSibgatulin.location.Square;
import com.AntonSibgatulin.user.User;

public class HouseModel implements Cloneable {
	public HashMap<User, String> hashMap = new HashMap<>();
	public ArrayList<User> users = new ArrayList<>();
	public String id = null;

	public void addUser(User user) {
		hashMap.put(user, user.login);
		users.add(user);
	}

	public void removeUser(User user) {
		hashMap.remove(user);
		users.remove(user);
	}

	public String getUser(User user) {
		return hashMap.get(user);
	}

	public Square position = null;

	public ArrayList<MessageModel> messages = new ArrayList<>();

	public HouseModel(String id, int x, int y, int w, int h) {
		position = new Square(x, y, w, h);
		this.id = id;

	}

	public void addMessage(String text, User user) {
		if (getUser(user) != null
				&& user.playerController.position.isIntersect(user.playerController.position, position)) {
			MessageModel messageModel = new MessageModel(user.login, user.player.name, text);
			messages.add(messageModel);

		}
		if (getUser(user) != null
				&& user.playerController.position.isIntersect(user.playerController.position, position) == false) {
			hashMap.remove(user);
			users.remove(user);
		}
	}

	public void send_to_all(String text) {
		for (int i = 0; i < users.size(); i++) {
			User user = users.get(i);
			if (user.playerController.position.isIntersect(user.playerController.position, position)) {
				user.send("battle;home;message;" + user.login + ";" + user.player.name + ";" + text);
			} else {
				hashMap.remove(user);
				users.remove(user);
			}
		}

	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public HouseModel cloneHouse() {
		return new HouseModel(id, (int) position.x, (int) position.y, (int) position.w, (int) position.h);
	}

}
