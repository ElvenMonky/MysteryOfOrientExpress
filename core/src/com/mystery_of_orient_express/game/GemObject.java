package com.mystery_of_orient_express.game;

import com.badlogic.gdx.graphics.Texture;

public class GemObject extends GameObject implements IPickable
{
	public int kind;
	
	public GemObject(int kind, Texture image, float posX, float posY, int sizeX, int sizeY)
	{
		super(image, posX, posY, sizeX, sizeY);
		this.kind = kind;
	}
	
	@Override
	public boolean pick(float x, float y)
	{
		return this.posX - sizeX / 2 <= x && x <= this.posX + sizeX / 2 &&
			this.posX - sizeX / 2 <= x && x <= this.posX + sizeX / 2;
	}
}