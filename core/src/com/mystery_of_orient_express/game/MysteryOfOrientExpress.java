package com.mystery_of_orient_express.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MysteryOfOrientExpress extends Game
{
	private int screenWidth, screenHeight;
	private SpriteBatch batch;
	private AssetManager assetManager;
	private Texture splashImage;

	private GameScreen gameScreen;
	
    @Override
	public void create()
    {
    	this.screenWidth = Gdx.graphics.getWidth();
		this.screenHeight = Gdx.graphics.getHeight();
		this.batch = new SpriteBatch();
		this.assetManager = new AssetManager();
		this.assetManager.load("video.png", Texture.class);
		this.assetManager.finishLoading();
		this.splashImage = this.assetManager.get("video.png", Texture.class);
		this.gameScreen = new GameScreen(this.screenWidth, this.screenHeight, this.batch, this.assetManager);
		this.gameScreen.load();
	}
    
    @Override
    public void render()
    {
    	Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		this.batch.begin();
    	if (this.getScreen() != null)
    	{
    		super.render();
    	}
    	else
    	{
	    	if (this.assetManager.update())
	    	{
	    		this.gameScreen.initialize();
	    		this.setScreen(this.gameScreen);
	    		Gdx.input.setInputProcessor(this.gameScreen);
	    	}
	   		float progress = this.assetManager.getProgress();
	    	this.batch.draw(this.splashImage, 0, 0, this.screenWidth, this.screenHeight * progress);
    	}
    	this.batch.end();
    }
	
	@Override
    public void dispose()
	{
        this.batch.dispose();
        this.assetManager.dispose();
        if (this.gameScreen != null)
        {
        	this.gameScreen.dispose();
        }
    }
}