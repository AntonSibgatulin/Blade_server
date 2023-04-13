package com.AntonSibgatulin.location.deamons;

import java.util.Random;

import com.AntonSibgatulin.Players.IBreath;
import com.AntonSibgatulin.Players.Player;
import com.AntonSibgatulin.location.LocationModel;
import com.AntonSibgatulin.location.PlayerController;
import com.AntonSibgatulin.location.Square;
import com.AntonSibgatulin.location.Vector2d;
import com.AntonSibgatulin.location.generation.MapGeneration;

public abstract class EDeamon {
	public Player player = null;
	public static final int TIME_DEACTIVATE = 3000;
	public static final int PROBABILITY_DEACTIVATE = 1 / 10;
	public static final int SIZE_OF_LENGTH = 100;
	public int side = 0;
	public boolean shot = false;
	public static int w = MapGeneration.SIZE * 2;
	public static int h = MapGeneration.SIZE * 3;

	public LocationModel locationModel = null;
	public Random random = new Random();
	public PlayerController playerController = null;
	public boolean deactivate = false;
	public long timeFromStartDeactivate = 0L;
	public long health = 100L;

	public static final double NPC_DEAMON_SHOT = 1.6;
	public final static int SCORE_FOR_NPC = 10;
	public final static int MONEY_FOR_NPC = 5;
	public Square pos = null;
	public Square lastPos = null;
	public String id = null;
	public boolean active = true;
	public double jump = 0;
	public double speed = 0;
	public boolean up = false;

	public boolean right = false;
	public IBreath IBreath = null;
	public String name = null;

	public int getSide() {
		// TODO Auto-generated method stub
		return side;
	}

	public int getW() {
		// TODO Auto-generated method stub
		return w;
	}

	public int getH() {
		// TODO Auto-generated method stub
		return h;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isAnotherPosition() {
		if (lastPos == null || pos.x != lastPos.x || pos.y != lastPos.y) {
			if (lastPos == null)
				lastPos = new Square(pos.x, pos.y, pos.w, pos.y);
			else {
				lastPos.x = pos.x;
				lastPos.y = pos.y;
			}
			return true;
		} else {
			return false;
		}
	}

	public void init(Player player) {
		System.out.println(player);
		this.player = player;
		this.health = player.health;
		this.speed = player.speed;
		// this.IBreath = (com.AntonSibgatulin.Players.IBreath)
		// player.ibreath.cl();
		// IBreath.setStyle(0);
		// this.speed = (int) Math.ceil(player.go);

	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public Vector2d getPosition() {
		// TODO Auto-generated method stub
		return new Vector2d(pos.x, pos.y);
	}

	public void jump() {
		if (up == false && checkGround()) {
			up = true;
			this.jump = (int) (player.jump);
			if (shot) {
				jump *= 1.91;
			} else
				jump *= 1.12;
			// speed *=1.02;
			// speed *= (6+1+new Random().nextInt(3));

		}
	}

	public long getHelath() {
		// TODO Auto-generated method stub
		return health;
	}

	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}

	public boolean checkGround() {
		int[][] map = null;
		if (playerController != null) {
			map = playerController.loc.map;
		}
		if (locationModel != null) {
			map = locationModel.maps.map;
		}
		int posX = (int) (pos.x / MapGeneration.SIZE);
		int posY = (int) (pos.y / MapGeneration.SIZE);

		int size = 4;

		for (int i = posX; i < (int) Math.ceil(posX + w / MapGeneration.SIZE); i++) {
			for (int j = posY; j < (int) Math.ceil(posY + h / MapGeneration.SIZE + 1); j++) {
				if (i >= 0 && j >= 0 && i < map.length && j < map[0].length) {
					if (map[i][j] == 2 || map[i][j] == 1) {
						Square s = new Square(i * MapGeneration.SIZE, j * MapGeneration.SIZE - MapGeneration.SIZE / 4,
								MapGeneration.SIZE, MapGeneration.SIZE);

						if (Square.isIntersect(s, pos))
							return true;
					}
				}

			}
		}
		return false;
	}

	public void deactivate() {

	}

	public Square getSquarePosition() {
		return pos;
	}

	public void setPlayerController(PlayerController controller) {
		this.playerController = controller;

	}

	public void setActive(boolean b) {
		active = b;

	}

	public void setLocation(LocationModel locationModel) {
		this.locationModel = locationModel;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
}
