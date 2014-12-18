package com.mystery_of_orient_express.match3_engine.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.mystery_of_orient_express.game.GameScreen;
import com.mystery_of_orient_express.match3_engine.model.CellObject;
import com.mystery_of_orient_express.match3_engine.model.Field;
import com.mystery_of_orient_express.match3_engine.model.GameObject;

public class GameFieldController implements IAnimationHandler, InputProcessor
{
	public static final String[] gemNames = { "gem_yellow.png", "gem_red.png", "gem_green.png", "gem_blue.png", "gem_purple.png", "gem_white.png" };
	public static final String[] soundNames = { "knock.wav", "mystery3_3.wav", "mystery3_4.wav" };

	//Resources
	private GameScreen gameScreen;
	private AssetManager assetManager;

	//Field coordinates
	private Field field;
	
	//Screen coordinates
	private int boardOffset;
	public int cellSize;
	private int gemSize;

	private int touchedX = -1;
	private int touchedY = -1;
	private boolean canMove = false;
	private boolean needKnock = false;
	private int combo = 0;

	public GameFieldController(GameScreen gameScreen, AssetManager assetManager)
	{
		this.gameScreen = gameScreen;
		this.assetManager = assetManager;
		
		this.cellSize = 96;
		this.gemSize = 96;

		this.field = new Field((this.gameScreen.minScreenSize - 16) / this.cellSize);

		this.boardOffset = (this.gameScreen.minScreenSize - this.field.size * this.cellSize) / 2;

		this.gameScreen.objects.clear();
		for (int i = 0; i < this.field.size; ++i)
		{
			for (int j = 0; j < this.field.size; ++j)
			{
				this.field.cells[i][j].object = this.newGem(i, j);
			}
		}
	}
	
	private static boolean match3(CellObject prevGem, CellObject thisGem, CellObject nextGem)
	{
		return thisGem != null && prevGem != null && nextGem != null &&
				thisGem.kind != -1 && prevGem.kind != -1 && nextGem.kind != -1 &&
				prevGem.kind == thisGem.kind && thisGem.kind == nextGem.kind;
	}
	
	private static Integer match2(CellObject prevGem, CellObject thisGem, CellObject nextGem)
	{
		if (thisGem == null || prevGem == null || nextGem == null ||
			thisGem.kind == -1 || prevGem.kind == -1 || nextGem.kind == -1)
			return null;
		if (prevGem.kind == thisGem.kind)
			return 1;
		if (thisGem.kind == nextGem.kind)
			return -1;
		if (prevGem.kind == nextGem.kind)
			return 0;
		return null;
	}
	
	private boolean checkIndex(int index)
	{
		return 0 <= index && index < this.field.size;
	}
	
	private int coordToIndex(float coord)
	{
		return (int)((coord - this.boardOffset) / this.cellSize);
	}
	
	private float indexToCoord(int index)
	{
		return (float)(this.boardOffset + (index + 0.5f) * this.cellSize);
	}
	
	private GameObject newGem(int i, int j)
	{
		int kind = (int)(Math.random() * GameFieldController.gemNames.length);
		GameObject newGem = new GameObject(kind, this.indexToCoord(i), this.indexToCoord(j), this.gemSize, this.gemSize);
		this.gameScreen.objects.add(newGem);
		return newGem;
	}
	
	private void swapObjects(int i1, int j1, int i2, int j2)
	{
		CellObject cellObject = this.field.cells[i1][j1].object;
		this.field.cells[i1][j1].object = this.field.cells[i2][j2].object;
		this.field.cells[i2][j2].object = cellObject;
	}
	
	private void AddGem(Map<GameObject, Integer> map, GameObject gem)
	{
		map.put(gem, map.containsKey(gem) ? map.get(gem) + 1 : 0);
	}
	
	private Map<GameObject, Integer> findMatchedGemsInRows()
	{
		Map<GameObject, Integer> matched = new HashMap<GameObject, Integer>();
		for (int j = 0; j < this.field.size; ++j)
		{
			for (int i = 1; i < this.field.size - 1; ++i)
			{
				CellObject prevGem = this.field.cells[i - 1][j].object;
				CellObject thisGem = this.field.cells[i * 1][j].object;
				CellObject nextGem = this.field.cells[i + 1][j].object;
				if (GameFieldController.match3(prevGem, thisGem, nextGem))
				{
					this.AddGem(matched, (GameObject)prevGem);
					this.AddGem(matched, (GameObject)thisGem);
					this.AddGem(matched, (GameObject)nextGem);
				}
			}
		}
		return matched;
	}
	
