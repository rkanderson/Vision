package com.shsgd.vision;

import com.badlogic.gdx.Game;
import com.shsgd.vision.Screens.MenuScreen;

public class Main extends Game {
    public static final String title = "VISION";
    public static final int V_WIDTH=600, V_HEIGHT=600; //Virtual height and width
	public static final int LEVEL_COUNT = 25; //Will go to thx for beta testing screen rather than crash
	private Main game;

	@Override
	public void create () {
		game = this;
		setScreen(new MenuScreen(this));
	}


	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void render () {

		super.render();
	}
}
