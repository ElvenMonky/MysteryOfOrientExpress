package com.mystery_of_orient_express.match3_engine.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mystery_of_orient_express.match3_engine.model.CellObject;
import com.mystery_of_orient_express.match3_engine.model.Field;
import com.mystery_of_orient_express.match3_engine.model.GameObject;
import com.mystery_of_orient_express.match3_engine.model.IAnimation;
import com.mystery_of_orient_express.match3_engine.model.IGameController;
import com.mystery_of_orient_express.match3_engine.model.IGameObjectFactory;
import com.mystery_of_orient_express.match3_engine.score_controller.ScoreController;

public class GameFieldController implements IGameController, IAnimationHandler, IGameFieldInputController, IGameObjectFactory
{
	private static final String[] gemNames = { "gem_yellow.png", "gem_red.png", "gem_green.png", "gem_blue.png", "gem_purple.png", "gem_white.png" };
	private static final String[] soundNames = { "knock.wav", "mystery3_3.wav", "mystery3_4.wav" };

	private ScoreController scoreController;
	
	//Field coordinates
	private Field field;
	
	private List<GameObject> objects = new ArrayList<GameObject>();
	private List<IAnimation> animations = new ArrayList<IAnimation>();

	//Screen coordinates
	private GameInputProcessor gameInputProcessor;
	private int minScreenSize;
	private int cellSize;
	private int gemSize;
	
	private boolean canMove = false;
	private boolean needKnock = false;

	public GameFieldController(ScoreController scoreController, int minScreenSize)
	{
		this.scoreController = scoreController;

		this.minScreenSize = minScreenSize;
		this.cellSize = 96;
		this.gemSize = 96;
		int fieldSize = (minScreenSize - 16) / this.cellSize;

		this.objects.clear();
		this.gameInputProcessor = new GameInputProcessor(this, this.cellSize,
				(minScreenSize - fieldSize * this.cellSize) / 2);
		this.field = new Field(this, this.scoreController, fieldSize);
	}

	@Override
	public void load(AssetManager assetManager)
	{
		assetManager.load("field.png", Texture.class);
		for (String name: GameFieldController.gemNames)
		{
			assetManager.load(name, Texture.class);
		}
		for (String name: GameFieldController.soundNames)
		{
			assetManager.load(name, Sound.class);
		}
	}

	@Override
	public void render(float delta, SpriteBatch batch, AssetManager assetManager)
	{
		//Play animations
		for (int index = 0; index < this.animations.size(); ++index)
		{
			this.animations.get(index).update(delta);
		}
		
		this.updateFieldState(assetManager);

		Texture boardImage = assetManager.get("field.png");
		batch.draw(boardImage, 0, 0, this.minScreenSize, this.minScreenSize);
		for (int index = 0; index < this.objects.size(); ++index)
		{
			this.drawObject(batch, assetManager, this.objects.get(index));
		}
	}

	public void drawObject(SpriteBatch batch, AssetManager assetManager, GameObject obj)
	{
		Texture image = null;
		if (obj.kind != -1)
		{
			image = assetManager.get(GameFieldController.gemNames[obj.kind], Texture.class);
			float minX = obj.posX - 0.5f * obj.sizeX;
			float minY = obj.posY - 0.5f * obj.sizeY;
			if (obj.effect == CellObject.Effects.AREA)
			{
				batch.draw(image, minX - 10, minY, obj.sizeX, obj.sizeY);
				batch.draw(image, minX, minY - 10, obj.sizeX, obj.sizeY);
				batch.draw(image, minX + 10, minY, obj.sizeX, obj.sizeY);
				batch.draw(image, minX, minY + 10, obj.sizeX, obj.sizeY);
			}
			else if (obj.effect == CellObject.Effects.H_RAY)
			{
				batch.draw(image, minX - 10, minY, obj.sizeX, obj.sizeY);
				batch.draw(image, minX + 10, minY, obj.sizeX, obj.sizeY);
			}
			else if (obj.effect == CellObject.Effects.V_RAY)
			{
				batch.draw(image, minX, minY - 10, obj.sizeX, obj.sizeY);
				batch.draw(image, minX, minY + 10, obj.sizeX, obj.sizeY);
			}
			batch.draw(image, minX, minY, obj.sizeX, obj.sizeY);
		}
		else if (obj.effect == CellObject.Effects.KIND)
		{
			float sX = 0.5f * obj.sizeX;
			float sY = 0.5f * obj.sizeY;
			float dX = 0.5f * sX;
			float dY = 0.5f * sY;
			double a = 2 * Math.PI / GameFieldController.gemNames.length;
			for (int i = 0; i < GameFieldController.gemNames.length; ++i)
			{
				image = assetManager.get(GameFieldController.gemNames[i], Texture.class);
				batch.draw(image, obj.posX - dX + dX * (float)Math.sin(i * a),
						obj.posY - dY + dY * (float)Math.cos(i * a), sX, sY);
			}
		}
	}

