package com.AntonSibgatulin.location.deamons;

import com.AntonSibgatulin.Players.Player;
import com.AntonSibgatulin.location.LocationModel;
import com.AntonSibgatulin.location.PlayerController;
import com.AntonSibgatulin.location.Square;
import com.AntonSibgatulin.location.Vector2d;

public interface IDeamons {
	public void fire();

	public void update(PlayerController playerController);

	public String getName();

	public void update();

	public long getHelath();

	public void shot(int shoot, PlayerController playerController);

	public Vector2d getPosition();

	public void init(Player player);

	public void jump();

	public void setLocation(LocationModel locationModel);

	public boolean checkGround();

	public Object clone();

	public void deactivate();

	public void setPlayerController(PlayerController controller);

	public void setActive(boolean b);

	public String getId();

	public boolean isActive();

	public boolean isAnotherPosition();

	public int getSide();

	public int getW();

	public int getH();

	public Square getSquarePosition();

}
