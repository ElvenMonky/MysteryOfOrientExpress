package com.mystery_of_orient_express.game;

import java.util.List;
import java.util.ArrayList;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen extends ScreenAdapter implements InputProcessor
{
	//Resources
	private SpriteBatch batch;
	private AssetManager assetManager;
	private Texture backgroundImage;
	private Texture boardImage;
	private GameField gameField;

	//Screen coordinates
	private int screenWidth, screenHeight;
	public int minScreenSize;
	
	private InputProcessor inputProcessor = null;
	
	public List<IDrawable> objects = new ArrayList<IDrawable>();
	public List<IAnimation> animations = new ArrayList<IAnimation>();

	public GameScreen(int screenWidth, int screenHeight, SpriteBatch batch, AssetManager assetManager)
	{
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.minScreenSize = Math.min(this.screenWidth, this.screenHeight);
		this.batch = batch;
		this.assetManager = assetManager;
		this.gameField = null;
	}
	
	public void load()
	{
		this.assetManager.load("field.png", Texture.class);
		for (String name: GameField.gemNames)
		{
			this.assetManager.load(name, Texture.class);
		}
		for (String name: GameField.soundNames)
		{
			this.assetManager.load(name, Sound.class);
		}
	}

	public void initialize()
	{
		this.backgroundImage = this.assetManager.get("video.png");
		this.boardImage = this.assetManager.get("field.png");
		this.gameField = new GameField(this, this.assetManager);
	}
	
	@Override
	public void render(float delta)
	{
		this.gameField.updateFieldState();
		for (int index = 0; index < this.animations.size(); ++index)
		{
			this.animations.get(index).update(delta);
		}
		this.batch.draw(this.boardImage, 0, 0, this.minScreenSize, this.minScreenSize);
		for (int index = 0; index < this.objects.size(); ++index)
		{
			this.objects.get(index).draw(this.batch);
		}
		this.batch.draw(this.backgroundImage, 0, this.minScreenSize, this.screenWidth, this.screenHeight - this.minScreenSize);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		if (pointer != 0)
			return false;

		if (this.gameField.touchDown(screenX, this.screenHeight - screenY, pointer, button))
		{
			this.inputProcessor = this.gameField;
			return true;
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