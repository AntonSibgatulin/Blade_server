package com.AntonSibgatulin.shop;

public class ShopPlayerModel {

	public int price = 0;
	public String id_ = null;
	public int score = 0;
	public int magicKey = -1;
	public String cards = null;
	public int count_of_cards = 0;

	public ShopPlayerModel(String id, int price, int score, int magicKey, String cards, int count_of_cards) {
		this.id_ = id;
		this.price = price;
		this.score = score;
		this.magicKey = magicKey;
		this.cards = cards;
		this.count_of_cards = count_of_cards;
	}

}
