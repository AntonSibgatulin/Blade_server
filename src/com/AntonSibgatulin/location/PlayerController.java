package com.AntonSibgatulin.location;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Timer;

import org.json.JSONArray;
import org.json.JSONObject;

import com.AntonSibgatulin.Players.IBreath;
import com.AntonSibgatulin.Players.Information;
import com.AntonSibgatulin.Players.Player;
import com.AntonSibgatulin.location.deamons.IDeamons;
import com.AntonSibgatulin.location.deamons.lowmoon.StuffModel.BallsModel;
import com.AntonSibgatulin.location.deamons.npc.NPCDeamon;
import com.AntonSibgatulin.location.generation.MapGeneration;
import com.AntonSibgatulin.user.User;

public class PlayerController implements Cloneable {

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

	public String team = null;
	public boolean shot = false;
	public int idPlayer = 0;
	public int lastSent = 0;
	public int side = 1;// -1 = left 1 = right
	public ArrayList<Shot> fights = new ArrayList<>();
	public static int GRAVITY = 12;
	public int g = GRAVITY;
	public int width = 0;
	public int height = 0;
	public boolean single = true;
	public boolean active = true;
	public LocationModel locationModel = null;
	public boolean left = false;
	public boolean right = false;
	public boolean up = false;
	public boolean down = false;
	public boolean pause = false;

	public boolean shift_jump = false;
	public boolean shift = false;
	public TaskManager taskManager = null;
	public TaskModel taskModel = null;

	public boolean train = false;
	public boolean moveon = false;

	public double jump = 0;
	public Square lastPos = null;

	public double powerInventory = 1;
	public double nitroInventory = 1;
	public double amorInventory = 1;

	public int heart = 0;

	public long powerInventoryStart = 0;
	public long amorInventoryStart = 0;
	public long nitroInventoryStart = 0;

	public boolean gameOver = false;
	public static final int TIME_POWER_INVENTORY = 20000;// in ms
	public static final int TIME_AMOR_INVENTORY = 20000;// in ms
	public static final int TIME_NITRO_INVENTORY = 20000;// in ms

	public Player player = null;
	public Information info = null;
	public IBreath ibreath = null;
	public double breath = 0;
	public Square position = null;
	public int power = 0;
	public double energy = 0;
	public double energy_recovery = 0;

	public User user = null;
	public long health = 0;
	public double speed = 0;
	public MapGeneration loc = null;

	public int live = 3;

	public void gameOver() {
		active = false;
		gameOver = true;
		send("battle;gameOver");

	}

	public void sendCountOfHeart() {

		send("battle;set_heart;" + heart);
	}

	public void send_shot_me(Double damage) {
		user.send("battle;shot_me;" + (health * 100 / player.health) + ";" + (damage));
	}

	public void sendData() {
		user.send("battle;set_health_me;" + (health * 100 / player.health));
	}

	public void shot_me(double damage) {
		health -= damage;

		if (health <= 0) {
			heart -= 1;
			if (heart <= -1) {
				if (locationModel == null) {
					gameOver();
				} else {
					locationModel.gameOver(this);
				}
			} else {
				health = player.health;
				sendCountOfHeart();
				sendData();
			}
		} else {
			send_shot_me(damage);

		}

	}

	public void actionMultiply() {

	}

