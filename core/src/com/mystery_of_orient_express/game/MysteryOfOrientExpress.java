package com.mystery_of_orient_express.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mystery_of_orient_express.match3_engine.view.GameScreen;
import com.mystery_of_orient_express.match3_engine.view.IScreen;
import com.mystery_of_orient_express.match3_engine.view.SplashScreen;

public class MysteryOfOrientExpress extends Game
{
	private int screenWidth;
	private int screenHeight;
	private SpriteBatch batch;
	private Viewport viewport;
	private AssetManager assetManager;
	private IScreen nextScreen;

	private SplashScreen splashScreen;
	private GameScreen gameScreen;

	@Override
	public void create()
	{
		this.batch = new SpriteBatch();
		this.viewport = new ScreenViewport();
		this.assetManager = new AssetManager();
		this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.splashScreen = new SplashScreen(this.batch);
		this.splashScreen.load(this.assetManager);
		this.gameScreen = new GameScreen(this.batch);
		this.setNextScreen(this.gameScreen);
		//this.nextScreen = null;
	}

	public void setNextScreen(IScreen screen)
	{
		screen.load(this.assetManager);
		this.nextScreen = screen;
	}

	@Override
	public void resize(int width, int height)
	{
		this.screenWidth = width;
		this.screenHeight = height;
		this.viewport.update(width, height, true);
		super.resize(width, height);
	}

	@Override
	public void render()
	{
		this.batch.setProjectionMatrix(this.viewport.getCamera().projection);
		this.batch.setTransformMatrix(this.viewport.getCamera().view);
		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		this.batch.begin();
		if (this.getScreen() == null)
		{
			this.setScreen(this.splashScreen);
		}
		if (this.getScreen() == this.splashScreen && this.nextScreen != null && this.assetManager.update())
		{
			this.nextScreen.resize(this.screenWidth, this.screenHeight);
			this.setScreen(this.nextScreen);
			Gdx.input.setInputProcessor(this.nextScreen.getInputProcessor());
		}
		super.render();
		this.batch.end();
	}

	@Override
	public void dispose()
	{
		this.batch.dispose();
		this.assetManager.dispose();
		if (this.splashScreen != null)
		{
			this.splashScreen.dispose();
		}
		if (this.gameScreen != null)
		{
			this.gameScreen.dispose();
		}
		super.dispose();
	}
}