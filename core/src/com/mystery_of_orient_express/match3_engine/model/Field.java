package com.mystery_of_orient_express.match3_engine.model;

public class Field
{
	public int size;
	public Cell[][] cells;
	
	public Field(int size)
	{
		this.size = size;
		this.cells = new Cell[this.size][this.size];
		for (int i = 0; i < this.size; ++i)
		{
			for (int j = 0; j < this.size; ++j)
			{
				this.cells[i][j] = new Cell();
			}
		}
	}
}
