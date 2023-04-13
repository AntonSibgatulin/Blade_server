package com.AntonSibgatulin.Players.thunder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.json.JSONArray;
import org.json.JSONObject;

import com.AntonSibgatulin.Players.EBreath;
import com.AntonSibgatulin.Players.EntityBreath;
import com.AntonSibgatulin.Players.IBreath;
import com.AntonSibgatulin.Players.StyleLoader;
import com.AntonSibgatulin.Players.StyleModel;
import com.AntonSibgatulin.location.Shot;
import com.AntonSibgatulin.location.Timer;
import com.AntonSibgatulin.location.generation.MapGeneration;

public class ThunderModel extends EntityBreath implements IBreath, Cloneable {

	public ThunderModel(JSONObject jsonObject) {

		this.jsonObject = jsonObject;

		EBreath style = EBreath.valueOf(jsonObject.getString("id"));
		this.styleLoader = new StyleLoader(jsonObject.getJSONArray("styles"), style);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSetting(String str) {

		JSONObject jsonObject = new JSONObject(str);
		setting = jsonObject;
		JSONArray JsonArray = jsonObject.getJSONArray("styles");
		for (int i = 0; i < JsonArray.length(); i++) {
			Integer integer = JsonArray.getInt(i);
			StyleModel styleModel = styleLoader.hashMap.get(integer);
			if (styleModel == null)
				return;
			addStyle(styleModel);
		}
		// mainStyle = styles.get(0);

	}

	@Override
	public int getNumberofStyle() {
		// TODO Auto-generated method stub
		if (mainStyle != null)
			return mainStyle.type;
		else
			return -1;
	}

	@Override
	public int posAttack() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void fire() {
		if (mainTimer != null && mainTimer.timer.isRunning())
			return;
		cleanStyles();

		int key = getNumberofStyle();
		StyleModel style = hashStyles.get(key);
		if (style == null)
			return;
		sendFire();

		int time = style.maxTime - (style.maxTime - style.minTime)
				* (player.user.player.id * 100 / (player.user.player.maxPlayers - 1)) / 100;

		// System.out.println(time + " time");
		if (key == 0) {
			int size = MapGeneration.SIZE_FIGHT;
			final int speed_blade = size / 4;
			double w = player.side == 1 ? player.position.w / 2 : 0;

			// final Square square = new Square(player.position.x + size * 2 +
			// size / 2,
			// player.position.y + h0 / 2 - size / 2, size, size);

			final Shot shot = new Shot(player.position, size, size, player.side * (3 * size + w),
					(player.position.h / 2 - size / 2), speed_blade * player.side, 0);
			player.fights.add(shot);
			int end = size * 6;
			int col = 14;
			mainTimer = new Timer(time, col, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// square.x -= speed_blade;
					shot.blade();
					speedHack(2.3);
				}
			}, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					speedGo();
					cleanStyles();

				}
			});
			mainTimer.start();
		}

		if (key == 1) {
			int size = MapGeneration.SIZE_FIGHT;
			final int speed_blade = size / 4;

			double w = player.side == 1 ? player.position.w / 2 : 0;

			// final Square square = new Square(player.position.x + size * 2 +
			// size / 2,
			// player.position.y + h0 / 2 - size / 2, size, size);

			final Shot shot = new Shot(player.position, size, size, player.side * (5 * size + w),
					(player.position.h / 2 - size / 2), speed_blade * player.side, 0);
			player.fights.add(shot);
			int end = size * 6;
			int col = 24;
			mainTimer = new Timer(time, col, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// square.x -= speed_blade;
					shot.blade();
					speedHack(2.3);
				}
			}, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					speedGo();
					cleanStyles();

				}
			});
			mainTimer.start();
		}

		if (key == 2) {
			final int size = (int) (MapGeneration.SIZE_FIGHT);
			int s = MapGeneration.SIZE_FIGHT;
			final int speed_blade = size / 4;

			final Shot shot = new Shot(player.position, s, s,
					player.side * (player.position.w / 2 - size / 2 + 3 * size), (player.position.h - size), 0, 0);
			shot.angle = 90;
			player.fights.add(shot);
			int col = 14;
			final int end = 360 / col;

			mainTimer = new Timer(time, col, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					speedHack(2.3);
					if (shot.dty >= player.position.h - size) {
						shot.my = 3 * speed_blade;
					}
					if (shot.dty <= 0) {
						shot.my = -3 * speed_blade;
					}
					shot.dtx *= player.side;
					shot.update();
					// square.x -= speed_blade;
					/*
					 * shot.dtx += Math.sin(shot.angle / 180 * 3.14) * 3 * size
					 * * player.side; shot.dty += Math.cos(shot.angle / 180 *
					 * 3.14) * 3 * size * player.side;
					 */
					// shot.angle -= end * player.side;
					// shot.update();

				}
			}, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					speedGo();
					cleanStyles();

				}
			});
			mainTimer.start();
		}

		if (key == 3) {
			int size = MapGeneration.SIZE_FIGHT;
			final int speed_blade = size / 4;

			double w = player.side == 1 ? player.position.w / 2 : 0;

			// final Square square = new Square(player.position.x + size * 2 +
			// size / 2,
			// player.position.y + h0 / 2 - size / 2, size, size);

			final Shot shot = new Shot(player.position, size, size, player.side * (5 * size + w),
					(player.position.h / 2 - size / 2), speed_blade * player.side, 0);
			player.fights.add(shot);
			int end = size * 6;
			int col = 24;
			mainTimer = new Timer(time, col, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// square.x -= speed_blade;
					shot.blade();
					speedHack(3);
				}
			}, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					speedGo();
					cleanStyles();

				}
			});
			mainTimer.start();
		}

		if (key == 4) {
			int size = MapGeneration.SIZE_FIGHT;
			final int speed_blade = size / 4;

			// final Square square = new Square(player.position.x + size * 2 +
			// size / 2,
			// player.position.y + h0 / 2 - size / 2, size, size);

			final Shot shot = new Shot(player.position, size, size, player.position.w,
					(player.position.h / 2 - size / 2), -speed_blade * player.side, 0);
			player.fights.add(shot);
			int end = size * 6;
			int col = 3;
			mainTimer = new Timer(time, col, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// square.x -= speed_blade;
					shot.blade();
					speedHack(2 * 1.4);

				}
			}, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					cleanStyles();
					speedGo();// (6 * 1.4);

				}
			});
			mainTimer.start();
		}

		if (key == 5) {
			final int size = (int) (MapGeneration.SIZE_FIGHT / 2 * 1.8);
			int s = MapGeneration.SIZE_FIGHT;

			final Shot shot = new Shot(player.position, s, s, player.side * (player.position.w / 2 - size / 2),
					(player.position.h / 2 - size) - 3 * size * 2, 0, 0);
			shot.angle = 90;
			player.fights.add(shot);
			int col = 14;
			final int end = 360 / col;

			mainTimer = new Timer((int) (time / 1.2), col, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// square.x -= speed_blade;
					shot.dtx += Math.sin(shot.angle / 180 * 3.14) * 3 * size * player.side;
					shot.dty += Math.cos(shot.angle / 180 * 3.14) * 3 * size * player.side;
					shot.angle -= end * player.side;
					// shot.update();

				}
			}, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					cleanStyles();

				}
			});
			mainTimer.start();
		}

		if (key == 6) {
			int size = MapGeneration.SIZE_FIGHT;
			final int speed_blade = size;

			// final Square square = new Square(player.position.x + size * 2 +
			// size / 2,
			// player.position.y + h0 / 2 - size / 2, size, size);
			double w = player.side == 1 ? player.position.w : -size;
			final Shot shot = new Shot(player.position, size, size, w, (player.position.h / 2 - size / 2),
					-speed_blade * player.side, 0);
			player.fights.add(shot);
			int end = size * 6;
			int col = 4;
			mainTimer = new Timer(time, col, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// square.x -= speed_blade;
					shot.blade();

				}
			}, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					cleanStyles();

				}
			});
			mainTimer.start();
		}

		if (key == 7) {
			final int size = (int) (MapGeneration.SIZE_FIGHT);
			int s = MapGeneration.SIZE_FIGHT;

			final Shot shot = new Shot(player.position, s, s, player.side * (player.position.w / 2 - size / 2),
					(player.position.h / 2 - size) - 3 * size, 0, 0);
			shot.angle = 90;
			player.fights.add(shot);
			int col = 14;
			final int end = 200 / col;

			mainTimer = new Timer((int) (time / 1.2), col - 4, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// square.x -= speed_blade;
					shot.dtx += Math.sin(shot.angle / 180 * 3.14) * size * player.side;
					shot.dty += Math.cos(shot.angle / 180 * 3.14) * size * player.side;
					shot.angle -= end * player.side;
					// shot.update();

				}
			}, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					cleanStyles();

				}
			});
			mainTimer.start();
		}

		if (key == 8) {
			final int size = (int) (MapGeneration.SIZE_FIGHT);
			int s = MapGeneration.SIZE_FIGHT;

			final Shot shot = new Shot(player.position, s, s, player.side * (player.position.w / 2 - size / 2),
					(player.position.h / 2 - size) - 3 * size, 0, 0);
			shot.angle = 90;
			player.fights.add(shot);
			int col = 14;
			final int end = 200 / col;

			mainTimer = new Timer((int) (time / 1.2), col - 4, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// square.x -= speed_blade;
					shot.dtx += Math.sin(shot.angle / 180 * 3.14) * size * player.side;
					shot.dty += Math.cos(shot.angle / 180 * 3.14) * size * player.side;
					shot.angle -= end * player.side;
					// shot.update();

				}
			}, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					cleanStyles();

				}
			});
			mainTimer.start();
		}

		if (key == 9) {
			int size = MapGeneration.SIZE_FIGHT;
			final int speed_blade = size / 4;
			double w = player.side == 1 ? player.position.w : -size;

			// final Square square = new Square(player.position.x + size * 2 +
			// size / 2,
			// player.position.y + h0 / 2 - size / 2, size, size);

			final Shot shot = new Shot(player.position, size, size, w, (player.position.h / 2 - size / 2),
					-speed_blade * player.side, 0);
			player.fights.add(shot);
			int end = size * 6;
			int col = 14;
			mainTimer = new Timer(time, col - 6, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// square.x -= speed_blade;
					shot.blade();

				}
			}, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					cleanStyles();

				}
			});
			mainTimer.start();
		}

		if (key == 10) {
			final int size = (int) (MapGeneration.SIZE_FIGHT);
			int s = MapGeneration.SIZE_FIGHT;
			final int speed_blade = size / 4;
			double w = player.side == 1 ? player.position.w : 0;
			final Shot shot = new Shot(player.position, s, s, player.side * (w / 2 - size / 2 + 2 * size),
					(player.position.h - size), 0, 0);
			shot.angle = 90;
			player.fights.add(shot);
			int col = 14;
			final int end = 360 / col;

			mainTimer = new Timer(time, col, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (shot.dty >= player.position.h - size) {
						shot.my = 3 * speed_blade;
					}
					if (shot.dty <= 0) {
						shot.my = -3 * speed_blade;
					}

					// shot.dtx =w;
					shot.update();
					// square.x -= speed_blade;
					/*
					 * shot.dtx += Math.sin(shot.angle / 180 * 3.14) * 3 * size
					 * * player.side; shot.dty += Math.cos(shot.angle / 180 *
					 * 3.14) * 3 * size * player.side;
					 */
					// shot.angle -= end * player.side;
					// shot.update();

				}
			}, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					cleanStyles();

				}
			});
			mainTimer.start();
		}

	}

	@Override
	public void go() {
		// TODO Auto-generated method stub

	}

	@Override
	public void length() {
		// TODO Auto-generated method stub

	}

	@Override
	public EBreath getBreath() {
		// TODO Auto-generated method stub
		return EBreath.WATER;
	}

	@Override
	public JSONObject getStyles() {
		if (stylesSetting == null) {
			stylesSetting = new JSONArray();
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", this.getBreath().toString());
		jsonObject.put("styles", stylesSetting);

		// TODO Auto-generated method stub
		return jsonObject;
	}

}
