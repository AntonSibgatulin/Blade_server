package com.AntonSibgatulin.location.deamons.npc;

import java.awt.Point;
import java.util.ArrayList;

import com.AntonSibgatulin.Players.Player;
import com.AntonSibgatulin.location.LocationModel;
import com.AntonSibgatulin.location.PlayerController;
import com.AntonSibgatulin.location.Square;
import com.AntonSibgatulin.location.Vector2d;
import com.AntonSibgatulin.location.deamons.lowmoon.StuffModel.BallsModel;
import com.AntonSibgatulin.location.generation.MapGeneration;

public class NPCDeamon implements Cloneable {
	public static final double NPC_DEAMON_SHOT = 1.6;
	public final static int SCORE_FOR_NPC = 10;
	public final static int MONEY_FOR_NPC = 5;

	public boolean parametrFast = false;
	public double parametreFast = 1;

	public boolean parametrAmor = false;

	public ArrayList<BallsModel> ballsModels = new ArrayList<>();
	public int MAX_SIZE_LIST_OF_BALLS = 5;
	public long idBalls = 0;

	public void initParametreAmor() {
		parametrAmor = true;

	}

	public int getAngle(PlayerController playerController) {

		int angle = 0;
		Vector2d position = getPosition();

		if (playerController.getPosition().x - position.x < 0 && playerController.getPosition().y - position.y < 0) {
			angle = (int) Math.abs(Math.atan2(playerController.getPosition().y - position.y,
					playerController.getPosition().x - position.x) * 180 / Math.PI);
		}

		if (playerController.getPosition().x - position.x < 0 && playerController.getPosition().y - position.y > 0) {
			angle = 180 + 180 - (int) (Math.atan2(playerController.getPosition().y - position.y,
					playerController.getPosition().x - position.x) * 180 / Math.PI);
		}

		if (playerController.getPosition().x - position.x > 0 && playerController.getPosition().y - position.y > 0) {
			angle = 360 - (int) (Math.atan2(playerController.getPosition().y - position.y,
					playerController.getPosition().x - position.x) * 180 / Math.PI);
		}
		if (playerController.getPosition().x - position.x > 0 && playerController.getPosition().y - position.y < 0) {
			angle = (int) Math.abs(Math.atan2(playerController.getPosition().y - position.y,
					playerController.getPosition().x - position.x) * 180 / Math.PI);
		}
		if (playerController.position.x == position.x && position.y > playerController.getPosition().y) {
			angle = 90;
		}
		if (playerController.position.x == position.x && position.y < playerController.getPosition().y) {
			angle = 270;
		}

		if (playerController.position.x > position.x && position.y == playerController.getPosition().y) {
			angle = 0;
		}
		if (playerController.position.x < position.x && position.y == playerController.getPosition().y) {
			angle = 180;
		}
		return angle;
	}

	public PlayerController playerController = null;
	public LocationModel locationModel = null;

	public void initParametre(double fastParametre) {
		parametreFast = fastParametre;

	}

	public Square pos = null;
	public Square lastPos = null;

	public int u = 20;
	public int side = 0;
	public boolean shot = false;
	public static int w = MapGeneration.SIZE * 2;
	public static int h = MapGeneration.SIZE * 3;
	public long health = 0;
	public String id = null;
	public boolean active = true;
	public double jump = 0;
	public double speed = 0;
	public boolean up = false;
	public Player player = null;
	public boolean right = false;

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void shot(int power, int map[][], PlayerController playerController) {
		health -= power * parametreFast;
		shot = true;
		jump(map);

		// jump
		if (health <= 0) {
			playerController.kill(power, this);
		} else {
			playerController.send("battle;shot;" + power);
		}

	}

	public NPCDeamon(Player player, String id, Point spawn) {
		this.player = player;

		this.id = id;
		pos = new Square(spawn.x, spawn.y, w, h);
		if (player != null) {
			this.health = (long) (player.health * parametreFast);
			this.speed = (player.go * parametreFast);

		}

		// TODO Auto-generated constructor stub
	}

	public void init() {

		this.health = (long) (player.health * parametreFast);
		this.speed = (int) Math.ceil(player.go * parametreFast);

	}

	public Vector2d getPosition() {
		return new Vector2d(pos.x, pos.y);
	}

