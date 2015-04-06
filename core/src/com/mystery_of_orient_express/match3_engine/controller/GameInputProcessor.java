package com.mystery_of_orient_express.match3_engine.controller;

import com.badlogic.gdx.InputProcessor;

public class GameInputProcessor implements InputProcessor
{
	//Screen coordinates
	private IGameFieldInputController controller;
	private int boardOffset;
	public int cellSize;

	private int touchedX = -1;
	private int touchedY = -1;

	public GameInputProcessor(IGameFieldInputController controller, int cellSize, int boardOffset)
	{
		this.controller = controller;
		this.cellSize = cellSize;
		this.boardOffset = boardOffset;
	}

	public int coordToIndex(float coord)
	{
		return (int)((coord - this.boardOffset) / this.cellSize);
	}
	
	public float indexToCoord(int index)
	{
		return (float)(this.boardOffset + (index + 0.5f) * this.cellSize);
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
			
			if (this.controller.checkIndex(i2) && this.controller.checkIndex(j2))
			{
				this.controller.swap(i1, j1, i2, j2);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		if (!this.controller.canMove())
			return false;

		int i = this.coordToIndex(screenX);
		int j = this.coordToIndex(screenY);
		if (this.controller.checkIndex(i) && this.controller.checkIndex(j))
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
