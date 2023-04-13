package com.AntonSibgatulin.location;

import com.AntonSibgatulin.Players.Player;

public class Shot {
	public double w, h, dtx, dty = 0;
	public Square position = null;
	public Square pos = null;
	public double mx = 0;
	public double my = 0;
	public double angle = 0;

	public PlayerController playerController = null;
	public double velocity = 0;
	public double linearVelocity = 0;
	public Vector2d size = null;

	public Shot(Square position, double w, double h, double dx, double dy, double mx, double my) {
		this.position = position;
		this.h = h;
		this.w = w;
		this.dtx = dx;
		this.dty = dy;
		pos = new Square(position.x + dtx, position.y + dty, w, h);
		this.mx = mx;
		this.my = my;
	}

	public Shot(PlayerController playerController, double velocity, double linearVelocity, Vector2d size, int angle) {

		this.playerController = playerController;
		this.pos = new Square(playerController.position.x, playerController.position.y, size.x, size.y);
		this.h = size.y;
		this.w = size.x;
		this.velocity = velocity;
		this.linearVelocity = linearVelocity;
		this.size = size;
		this.angle = angle;

	}

	public void updateBlade() {
		angle += velocity;

		pos.x = playerController.position.x + playerController.position.w / 2
				+ Math.cos(fromAngleToRad()) * linearVelocity;
		pos.y = playerController.position.y + playerController.position.h / 2
				+ Math.sin(fromAngleToRad()) * linearVelocity;

	}

	public double fromAngleToRad() {
		return angle / 180 * Math.PI;
	}

	public void blade() {
		pos.x = position.x + dtx;
		pos.y = position.y + dty;
		dtx -= mx;
		dty -= my;
	}

	public void updownblad(Player player) {

	}

	public void updatePos() {
		if (position != null) {
			pos.x = position.x + dtx;
			pos.y = position.y + dty;
		}
	}

	public void update() {
		pos.x = position.x + dtx;
		pos.y = position.y + dty;
		dtx -= mx;
		dty -= my;
	}
}
