package com.AntonSibgatulin.location.deamons.lowmoon.RuiModel;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.AntonSibgatulin.location.PlayerController;
import com.AntonSibgatulin.location.Shot;
import com.AntonSibgatulin.location.Square;
import com.AntonSibgatulin.location.Vector2d;
import com.AntonSibgatulin.location.deamons.EDeamon;
import com.AntonSibgatulin.location.deamons.IDeamons;
import com.AntonSibgatulin.location.deamons.lowmoon.StuffModel.LineModel;
import com.AntonSibgatulin.location.deamons.npc.NPCDeamon;
import com.AntonSibgatulin.location.generation.MapGeneration;

public class RuiModel extends EDeamon implements IDeamons, Cloneable {

	public static int w = MapGeneration.SIZE * 2;
	public static int h = MapGeneration.SIZE * 3;
	public static int MAX_COUNT_LINE = 5;

	public ArrayList<LineModel> lineModels = new ArrayList<>();
	public int indexI = 0;
	public int u = 4;

	public RuiModel(Vector2d vector2d) {
		this.pos = new Square(vector2d.x, vector2d.y, w, h);

	}

	@Override
	public void shot(int shoot, PlayerController playerController) {
		health -= shoot;
		shot = true;
		jump();

		// jump
		if (health <= 0) {
			playerController.kill(shoot, this);
		} else {
			playerController.send("battle;shot;" + shoot);
		}

	}

	public RuiModel(String id, Vector2d vector2d, String name) {

		this.id = id;

		this.pos = new Square(vector2d.x, vector2d.y, w, h);

		this.name = name;
	}

	@Override
	public void fire() {

		if (locationModel == null) {
			if (playerController == null) {
				return;
			}

		} else {

		}
	}

	@Override
	public void update(PlayerController playerController) {
		if (this.locationModel == null)
			return;

		int[][] map = locationModel.maps.map;
		JSONArray jsonArray = new JSONArray();
		ArrayList<LineModel> lineModels = (ArrayList<LineModel>) this.lineModels.clone();
		for (int i = 0; i < lineModels.size(); i++) {
			LineModel lineModel = lineModels.get(i);
			if (lineModel.length < lineModel.lengthMax) {
				lineModel.length += lineModel.speedLength;
				if (lineModel.length >= lineModel.lengthMax) {
					lineModel.length = lineModel.lengthMax;
				}
				lineModel.time_start = System.currentTimeMillis();
			}
			if (lineModel.time_start != 0
					&& System.currentTimeMillis() - lineModel.time_start > lineModel.TIME_IS_USING_LINE) {
				this.lineModels.remove(lineModel);
			}

			for (int k = 0; k < playerController.fights.size(); k++) {
				Shot shot = playerController.fights.get(k);
				if (lineModel.CheckCollision(shot.pos)) {
					this.lineModels.remove(lineModel);

				}

			}
			lineModel.angle = getAngle(playerController);

			lineModel.pointStart.x = getPosition().x + pos.w / 2;
			lineModel.pointStart.y = getPosition().y + pos.h / 2;

			jsonArray.put(lineModel.getJSONData());

			/*
			 * if (lineModel.CheckCollision(playerController.position)) {
			 * boolean right = pos.x > playerController.position.x ? true :
			 * false; playerController.jump(player.jump, player.speed * 2,
			 * right);
			 * 
			 * playerController .shot_me((1 + player.id) *
			 * NPCDeamon.NPC_DEAMON_SHOT / playerController.amorInventory); }
			 */
			if (lineModel.CheckCollision(playerController.position)) {
				boolean right = pos.x > playerController.position.x ? true : false;
				playerController.jump(player.jump, player.speed * 2, right);

				playerController.shot_me((1 + player.id) * NPCDeamon.NPC_DEAMON_SHOT / playerController.amorInventory);
			}
		}
		if (jsonArray.length() > 0) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lines", jsonArray);
			jsonObject.put("id", id);
			locationModel.sendEverybody("battle;lines;" + jsonObject);
		}

		if (Math.abs(playerController.position.x - pos.x) > SIZE_OF_LENGTH * MapGeneration.SIZE)
			return;