	@Override
	public InputProcessor getInputProcessor()
	{
		return this.gameInputProcessor;
	}

	// TODO use or remove this function
	public boolean pickObject(GameObject obj, float x, float y)
	{
		return obj.posX - obj.sizeX / 2 <= x && x <= obj.posX + obj.sizeX / 2 &&
				obj.posY - obj.sizeY / 2 <= y && y <= obj.posY + obj.sizeY / 2;
	}

	@Override
	public boolean canMove()
	{
		return this.canMove;
	}
	
	@Override
	public boolean checkIndex(int index)
	{
		return this.field.checkIndex(index);
	}

	@Override
	public GameObject newGem(int i, int j)
	{
		int kind = (int)(Math.random() * GameFieldController.gemNames.length);
		GameObject newGem = new GameObject(kind, this.gameInputProcessor.indexToCoord(i), this.gameInputProcessor.indexToCoord(j), this.gemSize, this.gemSize);
		this.objects.add(newGem);
		return newGem;
	}

	public void updateFieldState(AssetManager assetManager)
	{
		//Check if all animations played
		if (this.animations.size() > 0)
			return;

		//If there is gems to fall - make them all fall first
		Set<GameObject> gemsToFall = this.field.findGemsToFall();
		if (gemsToFall.size() > 0)
		{
			this.needKnock = true;
			this.animations.add(new FallAnimation(gemsToFall, this.cellSize, this));
			return;
		}

		//If all Fall animations complete - knock
		if (this.needKnock)
		{
			this.needKnock = false;
			assetManager.get(GameFieldController.soundNames[0], Sound.class).play();
		}

		//When no gems to fall - find gems to disappear
		Set<GameObject> matchedAll = this.field.findMatchedGems();
		if (matchedAll.size() > 0)
		{
			// TODO add effect animations for gems with effects
			this.animations.add(new DisappearAnimation(matchedAll, this.gemSize, this));
			assetManager.get(GameFieldController.soundNames[Math.min(this.scoreController.getCombo(), 2)], Sound.class).play(0.01f);
			return;
		}
		
		if (this.field.testNoMoves())
		{
			this.animations.add(new DisappearAnimation(this.field.getAllGems(), this.gemSize, this));
			return;
		}
		
		this.canMove = true;
	}

	@Override
	public void swap(int i1, int j1, int i2, int j2)
	{
		boolean success = this.field.testSwap(i1, j1, i2, j2);
		this.animations.add(new SwapAnimation(this.field.getGem(i1, j1), this.field.getGem(i2, j2), !success, this));
		if (success)
		{
			this.canMove = false;
		}
	}

	@Override
	public void onComplete(IAnimation animation)
	{
		if (animation.getClass() == DisappearAnimation.class)
		{
			DisappearAnimation disappearAnimation = (DisappearAnimation)animation;
			this.field.removeGems(disappearAnimation.gems);
			for (GameObject gem: disappearAnimation.gems)
			{
				this.objects.remove(gem);
			}
		}
		this.animations.remove(animation);
	}
}