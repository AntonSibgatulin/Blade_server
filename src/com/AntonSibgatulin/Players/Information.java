package com.AntonSibgatulin.Players;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.AntonSibgatulin.user.User;
import org.json.JSONObject;

@Entity
@Table(name = "players", schema = "public")
public class Information implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public User user = null;
	
	public int id = 0;
	public String players;
	public int userId = 0;
	@Column(name="id")
	public int getId(){
		return id;
		
	}
	public void setId(int id){
		this.id=id;
	}
	@Column(name="players")
	public String getPlayers(){
		return players;
	}
	public void setPlayers(String players){
		this.players = players;
	}
	@Column(name="userId")
	public int getUserId(){
		return this.userId;
	}
	public void setUserId(int userId){
		this.userId = userId;
	}
	public Information() {
		// TODO Auto-generated constructor stub
	}
	public Information(int id){
		setUserId(id);
		JSONObject jsonObject = new JSONObject("{\"players\":[{\"breath\":[{\"styles\":[0,1,2,3,4,5,6,7,8,9,10],\"is\":true,\"id\":\"WATER\"}],\"score\":0,\"is\":true,\"location_end\":[],\"id\":\"tanjiro\",\"type\":0,\"tasks\":[]}],\"location_end\":[]}");
		setPlayers(jsonObject.toString());

	}

	
	
	
	
	
}
