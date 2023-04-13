package com.AntonSibgatulin.shop;

public class ShopBoxModel {
	public int count = 0;
	public String id = null;
	public int price = 0;
	public int price_coin = 0;
	public int magikKey = 0;

	public ShopBoxModel(String id, int count, int price, int price_coin, int magicKey) {
		this.id = id;
		this.count = count;
		this.price = price;
		this.price_coin = price_coin;
		this.magikKey = magicKey;
	}
}