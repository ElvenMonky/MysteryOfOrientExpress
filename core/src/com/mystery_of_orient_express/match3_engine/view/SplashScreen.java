package com.mystery_of_orient_express.match3_engine.view;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SplashScreen extends ScreenAdapter implements IScreen
{
	private int screenWidth;
	private int screenHeight;
	private SpriteBatch batch;
	private AssetManager assetManager;
	private Texture splashImage;
	private Texture progressImage;

	public SplashScreen(SpriteBatch batch)
	{
		this.batch = batch;
	}

	@Override
	public void resize(int width, int height)
	{
		this.screenWidth = width;
		this.screenHeight = height;
	}

	@Override
	public void load(AssetManager assetManager)
	{
		this.assetManager = assetManager;
		this.assetManager.load("splash.png", Texture.class);
		this.assetManager.load("loaded.png", Texture.class);
		this.assetManager.finishLoading();
		this.splashImage = this.assetManager.get("splash.png", Texture.class);
		this.progressImage = this.assetManager.get("loaded.png", Texture.class);
	}

	@Override
	public InputProcessor getInputProcessor()
	{
		return null;
	}

	@Override
	public void render(float delta)
	{
		float progress = this.assetManager.getProgress();
		this.batch.draw(this.splashImage, 0, 0, this.screenWidth, this.screenHeight);
		//this.batch.draw(this.progressImage, 0, 0, 100, 100);
		this.batch.draw(this.progressImage, 0.0f, 0.0f, this.screenWidth * progress, (float)this.screenHeight,
			0, 0, (int)(this.progressImage.getWidth() * progress), this.progressImage.getHeight(), false, false);
	}
}