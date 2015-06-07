package com.mystery_of_orient_express.match3_engine.model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.InputProcessor;

public interface IGameControl
{
	public void load(AssetManager assetManager);
	public void resize(int x, int y, int width, int height);
	public void render(float delta, SpriteBatch batch, AssetManager assetManager);
	public InputProcessor getInputProcessor();
}
