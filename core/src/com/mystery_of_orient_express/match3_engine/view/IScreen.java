package com.mystery_of_orient_express.match3_engine.view;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;

public interface IScreen extends Screen
{
	public void load(AssetManager assetManager);
	public InputProcessor getInputProcessor();
}
