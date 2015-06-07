package com.mystery_of_orient_express.match3_engine.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mystery_of_orient_express.match3_engine.controller.DisappearAnimation;
import com.mystery_of_orient_express.match3_engine.controller.FallAnimation;
import com.mystery_of_orient_express.match3_engine.controller.GameInputProcessor;
import com.mystery_of_orient_express.match3_engine.controller.IAnimationHandler;
import com.mystery_of_orient_express.match3_engine.controller.IGameFieldInputController;
import com.mystery_of_orient_express.match3_engine.controller.SwapAnimation;
import com.mystery_of_orient_express.match3_engine.model.CellObject;
import com.mystery_of_orient_express.match3_engine.model.Field;
import com.mystery_of_orient_express.match3_engine.model.GameObject;
import com.mystery_of_orient_express.match3_engine.model.IAnimation;
import com.mystery_of_orient_express.match3_engine.model.IGameControl;
import com.mystery_of_orient_express.match3_engine.model.IGameObjectFactory;

public class GameFieldControl implements IGameControl, IAnimationHandler, IGameFieldInputController, IGameObjectFactory
{
	private static final String[] gemNames = { "gem_yellow.png", "gem_red.png", "gem_green.png", "gem_blue.png", "gem_purple.png", "gem_white.png" };
	private static final String[] soundNames = { "knock.wav", "mystery3_3.wav", "mystery3_4.wav" };

	private ScoreControl scoreControl;
	
	//Field coordinates
	private Field field;
	
	private List<GameObject> objects = new ArrayList<GameObject>();
	private List<IAnimation> animations = new ArrayList<IAnimation>();

	//Screen coordinates
	private GameInputProcessor gameInputProcessor;
	private int boardSize;
	private static final float cellSize = 1;
	private static final float gemSize = 1;
	
	private boolean canMove = false;
	private boolean needKnock = false;

	public GameFieldControl(ScoreControl scoreControl, int fieldSize)
	{
		this.scoreControl = scoreControl;

		this.objects.clear();
		this.gameInputProcessor = new GameInputProcessor(this);
		this.field = new Field(this, this.scoreControl, fieldSize, GameFieldControl.gemNames.length);
	}

	@Override
	public void load(AssetManager assetManager)
	{
		assetManager.load("field.png", Texture.class);
		for (String name: GameFieldControl.gemNames)
		{
			assetManager.load(name, Texture.class);
		}
		for (String name: GameFieldControl.soundNames)
		{
			assetManager.load(name, Sound.class);
		}
	}

	@Override
	public void resize(int x, int y, int width, int height)
	{
		this.boardSize = width;
		int fieldSize = this.field.getSize();
		int cellSize = (int)(0.96 * width / fieldSize);
		int boardOffset = (width - cellSize * fieldSize) / 2;
		this.gameInputProcessor.resize(cellSize, x, y, boardOffset);
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
		batch.draw(boardImage, this.gameInputProcessor.getOffset(true),
				this.gameInputProcessor.getOffset(false), this.boardSize, this.boardSize);
		for (int index = 0; index < this.objects.size(); ++index)
		{
			this.drawObject(batch, assetManager, this.objects.get(index));
		}
	}
	
	private void drawImage(SpriteBatch batch, Texture image, float x, float y, float width, float height)
	{
		batch.draw(image, this.gameInputProcessor.indexToCoord(x, true), this.gameInputProcessor.indexToCoord(y, false),
				this.gameInputProcessor.sizeToCoord(width), this.gameInputProcessor.sizeToCoord(height));
	}

