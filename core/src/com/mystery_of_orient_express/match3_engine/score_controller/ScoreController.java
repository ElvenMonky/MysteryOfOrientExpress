package com.mystery_of_orient_express.match3_engine.score_controller;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mystery_of_orient_express.match3_engine.model.IGameController;
import com.mystery_of_orient_express.match3_engine.model.IScoreController;

public class ScoreController implements IGameController, IScoreController
{
	private static final String[] digitNames = {
		"score_zero.png", "score_one.png", "score_two.png", "score_three.png", "score_four.png",
		"score_five.png", "score_six.png", "score_seven.png", "score_eight.png", "score_nine.png" 
	};
	private static final String emptyName = "score_empty.png";
	
	private static final int scoreDigits = 8; 
	
	private int score = 0;
	private int combo = 0;
	
	private int minScreenSize;
	private int screenWidth;
	private int screenHeight;

	private int x;
	private int y;
	private int scoreCellSize;
	private int scoreHeight;

	public ScoreController(int minScreenSize, int screenWidth, int screenHeight)
	{
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.minScreenSize = minScreenSize;

		this.x = 20;
		this.y = minScreenSize + 20;
		this.scoreCellSize = 72;
		this.scoreHeight = 96;
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
		assetManager.load("video.png", Texture.class);
		assetManager.load(ScoreController.emptyName, Texture.class);
		for (String name: ScoreController.digitNames)
		{
			assetManager.load(name, Texture.class);
		}
	}

	@Override
	public void render(float delta, SpriteBatch batch, AssetManager assetManager)
	{
		Texture image = assetManager.get("video.png");;
		batch.draw(image, 0, this.minScreenSize, this.screenWidth, this.screenHeight - this.minScreenSize);
		int tempScore = this.score;
		for (int i = 0; i < ScoreController.scoreDigits; ++i)
		{
			if (tempScore > 0 || i == 0)
			{
				image = assetManager.get(ScoreController.digitNames[tempScore % ScoreController.digitNames.length], Texture.class);
				tempScore /= ScoreController.digitNames.length;
			}
			else
			{
				image = assetManager.get(ScoreController.emptyName, Texture.class);
			}
			batch.draw(image, x + scoreCellSize * (ScoreController.scoreDigits - i - 1), y, scoreCellSize, scoreHeight);
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