	public void update(int[][] map, PlayerController[] players) {
		if (active) {
			PlayerController player = null;
			double length = 2147400000;

			if (players.length == 1) {
				player = players[0];
				length = player.getPosition().distanceToWithoutZ(getPosition());
			} else {

				for (int i = 0; i < players.length; i++) {
					PlayerController pl = players[i];
					if (pl.getPosition().distanceToWithoutZ(getPosition()) < length) {
						length = pl.getPosition().distanceToWithoutZ(getPosition());
						player = pl;
					}

				}
			}
			pos.y = (pos.y + PlayerController.GRAVITY - jump);

			ArrayList<Square> lists = new ArrayList<>();
			int size = 4;
			int posX = (int) (pos.x / MapGeneration.SIZE);
			int posY = (int) (pos.y / MapGeneration.SIZE);
			if (up) {
				jump -= PlayerController.GRAVITY / 2;
				if (jump <= -6)
					jump = -6;
				if (checkGround(map)) {
					jump = 0;
					up = !up;
					shot = false;
					speed = (int) Math.ceil(this.player.go * parametreFast);
				}
			}
			if (this.parametrAmor) {
				if (this.ballsModels.size() < MAX_SIZE_LIST_OF_BALLS) {
					idBalls += 1;
					BallsModel ballsModel = new BallsModel("Balls" + idBalls, getAngle(player), getPosition(),
							new Vector2d(1 * MapGeneration.SIZE, 1 * MapGeneration.SIZE));
					// this.ballsModels.add(ballsModel);
				}
			}

			int PX = (int) (pos.x - pos.w * 2) / MapGeneration.SIZE;
			int PY = (int) (pos.y) / MapGeneration.SIZE;

			for (int i = PX; i < PX + (pos.w * 5 / MapGeneration.SIZE); i++) {
				for (int j = PY; j < PY + pos.h / MapGeneration.SIZE + 1; j++) {
					if (i < 0 || j < 0 || i >= map.length || j >= map[0].length)
						continue;
					Square s = new Square(i * MapGeneration.SIZE, j * MapGeneration.SIZE, MapGeneration.SIZE,
							MapGeneration.SIZE);

					// Square.isLockIntersect(pos, s);
					Square.isLockIntersect(s, pos);
				}
			}

			/*
			 * for (int i = posX - size; i < posX + size; i++) { for (int j =
			 * posY - size; j < posY + size; j++) { if (i >= 0 && j >= 0 && i <
			 * map.length && j < map[0].length) { if (map[i][j] == 2 ||
			 * map[i][j] == 1) { Square s = new Square(i * MapGeneration.SIZE, j
			 * * MapGeneration.SIZE, MapGeneration.SIZE, MapGeneration.SIZE);
			 * lists.add(s); } }
			 * 
			 * } }
			 * 
			 * for (int i = 0; i < lists.size(); i++) { Square s = lists.get(i);
			 * Square.isLockIntersect(pos, s); }
			 */

			// if(player == null)return;
			// if(length>player.width*2)return;
			if (!shot) {
				if (player.position.x > pos.x) {
					right = true;
				} else
					right = false;

				if (right) {

					speed = (this.player.speed * parametreFast);
					side = 1;

				} else {
					side = -1;
					speed = -(this.player.speed * parametreFast);
				}
				/*
				 * if (!checkGround(map)) { // speed *= (6 + 1 + new
				 * Random().nextInt(3));
				 * 
				 * }
				 */

				pos.x += speed;

				if (pos.x > map.length * MapGeneration.SIZE)
					pos.x = map.length * MapGeneration.SIZE;
				if (pos.y > map[0].length * MapGeneration.SIZE) {
					active = false;
					pos.y = map[0].length * MapGeneration.SIZE;

				}
				if (pos.x <= 0)
					pos.x = 0;
				if (pos.y <= 0) {
					pos.y = 0;
					active = false;
				}
				// System.out.println(id+" "+checkGround(map));
				jump(map);

			} else {
				if (player.position.x > pos.x) {
					right = true;
				} else
					right = false;

				if (right) {
					speed = -(this.player.speed / parametreFast) * u;

				} else {
					speed = (this.player.speed / parametreFast) * u;
				}
				pos.x += speed;

			}
			/*
			 * for (int i = 0; i < shots.size(); i++) {
			 * 
			 * Shot shot = shots.get(i);
			 * 
			 * }
			 */
		}
	}

	public void jump(int[][] map) {
		if (up == false && checkGround(map)) {
			up = true;
			this.jump = (int) (player.jump);
			if (shot) {
				jump *= 5;
			} else
				jump *= 1.12;
			// speed *=1.02;
			// speed *= (6+1+new Random().nextInt(3));

		}
	}

	public boolean checkGround(int map[][]) {
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

}
