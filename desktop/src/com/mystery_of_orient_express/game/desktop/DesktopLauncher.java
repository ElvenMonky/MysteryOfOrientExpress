package com.mystery_of_orient_express.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mystery_of_orient_express.game.MysteryOfOrientExpress;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 1024;
		new LwjglApplication(new MysteryOfOrientExpress(), config);
	}
}
