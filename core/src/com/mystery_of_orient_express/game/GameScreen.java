package com.mystery_of_orient_express.game;

import java.util.List;
import java.util.ArrayList;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mystery_of_orient_express.match3_engine.controller.GameFieldController;
import com.mystery_of_orient_express.match3_engine.model.IGameController;
import com.mystery_of_orient_express.match3_engine.score_controller.ScoreController;

public class GameScreen extends ScreenAdapter implements InputProcessor
{
	//Resources
	private SpriteBatch batch;
	private AssetManager assetManager;
	private List<IGameController> controllers;

	//Screen coordinates
	private int screenWidth;
	private int screenHeight;
	private int minScreenSize;
	
	private InputProcessor inputProcessor = null;
	
	public GameScreen(int screenWidth, int screenHeight, SpriteBatch batch, AssetManager assetManager)
	{
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.minScreenSize = Math.min(this.screenWidth, this.screenHeight);
		this.batch = batch;
		this.assetManager = assetManager;
		this.controllers = new ArrayList<IGameController>();
		ScoreController scoreController = new ScoreController(this.minScreenSize, this.screenWidth, this.screenHeight);
		this.controllers.add(new GameFieldController(scoreController, this.minScreenSize));
		this.controllers.add(scoreController);
	}
	
	public void load()
	{
		for (IGameController controller: this.controllers)
		{
			controller.load(this.assetManager);
		}
	}

	@Override
	public void render(float delta)
	{
		for (IGameController controller: this.controllers)
		{
			controller.render(delta, this.batch, this.assetManager);
		}
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		if (pointer != 0)
			return false;

		for (IGameController controller: this.controllers)
		{
			InputProcessor inputProcessor = controller.getInputProcessor();
			if (inputProcessor != null && inputProcessor.touchDown(screenX, this.screenHeight - screenY, pointer, button))
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