package com.mystery_of_orient_express.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.mystery_of_orient_express.game.MysteryOfOrientExpress;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
        	GwtApplicationConfiguration config = new GwtApplicationConfiguration(480, 640);
        	return config;
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new MysteryOfOrientExpress();
        }
}