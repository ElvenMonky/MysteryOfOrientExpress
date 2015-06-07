package com.mystery_of_orient_express.match3_engine.view;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mystery_of_orient_express.match3_engine.model.IGameControl;
import com.mystery_of_orient_express.match3_engine.model.IScoreController;

public class ScoreControl implements IGameControl, IScoreController
{
	private static final String[] digitNames = {
		"score_zero.png", "score_one.png", "score_two.png", "score_three.png", "score_four.png",
		"score_five.png", "score_six.png", "score_seven.png", "score_eight.png", "score_nine.png" 
	};
	private static final String emptyName = "score_empty.png";
	
	private static final int scoreDigits = 8; 
	
	private int score = 0;
	private int combo = 0;
	
	private int x;
	private int y;
	private int width;
	private int height;

	private int scoreX;
	private int scoreY;
	private int scoreItemWidth;
	private int scoreHeight;

	public ScoreControl()
	{
	}
	
	@Override
	public void updateCombo(int matches)
	{
		this.combo = matches == 0 ? 0 : this.combo + matches;
	}

	@Override
	public void updateScore(int score)
	{
		this.score += this.combo * score;
	}

	@Override
	public void load(AssetManager assetManager)
	{
		//assetManager.load("video.png", Texture.class);
		assetManager.load(ScoreControl.emptyName, Texture.class);
		for (String name: ScoreControl.digitNames)
		{
			assetManager.load(name, Texture.class);
		}
	}

	@Override
	public void resize(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		this.scoreHeight = (int)Math.min(72, 0.8f * Math.min(this.height, this.width / (0.75f * ScoreControl.scoreDigits)));
		this.scoreItemWidth = (int)(0.75f * this.scoreHeight);
		int borderX = (this.width - this.scoreItemWidth * ScoreControl.scoreDigits) / 2;
		int borderY = (this.height - this.scoreHeight) / 2;
		this.scoreX = this.x + borderX;
		this.scoreY = this.y + borderY;
	}

	@Override
	public void render(float delta, SpriteBatch batch, AssetManager assetManager)
	{
		int tempScore = this.score;
		Texture image;
		//Texture image = assetManager.get("video.png");;
		//batch.draw(image, this.x, this.y, this.width, this.height);
		int scoreItemX = this.scoreX + this.scoreItemWidth * ScoreControl.scoreDigits;
		for (int i = 0; i < ScoreControl.scoreDigits; ++i)
		{
			if (tempScore > 0 || i == 0)
			{
				image = assetManager.get(ScoreControl.digitNames[tempScore % ScoreControl.digitNames.length], Texture.class);
				tempScore /= ScoreControl.digitNames.length;
			}
			else
			{
				image = assetManager.get(ScoreControl.emptyName, Texture.class);
			}
			scoreItemX -= this.scoreItemWidth;
			batch.draw(image, scoreItemX, this.scoreY, this.scoreItemWidth, this.scoreHeight);
		}
	}

	@Override
	public InputProcessor getInputProcessor()
	{
		return null;
	}

	public int getCombo()
	{
		return this.combo;
	}
}