		// ---------------------------------------------------------------
		if (active) {

			pos.y = (pos.y + PlayerController.GRAVITY - jump);

			ArrayList<Square> lists = new ArrayList<>();
			int size = 4;
			int posX = (int) (pos.x / MapGeneration.SIZE);
			int posY = (int) (pos.y / MapGeneration.SIZE);
			if (up) {
				jump -= PlayerController.GRAVITY / 2;
				if (jump <= -6)
					jump = -6;
				if (checkGround()) {
					jump = 0;
					up = !up;
					shot = false;
					speed = (int) Math.ceil(this.player.go);
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

			if (!shot) {
				if (playerController.position.x > pos.x) {
					right = true;
				} else
					right = false;
				if (playerController.getPosition().distanceToWithoutZ(getPosition()) > 8 * MapGeneration.SIZE
						|| right && side == -1 || !right && side == 1) {

					if (right) {

						speed = (this.player.speed);
						side = 1;

					} else {
						side = -1;
						speed = -(this.player.speed);
					}
					if (!checkGround()) {
						// speed *= (6 + 1 + new Random().nextInt(3));

					}
					pos.x += speed;

				} else {
					if (lineModels.size() < MAX_COUNT_LINE) {
						Vector2d vector2d = getPosition();
						vector2d.x += w / 2;
						vector2d.y += h / 2;
						int angle = 0;
						indexI += 1;

						LineModel lineModel = new LineModel("Id" + indexI, vector2d, angle);
						lineModel.lengthMax = 10 * MapGeneration.SIZE;
						this.lineModels.add(lineModel);
					}
				}
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
				jump();

			} else {
				if (playerController.position.x > pos.x) {
					right = true;
				} else
					right = false;

				if (right) {
					speed = -(this.player.speed) * u;

				} else {
					speed = (this.player.speed) * u;
				}
				pos.x += speed;

			}
		}
	}

	// @Override
	public void update1(PlayerController playerController) {

		if (this.locationModel == null)
			return;

		int[][] map = locationModel.maps.map;

		ArrayList<LineModel> lineModels = (ArrayList<LineModel>) this.lineModels.clone();
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < lineModels.size(); i++) {
			LineModel lineModel = lineModels.get(i);
			if (lineModel.length < lineModel.lengthMax) {
				lineModel.length += lineModel.speedLength;
				if (lineModel.length >= lineModel.lengthMax) {
					lineModel.length = lineModel.lengthMax;
				}
			}
			lineModel.pointStart.x = getPosition().x + pos.w / 2;
			lineModel.pointStart.y = getPosition().y + pos.y / 2;
			jsonArray.put(lineModel.getJSONData());

			for (int k = 0; k < playerController.fights.size(); k++) {
				Shot shot = playerController.fights.get(k);
				if (lineModel.CheckCollision(shot.pos)) {
					this.lineModels.remove(lineModel);

				}

			}

			if (lineModel.CheckCollision(playerController.position)) {
				boolean right = pos.x > playerController.position.x ? true : false;
				playerController.jump(player.jump, player.speed * 2, right);

				playerController.shot_me((1 + player.id) * NPCDeamon.NPC_DEAMON_SHOT / playerController.amorInventory);
			}

		}
		if (jsonArray.length() > 0) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lines", jsonArray);
			jsonObject.put("id", id);

			// TODO Auto-generated method stub
			locationModel.sendEverybody("battle;lines;" + jsonObject);

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
			if (checkGround()) {
				jump = 0;
				up = !up;
				shot = false;
				speed = (int) Math.ceil(this.player.go);
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

		PlayerController player = playerController;
		if (Math.abs(player.position.x - pos.x) > SIZE_OF_LENGTH * MapGeneration.SIZE)
			return;
		// ---------------------------------------------------------------
		if (active) {

			if (!shot) {
				if (player.position.x > pos.x) {
					right = true;
				} else
					right = false;
				if (player.getPosition().distanceToWithoutZ(getPosition()) > 8 * MapGeneration.SIZE
						|| right && side == -1 || !right && side == 1) {

					if (right) {

						speed = (this.player.speed);
						side = 1;

					} else {
						side = -1;
						speed = -(this.player.speed);
					}
					if (!checkGround()) {
						// speed *= (6 + 1 + new Random().nextInt(3));

					}
					pos.x += speed;

				} else {
					if (lineModels.size() < MAX_COUNT_LINE) {
						Vector2d vector2d = getPosition();
						vector2d.x += w / 2;
						vector2d.y += h / 2;
						int angle = 0;
						indexI += 1;

						LineModel lineModel = new LineModel("Id" + indexI, vector2d, angle);
						lineModel.lengthMax = 6 * MapGeneration.SIZE;
						this.lineModels.add(lineModel);
					}
				}
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
				jump();

			} else {
				if (player.position.x > pos.x) {
					right = true;
				} else
					right = false;

				if (right) {
					speed = -(this.player.speed) * u;

				} else {
					speed = (this.player.speed) * u;
				}
				pos.x += speed;

			}

			return;
		}
	}

	@Override
	public void update() {
		int[][] map = null;
		if (playerController != null) {
			map = playerController.loc.map;
		}
		if (locationModel != null) {
			map = locationModel.maps.map;
		}
		if (locationModel == null) {
			if (playerController == null) {
				return;
			}

			JSONArray jsonArray = new JSONArray();
			ArrayList<LineModel> lineModels = (ArrayList<LineModel>) this.lineModels.clone();
			for (int i = 0; i < lineModels.size(); i++) {
				LineModel lineModel = lineModels.get(i);
				if (lineModel.length < lineModel.lengthMax) {
					lineModel.length += lineModel.speedLength;
					if (lineModel.length >= lineModel.lengthMax) {
						lineModel.length = lineModel.lengthMax;
					}
					lineModel.time_start = System.currentTimeMillis();
				}
				if (lineModel.time_start != 0
						&& System.currentTimeMillis() - lineModel.time_start > lineModel.TIME_IS_USING_LINE) {
					this.lineModels.remove(lineModel);
				}

				for (int k = 0; k < playerController.fights.size(); k++) {
					Shot shot = playerController.fights.get(k);
					if (lineModel.CheckCollision(shot.pos)) {
						this.lineModels.remove(lineModel);

					}

				}
				lineModel.angle = getAngle(playerController);

				lineModel.pointStart.x = getPosition().x + pos.w / 2;
				lineModel.pointStart.y = getPosition().y + pos.h / 2;

				jsonArray.put(lineModel.getJSONData());

				/*
				 * if (lineModel.CheckCollision(playerController.position)) {
				 * boolean right = pos.x > playerController.position.x ? true :
				 * false; playerController.jump(player.jump, player.speed * 2,
				 * right);
				 * 
				 * playerController .shot_me((1 + player.id) *
				 * NPCDeamon.NPC_DEAMON_SHOT / playerController.amorInventory);
				 * }
				 */
				if (lineModel.CheckCollision(playerController.position)) {
					boolean right = pos.x > playerController.position.x ? true : false;
					playerController.jump(player.jump, player.speed * 2, right);

					playerController
							.shot_me((1 + player.id) * NPCDeamon.NPC_DEAMON_SHOT / playerController.amorInventory);
				}
			}
			if (jsonArray.length() > 0) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("lines", jsonArray);
				jsonObject.put("id", id);
				playerController.send("battle;lines;" + jsonObject);
			}

			PlayerController player = playerController;

			if (Math.abs(player.position.x - pos.x) > SIZE_OF_LENGTH * MapGeneration.SIZE)
				return;

			// ---------------------------------------------------------------
			if (active) {

				pos.y = (pos.y + PlayerController.GRAVITY - jump);

				ArrayList<Square> lists = new ArrayList<>();
				int size = 4;
				int posX = (int) (pos.x / MapGeneration.SIZE);
				int posY = (int) (pos.y / MapGeneration.SIZE);
				if (up) {
					jump -= PlayerController.GRAVITY / 2;
					if (jump <= -6)
						jump = -6;
					if (checkGround()) {
						jump = 0;
						up = !up;
						shot = false;
						speed = (int) Math.ceil(this.player.go);
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

				if (!shot) {
					if (player.position.x > pos.x) {
						right = true;
					} else
						right = false;
					if (player.getPosition().distanceToWithoutZ(getPosition()) > 8 * MapGeneration.SIZE
							|| right && side == -1 || !right && side == 1) {

						if (right) {

							speed = (this.player.speed);
							side = 1;

						} else {
							side = -1;
							speed = -(this.player.speed);
						}
						if (!checkGround()) {
							// speed *= (6 + 1 + new Random().nextInt(3));

						}
						pos.x += speed;

					} else {
						if (lineModels.size() < MAX_COUNT_LINE) {
							Vector2d vector2d = getPosition();
							vector2d.x += w / 2;
							vector2d.y += h / 2;
							int angle = 0;
							indexI += 1;

							LineModel lineModel = new LineModel("Id" + indexI, vector2d, angle);
							lineModel.lengthMax = 6 * MapGeneration.SIZE;
							this.lineModels.add(lineModel);
						}
					}
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
					jump();

				} else {
					if (player.position.x > pos.x) {
						right = true;
					} else
						right = false;

					if (right) {
						speed = -(this.player.speed) * u;

					} else {
						speed = (this.player.speed) * u;
					}
					pos.x += speed;

				}
			}

		}
	}

	// get angle
	public static void main1(String... args) {
		PlayerController playerController = new PlayerController();
		playerController.position = new Square(2, -2, 1, 1);
		RuiModel ruiModel = new RuiModel(new Vector2d(0, 0));

		// System.out.println(Math.atan2(-2, -4) * 180 / Math.PI);

		System.out.println(ruiModel.getAngle(playerController));
	}

	public int getAngle(PlayerController playerController1) {
		PlayerController playerController = new PlayerController(
				new Vector2d(playerController1.position.x, playerController1.position.y),
				new Vector2d(playerController1.position.w, playerController1.position.h));
		playerController.position.y += playerController.position.h / 2
				- new Random().nextInt((int) (playerController.position.h * (player.jump + 2)));
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

}
