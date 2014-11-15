package com.mystery_of_orient_express.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameObject implements IDrawable
{
	private Texture image;
	public float posX = 0;
	public float posY = 0;
	public float sizeX = 0;
	public float sizeY = 0;

	public GameObject(Texture image, float posX, float posY, float sizeX, float sizeY)
	{
		this.image = image;
		this.posX = posX;
		this.posY = posY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	@Override
	public void draw(SpriteBatch batch)
	{
		batch.draw(this.image, this.posX - sizeX / 2, this.posY - sizeY / 2, sizeX, sizeY);
	}
}