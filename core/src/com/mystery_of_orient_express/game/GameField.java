package com.mystery_of_orient_express.game;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class GameField implements IAnimationHandler, InputProcessor
{
	public static final String[] gemNames = { "gem_yellow.png", "gem_red.png", "gem_green.png", "gem_blue.png", "gem_purple.png", "gem_white.png" };
	public static final String[] soundNames = { "knock.wav", "mystery3_3.wav", "mystery3_4.wav" };

	//Resources
	private GameScreen gameScreen;
	private AssetManager assetManager;

	//Field coordinates
	private int fieldSize;
	
	//Screen coordinates
	private int boardOffset;
	public int cellSize;
	private int gemSize;

	private GemObject[][] gems;
	
	private int touchedX = -1;
	private int touchedY = -1;
	private boolean canMove = false;
	private boolean needKnock = false;
	private int combo = 0;

	public GameField(GameScreen gameScreen, AssetManager assetManager)
	{
		this.gameScreen = gameScreen;
		this.assetManager = assetManager;
		
		this.cellSize = 96;
		this.gemSize = 96;

		this.fieldSize = (this.gameScreen.minScreenSize - 16) / this.cellSize;
		this.gems = new GemObject[this.fieldSize][this.fieldSize];

		this.boardOffset = (this.gameScreen.minScreenSize - this.fieldSize * this.cellSize) / 2;

		this.gameScreen.objects.clear();
		for (int i = 0; i < this.fieldSize; ++i)
		{
			for (int j = 0; j < this.fieldSize; ++j)
			{
				this.gems[i][j] = this.newGem(i, j);
			}
		}
	}
	
	private static boolean match3(GemObject prevGem, GemObject thisGem, GemObject nextGem)
	{
		return thisGem != null && prevGem != null && nextGem != null &&
				prevGem.kind == thisGem.kind && thisGem.kind == nextGem.kind;
	}
	
	private static Integer match2(GemObject prevGem, GemObject thisGem, GemObject nextGem)
	{
		if (thisGem == null || prevGem == null || nextGem == null)
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
		return 0 <= index && index < this.fieldSize;
	}
	
	private int coordToIndex(float coord)
	{
		return (int)((coord - this.boardOffset) / this.cellSize);
	}
	
	private float indexToCoord(int index)
	{
		return (float)(this.boardOffset + (index + 0.5f) * this.cellSize);
	}
	
	private GemObject newGem(int i, int j)
	{
		int gemTypeIndex = (int)(Math.random() * GameField.gemNames.length);
		GemObject newGem = new GemObject(gemTypeIndex, this.assetManager.get(GameField.gemNames[gemTypeIndex], Texture.class),
				this.indexToCoord(i), this.indexToCoord(j), this.gemSize, this.gemSize);
		this.gameScreen.objects.add(newGem);
		return newGem;
	}
	
	private void swapGems(int i1, int j1, int i2, int j2)
	{
		GemObject gem = this.gems[i1][j1];
		this.gems[i1][j1] = this.gems[i2][j2];
		this.gems[i2][j2] = gem;
	}
	
	private Set<GemObject> findMatchedGemsInRows()
	{
		Set<GemObject> matched = new HashSet<GemObject>();
		for (int j = 0; j < this.fieldSize; ++j)
		{
			for (int i = 1; i < this.fieldSize - 1; ++i)
			{
				GemObject prevGem = this.gems[i - 1][j];
				GemObject thisGem = this.gems[i * 1][j];
				GemObject nextGem = this.gems[i + 1][j];
				if (GameField.match3(prevGem, thisGem, nextGem))
				{
					matched.add(prevGem);
					matched.add(thisGem);
					matched.add(nextGem);
				}
			}
		}
		return matched;
	}
	
	private Set<GemObject> findMatchedGemsInCols()
	{
		Set<GemObject> matched = new HashSet<GemObject>();
		for (int i = 0; i < this.fieldSize; ++i)
		{
			for (int j = 1; j < this.fieldSize - 1; ++j)
			{
				GemObject prevGem = this.gems[i][j - 1];
				GemObject thisGem = this.gems[i][j * 1];
				GemObject nextGem = this.gems[i][j + 1];
				if (GameField.match3(prevGem, thisGem, nextGem))
				{
					matched.add(prevGem);
					matched.add(thisGem);
					matched.add(nextGem);
				}
			}
		}
		return matched;
	}

	private Set<GemObject> findGemsToFall()
	{
		Set<GemObject> gemsToFall = new HashSet<GemObject>();
		for (int i = 0; i < this.fieldSize; ++i)
		{
			for (int j = 0; j < this.fieldSize; ++j)
			{
				GemObject thisGem = this.gems[i][j];
				if (thisGem != null)
					continue;

				if (j == this.fieldSize - 1)
				{
					thisGem = this.newGem(i, j + 1);
				}
				else
				{
					thisGem = this.gems[i][j + 1];
					this.gems[i][j + 1] = null; // enables chained falling
				}
				this.gems[i][j] = thisGem;
				if (thisGem == null)
					continue;
				
				gemsToFall.add(thisGem);
			}
		}
		return gemsToFall;
	}
	
	private boolean testNoMoves()
	{
		for (int i = 0; i < this.fieldSize; ++i)
		{
			for (int j = 1; j < this.fieldSize - 1; ++j)
			{
				GemObject prevGem = this.gems[i][j - 1];
				GemObject thisGem = this.gems[i][j * 1];
				GemObject nextGem = this.gems[i][j + 1];
				Integer result = GameField.match2(prevGem, thisGem, nextGem);
				if (result == null)
					continue;

				int kind = result == -1 ? nextGem.kind : prevGem.kind;
				int index = j + 2 * result;
				if (this.checkIndex(index) && this.gems[i][index].kind == kind)
					return false;

				index = j + result;
				if (this.checkIndex(i - 1) && this.gems[i - 1][index].kind == kind)
					return false;
				if (this.checkIndex(i + 1) && this.gems[i + 1][index].kind == kind)
					return false;
			}
		}
		
		for (int j = 0; j < this.fieldSize; ++j)
		{
			for (int i = 1; i < this.fieldSize - 1; ++i)
			{
				GemObject prevGem = this.gems[i - 1][j];
				GemObject thisGem = this.gems[i * 1][j];
				GemObject nextGem = this.gems[i + 1][j];
				Integer result = GameField.match2(prevGem, thisGem, nextGem);
				if (result == null)
					continue;

				int kind = result == -1 ? nextGem.kind : prevGem.kind;
				int index = i + 2 * result;
				if (this.checkIndex(index) && this.gems[index][j].kind == kind)
					return false;

				index = i + result;
				if (this.checkIndex(j - 1) && this.gems[index][j - 1].kind == kind)
					return false;
				if (this.checkIndex(j + 1) && this.gems[index][j + 1].kind == kind)
					return false;
			}
		}
		return true;
	}

	public void updateFieldState()
	{
		if (this.gameScreen.animations.size() > 0)
			return;

		Set<GemObject> gemsToFall = this.findGemsToFall();
		if (gemsToFall.size() > 0)
		{
			this.needKnock = true;
			this.gameScreen.animations.add(new GemFallAnimation(gemsToFall, this.cellSize, this));
			return;
		}
		
		if (this.needKnock)
		{
			this.needKnock = false;
			Sound disappearSound = this.assetManager.get(GameField.soundNames[0], Sound.class);
			disappearSound.play();
		}

		Set<GemObject> matchedInRows = this.findMatchedGemsInRows();
		Set<GemObject> matchedInCols = this.findMatchedGemsInCols();
		if (matchedInRows.size() > 0 || matchedInCols.size() > 0)
		{
			if (this.combo < 2)
				++this.combo;
			Set<GemObject> matchedAll = new HashSet<GemObject>();
			matchedAll.addAll(matchedInRows);
			matchedAll.addAll(matchedInCols);
			this.gameScreen.animations.add(new GemDisappearAnimation(matchedAll, this.gemSize, this));
			Sound disappearSound = this.assetManager.get(GameField.soundNames[this.combo], Sound.class);
			disappearSound.play();
			return;
		}
		
		this.combo = 0;
		
		if (this.testNoMoves())
		{
			Set<GemObject> all = new HashSet<GemObject>();
			for (int i = 0; i < this.fieldSize; ++i)
			{
				for (int j = 0; j < this.fieldSize; ++j)
				{
					all.add(this.gems[i][j]);
				}
			}
			this.gameScreen.animations.add(new GemDisappearAnimation(all, this.gemSize, this));
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
				this.swapGems(i1, j1, i2, j2);
				Set<GemObject> matchedInRows = this.findMatchedGemsInRows();
				Set<GemObject> matchedInCols = this.findMatchedGemsInCols();
				boolean success = matchedInRows.size() > 0 || matchedInCols.size() > 0;
				this.gameScreen.animations.add(new GemSwapAnimation(this.gems[i1][j1], this.gems[i2][j2], !success, this));
				if (!success)
				{
					this.swapGems(i1, j1, i2, j2);
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
		if (animation.getClass() == GemDisappearAnimation.class)
		{
			GemDisappearAnimation gemDisappearAnimation = (GemDisappearAnimation)animation;
			for (int i = 0; i < this.fieldSize; ++i)
			{
				for (int j = 0; j < this.fieldSize; ++j)
				{
					GemObject thisGem = this.gems[i][j];
					if (gemDisappearAnimation.gems.contains(thisGem))
					{
						this.gameScreen.objects.remove(thisGem);
						this.gems[i][j] = null;
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