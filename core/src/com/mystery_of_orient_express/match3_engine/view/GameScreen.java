package com.mystery_of_orient_express.match3_engine.view;

import java.util.List;
import java.util.ArrayList;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mystery_of_orient_express.match3_engine.model.IGameControl;

public class GameScreen extends ScreenAdapter implements IScreen, InputProcessor
{
	private int screenWidth;
	private int screenHeight;
	private int gameFieldSize;
	private SpriteBatch batch;
	private AssetManager assetManager;
	private List<IGameControl> controls;
	private InputProcessor inputProcessor = null;
	
	public GameScreen(SpriteBatch batch)
	{
		this.batch = batch;
		this.controls = new ArrayList<IGameControl>();
		ScoreControl scoreControl = new ScoreControl();
		this.controls.add(new GameFieldControl(scoreControl, 8));
		this.controls.add(scoreControl);
	}

	@Override
	public void load(AssetManager assetManager)
	{
		this.assetManager = assetManager;
		assetManager.load("video.png", Texture.class);
		for (IGameControl control: this.controls)
		{
			control.load(this.assetManager);
		}
	}

	@Override
	public void resize(int width, int height)
	{
		this.screenWidth = width;
		this.screenHeight = height;
		this.gameFieldSize = Math.min(this.screenWidth, (int)(0.9f * this.screenHeight));
		for (IGameControl control: this.controls)
		{
			if (control instanceof ScoreControl)
			{
				control.resize(0, this.gameFieldSize, this.screenWidth, this.screenHeight - this.gameFieldSize);
			}
			if (control instanceof GameFieldControl)
			{
				control.resize((width - this.gameFieldSize) / 2, 0, this.gameFieldSize, this.gameFieldSize);
			}
		}
	}

	@Override
	public InputProcessor getInputProcessor()
	{
		return this;
	}

	@Override
	public void render(float delta)
	{
		Texture image = assetManager.get("video.png");;
		batch.draw(image, 0, 0, this.screenWidth, this.screenHeight);
		for (IGameControl control: this.controls)
		{
			control.render(delta, this.batch, this.assetManager);
		}
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		if (pointer != 0)
			return false;

		for (IGameControl control: this.controls)
		{
			InputProcessor inputProcessor = control.getInputProcessor();
			if (inputProcessor != null && inputProcessor.touchDown(screenX, this.screenHeight - screenY, pointer, button))
			{
				this.inputProcessor = control.getInputProcessor();
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