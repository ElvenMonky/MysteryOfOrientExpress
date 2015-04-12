package com.mystery_of_orient_express.match3_engine.model;

public class GameObject extends CellObject
{
	public float posX = 0;
	public float posY = 0;
	public float sizeX = 0;
	public float sizeY = 0;

	public GameObject(int kind, float posX, float posY, float sizeX, float sizeY)
	{
		this.kind = kind;
		this.posX = posX;
		this.posY = posY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
}