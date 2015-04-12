package com.mystery_of_orient_express.match3_engine.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Field
{
	private IGameObjectFactory objectFactory;
	private int size;
	private Cell[][] cells;
	
	public Field(IGameObjectFactory objectFactory, int size)
	{
		this.size = size;
		this.objectFactory = objectFactory;
		this.cells = new Cell[this.size][this.size];
		for (int i = 0; i < this.size; ++i)
		{
			for (int j = 0; j < this.size; ++j)
			{
				this.cells[i][j] = new Cell();
				if (this.objectFactory != null)
				{
					this.cells[i][j].object = this.objectFactory.newGem(i, j);
				}
			}
		}
	}
	
	public boolean checkIndex(int index)
	{
		return 0 <= index && index < this.size;
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
	
	private static void addGemToMap(Map<GameObject, Integer> map, GameObject gem)
	{
		map.put(gem, map.containsKey(gem) ? map.get(gem) + 1 : 0);
	}
	
	public GameObject getGem(int i,int j)
	{
		return (GameObject)this.cells[i][j].object;
	}
	
	public Set<GameObject> getAllGems()
	{
		Set<GameObject> all = new HashSet<GameObject>();
		for (int i = 0; i < this.size; ++i)
		{
			for (int j = 0; j < this.size; ++j)
			{
				all.add((GameObject)this.cells[i][j].object);
			}
		}
		return all;
	}
	
	public void removeGems(Set<GameObject> gems)
	{
		for (int i = 0; i < this.size; ++i)
		{
			for (int j = 0; j < this.size; ++j)
			{
				CellObject thisGem = this.cells[i][j].object;
				if (gems.contains(thisGem))
				{
					this.cells[i][j].object = null;
				}
			}
		}
	}

	private void swapObjects(int i1, int j1, int i2, int j2)
	{
		CellObject cellObject = this.cells[i1][j1].object;
		this.cells[i1][j1].object = this.cells[i2][j2].object;
		this.cells[i2][j2].object = cellObject;
	}

	public Map<GameObject, Integer> findMatchedGemsInRows()
	{
		Map<GameObject, Integer> matched = new HashMap<GameObject, Integer>();
		for (int j = 0; j < this.size; ++j)
		{
			for (int i = 1; i < this.size - 1; ++i)
			{
				CellObject prevGem = this.cells[i - 1][j].object;
				CellObject thisGem = this.cells[i * 1][j].object;
				CellObject nextGem = this.cells[i + 1][j].object;
				if (Field.match3(prevGem, thisGem, nextGem))
				{
					Field.addGemToMap(matched, (GameObject)prevGem);
					Field.addGemToMap(matched, (GameObject)thisGem);
					Field.addGemToMap(matched, (GameObject)nextGem);
				}
			}
		}
		return matched;
	}

	public Map<GameObject, Integer> findMatchedGemsInCols()
	{
		Map<GameObject, Integer> matched = new HashMap<GameObject, Integer>();
		for (int i = 0; i < this.size; ++i)
		{
			for (int j = 1; j < this.size - 1; ++j)
			{
				CellObject prevGem = this.cells[i][j - 1].object;
				CellObject thisGem = this.cells[i][j * 1].object;
				CellObject nextGem = this.cells[i][j + 1].object;
				if (Field.match3(prevGem, thisGem, nextGem))
				{
					Field.addGemToMap(matched, (GameObject)prevGem);
					Field.addGemToMap(matched, (GameObject)thisGem);
					Field.addGemToMap(matched, (GameObject)nextGem);
				}
			}
		}
		return matched;
	}

	public Set<GameObject> findGemsToFall()
	{
		Set<GameObject> gemsToFall = new HashSet<GameObject>();
		for (int i = 0; i < this.size; ++i)
		{
			for (int j = 0; j < this.size; ++j)
			{
				CellObject thisGem = this.cells[i][j].object;
				if (thisGem != null)
					continue;

				if (j == this.size - 1)
				{
					thisGem = this.objectFactory.newGem(i, j + 1);
				}
				else
				{
					thisGem = this.cells[i][j + 1].object;
					this.cells[i][j + 1].object = null; // enables chained falling
				}
				this.cells[i][j].object = thisGem;
				if (thisGem == null)
					continue;
				
				gemsToFall.add((GameObject)thisGem);
			}
		}
		return gemsToFall;
	}

	public boolean testNoMoves()
	{
		for (int i = 0; i < this.size; ++i)
		{
			for (int j = 1; j < this.size - 1; ++j)
			{
				CellObject prevGem = this.cells[i][j - 1].object;
				CellObject thisGem = this.cells[i][j * 1].object;
				CellObject nextGem = this.cells[i][j + 1].object;
				Integer result = Field.match2(prevGem, thisGem, nextGem);
				if (result == null)
					continue;

				int kind = result == -1 ? nextGem.kind : prevGem.kind;
				int index = j + 2 * result;
				if (this.checkIndex(index) && this.cells[i][index].object.kind == kind)
					return false;

				index = j + result;
				if (this.checkIndex(i - 1) && this.cells[i - 1][index].object.kind == kind)
					return false;
				if (this.checkIndex(i + 1) && this.cells[i + 1][index].object.kind == kind)
					return false;
			}
		}

		for (int j = 0; j < this.size; ++j)
		{
			for (int i = 1; i < this.size - 1; ++i)
			{
				CellObject prevGem = this.cells[i - 1][j].object;
				CellObject thisGem = this.cells[i * 1][j].object;
				CellObject nextGem = this.cells[i + 1][j].object;
				Integer result = Field.match2(prevGem, thisGem, nextGem);
				if (result == null)
					continue;

				int kind = result == -1 ? nextGem.kind : prevGem.kind;
				int index = i + 2 * result;
				if (this.checkIndex(index) && this.cells[index][j].object.kind == kind)
					return false;

				index = i + result;
				if (this.checkIndex(j - 1) && this.cells[index][j - 1].object.kind == kind)
					return false;
				if (this.checkIndex(j + 1) && this.cells[index][j + 1].object.kind == kind)
					return false;
			}
		}
		return true;
	}
	
	public boolean testSwap(int i1, int j1, int i2, int j2)
	{
		this.swapObjects(i1, j1, i2, j2);
		GameObject obj1 = this.getGem(i1, j1);
		GameObject obj2 = this.getGem(i2, j2);
		Map<GameObject, Integer> matchedInRows = this.findMatchedGemsInRows();
		Map<GameObject, Integer> matchedInCols = this.findMatchedGemsInCols();
		boolean success = matchedInRows.size() > 0 || matchedInCols.size() > 0 ||
				obj1.effect == CellObject.Effects.KIND || obj2.effect == CellObject.Effects.KIND ||
				(obj1.effect != CellObject.Effects.NONE && obj2.effect != CellObject.Effects.NONE);
		if (!success)
		{
			this.swapObjects(i1, j1, i2, j2);
		}
		return success;
	}
}