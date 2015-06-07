package com.mystery_of_orient_express.match3_engine.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Field
{
	private IGameObjectFactory objectFactory;
	private IScoreController scoreController;
	private int size;
	private int kindsCount;
	private Cell[][] cells;
	private List<Match> rowMatches;
	private List<Match> colMatches;

	public Field(IGameObjectFactory objectFactory, IScoreController scoreController, int size, int kindsCount)
	{
		this.size = size;
		this.kindsCount = kindsCount;
		this.objectFactory = objectFactory;
		this.scoreController = scoreController;
		this.cells = new Cell[this.size][this.size];
		this.rowMatches = new ArrayList<Match>();
		this.colMatches = new ArrayList<Match>();
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
	
	public int getSize()
	{
		return this.size;
	}

	public boolean checkIndex(int index)
	{
		return 0 <= index && index < this.size;
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

	public CellObject getGem(int i,int j)
	{
		return this.cells[i][j].object;
	}

	public Set<CellObject> getAllGems()
	{
		Set<CellObject> all = new HashSet<CellObject>();
		for (int i = 0; i < this.size; ++i)
		{
			for (int j = 0; j < this.size; ++j)
			{
				CellObject gem = this.getGem(i, j);
				if (gem != null)
				{
					all.add(gem);
				}
			}
		}
		return all;
	}
	
	public Set<CellObject> getAllGems(int kind)
	{
		Set<CellObject> all = new HashSet<CellObject>();
		for (int i = 0; i < this.size; ++i)
		{
			for (int j = 0; j < this.size; ++j)
			{
				CellObject gem = this.getGem(i, j);
				if (gem != null && gem.kind == kind)
				{
					all.add(gem);
				}
			}
		}
		return all;
	}

	public Set<CellObject> removeGems(Set<CellObject> gems)
	{
		Set<CellObject> chained = new HashSet<CellObject>();
		for (int i = 0; i < this.size; ++i)
		{
			for (int j = 0; j < this.size; ++j)
			{
				CellObject thisGem = this.getGem(i, j);
				if (thisGem != null && gems.contains(thisGem))
				{
					this.cells[i][j].object = null;
					if (thisGem.effect == CellObject.Effects.H_RAY)
					{
						for (int newI = 0; newI < this.size; ++newI)
						{
							CellObject gem = this.getGem(newI, j);
							if (gem != null && gem.activity == -1)
							{
								chained.add(gem);
							}
						}
					}
					else if (thisGem.effect == CellObject.Effects.V_RAY)
					{
						for (int newJ = 0; newJ < this.size; ++newJ)
						{
							CellObject gem = this.getGem(i, newJ);
							if (gem != null && gem.activity == -1)
							{
								chained.add(gem);
							}
						}
					}
					else if (thisGem.effect == CellObject.Effects.AREA)
					{
						for (int newI = Math.max(0, i - 1); newI <= Math.min(this.size - 1, i + 1); ++newI)
						{
							for (int newJ = Math.max(0, j - 1); newJ <= Math.min(this.size - 1, j + 1); ++newJ)
							{
								CellObject gem = this.getGem(newI, newJ);
								if (gem != null && gem.activity == -1)
								{
									chained.add(gem);
								}
							}
						}
					}
					else if (thisGem.effect == CellObject.Effects.KIND)
					{
						int kind = (int) (Math.random() * this.kindsCount);
						for (int newI = 0; newI < this.size; ++newI)
						{
							for (int newJ = 0; newJ < this.size; ++newJ)
							{
								CellObject gem = this.getGem(newI, newJ);
								if (gem != null && gem.activity == -1 && gem.kind == kind)
								{
									chained.add(gem);
								}
							}
						}
					}
				}
			}
		}
		return chained;
	}

	private void swapObjects(int i1, int j1, int i2, int j2)
	{
		CellObject cellObject = this.cells[i1][j1].object;
		this.cells[i1][j1].object = this.cells[i2][j2].object;
		this.cells[i2][j2].object = cellObject;
	}

	private void findMatchedGems(boolean rows)
	{
		List<Match> matched = rows ? this.rowMatches : this.colMatches;
		matched.clear();
		Match current;
		for (int outer = 0; outer < this.size; ++outer)
		{
			current = new Match();
			for (int inner = 0; inner < this.size; ++inner)
			{
				CellObject gem = this.cells[rows ? inner : outer][rows ? outer : inner].object;
				boolean validGem = gem != null && gem.activity == -1 && gem.kind >= 0;
				if (validGem && gem.kind == current.kind)
				{
					++current.length;
					if (current.length == 3)
					{
						current.i = rows ? inner - 2 : outer;
						current.j = rows ? outer : inner - 2;
						matched.add(current);
					}
				}
				else
				{
					if (current.length >= 3)
					{
						current = new Match();
					}
					current.length = validGem ? 1 : 0;
					current.kind = validGem ? gem.kind : -1;
				}
			}
		}
	}

	public Set<CellObject> findGemsToFall()
	{
		Set<CellObject> gemsToFall = new HashSet<CellObject>();
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
				
				gemsToFall.add(thisGem);
			}
		}
		return gemsToFall;
	}

	public Set<CellObject> findMatchedGems()
	{
		this.findMatchedGems(true);
		this.findMatchedGems(false);
		this.scoreController.updateCombo(this.rowMatches.size() + this.colMatches.size());
		Set<CellObject> matchedAll = new HashSet<CellObject>();
		// Add all matched gems with effects
		for (Match match: this.rowMatches)
		{
			for (int d = 0; d < match.length; ++d)
			{
				CellObject gem = this.getGem(match.i + d, match.j);
				if (gem.effect != CellObject.Effects.NONE)
				{
					matchedAll.add(gem);
				}
			}
		}
		for (Match match: this.colMatches)
		{
			for (int d = 0; d < match.length; ++d)
			{
				CellObject gem = this.getGem(match.i, match.j + d);
				if (gem.effect != CellObject.Effects.NONE)
				{
					this.scoreController.updateScore(10);
					matchedAll.add(gem);
				}
			}
		}
		// Mark with AREA effect all cross matched gems without effect
		for (Match rowMatch: this.rowMatches)
		{
			for (Match colMatch: this.colMatches)
			{
				if (rowMatch.i <= colMatch.i && colMatch.i < rowMatch.i + rowMatch.length &&
					colMatch.j <= rowMatch.j && rowMatch.j < colMatch.j + colMatch.length)
				{
					this.scoreController.updateScore(rowMatch.length * (rowMatch.length - 2) * rowMatch.length * (colMatch.length - 2));
					CellObject gem = this.getGem(colMatch.i, rowMatch.j);
					if (gem.effect == CellObject.Effects.NONE)
					{
						gem.effect = CellObject.Effects.AREA;
					}
				}
			}
		}
		// Mark one random gem among long matches with some effect 
		for (Match match: this.rowMatches)
		{
			this.scoreController.updateScore(match.length * (match.length - 1) * (match.length - 2) / 2);
			if (match.length > 3)
			{
				List<CellObject> freeGems = new ArrayList<CellObject>();
				for (int d = 0; d < match.length; ++d)
				{
					CellObject gem = this.getGem(match.i + d, match.j);
					if (gem.effect == CellObject.Effects.NONE)
					{
						freeGems.add(gem);
					}
				}
				if (freeGems.size() > 0)
				{
					CellObject gem = freeGems.get((int)(Math.random() * freeGems.size()));
					if (match.length == 4)
					{
						gem.effect = CellObject.Effects.V_RAY;
					}
					else
					{
						gem.effect = CellObject.Effects.KIND;
						gem.kind = -1;
					}
				}
			}
		}
		for (Match match: this.colMatches)
		{
			if (match.length > 3)
			{
				List<CellObject> freeGems = new ArrayList<CellObject>();
				for (int d = 0; d < match.length; ++d)
				{
					CellObject gem = this.getGem(match.i, match.j + d);
					if (gem.effect == CellObject.Effects.NONE)
					{
						freeGems.add(gem);
					}
				}
				if (freeGems.size() > 0)
				{
					CellObject gem = freeGems.get((int)(Math.random() * freeGems.size()));
					if (match.length == 4)
					{
						gem.effect = CellObject.Effects.H_RAY;
					}
					else
					{
						gem.effect = CellObject.Effects.KIND;
						gem.kind = -1;
					}
				}
			}
		}
		// Add all matched gems without effects
		for (Match match: this.rowMatches)
		{
			for (int d = 0; d < match.length; ++d)
			{
				CellObject gem = this.getGem(match.i + d, match.j);
				if (gem.effect == CellObject.Effects.NONE)
				{
					matchedAll.add(gem);
				}
			}
		}
		for (Match match: this.colMatches)
		{
			for (int d = 0; d < match.length; ++d)
			{
				CellObject gem = this.getGem(match.i, match.j + d);
				if (gem.effect == CellObject.Effects.NONE)
				{
					matchedAll.add(gem);
				}
			}
		}
		return matchedAll;
	}

	public boolean testNoMoves()
	{
		// TODO test for gem pairs with effects
		for (int i = 0; i < this.size; ++i)
		{
			for (int j = 0; j < this.size; ++j)
			{
				CellObject thisGem = this.cells[i][j].object;
				if (thisGem.effect == CellObject.Effects.KIND)
					return false;
			}
		}

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
		CellObject obj1 = this.getGem(i1, j1);
		CellObject obj2 = this.getGem(i2, j2);
		this.findMatchedGems(true);
		this.findMatchedGems(false);
		boolean success = this.rowMatches.size() > 0 || this.colMatches.size() > 0 ||
				obj1.effect == CellObject.Effects.KIND || obj2.effect == CellObject.Effects.KIND ||
				(obj1.effect != CellObject.Effects.NONE && obj2.effect != CellObject.Effects.NONE);
		if (!success)
		{
			this.swapObjects(i1, j1, i2, j2);
		}
		return success;
	}
}