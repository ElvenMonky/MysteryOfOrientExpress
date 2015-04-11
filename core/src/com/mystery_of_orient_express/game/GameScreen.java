package com.mystery_of_orient_express.game;

import java.util.List;
import java.util.ArrayList;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mystery_of_orient_express.match3_engine.controller.IGameController;
import com.mystery_of_orient_express.match3_engine.controller.GameFieldController;

public class GameScreen extends ScreenAdapter implements InputProcessor
{
	//Resources
	private SpriteBatch batch;
	private AssetManager assetManager;
	private Texture topPanelImage;
	private Texture boardImage;
	private List<IGameController> controllers;

	//Screen coordinates
	private int screenWidth, screenHeight;
	public int minScreenSize;
	
	private InputProcessor inputProcessor = null;
	
	public GameScreen(int screenWidth, int screenHeight, SpriteBatch batch, AssetManager assetManager)
	{
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.minScreenSize = Math.min(this.screenWidth, this.screenHeight);
		this.batch = batch;
		this.assetManager = assetManager;
		this.controllers = new ArrayList<IGameController>();
		this.controllers.add(new GameFieldController(this));
	}
	
	public void load()
	{
		this.assetManager.load("video.png", Texture.class);
		this.assetManager.load("field.png", Texture.class);
		for (IGameController controller: this.controllers)
		{
			controller.load(this.assetManager);
		}
	}

	public void initialize()
	{
		this.topPanelImage = this.assetManager.get("video.png");
		this.boardImage = this.assetManager.get("field.png");
	}
	
	@Override
	public void render(float delta)
	{
		this.batch.draw(this.boardImage, 0, 0, this.minScreenSize, this.minScreenSize);
		for (IGameController controller: this.controllers)
		{
			controller.update(delta);
			controller.draw(this.batch, this.assetManager);
		}
		this.batch.draw(this.topPanelImage, 0, this.minScreenSize, this.screenWidth, this.screenHeight - this.minScreenSize);
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		if (pointer != 0)
			return false;

		for (IGameController controller: this.controllers)
		{
			if (controller.getInputProcessor().touchDown(screenX, this.screenHeight - screenY, pointer, button))
			{
				this.inputProcessor = controller.getInputProcessor();
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		if (pointer != 0 || this.inputProcessor == null)
			return false;

		if (this.inputProcessor.touchUp(screenX, this.screenHeight - screenY, pointer, button))
			this.inputProcessor = null;

		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		if (pointer != 0 || this.inputProcessor == null)
			return false;

		if (this.inputProcessor.touchDragged(screenX, this.screenHeight - screenY, pointer))
			this.inputProcessor = null;

		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		return false;
	}

	@Override
	public boolean scrolled(int amount)
	{
		return false;
	}

	@Override
	public boolean keyDown(int keycode)
	{
		return false;
	}

	@Override
	public boolean keyUp(int keycode)
	{
		return false;
	}

	@Override
	public boolean keyTyped(char character)
	{
		return false;
	}
}