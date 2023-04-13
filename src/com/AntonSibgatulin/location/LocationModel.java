package com.AntonSibgatulin.location;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.Timer;

import org.json.JSONArray;
import org.json.JSONObject;

import com.AntonSibgatulin.Players.Player;
import com.AntonSibgatulin.location.CP.PointModel;
import com.AntonSibgatulin.location.deamons.IDeamons;
import com.AntonSibgatulin.location.deamons.npc.NPCDeamon;
import com.AntonSibgatulin.location.generation.MapGeneration;
import com.AntonSibgatulin.server.Server;
import com.AntonSibgatulin.user.User;

public class LocationModel implements ActionListener {

	public Server server = null;

	public long repeat = 50;
	public int timeWithouSomebody = 0;
	public static final int TIME_WAITING_OF_PLAYERS = 30000;
	public static final int TIME_OUT_OF_WAITING = TIME_WAITING_OF_PLAYERS / 50;

	public int maxPlayer = 0;
	public String id = null;
	public int idPlayer = 0;
	public String name = null;
	public String type = null;
	public PointModel pointModel = null;
	public JSONObject setting = null;

	public ArrayList<PlayerController> playerControllers = new ArrayList<>();
	public ArrayList<IDeamons> deamons = new ArrayList<>();

	public ArrayList<PlayerController> redPlayerController = new ArrayList<>();
	public ArrayList<PlayerController> bluePlayerController = new ArrayList<>();

	public HashMap<String, PlayerController> playerControllersHash = new HashMap<>();

	public boolean addPlayer(User user) {

		if (!isHasPlayer(user)) {
			idPlayer++;
			user.playerController.idPlayer = idPlayer;

			playerControllers.add(user.playerController);
			playerControllersHash.put(user.login, user.playerController);
			user.send("battle;enter_to_battle;" + (idPlayer));
			send_all_change();
			return true;
		}
		return false;

	}

	public boolean addPlayer(User user, String team) {
		if (playerControllers.size() >= this.maxPlayer) {
			// enough players
			return false;
		}
		if (team == null) {
			return addPlayer(user);

		}
		if (!isHasPlayer(user)) {
			if (team.equals("red")) {
				user.playerController.team = team;
				redPlayerController.add(user.playerController);
			} else if (team.equals("blue")) {
				user.playerController.team = team;
				bluePlayerController.add(user.playerController);
			} else {
				return false;
			}
			idPlayer++;
			user.playerController.idPlayer = idPlayer;

			playerControllers.add(user.playerController);
			playerControllersHash.put(user.login, user.playerController);
			user.send("battle;enter_to_battle;" + (idPlayer));
			send_all_change();
			return true;
		}
		return false;
	}

	public void send_all_change() {
		server.sendEverybody("lobby;change_count_of_people;" + id + ";" + playerControllers.size());
	}

	public void removeUser(User user) {
		if (user == null)
			return;
		PlayerController playerController = playerControllersHash.get(user.login);
		if (playerController == null)
			return;
		playerControllersHash.remove(playerController);
		this.playerControllers.remove(playerController);
		if (playerController.team != null) {
			redPlayerController.remove(playerController);
			bluePlayerController.remove(playerController);
		}
		sendEverybody("battle;remove_user;" + playerController.idPlayer);
		System.out.println(
				"Size of hashMap " + playerControllersHash.size() + " Size of arraylist " + playerControllers.size());
		send_all_change();
	}

