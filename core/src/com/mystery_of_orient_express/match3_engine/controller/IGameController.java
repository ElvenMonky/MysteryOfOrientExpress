package com.mystery_of_orient_express.match3_engine.controller;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.InputProcessor;

public interface IGameController
{
	public void load(AssetManager assetManager);
	public void update(float delta);
	public void draw(SpriteBatch batch, AssetManager assetManager);
	public InputProcessor getInputProcessor();
}