	public void drawObject(SpriteBatch batch, AssetManager assetManager, GameObject obj)
	{
		Texture image = null;
		if (obj.kind != -1)
		{
			image = assetManager.get(GameFieldControl.gemNames[obj.kind], Texture.class);
			float minX = obj.posX - 0.5f * obj.sizeX;
			float minY = obj.posY - 0.5f * obj.sizeY;
			if (obj.effect == CellObject.Effects.AREA)
			{
				this.drawImage(batch, image, minX - 0.1f, minY, obj.sizeX, obj.sizeY);
				this.drawImage(batch, image, minX, minY - 0.1f, obj.sizeX, obj.sizeY);
				this.drawImage(batch, image, minX + 0.1f, minY, obj.sizeX, obj.sizeY);
				this.drawImage(batch, image, minX, minY + 0.1f, obj.sizeX, obj.sizeY);
			}
			else if (obj.effect == CellObject.Effects.H_RAY)
			{
				this.drawImage(batch, image, minX - 0.1f, minY, obj.sizeX, obj.sizeY);
				this.drawImage(batch, image, minX + 0.1f, minY, obj.sizeX, obj.sizeY);
			}
			else if (obj.effect == CellObject.Effects.V_RAY)
			{
				this.drawImage(batch, image, minX, minY - 0.1f, obj.sizeX, obj.sizeY);
				this.drawImage(batch, image, minX, minY + 0.1f, obj.sizeX, obj.sizeY);
			}
			this.drawImage(batch, image, minX, minY, obj.sizeX, obj.sizeY);
		}
		else if (obj.effect == CellObject.Effects.KIND)
		{
			float sX = 0.5f * obj.sizeX;
			float sY = 0.5f * obj.sizeY;
			float dX = 0.5f * sX;
			float dY = 0.5f * sY;
			double a = 2 * Math.PI / GameFieldControl.gemNames.length;
			for (int i = 0; i < GameFieldControl.gemNames.length; ++i)
			{
				image = assetManager.get(GameFieldControl.gemNames[i], Texture.class);
				this.drawImage(batch, image, obj.posX - dX + dX * (float)Math.sin(i * a),
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
		int kind = (int)(Math.random() * GameFieldControl.gemNames.length);
		GameObject newGem = new GameObject(kind, i, j, GameFieldControl.gemSize, GameFieldControl.gemSize);
		this.objects.add(newGem);
		return newGem;
	}

	public void updateFieldState(AssetManager assetManager)
	{
		//Check if all animations played
		if (this.animations.size() > 0)
			return;

		//If there is gems to fall - make them all fall first
		Set<CellObject> gemsToFall = this.field.findGemsToFall();
		if (gemsToFall.size() > 0)
		{
			this.needKnock = true;
			this.animations.add(new FallAnimation(gemsToFall, GameFieldControl.cellSize, this));
			return;
		}

		//If all Fall animations complete - knock
		if (this.needKnock)
		{
			this.needKnock = false;
			assetManager.get(GameFieldControl.soundNames[0], Sound.class).play();
		}

		//When no gems to fall - find gems to disappear
		Set<CellObject> matchedAll = this.field.findMatchedGems();
		if (matchedAll.size() > 0)
		{
			// TODO add effect animations for gems with effects
			this.animations.add(new DisappearAnimation(matchedAll, GameFieldControl.gemSize, this));
			assetManager.get(GameFieldControl.soundNames[Math.min(this.scoreControl.getCombo(), 2)], Sound.class).play(0.01f);
			return;
		}
		
		if (this.field.testNoMoves())
		{
			this.animations.add(new DisappearAnimation(this.field.getAllGems(), GameFieldControl.gemSize, this));
			return;
		}
		
		this.canMove = true;
	}

	@Override
	public void swap(int i1, int j1, int i2, int j2)
	{
		boolean success = this.field.testSwap(i1, j1, i2, j2);
		this.animations.add(new SwapAnimation((GameObject)this.field.getGem(i1, j1), (GameObject)this.field.getGem(i2, j2), !success, this));
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
			Set<CellObject> chained = this.field.removeGems(disappearAnimation.gems);
			if (chained.size() > 0)
			{
				this.animations.add(new DisappearAnimation(chained, GameFieldControl.gemSize, this));
			}
			for (CellObject gem: disappearAnimation.gems)
			{
				this.objects.remove(gem);
			}
		}
		if (animation.getClass() == SwapAnimation.class)
		{
			SwapAnimation swapAnimation = (SwapAnimation)animation;
			if (!swapAnimation.swapBack)
			{
				if (swapAnimation.gem1.effect == CellObject.Effects.KIND)
				{
					if (swapAnimation.gem2.effect == CellObject.Effects.KIND)
					{
						Set<CellObject> all = this.field.getAllGems();
						this.animations.add(new DisappearAnimation(all, GameFieldControl.gemSize, this));
					}
					else
					{
						Set<CellObject> allOfKind = this.field.getAllGems(swapAnimation.gem2.kind);
						if (swapAnimation.gem2.effect == CellObject.Effects.AREA)
						{
							for (CellObject gem: allOfKind)
							{
								gem.effect = CellObject.Effects.AREA;
							}
						}
						else if (swapAnimation.gem2.effect != CellObject.Effects.NONE)
						{
							for (CellObject gem: allOfKind)
							{
								if (gem != swapAnimation.gem2)
								{
									gem.effect = Math.random() >= 0.5f ? CellObject.Effects.H_RAY : CellObject.Effects.V_RAY;
								}
							}
						}
						allOfKind.add(swapAnimation.gem1);
						this.animations.add(new DisappearAnimation(allOfKind, GameFieldControl.gemSize, this));
					}
				}
				else if (swapAnimation.gem2.effect == CellObject.Effects.KIND)
				{
					Set<CellObject> allOfKind = this.field.getAllGems(swapAnimation.gem1.kind);
					if (swapAnimation.gem1.effect == CellObject.Effects.AREA)
					{
						for (CellObject gem: allOfKind)
						{
							gem.effect = CellObject.Effects.AREA;
						}
					}
					else if (swapAnimation.gem1.effect != CellObject.Effects.NONE)
					{
						for (CellObject gem: allOfKind)
						{
							if (gem != swapAnimation.gem1)
							{
								gem.effect = Math.random() >= 0.5f ? CellObject.Effects.H_RAY : CellObject.Effects.V_RAY;
							}
						}
					}
					allOfKind.add(swapAnimation.gem2);
					this.animations.add(new DisappearAnimation(allOfKind, GameFieldControl.gemSize, this));
				}
				else if (swapAnimation.gem1.effect == CellObject.Effects.AREA)
				{
					if (swapAnimation.gem2.effect == CellObject.Effects.AREA)
					{
						Set<CellObject> gems = new HashSet<CellObject>();
						// TODO remove 5x5 block
						gems.add(swapAnimation.gem1);
						gems.add(swapAnimation.gem2);
						this.animations.add(new DisappearAnimation(gems, GameFieldControl.gemSize, this));
					}
					else if (swapAnimation.gem2.effect != CellObject.Effects.NONE)
					{
						Set<CellObject> gems = new HashSet<CellObject>();
						// TODO remove 3 rows or 3 columns
						gems.add(swapAnimation.gem1);
						gems.add(swapAnimation.gem2);
						this.animations.add(new DisappearAnimation(gems, GameFieldControl.gemSize, this));
					}
				}
				else if (swapAnimation.gem2.effect == CellObject.Effects.AREA)
				{
					if (swapAnimation.gem1.effect != CellObject.Effects.NONE)
					{
						Set<CellObject> gems = new HashSet<CellObject>();
						// TODO remove 3 rows or 3 columns
						gems.add(swapAnimation.gem1);
						gems.add(swapAnimation.gem2);
						this.animations.add(new DisappearAnimation(gems, GameFieldControl.gemSize, this));
					}
				}
				else if (swapAnimation.gem1.effect != CellObject.Effects.NONE &&
						swapAnimation.gem2.effect != CellObject.Effects.NONE)
				{
					Set<CellObject> gems = new HashSet<CellObject>();
					// TODO remove 1 row and 1 column
					gems.add(swapAnimation.gem1);
					gems.add(swapAnimation.gem2);
					this.animations.add(new DisappearAnimation(gems, GameFieldControl.gemSize, this));
				}
			}
		}
		this.animations.remove(animation);
	}
}