	public void send_players(User user) {
		for (int i = 0; i < playerControllers.size(); i++) {
			PlayerController playerController = playerControllers.get(i);
			if (playerController == null)
				continue;
			if (user.playerController.idPlayer != playerController.idPlayer) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("posX", playerController.position.x);
				jsonObject.put("posY", playerController.position.x);
				jsonObject.put("w", playerController.position.w);
				jsonObject.put("h", playerController.position.h);
				jsonObject.put("energy", playerController.energy);
				jsonObject.put("health", playerController.health);
				jsonObject.put("side", playerController.side);
				jsonObject.put("login", playerController.user.login);

				user.send("battle;add_player;" + playerController.idPlayer + ";" + playerController.player.name + "_"
						+ playerController.player.type + ";" + jsonObject);
				jsonObject = new JSONObject();
				jsonObject.put("posX", user.playerController.position.x);
				jsonObject.put("posY", user.playerController.position.x);
				jsonObject.put("w", user.playerController.position.w);
				jsonObject.put("h", user.playerController.position.h);
				jsonObject.put("energy", user.playerController.energy);
				jsonObject.put("health", user.playerController.health);
				jsonObject.put("side", user.playerController.side);
				jsonObject.put("login", user.login);

				playerController.send(
						"battle;add_player;" + user.playerController.idPlayer + ";" + user.playerController.player.name
								+ "_" + user.playerController.player.type + ";" + jsonObject);
			}
		}
	}

	public boolean isHasPlayer(User user) {
		for (PlayerController playerController : playerControllers) {
			if (playerController == user.playerController)
				return true;
		}
		return false;
	}

	public MapGeneration maps = null;

	public Timer timer = new Timer(40, this);

	public LocationModel(String id, MapGeneration maps, JSONObject setting, Server server,
			HashMap<String, Player> players) {
		this.server = server;
		this.maps = maps;
		this.setting = setting;
		this.id = id;
		this.name = setting.getString("name");
		this.maxPlayer = setting.getInt("maxPlayer");

		if (maxPlayer >= 14)
			maxPlayer = 14;

		this.type = setting.getString("type");

		if (name.length() >= 15 || name.length() <= 5) {
			name = "Battle for friends.";
		}

		if (type.contains("CP")) {
			this.type = "CP";
		} else if (type.equals("TDM")) {
			this.type = "TDM";
		} else if (type.contains("CTF")) {
			this.type = "CTF";
		} else if (type.contains("TM")) {
			this.type = "TM";
			if (this.maxPlayer >= 6) {
				this.maxPlayer = 6;
			}

		} else {
			this.type = "Cheater";
		}
		int level_of_deamons = 5;
		maps.initDeamons((Player) players.get("deamon_" + level_of_deamons).clone());
		maps.initIDeamons(players, level_of_deamons);

		maps.initIDeamons(this);

		// , level_of_deamons);//
		// TODO Auto-generated constructor stub
	}

	public void executeCommands(String command, User user) {
		String str[] = command.split(";");
		if (str.equals("enter")) {

		}
	}

	public void addUser(User user) {
		PlayerController playerController = new PlayerController(user);

	}

	public void sendEverybody(String str) {
		for (int i = 0; i < playerControllers.size(); i++) {
			PlayerController playerController = playerControllers.get(i);
			if (playerController == null)
				continue;
			playerController.send(str);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (int d = 0; d < playerControllers.size(); d++) {
			PlayerController playerController = playerControllers.get(d);
			if (playerController == null || playerController.loc == null || maps == null
					|| playerController.loc.map == null)
				continue;

			if (playerController.active) {

				// System.out.println(player.jsonObject.toString());
				playerController.energy += playerController.energy_recovery * playerController.powerInventory;
				if (playerController.shift && (playerController.left || playerController.right)) {
					playerController.energy -= playerController.energy_recovery;
				}
				if (playerController.left || playerController.right) {
					playerController.energy -= playerController.energy_recovery / 2;
				}
				if (playerController.energy >= playerController.player.energy)
					playerController.energy = playerController.player.energy;
				if (playerController.energy <= 0)
					playerController.energy = 0;

				if (playerController.checkGround() == false || playerController.up)
					playerController.update();

				if (playerController.position.x <= 0)
					playerController.position.x = 0;
				if (playerController.position.y <= 0)
					playerController.position.y = 0;

				if (playerController.position.x >= MapGeneration.SIZE * playerController.loc.map.length)
					playerController.position.x = MapGeneration.SIZE * playerController.loc.map.length;
				if (playerController.position.y >= MapGeneration.SIZE * playerController.loc.map[0].length)
					playerController.position.y = MapGeneration.SIZE * playerController.loc.map[0].length;

				int posX = (int) (playerController.position.x / MapGeneration.SIZE);
				int posY = (int) (playerController.position.y / MapGeneration.SIZE);

				ArrayList<Square> lists = new ArrayList<>();

				if (playerController.up) {
					playerController.jump -= playerController.GRAVITY / 6;
					if (playerController.jump <= -5)
						playerController.jump = -5;
					if (playerController.checkGround()) {
						playerController.jump = 0;
						playerController.up = !playerController.up;
						playerController.shift_jump = false;
						playerController.speed = (playerController.player.go * playerController.nitroInventory);
						playerController.shot = false;

					}
				}

				// if(right){

				// }
				if (playerController.shift && !playerController.up && playerController.shot == false) {
					playerController.speed = playerController.player.speed * playerController.nitroInventory;
				}

				int size = 4;

				// check time out of inventory
				if (playerController.powerInventory != 1) {
					if (playerController.getTime()
							- playerController.powerInventoryStart >= playerController.TIME_POWER_INVENTORY) {
						playerController.powerInventory = 1;
						sendEverybody("battle;remove_supplies;power;" + playerController.idPlayer);
					}
				}

				if (playerController.nitroInventory != 1) {
					if (playerController.getTime()
							- playerController.nitroInventoryStart >= playerController.TIME_NITRO_INVENTORY) {
						playerController.nitroInventory = 1;
						sendEverybody("battle;remove_supplies;nitro;" + playerController.idPlayer);
					}
				}

				if (playerController.amorInventory != 1) {
					if (playerController.getTime()
							- playerController.amorInventoryStart >= playerController.TIME_AMOR_INVENTORY) {
						playerController.amorInventory = 1;
						sendEverybody("battle;remove_supplies;amor;" + playerController.idPlayer);
					}
				}

				// check collision with the map
				int PX = (int) (playerController.position.x - playerController.position.w * 2) / MapGeneration.SIZE;
				int PY = (int) (playerController.position.y) / MapGeneration.SIZE;
				for (int i = PX; i < PX + (playerController.position.w * 5 / MapGeneration.SIZE); i++) {
					for (int j = PY; j < PY + playerController.position.h / MapGeneration.SIZE + 1; j++) {
						if (i < 0 || j < 0 || i >= playerController.loc.map.length
								|| j >= playerController.loc.map[0].length)
							continue;
						if (playerController.loc.map[i][j] == 2 || playerController.loc.map[i][j] == 1) {
							Square square = new Square(i * MapGeneration.SIZE, j * MapGeneration.SIZE,
									MapGeneration.SIZE, MapGeneration.SIZE);
							Square.isLockIntersect(playerController.position, square);

							// return true;
						}
						if (playerController.loc.map[i][j] == 200) {
							Square s = new Square(i * MapGeneration.SIZE, j * MapGeneration.SIZE, MapGeneration.SIZE,
									MapGeneration.SIZE);
							if (Square.isIntersect(s, playerController.position)) {
								playerController.loc.map[i][j] = 0;
								send_change_tile(i, j, 0);
								playerController.user.money += 1;
								playerController.user.send_score_money();

							}
						}
						// health
						if (playerController.loc.map[i][j] == 4) {
							Square s = new Square(i * MapGeneration.SIZE, j * MapGeneration.SIZE, MapGeneration.SIZE,
									MapGeneration.SIZE);
							if (Square.isIntersect(s, playerController.position)) {
								playerController.loc.map[i][j] = 0;
								send_change_tile(i, j, 0);
								playerController.health = playerController.player.health;
								playerController.sendData();
							}
						}
						// power
						if (playerController.loc.map[i][j] == 5) {
							Square s = new Square(i * MapGeneration.SIZE, j * MapGeneration.SIZE, MapGeneration.SIZE,
									MapGeneration.SIZE);
							if (Square.isIntersect(s, playerController.position)) {
								playerController.loc.map[i][j] = 0;
								send_change_tile(i, j, 0);
								playerController.health = playerController.player.health;
								playerController.powerInventory = 2;
								playerController.powerInventoryStart = playerController.getTime();
								sendEverybody("battle;add_supplies;power;" + playerController.idPlayer);

							}
						}
						// amor
						if (playerController.loc.map[i][j] == 6) {
							Square s = new Square(i * MapGeneration.SIZE, j * MapGeneration.SIZE, MapGeneration.SIZE,
									MapGeneration.SIZE);
							if (Square.isIntersect(s, playerController.position)) {
								playerController.loc.map[i][j] = 0;
								send_change_tile(i, j, 0);
								playerController.amorInventory = 2;
								playerController.amorInventoryStart = playerController.getTime();
								sendEverybody("battle;add_supplies;amor;" + playerController.idPlayer);
							}
						}
						// nitro
						if (playerController.loc.map[i][j] == 7) {
							Square s = new Square(i * MapGeneration.SIZE, j * MapGeneration.SIZE, MapGeneration.SIZE,
									MapGeneration.SIZE);
							if (Square.isIntersect(s, playerController.position)) {
								playerController.loc.map[i][j] = 0;
								send_change_tile(i, j, 0);
								playerController.nitroInventory = 1.3;
								playerController.nitroInventoryStart = playerController.getTime();
								sendEverybody("battle;add_supplies;nitro;" + playerController.idPlayer);

							}
						}

					}
				}

				if (!playerController.shot) {
					if (playerController.left) {
						playerController.position.x -= playerController.nitroInventory * playerController.speed;
					}
					if (playerController.right) {

						playerController.position.x += playerController.nitroInventory * playerController.speed;
					}
				} else {
					playerController.position.x += (playerController.speed) / (playerController.nitroInventory);
				}

				if (playerController.user != null) {

					if (playerController.lastPos != null
							&& (playerController.lastPos.x - playerController.position.x == 0)
							&& playerController.moveon == false) {
						sendEverybody("battle;pos_stay;" + playerController.idPlayer);

						playerController.moveon = true;

					}
					if (playerController.lastPos == null
							|| playerController.lastPos.x - playerController.position.x != 0
							|| playerController.lastPos.y - playerController.position.y != 0) {
						playerController.lastPos = new Square(playerController.position.x, playerController.position.y,
								10, 10);
						sendEverybody("battle;pos;" + playerController.idPlayer + ";" + playerController.position.x
								+ ";" + playerController.position.y + ";" + playerController.side);
						playerController.moveon = false;
					}

				}

				// user.send("battle;pos;"+position.x+";"+position.y);

				// check finish
				if (type.equals("TM")) {
					if (maps.deamons.size() == 0) {
						playerController.user.money += playerController.loc.money / (playerController.user.rang + 1);
						playerController.user.score += playerController.loc.score / (playerController.user.rang + 1);
						// System.out.println("MONEY " + loc.money +
						// "
						// SCORE " + loc.score);
						playerController
								.send("battle;end;" + (playerController.loc.money / (playerController.user.rang + 1))
										+ ";" + (playerController.loc.score / (playerController.user.rang + 1)));
						// finish();
						playerController.user.send_score_money();
						playerController.user.exit();
					}
				}

				if (playerController.position.x >= (playerController.loc.map.length - 6) * MapGeneration.SIZE) {

					playerController.position.x = (playerController.loc.map.length - 6) * MapGeneration.SIZE;
					int checkY = 0;
					for (int l = 0; l < playerController.loc.map[(playerController.loc.map.length - 6)].length; l++) {
						if (playerController.loc.map[(playerController.loc.map.length - 6)][l] == 2) {
							checkY = l - 1;
						}
					}

					if (checkY != 0
							&& playerController.position.x == (playerController.loc.map.length - 6) * MapGeneration.SIZE
							&& Math.abs(playerController.position.y - checkY * MapGeneration.SIZE) <= MapGeneration.SIZE
									* 3) {
						if (!playerController.loc.killAllDeamons
								|| playerController.loc.killAllDeamons && playerController.loc.deamons.size() == 0) {

							if (type.equals("TM")) {
								if (maps.deamons.size() == 0) {
									playerController.user.money += playerController.loc.money
											/ (playerController.user.rang + 1);
									playerController.user.score += playerController.loc.score
											/ (playerController.user.rang + 1);
									// System.out.println("MONEY " + loc.money +
									// " SCORE " + loc.score);
									playerController.send("battle;end;"
											+ (playerController.loc.money / (playerController.user.rang + 1)) + ";"
											+ (playerController.loc.score / (playerController.user.rang + 1)));
									// finish();
									playerController.user.send_score_money();
									playerController.user.exit();
								}
							}

						}

					}

				}

				// loading map
				if (playerController.lastSent
						- (posX + playerController.width / MapGeneration.SIZE) <= playerController.width
								/ MapGeneration.SIZE * 4) {
					JSONArray jsonArray = new JSONArray();
					int end = (playerController.lastSent + playerController.width / MapGeneration.SIZE * 12);
					if (end >= playerController.loc.map.length)
						end = playerController.loc.map.length;
					for (int i = playerController.lastSent; i < (int) end; i++) {
						// ||\\
						JSONArray jsonArray2 = new JSONArray();
						for (int j = 0; j < playerController.loc.map[0].length; j++) {
							jsonArray2.put(playerController.loc.map[i][j]);

						}
						jsonArray.put(jsonArray2);
						// ||\\
					}
					JSONObject jsonObject2 = new JSONObject();
					jsonObject2.put("map", jsonArray);

					// System.out.println("sent
					// "+jsonObject2.toString().length());
					playerController.lastSent = end;
					playerController.send("battle;map_add;" + jsonObject2);
				}

				// check colision with shot from breath
				if (playerController.ibreath != null) {
					JSONObject jsonObject = new JSONObject();
					JSONArray jsonArray = new JSONArray();

					for (int i = 0; i < playerController.fights.size(); i++) {
						Shot shot = playerController.fights.get(i);
						if (shot == null)
							continue;
						Square square = shot.pos;
						playerController.fights.get(i).updatePos();
						for (int j = 0; j < playerController.loc.deamons.size(); j++) {

							NPCDeamon deamon = playerController.loc.deamons.get(j);
							double distance = deamon.getPosition().distanceToWithoutZ(playerController.getPosition());
							if (distance <= playerController.width) {
								if (Square.isIntersect(square, deamon.pos)) {

									deamon.shot(
											(int) (playerController.ibreath.getPowerOfStrike()
													* playerController.powerInventory),
											playerController.loc.map, playerController);
								}
							}

						}
						for (int j = 0; j < maps.iDeamons.size(); j++) {

							IDeamons deamon = maps.iDeamons.get(j);
							double distance = deamon.getPosition().distanceToWithoutZ(playerController.getPosition());
							if (distance <= playerController.width) {
								if (Square.isIntersect(square, deamon.getSquarePosition())) {

									deamon.shot((int) (playerController.ibreath.getPowerOfStrike()
											* playerController.powerInventory), playerController);
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
						sendEverybody("battle;" + playerController.idPlayer + ";fight_box;" + jsonObject.toString());

				}

				if (playerController.energy >= playerController.player.energy)
					playerController.energy = playerController.player.energy;
				if (playerController.energy <= 0)
					playerController.energy = 0;

				if (playerController.energy != playerController.player.energy) {
					playerController.user.sendEnergy(playerController.energy);
				}

			}

		}

		if (maps != null) {

			for (int i = 0; i < maps.iDeamons.size(); i++) {
				IDeamons npcDeamon = maps.iDeamons.get(i);
				if (i >= maps.deamons.size())
					break;
				PlayerController playerController = null;
				int length = 10000000;
				ArrayList<PlayerController> playerControllers = (ArrayList<PlayerController>) this.playerControllers
						.clone();

				for (int l = 0; l < playerControllers.size(); l++) {

					PlayerController playerController2 = playerControllers.get(l);

					if (playerController2.getPosition().distanceToWithoutZ(npcDeamon.getPosition()) < length) {

						playerController = playerController2;

					}
				}

				if (playerController == null)
					break;

				// double distance =
				// npcDeamon.getPosition().distanceToWithoutZ(playerController.getPosition());
				if (npcDeamon.isActive()) {

					if (npcDeamon.getPosition().x <= 55 * MapGeneration.SIZE) {
						npcDeamon.getPosition().x = (new Random().nextInt(5) + 55) * MapGeneration.SIZE;
					}
					if (npcDeamon.getPosition().x >= (maps.map.length - 55) * MapGeneration.SIZE) {
						npcDeamon.getPosition().x = (maps.map.length - (new Random().nextInt(4) + 55))
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

					for (int kk = 0; kk < maps.iteration_of_deamons; kk++) {
						Square.isLockIntersect(npcDeamon.getSquarePosition(), playerController.position);
						npcDeamon.update(playerController);
					}

					if (npcDeamon.isAnotherPosition()) {
						/*
						 * System.out.println(npcDeamon.id +" "+ distance);
						 */

						if (playerController.lastPos == null)
							playerController.lastPos = new Square();
						playerController.lastPos.x = npcDeamon.getPosition().x;
						playerController.lastPos.y = npcDeamon.getPosition().y;

						JSONObject jsonObject = new JSONObject();
						jsonObject.put("posx", npcDeamon.getPosition().x);
						jsonObject.put("posy", npcDeamon.getPosition().y);
						jsonObject.put("side", npcDeamon.getSide());
						jsonObject.put("w", npcDeamon.getW());
						jsonObject.put("h", npcDeamon.getH());
						jsonObject.put("Type", npcDeamon.getName());
						jsonObject.put("id", npcDeamon.getId());

						sendEverybody("battle;npc;" + jsonObject.toString());

					}
				} else {
					sendEverybody("battle;npc_remove;" + npcDeamon.getId());
					maps.iDeamons.remove(npcDeamon);

				}
				// }
			}

			// System.out.println("size "+loc.deamons.size());
			for (int i = 0; i < maps.deamons.size(); i++) {
				NPCDeamon npcDeamon = maps.deamons.get(i);
				if (i >= maps.deamons.size())
					break;
				int npcI = 0;
				for (int d = 0; d < playerControllers.size(); d++) {
					PlayerController playerController = playerControllers.get(d);
					if (playerController == null)
						continue;

					double distance = npcDeamon.getPosition().distanceToWithoutZ(playerController.getPosition());
					if (distance <= playerController.width / 3 * 2) {
						if (npcDeamon.active) {

							if (npcDeamon.pos.x <= 55 * MapGeneration.SIZE) {
								npcDeamon.pos.x = (new Random().nextInt(5) + 55) * MapGeneration.SIZE;
							}
							if (npcDeamon.pos.x >= (maps.map.length - 55) * MapGeneration.SIZE) {
								npcDeamon.pos.x = (maps.map.length - (new Random().nextInt(4) + 55))
										* MapGeneration.SIZE;
							}

							if (Square.isIntersect(playerController.position, npcDeamon.pos)) {
								boolean right = npcDeamon.pos.x > playerController.position.x ? true : false;
								playerController.jump(playerController.player.jump,
										npcDeamon.player.speed + playerController.player.speed * 2, right);
								playerController.shot_me((1 + npcDeamon.player.id) * NPCDeamon.NPC_DEAMON_SHOT
										/ playerController.amorInventory);
								// dont forget about amor
							}
							Square.isLockIntersect(npcDeamon.pos, playerController.position);

							if (npcI == 0) {
								for (int kk = 0; kk < maps.iteration_of_deamons; kk++) {

									npcDeamon.update(playerController.loc.map,
											new PlayerController[] { playerController });
								}
								npcI = 1;
							}

						} else {
							sendEverybody("battle;npc_remove;" + npcDeamon.id);
							maps.deamons.remove(npcDeamon);

						}
					}
				}

				if (npcDeamon.lastPos == null || npcDeamon.pos.x != npcDeamon.lastPos.x
						|| npcDeamon.pos.y != npcDeamon.lastPos.y) {
					/*
					 * System.out.println(npcDeamon.id +" "+ distance);
					 */

					if (npcDeamon.lastPos == null)
						npcDeamon.lastPos = new Square();
					npcDeamon.lastPos.x = npcDeamon.pos.x;
					npcDeamon.lastPos.y = npcDeamon.pos.y;

					JSONObject jsonObject = new JSONObject();
					jsonObject.put("posx", npcDeamon.pos.x);
					jsonObject.put("posy", npcDeamon.pos.y);
					jsonObject.put("side", npcDeamon.side);
					jsonObject.put("w", npcDeamon.pos.w);
					jsonObject.put("h", npcDeamon.pos.h);
					jsonObject.put("Type", npcDeamon.player.name);
					jsonObject.put("id", npcDeamon.id);

					sendEverybody("battle;npc;" + jsonObject.toString());

				}
			}

		}

	}

	public void send_change_tile(int i, int j, int k) {
		sendEverybody("battle;change_tile;" + i + ";" + j + ";" + k);
		// TODO Auto-generated method stub

	}

	public void addHealth(PlayerController playerController) {
		String team = playerController.team;
		ArrayList<PlayerController> players = null;
		if (team == null) {
			players = this.playerControllers;
		} else if (team.equals("red")) {
			players = redPlayerController;
		} else if (team.equals("blue")) {
			players = bluePlayerController;
		}

		for (int i = 0; i < players.size(); i++) {
			PlayerController playerController2 = players.get(i);
			if (playerController2 != null && playerController2.idPlayer != playerController.idPlayer) {
				if (playerController2.active == true) {
					if (playerController2.heart > 1) {
						playerController2.heart -= 1;
						playerController2.health = playerController2.player.health;

						playerController2.sendCountOfHeart();
						playerController2.send("battle;help_of_player;" + playerController.idPlayer + ";"
								+ playerController.user.login);
						playerController.position.x = maps.px;
						playerController.position.y = maps.py;

						playerController.health = playerController.player.health;
						playerController.sendData();

						return;
					}
				}
			}
		}

		playerController.gameOver();
		playerController.active = false;

	}

	public void gameOver(PlayerController playerController) {
		if (type.equals("DM")) {
			playerController.gameOver();
		} else {
			if (type.equals("CTF") || type.equals("CTP") || type.equals("TDM") || type.equals("TM")) {
				addHealth(playerController);
			}
		}

	}

}