	private Map<GameObject, Integer> findMatchedGemsInCols()
	{
		Map<GameObject, Integer> matched = new HashMap<GameObject, Integer>();
		for (int i = 0; i < this.field.size; ++i)
		{
			for (int j = 1; j < this.field.size - 1; ++j)
			{
				CellObject prevGem = this.field.cells[i][j - 1].object;
				CellObject thisGem = this.field.cells[i][j * 1].object;
				CellObject nextGem = this.field.cells[i][j + 1].object;
				if (GameFieldController.match3(prevGem, thisGem, nextGem))
				{
					this.AddGem(matched, (GameObject)prevGem);
					this.AddGem(matched, (GameObject)thisGem);
					this.AddGem(matched, (GameObject)nextGem);
				}
			}
		}
		return matched;
	}

	private Set<GameObject> findGemsToFall()
	{
		Set<GameObject> gemsToFall = new HashSet<GameObject>();
		for (int i = 0; i < this.field.size; ++i)
		{
			for (int j = 0; j < this.field.size; ++j)
			{
				CellObject thisGem = this.field.cells[i][j].object;
				if (thisGem != null)
					continue;

				if (j == this.field.size - 1)
				{
					thisGem = this.newGem(i, j + 1);
				}
				else
				{
					thisGem = this.field.cells[i][j + 1].object;
					this.field.cells[i][j + 1].object = null; // enables chained falling
				}
				this.field.cells[i][j].object = thisGem;
				if (thisGem == null)
					continue;
				
				gemsToFall.add((GameObject)thisGem);
			}
		}
		return gemsToFall;
	}
	
	private boolean testNoMoves()
	{
		for (int i = 0; i < this.field.size; ++i)
		{
			for (int j = 1; j < this.field.size - 1; ++j)
			{
				CellObject prevGem = this.field.cells[i][j - 1].object;
				CellObject thisGem = this.field.cells[i][j * 1].object;
				CellObject nextGem = this.field.cells[i][j + 1].object;
				Integer result = GameFieldController.match2(prevGem, thisGem, nextGem);
				if (result == null)
					continue;

				int kind = result == -1 ? nextGem.kind : prevGem.kind;
				int index = j + 2 * result;
				if (this.checkIndex(index) && this.field.cells[i][index].object.kind == kind)
					return false;

				index = j + result;
				if (this.checkIndex(i - 1) && this.field.cells[i - 1][index].object.kind == kind)
					return false;
				if (this.checkIndex(i + 1) && this.field.cells[i + 1][index].object.kind == kind)
					return false;
			}
		}
		
		for (int j = 0; j < this.field.size; ++j)
		{
			for (int i = 1; i < this.field.size - 1; ++i)
			{
				CellObject prevGem = this.field.cells[i - 1][j].object;
				CellObject thisGem = this.field.cells[i * 1][j].object;
				CellObject nextGem = this.field.cells[i + 1][j].object;
				Integer result = GameFieldController.match2(prevGem, thisGem, nextGem);
				if (result == null)
					continue;

				int kind = result == -1 ? nextGem.kind : prevGem.kind;
				int index = i + 2 * result;
				if (this.checkIndex(index) && this.field.cells[index][j].object.kind == kind)
					return false;

				index = i + result;
				if (this.checkIndex(j - 1) && this.field.cells[index][j - 1].object.kind == kind)
					return false;
				if (this.checkIndex(j + 1) && this.field.cells[index][j + 1].object.kind == kind)
					return false;
			}
		}
		return true;
	}

