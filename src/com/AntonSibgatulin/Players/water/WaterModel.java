package com.AntonSibgatulin.Players.water;

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
import com.AntonSibgatulin.location.Vector2d;
import com.AntonSibgatulin.location.generation.MapGeneration;

public class WaterModel extends EntityBreath implements IBreath, Cloneable {

	public WaterModel(JSONObject jsonObject) {

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
			double size = MapGeneration.SIZE_FIGHT;
			final double speed_blade = size / 4;
			double w = player.side == 1 ? player.position.w / 2 : 0;

			// final Square square = new Square(player.position.x + size * 2 +
			// size / 2,
			// player.position.y + h0 / 2 - size / 2, size, size);

			final Shot shot = new Shot(player.position, size, size, player.side * (3 * size + w),
					(player.position.h / 2 - size / 2), speed_blade * player.side, 0);
			player.fights.add(shot);
			double end = size * 6;
			int col = 14;
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

		if (key == 1) {
			final int size = MapGeneration.SIZE;// (int)
												// (MapGeneration.SIZE_FIGHT / 2
												// * 1.8);
			int size0 = MapGeneration.SIZE;
			int radius = size0 * 3;
			/*
			 * final Shot shot = new Shot(player.position, s, s, player.side *
			 * (player.position.w / 2 - size / 2), (player.position.h / 2 -
			 * size) - 3 * size * 2, 0, 0);
			 */
			final Shot shot = new Shot(this.player, 360.0 / 14 * player.side, radius * player.side,
					new Vector2d(size, size), -90 * player.side);

			// shot.angle = 90;
			player.fights.add(shot);
			int col = 14;

			mainTimer = new Timer(time, col, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// square.x -= speed_blade;
					/*
					 * shot.dtx += Math.sin(shot.angle / 180 * 3.14) * 3 * size
					 * * player.side; shot.dty += Math.cos(shot.angle / 180 *
					 * 3.14) * 3 * size * player.side; shot.angle -= end *
					 * player.side;
					 */
					// shot.update();

					shot.updateBlade();

				}
			}, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
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
					cleanStyles();

				}
			});
			mainTimer.start();
		}

		if (key == 3) {
			/*
			 * int size = MapGeneration.SIZE_FIGHT; final int speed_blade = size
			 * / 4;
			 * 
			 * double w = player.side == 1 ? player.position.w / 2 : 0;
			 * 
			 */
			// final Square square = new Square(player.position.x + size * 2 +
			// size / 2,
			// player.position.y + h0 / 2 - size / 2, size, size);
			/*
			 * final Shot shot = new Shot(player.position, size, size,
			 * player.side * (5 * size + w), (player.position.h / 2 - size / 2),
			 * speed_blade * player.side, 0);
			 */
			final int size = MapGeneration.SIZE;// (int)
			// (MapGeneration.SIZE_FIGHT / 2
			// * 1.8);
			int size0 = MapGeneration.SIZE;
			int radius = size0 * 3;
			/*
			 * final Shot shot = new Shot(player.position, s, s, player.side *
			 * (player.position.w / 2 - size / 2), (player.position.h / 2 -
			 * size) - 3 * size * 2, 0, 0);
			 */
			final Shot shot = new Shot(this.player, 120.0 / 14 * player.side, radius * player.side,
					new Vector2d(size, size), -90 * player.side);

			player.fights.add(shot);
			int end = size * 6;
			int col = 24;
			mainTimer = new Timer(time, col, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// square.x -= speed_blade;
					// shot.blade();
					shot.updateBlade();

				}
			}, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
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
			double w = player.position.w * player.side;
			if (w < 0) {
				w = player.position.w / 2 * player.side;
			}
			final Shot shot = new Shot(player.position, size, size, w, (player.position.h / 2 - size / 2),
					-speed_blade * player.side, 0);

			player.fights.add(shot);
			int end = size * 6;
			int col = 3;
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

		if (key == 5) {
			final int size = MapGeneration.SIZE_FIGHT;
			final int speed_blade = size / 2;
			double w = player.side == 1 ? player.position.w : -size;

			// final Square square = new Square(player.position.x + size * 2 +
			// size / 2,
			// player.position.y + h0 / 2 - size / 2, size, size);

			final Shot shot = new Shot(player.position, size, size, w, (player.position.h / 2 - size / 2),
					-3 * speed_blade * player.side, 0);
			player.fights.add(shot);
			int end = size * 6;
			int col = 14;
			mainTimer = new Timer(time, col, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// square.x -= speed_blade;
					if (player.side == 1) {
						if (shot.dtx >= player.position.w + 2 * size) {
							shot.mx = 3 * speed_blade * player.side;
						}
						if (shot.dtx <= -2 * size) {
							shot.mx = -3 * speed_blade * player.side;
						}
					} else {
						if (shot.dtx >= player.position.w + 2 * size) {
							shot.mx = 3 * speed_blade * -player.side;
						}
						if (shot.dtx <= -2 * size) {
							shot.mx = -3 * speed_blade * -player.side;
						}
					}
					// shot.dtx *= player.side;
					shot.update();
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
			/*
			 * final int size = (int) (MapGeneration.SIZE_FIGHT); int s =
			 * MapGeneration.SIZE_FIGHT;
			 * 
			 * final Shot shot = new Shot(player.position, s, s, player.side *
			 * (player.position.w / 2 - size / 2), (player.position.h / 2 -
			 * size) - 3 * size, 0, 0); shot.angle = 90;
			 */
			final int size = MapGeneration.SIZE;// (int)
			// (MapGeneration.SIZE_FIGHT / 2
			// * 1.8);
			int size0 = MapGeneration.SIZE;
			int radius = size0 * 3;
			/*
			 * final Shot shot = new Shot(player.position, s, s, player.side *
			 * (player.position.w / 2 - size / 2), (player.position.h / 2 -
			 * size) - 3 * size * 2, 0, 0);
			 */
			final Shot shot = new Shot(this.player, 160.0 / 14 * player.side, radius * player.side,
					new Vector2d(size, size), -90 * player.side);

			player.fights.add(shot);
			int col = 14;

			mainTimer = new Timer(time, col, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// square.x -= speed_blade;
					/*
					 * shot.dtx += Math.sin(shot.angle / 180 * 3.14) * size *
					 * player.side; shot.dty += Math.cos(shot.angle / 180 *
					 * 3.14) * size * player.side; shot.angle -= end *
					 * player.side;
					 */
					shot.updateBlade();
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
			/*
			 * final int size = (int) (MapGeneration.SIZE_FIGHT); int s =
			 * MapGeneration.SIZE_FIGHT;
			 * 
			 * final Shot shot = new Shot(player.position, s, s, player.side *
			 * (player.position.w / 2 - size / 2), (player.position.h / 2 -
			 * size) - 3 * size, 0, 0); shot.angle = 90;
			 */
			final int size = MapGeneration.SIZE;// (int)
			// (MapGeneration.SIZE_FIGHT / 2
			// * 1.8);
			int size0 = MapGeneration.SIZE;
			int radius = size0 * 3;
			/*
			 * final Shot shot = new Shot(player.position, s, s, player.side *
			 * (player.position.w / 2 - size / 2), (player.position.h / 2 -
			 * size) - 3 * size * 2, 0, 0);
			 */
			final Shot shot = new Shot(this.player, 150.0 / 14 * player.side, radius * player.side,
					new Vector2d(size, size), -90 * player.side);

			player.fights.add(shot);
			int col = 14;

			mainTimer = new Timer(time, col, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// square.x -= speed_blade;
					/*
					 * shot.dtx += Math.sin(shot.angle / 180 * 3.14) * size *
					 * player.side; shot.dty += Math.cos(shot.angle / 180 *
					 * 3.14) * size * player.side; shot.angle -= end *
					 * player.side;
					 */
					shot.updateBlade();
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
			/*
			 * int size = MapGeneration.SIZE_FIGHT; final int speed_blade = size
			 * / 4; double w = player.side == 1 ? player.position.w : -size;
			 * 
			 * // final Square square = new Square(player.position.x + size * 2
			 * + // size / 2, // player.position.y + h0 / 2 - size / 2, size,
			 * size);
			 * 
			 * final Shot shot = new Shot(player.position, size, size, w,
			 * (player.position.h / 2 - size / 2), -speed_blade * player.side,
			 * 0);
			 */
			final int size = MapGeneration.SIZE;// (int)
			// (MapGeneration.SIZE_FIGHT / 2
			// * 1.8);
			int size0 = MapGeneration.SIZE;
			int radius = size0 * 3;
			/*
			 * final Shot shot = new Shot(player.position, s, s, player.side *
			 * (player.position.w / 2 - size / 2), (player.position.h / 2 -
			 * size) - 3 * size * 2, 0, 0);
			 */
			final Shot shot = new Shot(this.player, 160.0 / 14 * player.side, radius * player.side,
					new Vector2d(size, size), -90 * player.side);

			player.fights.add(shot);

			int col = 14;
			mainTimer = new Timer(time, col, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// square.x -= speed_blade;
					// shot.blade();
					shot.updateBlade();

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
