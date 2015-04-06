package com.mystery_of_orient_express.game;

import java.util.List;
import java.util.ArrayList;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mystery_of_orient_express.match3_engine.controller.IAnimation;
import com.mystery_of_orient_express.match3_engine.controller.GameFieldController;
import com.mystery_of_orient_express.match3_engine.model.GameObject;

public class GameScreen extends ScreenAdapter implements InputProcessor
{
	//Resources
	private SpriteBatch batch;
	private AssetManager assetManager;
	private Texture backgroundImage;
	private Texture boardImage;
	private GameFieldController gameFieldController;

	//Screen coordinates
	private int screenWidth, screenHeight;
	public int minScreenSize;
	
	private InputProcessor inputProcessor = null;
	
	public List<GameObject> objects = new ArrayList<GameObject>();
	public List<IAnimation> animations = new ArrayList<IAnimation>();

	public GameScreen(int screenWidth, int screenHeight, SpriteBatch batch, AssetManager assetManager)
	{
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.minScreenSize = Math.min(this.screenWidth, this.screenHeight);
		this.batch = batch;
		this.assetManager = assetManager;
		this.gameFieldController = null;
	}
	
	public void load()
	{
		this.assetManager.load("field.png", Texture.class);
		for (String name: GameFieldController.gemNames)
		{
			this.assetManager.load(name, Texture.class);
		}
		for (String name: GameFieldController.soundNames)
		{
			this.assetManager.load(name, Sound.class);
		}
	}

	public void initialize()
	{
		this.backgroundImage = this.assetManager.get("video.png");
		this.boardImage = this.assetManager.get("field.png");
		this.gameFieldController = new GameFieldController(this, this.assetManager);
	}
	
	@Override
	public void render(float delta)
	{
		this.gameFieldController.updateFieldState();
		for (int index = 0; index < this.animations.size(); ++index)
		{
			this.animations.get(index).update(delta);
		}
		this.batch.draw(this.boardImage, 0, 0, this.minScreenSize, this.minScreenSize);
		for (int index = 0; index < this.objects.size(); ++index)
		{
			this.drawObject(this.batch, this.objects.get(index));
		}
		this.batch.draw(this.backgroundImage, 0, this.minScreenSize, this.screenWidth, this.screenHeight - this.minScreenSize);
	}
	
	public void drawObject(SpriteBatch batch, GameObject obj)
	{
		Texture image = null;
		if (obj.kind != -1)
		{
			image = this.assetManager.get(GameFieldController.gemNames[obj.kind], Texture.class);
		}
		if (null != image)
		{
			batch.draw(image, obj.posX - obj.sizeX / 2, obj.posY - obj.sizeY / 2, obj.sizeX, obj.sizeY);
		}
	}
	
	public boolean pickObject(GameObject obj, float x, float y)
	{
		return obj.posX - obj.sizeX / 2 <= x && x <= obj.posX + obj.sizeX / 2 &&
				obj.posY - obj.sizeY / 2 <= y && y <= obj.posY + obj.sizeY / 2;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		if (pointer != 0)
			return false;

		if (this.gameFieldController.gameInputProcessor.touchDown(screenX, this.screenHeight - screenY, pointer, button))
		{
			this.inputProcessor = this.gameFieldController.gameInputProcessor;
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