	public void updateFieldState()
	{
		if (this.gameScreen.animations.size() > 0)
			return;

		Set<GameObject> gemsToFall = this.findGemsToFall();
		if (gemsToFall.size() > 0)
		{
			this.needKnock = true;
			this.gameScreen.animations.add(new FallAnimation(gemsToFall, this.cellSize, this));
			return;
		}
		
		if (this.needKnock)
		{
			this.needKnock = false;
			this.assetManager.get(GameFieldController.soundNames[0], Sound.class).play();
		}

		Map<GameObject, Integer> matchedInRows = this.findMatchedGemsInRows();
		Map<GameObject, Integer> matchedInCols = this.findMatchedGemsInCols();
		Map<GameObject, Integer> crossMatched = new HashMap<GameObject, Integer>();
		for (GameObject obj: matchedInRows.keySet())
		{
			if (matchedInCols.containsKey(obj))
			{
				crossMatched.put(obj, matchedInRows.get(obj) + matchedInCols.get(obj));
				obj.effect = 0;
			}
		}
		for (GameObject obj: crossMatched.keySet())
		{
			matchedInRows.remove(obj);
			matchedInCols.remove(obj);
		}
		if (matchedInRows.size() > 0 || matchedInCols.size() > 0)
		{
			++this.combo;
			Set<GameObject> matchedAll = new HashSet<GameObject>();
			matchedAll.addAll(matchedInRows.keySet());
			matchedAll.addAll(matchedInCols.keySet());
			this.gameScreen.animations.add(new DisappearAnimation(matchedAll, this.gemSize, this));
			this.assetManager.get(GameFieldController.soundNames[Math.min(this.combo, 2)], Sound.class).play();
			return;
		}
		
		this.combo = 0;
		
		if (this.testNoMoves())
		{
			Set<GameObject> all = new HashSet<GameObject>();
			for (int i = 0; i < this.field.size; ++i)
			{
				for (int j = 0; j < this.field.size; ++j)
				{
					all.add((GameObject)this.field.cells[i][j].object);
				}
			}
			this.gameScreen.animations.add(new DisappearAnimation(all, this.gemSize, this));
			return;
		}
		
		this.canMove = true;
	}
	
	public boolean trySwap(int screenX, int screenY, float swapDistance)
	{
		int dx = screenX - this.touchedX;
		int dy = screenY - this.touchedY;
		if (Math.abs(dx) > swapDistance || Math.abs(dy) > swapDistance)
		{
			int i1 = this.coordToIndex(this.touchedX);
			int j1 = this.coordToIndex(this.touchedY);
			
			int i2 = i1;
			int j2 = j1;

			if (Math.abs(dx) > Math.abs(dy)) // horizontal
			{
				i2 += dx > 0 ? 1 : -1;
			}
			else // vertical
			{
				j2 += dy > 0 ? 1 : -1;
			}
			
			if (this.checkIndex(i2) && this.checkIndex(j2))
			{
				this.swapObjects(i1, j1, i2, j2);
				Map<GameObject, Integer> matchedInRows = this.findMatchedGemsInRows();
				Map<GameObject, Integer> matchedInCols = this.findMatchedGemsInCols();
				boolean success = matchedInRows.size() > 0 || matchedInCols.size() > 0;
				GameObject obj1 = (GameObject)this.field.cells[i1][j1].object;
				GameObject obj2 = (GameObject)this.field.cells[i2][j2].object;
				this.gameScreen.animations.add(new SwapAnimation(obj1, obj2, !success, this));
				if (!success)
				{
					this.swapObjects(i1, j1, i2, j2);
				}
				else
				{
					this.canMove = false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void onComplete(IAnimation animation)
	{
		if (animation.getClass() == DisappearAnimation.class)
		{
			DisappearAnimation disappearAnimation = (DisappearAnimation)animation;
			for (int i = 0; i < this.field.size; ++i)
			{
				for (int j = 0; j < this.field.size; ++j)
				{
					CellObject thisGem = this.field.cells[i][j].object;
					if (disappearAnimation.gems.contains(thisGem))
					{
						this.gameScreen.objects.remove(thisGem);
						this.field.cells[i][j].object = null;
					}
				}
			}
		}
		this.gameScreen.animations.remove(animation);
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		if (!this.canMove)
			return false;

		int i = this.coordToIndex(screenX);
		int j = this.coordToIndex(screenY);
		if (this.checkIndex(i) && this.checkIndex(j))
		{
			this.touchedX = screenX;
			this.touchedY = screenY;
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		this.trySwap(screenX, screenY, 0.25f * this.cellSize);
		
		this.touchedX = -1;
		this.touchedY = -1;
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		if (this.trySwap(screenX, screenY, this.cellSize))
		{
			this.touchedX = -1;
			this.touchedY = -1;
			return true;
		}
		return false;
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