	int indexSending = 0;
	public Timer t = new Timer(40, new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			if (pause == false && gameOver == false) {
				// System.out.println("action");
				if (single) {

					if (active) {
						// System.out.println(player.jsonObject.toString());
						energy += energy_recovery * powerInventory;
						if (shift && (left || right)) {
							energy -= energy_recovery;
						}
						if (left || right) {
							energy -= energy_recovery / 2;
						}
						if (energy >= player.energy)
							energy = player.energy;
						if (energy <= 0)
							energy = 0;

						if (checkGround() == false || up)
							update();

						if (position.x <= 0)
							position.x = 0;
						if (position.y <= 0)
							position.y = 0;

						if (position.x >= MapGeneration.SIZE * loc.map.length)
							position.x = MapGeneration.SIZE * loc.map.length;
						if (position.y >= MapGeneration.SIZE * loc.map[0].length)
							position.y = MapGeneration.SIZE * loc.map[0].length;

						int posX = (int) (position.x / MapGeneration.SIZE);
						int posY = (int) (position.y / MapGeneration.SIZE);

						ArrayList<Square> lists = new ArrayList<>();

						/*
						 * if(checkGround()){ //g=1; }else{ // g=GRAVITY; }
						 */

						if (up) {
							jump -= GRAVITY / 6;
							if (jump <= -5)
								jump = -5;
							if (checkGround()) {
								jump = 0;
								up = !up;
								shift_jump = false;
								speed = (player.go * nitroInventory);
								shot = false;

							}
						}

						// if(right){

						// }
						if (shift && !up && shot == false && energy - energy_recovery >= 0) {

							// System.out.println("run "+player.run);
							// System.out.println("go "+player.go);

							speed = player.speed * nitroInventory;
						} else {
							if (shift && !up && shot == false && energy - energy_recovery < 0) {
								speed = player.go * nitroInventory;
							}
						}

						int size = 4;
						/*
						 * for (int i = posX - size; i < posX + size; i++) { for
						 * (int j = posY - size; j < posY + size; j++) { if (i
						 * >= 0 && j >= 0 && i < loc.map.length && j <
						 * loc.map[0].length) { if (loc.map[i][j] == 200) {
						 * Square s = new Square(i * MapGeneration.SIZE, j *
						 * MapGeneration.SIZE, MapGeneration.SIZE,
						 * MapGeneration.SIZE); if (Square.isIntersect(s,
						 * position)) { loc.map[i][j] = 0; send_change_tile(i,
						 * j, 0); user.money += 1; send_score_money();
						 * 
						 * } }
						 * 
						 * }
						 * 
						 * } }
						 */

						// check time out of inventory
						if (powerInventory != 1) {
							if (getTime() - powerInventoryStart >= TIME_POWER_INVENTORY) {
								powerInventory = 1;
								send("battle;remove_supplies;power");

							}
						}

						if (nitroInventory != 1) {
							if (getTime() - nitroInventoryStart >= TIME_NITRO_INVENTORY) {
								nitroInventory = 1;
								send("battle;remove_supplies;nitro");
							}
						}

						if (amorInventory != 1) {
							if (getTime() - amorInventoryStart >= TIME_AMOR_INVENTORY) {
								amorInventory = 1;
								send("battle;remove_supplies;amor");
							}
						}

						// check collision with the map
						int PX = (int) (position.x - position.w * 2) / MapGeneration.SIZE;
						int PY = (int) (position.y) / MapGeneration.SIZE;

						for (int i = PX; i < PX + (position.w * 5 / MapGeneration.SIZE); i++) {
							for (int j = PY; j < PY + position.h / MapGeneration.SIZE + 1; j++) {
								if (i < 0 || j < 0 || i >= loc.map.length || j >= loc.map[0].length)
									continue;
								if (loc.map[i][j] == 200) {
									Square s = new Square(i * MapGeneration.SIZE, j * MapGeneration.SIZE,
											MapGeneration.SIZE, MapGeneration.SIZE);
									if (Square.isIntersect(s, position)) {
										loc.map[i][j] = 0;
										send_change_tile(i, j, 0);
										user.money += 1;
										user.send_score_money();

									}
								}
								// health
								if (loc.map[i][j] == 4) {
									Square s = new Square(i * MapGeneration.SIZE, j * MapGeneration.SIZE,
											MapGeneration.SIZE, MapGeneration.SIZE);
									if (Square.isIntersect(s, position)) {
										loc.map[i][j] = 0;
										send_change_tile(i, j, 0);
										health = player.health;
										sendData();
									}
								}
								// power
								if (loc.map[i][j] == 5) {
									Square s = new Square(i * MapGeneration.SIZE, j * MapGeneration.SIZE,
											MapGeneration.SIZE, MapGeneration.SIZE);
									if (Square.isIntersect(s, position)) {
										loc.map[i][j] = 0;
										send_change_tile(i, j, 0);
										health = player.health;
										send("battle;add_supplies;power");
										powerInventory = 2;
										powerInventoryStart = getTime();

									}
								}
								// amor
								if (loc.map[i][j] == 6) {
									Square s = new Square(i * MapGeneration.SIZE, j * MapGeneration.SIZE,
											MapGeneration.SIZE, MapGeneration.SIZE);
									if (Square.isIntersect(s, position)) {
										loc.map[i][j] = 0;
										send_change_tile(i, j, 0);
										send("battle;add_supplies;amor");
										amorInventory = 2;
										amorInventoryStart = getTime();

									}
								}
								// nitro
								if (loc.map[i][j] == 7) {
									Square s = new Square(i * MapGeneration.SIZE, j * MapGeneration.SIZE,
											MapGeneration.SIZE, MapGeneration.SIZE);
									if (Square.isIntersect(s, position)) {
										loc.map[i][j] = 0;
										send_change_tile(i, j, 0);
										send("battle;add_supplies;nitro");
										nitroInventory = 1.3;
										nitroInventoryStart = getTime();

									}
								}
								if (loc.map[i][j] == 2 || loc.map[i][j] == 1) {

									/*
									 * Square.isLockIntersect(position, new
									 * Square(i * MapGeneration.SIZE, PY *
									 * MapGeneration.SIZE, MapGeneration.SIZE,
									 * MapGeneration.SIZE));
									 */
									Square square = new Square(i * MapGeneration.SIZE, j * MapGeneration.SIZE,
											MapGeneration.SIZE, MapGeneration.SIZE);
									Square.isLockIntersect(position, square);

									// return true;
								}

							}
						}
						/*
						 * int PX = (int) (position.x) / MapGeneration.SIZE; int
						 * PY = (int) (position.y) / MapGeneration.SIZE; for
						 * (int i = PX; i < PX + position.w /
						 * MapGeneration.SIZE; i++) { for (int j = PY; j < PY +
						 * position.h / MapGeneration.SIZE + 1; j++) { if
						 * (loc.map[i][j] == 2 || loc.map[i][j] == 1) {
						 * 
						 * Square square = new Square(i * MapGeneration.SIZE, j
						 * * MapGeneration.SIZE, MapGeneration.SIZE,
						 * MapGeneration.SIZE); Square.isLockIntersect(position,
						 * square); }
						 * 
						 * } }
						 */
						// updates();
						/*
						 * for (int i = 0; i < lists.size(); i++) { Square s =
						 * lists.get(i); if (Square.isIntersect(position, s)) {
						 * 
						 * } // Square.isLockIntersect(position, s); }
						 */
						if (!shot) {
							if (left) {
								position.x -= nitroInventory * speed;
							}
							if (right) {

								position.x += nitroInventory * speed;
							}
						} else {
							position.x += (speed) / (nitroInventory);
						}

						if (user != null) {

							if (lastPos != null && (lastPos.x - position.x == 0) && moveon == false) {
								user.send("battle;pos_stay");

								moveon = true;

							}
							if (lastPos == null || lastPos.x - position.x != 0 || lastPos.y - position.y != 0) {
								lastPos = new Square(position.x, position.y, 10, 10);

								user.send("battle;pos;" + position.x + ";" + position.y + ";" + side);
								moveon = false;
							}

						}

						// user.send("battle;pos;"+position.x+";"+position.y);

						// check finish
						if (position.x >= (loc.map.length - 6) * MapGeneration.SIZE) {

							position.x = (loc.map.length - 6) * MapGeneration.SIZE;
							int checkY = 0;
							for (int l = 0; l < loc.map[(loc.map.length - 6)].length; l++) {
								if (loc.map[(loc.map.length - 6)][l] == 2) {
									checkY = l - 1;
								}
							}

							if (checkY != 0 && position.x == (loc.map.length - 6) * MapGeneration.SIZE
									&& Math.abs(position.y - checkY * MapGeneration.SIZE) <= MapGeneration.SIZE * 3) {
								if (!loc.killAllDeamons || loc.killAllDeamons && loc.deamons.size() == 0) {
									pause = true;
									user.money += loc.money / (user.rang + 1);
									user.score += loc.score / (user.rang + 1);
									// System.out.println("MONEY " + loc.money +
									// "
									// SCORE " + loc.score);
									send("battle;end;" + (loc.money / (user.rang + 1)) + ";"
											+ (loc.score / (user.rang + 1)));
									finish();
									user.send_score_money();
									user.exit();
								}

							}

						}

						// loading map
						if (lastSent != loc.map.length
								&& lastSent - (posX + width / MapGeneration.SIZE) <= width / MapGeneration.SIZE * 4) {
							JSONArray jsonArray = new JSONArray();
							int end = (lastSent + width / MapGeneration.SIZE * 12);
							if (end >= loc.map.length)
								end = loc.map.length;
							for (int i = lastSent; i < (int) end; i++) {
								// ||\\
								JSONArray jsonArray2 = new JSONArray();
								for (int j = 0; j < loc.map[0].length; j++) {
									jsonArray2.put(loc.map[i][j]);

								}
								jsonArray.put(jsonArray2);
								// ||\\
							}
							JSONObject jsonObject2 = new JSONObject();
							jsonObject2.put("map", jsonArray);

							// System.out.println("sent
							// "+jsonObject2.toString().length());
							lastSent = end;
							send("battle;map_add;" + jsonObject2);
						}

						// check colision with shot from breath
						if (ibreath != null) {
							JSONObject jsonObject = new JSONObject();
							JSONArray jsonArray = new JSONArray();

							for (int i = 0; i < fights.size(); i++) {
								Shot shot = fights.get(i);
								if (shot == null)
									continue;
								Square square = shot.pos;
								fights.get(i).updatePos();
								for (int j = 0; j < loc.deamons.size(); j++) {

									NPCDeamon deamon = loc.deamons.get(j);
									double distance = deamon.getPosition().distanceToWithoutZ(getPosition());
									if (distance <= width) {
										if (Square.isIntersect(square, deamon.pos)) {

											deamon.shot((int) (ibreath.getPowerOfStrike() * powerInventory), loc.map,
													PlayerController.this);
										}
									}
								}
								for (int j = 0; j < loc.iDeamons.size(); j++) {

									IDeamons deamon = loc.iDeamons.get(j);
									double distance = deamon.getPosition().distanceToWithoutZ(getPosition());
									if (distance <= width) {
										if (Square.isIntersect(square, deamon.getSquarePosition())) {

											deamon.shot((int) (ibreath.getPowerOfStrike() * powerInventory),
													PlayerController.this);
										}
									}
								}

								JSONObject jsonObject2 = new JSONObject();
								jsonObject2.put("x", square.x);
								jsonObject2.put("y", square.y);
								jsonObject2.put("w", square.w);
								jsonObject2.put("h", square.h);
								jsonArray.put(jsonObject2);

							}
							jsonObject.put("fights", jsonArray);
							if (jsonArray.length() > 0)
								send("battle;fight_box;" + jsonObject.toString());

						}

					}

				}
				// update logic for deamons
				if (loc != null) {
					JSONArray jsonArrayNPC = new JSONArray();
					// System.out.println("size "+loc.deamons.size());
					for (int i = 0; i < loc.deamons.size(); i++) {
						NPCDeamon npcDeamon = loc.deamons.get(i);
						if (i >= loc.deamons.size())
							break;

						ArrayList<BallsModel> ballsModels = (ArrayList<BallsModel>) npcDeamon.ballsModels.clone();
						JSONArray jsonArray = new JSONArray();
						for (int x = 0; x < ballsModels.size(); x++) {
							BallsModel ballsModel = ballsModels.get(x);
							ballsModel.update();
							if (ballsModel.active == false) {
								npcDeamon.ballsModels.remove(ballsModel);
								send("battle;balls_remove;" + npcDeamon.id + ";" + ballsModel.id);
								continue;
							}
							if (Square.isIntersect(position, ballsModel.pos)) {

								npcDeamon.ballsModels.remove(ballsModel);
								send("battle;balls_remove;" + npcDeamon.id + ";" + ballsModel.id);
								boolean right = npcDeamon.pos.x > position.x ? true : false;
								jump(player.jump, player.speed * 2, right);
								shot_me((1 + npcDeamon.player.id) * NPCDeamon.NPC_DEAMON_SHOT / amorInventory);
								continue;
							}
							JSONObject jsonObject = ballsModel.getJSONObject();

							jsonArray.put(jsonObject);
						}
						if (jsonArray.length() > 0) {
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("balls", jsonArray);
							send("battle;balls;" + npcDeamon.id + ";" + jsonObject.toString());
						}

						double distance = npcDeamon.getPosition().distanceToWithoutZ(getPosition());
						if (distance <= width / 3 * 2) {
							if (npcDeamon.active) {

								if (npcDeamon.pos.x <= 55 * MapGeneration.SIZE) {
									npcDeamon.pos.x = (new Random().nextInt(5) + 55) * MapGeneration.SIZE;
								}
								if (npcDeamon.pos.x >= (loc.map.length - 55) * MapGeneration.SIZE) {
									npcDeamon.pos.x = (loc.map.length - (new Random().nextInt(4) + 55))
											* MapGeneration.SIZE;
								}

								if (Square.isIntersect(position, npcDeamon.pos)) {
									boolean right = npcDeamon.pos.x > position.x ? true : false;
									jump(player.jump, player.speed * 2, right);
									shot_me((1 + npcDeamon.player.id) * NPCDeamon.NPC_DEAMON_SHOT / amorInventory);
									// dont forget about amor
								}
								for (int kk = 0; kk < loc.iteration_of_deamons; kk++) {
									Square.isLockIntersect(npcDeamon.pos, position);
									npcDeamon.update(loc.map, new PlayerController[] { PlayerController.this });
								}

								if (npcDeamon.lastPos == null || npcDeamon.pos.x != npcDeamon.lastPos.x
										|| npcDeamon.pos.y != npcDeamon.lastPos.y) {
									/*
									 * System.out.println(npcDeamon.id +" "+
									 * distance);
									 */

									if (lastPos == null)
										lastPos = new Square();
									lastPos.x = npcDeamon.pos.x;
									lastPos.y = npcDeamon.pos.y;

									JSONObject jsonObject = new JSONObject();
									jsonObject.put("posx", npcDeamon.pos.x);
									jsonObject.put("posy", npcDeamon.pos.y);
									jsonObject.put("side", npcDeamon.side);
									jsonObject.put("w", npcDeamon.pos.w);
									jsonObject.put("h", npcDeamon.pos.h);
									jsonObject.put("Type", npcDeamon.player.name);
									jsonObject.put("id", npcDeamon.id);
									// jsonObject.put("balls", jsonArray);
									jsonArrayNPC.put(jsonObject);
									// send("battle;npc;" +
									// jsonObject.toString());

								}
							} else {
								send("battle;npc_remove;" + npcDeamon.id);
								loc.deamons.remove(npcDeamon);

							}
						}
					}
					/*
					 * if (jsonArrayNPC.length() > 0) { JSONObject jsonObjectNPC
					 * = new JSONObject(); jsonObjectNPC.put("npcs",
					 * jsonArrayNPC); send("battle;npcs;" + jsonObjectNPC); }
					 */
					// jsonArrayNPC = new JSONArray();

					for (int i = 0; i < loc.iDeamons.size(); i++) {
						IDeamons npcDeamon = loc.iDeamons.get(i);
						if (i >= loc.deamons.size())
							break;
						double distance = npcDeamon.getPosition().distanceToWithoutZ(getPosition());
						if (distance <= width / 3 * 2) {
							if (npcDeamon.isActive()) {

								if (npcDeamon.getPosition().x <= 55 * MapGeneration.SIZE) {
									npcDeamon.getPosition().x = (new Random().nextInt(5) + 55) * MapGeneration.SIZE;
								}
								if (npcDeamon.getPosition().x >= (loc.map.length - 55) * MapGeneration.SIZE) {
									npcDeamon.getPosition().x = (loc.map.length - (new Random().nextInt(4) + 55))
											* MapGeneration.SIZE;
								}

								// if (Square.isIntersect(position,
								// npcDeamon.pos)) {
								// boolean right = npcDeamon.pos.x > position.x
								// ? true : false;
								// jump(npcDeamon.player.jump, player.speed * 2,
								// right);
								// shot_me((1 + npcDeamon.player.id) *
								// NPCDeamon.NPC_DEAMON_SHOT / amorInventory);
								// dont forget about amor
								// }

								for (int kk = 0; kk < loc.iteration_of_deamons; kk++) {
									Square.isLockIntersect(npcDeamon.getSquarePosition(), position);
									npcDeamon.update();
								}

								if (npcDeamon.isAnotherPosition()) {
									/*
									 * System.out.println(npcDeamon.id +" "+
									 * distance);
									 */

									if (lastPos == null)
										lastPos = new Square();
									lastPos.x = npcDeamon.getPosition().x;
									lastPos.y = npcDeamon.getPosition().y;

									JSONObject jsonObject = new JSONObject();
									jsonObject.put("posx", npcDeamon.getPosition().x);
									jsonObject.put("posy", npcDeamon.getPosition().y);
									jsonObject.put("side", npcDeamon.getSide());
									jsonObject.put("w", npcDeamon.getW());
									jsonObject.put("h", npcDeamon.getH());
									jsonObject.put("Type", npcDeamon.getName());
									jsonObject.put("id", npcDeamon.getId());
									jsonArrayNPC.put(jsonObject);
									// send("battle;npc;" +
									// jsonObject.toString());

								}
							} else {
								send("battle;npc_remove;" + npcDeamon.getId());
								loc.iDeamons.remove(npcDeamon);

							}
						}
					}
					if (jsonArrayNPC.length() > 0) {
						JSONObject jsonObjectNPC = new JSONObject();
						jsonObjectNPC.put("npcs", jsonArrayNPC);
						send("battle;npcs;" + jsonObjectNPC);
					}

				}

				// System.out.println(train);
				if (train) {
					// System.out.println("train");
					if (taskManager != null) {
						// System.out.println("taskManager is not null");

						if (taskModel == null) {
							// System.out.println("Task size
							// "+taskManager.task_list.size());
							if (taskManager.task_list.size() != 0) {
								// System.out.println("Task got");
								taskModel = taskManager.task_list.get(0);
								send("battle;task_add;" + taskModel.jsonObject.toString());
								pause = true;
							}
						}

						if (taskModel != null) {

							// System.out.println(taskModel.id.split("_")[0]);
							if (taskModel.id.split("_")[0].equals("running")) {
								if (left || right)
									taskModel.distanceit += speed;

								// System.out.println(taskModel.distanceit+"
								// "+taskModel.distance);

								if (taskModel.distanceit >= taskModel.distance * MapGeneration.SIZE) {
									fineDistance(taskModel);
								}
							}
						}

					}

				}
				if (energy >= player.energy)
					energy = player.energy;
				if (energy <= 0)
					energy = 0;

				if (energy != player.energy) {
					user.sendEnergy(energy);
				}

			}

		}

	});

	public void finish() {
		// public static final String[] types = { "train", "way_of_slayer" };
		// <---------

		JSONObject jsonObject = new JSONObject(user.info.players);
		if (loc.category.equals(LocationLoader.types[0])) {
			JSONArray jsonArray = jsonObject.getJSONArray("players");
			JSONObject jsonObject2 = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject3 = jsonArray.getJSONObject(i);

				if (jsonObject3.getBoolean("is")) {

					jsonObject2 = jsonObject3;
				}
			}

			jsonObject2.getJSONArray("location_end").put(LocationLoader.types[0] + "_" + loc.idint);
			user.info.setPlayers(jsonObject.toString());
		}
		System.out.println("Location end " + jsonObject.getJSONArray("location_end") + " " + loc.category);

		if (loc.category.equals(LocationLoader.types[1])) {
			jsonObject.getJSONArray("location_end").put(LocationLoader.types[1] + "_" + loc.idint);
			user.info.setPlayers(jsonObject.toString());
			System.out.println("Location type " + LocationLoader.types[1] + "_" + loc.idint + " "
					+ jsonObject.getJSONArray("location_end") + " " + jsonObject);
		}

	}

	public static JSONObject getPlayerJSON(User user) {
		JSONArray jsonObject = new JSONObject(user.info.players).getJSONArray("players");
		for (int i = 0; i < jsonObject.length(); i++) {
			JSONObject jsonObject2 = jsonObject.getJSONObject(i);

			if (jsonObject2.getBoolean("is")) {

				return jsonObject2;
			}
		}
		return null;

	}

	public void parseTask(TaskModel taskModel, User user) {
		Player player = user.player;
		player.speed += taskModel.addspeed;
		player.run += taskModel.addrun;
		JSONObject jsonObject = player.jsonObject.getJSONArray("levels").getJSONObject(player.id);

		jsonObject.put("speed", player.speed);
		jsonObject.put("run", player.run);
		user.send("lobby;change_data;" + player.jsonObject.getString("id") + ";" + player.jsonObject.toString());

	}

	public void send_change_tile(int i, int j, int k) {
		send("battle;change_tile;" + i + ";" + j + ";" + k);
		// TODO Auto-generated method stub

	}

	private void fineDistance(TaskModel taskModel) {
		JSONObject jsonObject = new JSONObject(user.info.players);
		if (jsonObject.getJSONArray("players").getJSONObject(getPlayerIndex(user)).getJSONArray("tasks").length() > 0) {
			jsonObject.getJSONArray("players").getJSONObject(getPlayerIndex(user)).getJSONArray("tasks")
					.getJSONObject(0).getJSONArray("things").put(Integer.valueOf(taskModel.id.split("_")[1]));
			send("battle;task_end;");
			parseTask(taskModel, user);
		} else {
			JSONObject jsonObject2 = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			jsonArray.put(Integer.valueOf(taskModel.id.split("_")[1]));
			jsonObject2.put("things", jsonArray);
			jsonObject2.put("id", taskModel.id.split("_")[0]);

			jsonObject.getJSONArray("players").getJSONObject(getPlayerIndex(user)).getJSONArray("tasks")
					.put(jsonObject2);
		}
		if (taskManager.task_list.size() != 0) {
			// System.out.println("Task got");
			taskManager.task_list.remove(0);

			this.taskModel = null;
		}
		user.info.setPlayers(jsonObject.toString());

	}

	public static int getPlayerIndex(User user) {
		JSONArray jsonObject = new JSONObject(user.info.players).getJSONArray("players");
		for (int i = 0; i < jsonObject.length(); i++) {
			JSONObject jsonObject2 = jsonObject.getJSONObject(i);

			if (jsonObject2.getBoolean("is")) {

				return i;
			}
		}
		return -1;

	}

	public void jump() {
		if (up == false && checkGround()) {
			up = true;
			this.jump = (player.jump);
			if (shift)
				jump *= (1.1 + player.breath);
			else {
				// jump *= 1;
			}
			if (right || left) {
				shift_jump = true;
				speed = player.speed;

				if (shift)
					speed = player.speed * 1.14;// .1;

			}
		}
	}

	public void jump(double i, double speed, boolean right) {
		if (amorInventory == 1 && nitroInventory == 1)
			shot = true;
		up = true;
		this.jump = i * 1.2;

		if (right) {
			this.speed = -speed;
		} else {
			this.speed = speed;
		}
		position.x += 2 * this.speed;

	}

	public void send(String str) {
		user.send(str);

	}

	public void send(String str, boolean b) {
		user.send(str);

	}

	public boolean checkGround() {
		/*
		 * int posX = (int) (position.x / MapGeneration.SIZE); int posY = (int)
		 * (position.y / MapGeneration.SIZE);
		 * 
		 * int size = 4;
		 * 
		 * for (int i = posX - size; i < posX + size; i++) { for (int j = posY -
		 * size; j < posY + size; j++) { if (i >= 0 && j >= 0 && i <
		 * loc.map.length && j < loc.map[0].length) { if (loc.map[i][j] == 2 ||
		 * loc.map[i][j] == 1) { Square s = new Square(i * MapGeneration.SIZE, j
		 * * MapGeneration.SIZE - MapGeneration.SIZE / 4, MapGeneration.SIZE,
		 * MapGeneration.SIZE);
		 * 
		 * if (Square.isIntersect(s, position)) return true; } }
		 * 
		 * } } return false;
		 */
		int PX = (int) (position.x) / MapGeneration.SIZE;
		int PY = (int) (position.y) / MapGeneration.SIZE;
		for (int i = PX; i < PX + position.w / MapGeneration.SIZE; i++) {
			for (int j = PY; j < PY + position.h / MapGeneration.SIZE + 1; j++) {
				if (i < 0 || j < 0 || loc == null || loc.map == null || i >= loc.map.length || j >= loc.map[0].length)
					continue;
				if (loc.map[i][j] == 2 || loc.map[i][j] == 1) {
					/*
					 * Square.isLockIntersect(position, new Square(i *
					 * MapGeneration.SIZE, PY * MapGeneration.SIZE,
					 * MapGeneration.SIZE, MapGeneration.SIZE));
					 */
					Square square = new Square(i * MapGeneration.SIZE, j * MapGeneration.SIZE - MapGeneration.SIZE / 4,
							MapGeneration.SIZE, MapGeneration.SIZE);
					if (Square.isIntersect(position, square))
						return true;
				}

			}
		}
		return false;
	}

	public Vector2d getPosition() {
		return new Vector2d(position.x, position.y);
	}

	public void init() {
		t.start();
	}

	public PlayerController(User user) {
		this.user = user;
		this.player = user.player;
		this.health = player.health;

		// init();
		// TODO Auto-generated constructor stub
	}

	public PlayerController() {

	}

	public PlayerController(Vector2d pos, Vector2d size) {
		position = new Square(pos.x, pos.y, size.x, size.y);
	}

	public PlayerController(User user, Point spawn) {
		this.user = user;
		position = new Square(spawn.x * MapGeneration.SIZE, spawn.y * MapGeneration.SIZE, MapGeneration.SIZE * 2,
				MapGeneration.SIZE * 3);

		this.player = user.player;
		this.power = player.power;
		this.energy = player.energy;
		this.energy_recovery = player.energy_recovery;
		breath = player.breath;
		this.player = user.player;
		this.heart = user.player.heart;
		this.speed = Math.ceil(player.go * nitroInventory);
		this.health = player.health;
		if ((IBreath) player.ibreath != null) {
			ibreath = (IBreath) player.ibreath.cl();
			ibreath.setPlayerController(this);
			ibreath.setStyle(0);
		}
		// init();
		// TODO Auto-generated constructor stub
	}

	public JSONObject respawn(Point p) {
		JSONObject jsonObject = new JSONObject();
		position.x = p.x;
		position.y = p.y;

		this.power = player.power;
		this.energy = player.energy;
		this.energy_recovery = player.energy_recovery;
		breath = player.breath;
		this.player = user.player;
		this.health = player.health;

		jsonObject.put("power", power);
		jsonObject.put("energy", energy);
		jsonObject.put("health", health);
		jsonObject.put("breath", ibreath.getBreath().toString());
		jsonObject.put("map_spawn_x", position.x);
		jsonObject.put("map_spawn_y", position.y);

		return jsonObject;

	}

	public void update() {
		position.y = position.y + GRAVITY - jump;

	}

	public void unupdate() {

		position.y = position.y - GRAVITY + jump;

	}

	public void updates() {

		position.y = position.y + GRAVITY - jump;

	}

	public long getTime() {
		return System.currentTimeMillis();
	}

	public void kill(int power, NPCDeamon npcDeamon) {

		npcDeamon.active = false;
		this.send("battle;killshot;" + power);
		this.user.score += NPCDeamon.SCORE_FOR_NPC * (player.id + 1) * Integer.valueOf(npcDeamon.id.split("_")[1]);
		this.user.money += NPCDeamon.MONEY_FOR_NPC * (player.id + 1) * Integer.valueOf(npcDeamon.id.split("_")[1]);
		user.send_score_money();
	}

	public void kill(int power, IDeamons npcDeamon) {

		npcDeamon.setActive(false);
		this.send("battle;killshot;" + power);
		this.user.score += NPCDeamon.SCORE_FOR_NPC * (player.id + 1) * Integer.valueOf(npcDeamon.getId().split("_")[1]);
		this.user.money += NPCDeamon.MONEY_FOR_NPC * (player.id + 1) * Integer.valueOf(npcDeamon.getId().split("_")[1]);
		user.send_score_money();
	}

	public void fire() {
		if (ibreath == null && player.ibreath != null)
			ibreath = (IBreath) player.ibreath.cl();
		if (ibreath != null && energy - ibreath.getEnergy() >= 0) {
			energy -= ibreath.getEnergy();
			ibreath.fire();
		}

		// TODO Auto-generated method stub

	}
}
