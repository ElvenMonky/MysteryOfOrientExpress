package com.mystery_of_orient_express.match3_engine.controller;

import java.util.Set;

import com.mystery_of_orient_express.match3_engine.model.CellObject;
import com.mystery_of_orient_express.match3_engine.model.GameObject;
import com.mystery_of_orient_express.match3_engine.model.IAnimation;

public class DisappearAnimation implements IAnimation
{
	private static final float totalDuration = 0.1666666f;
	private static final float totalDurationInv = 6.0f;
	private IAnimationHandler handler;
	public Set<CellObject> gems;
	private CellObject[] gemsArray;
	private float currentDuration;
	private float gemSize;
	public DisappearAnimation(Set<CellObject> gems, float gemSize, IAnimationHandler handler)
	{
		this.handler = handler;
		this.gems = gems;
		this.gemsArray = gems.toArray(new CellObject[gems.size()]);
		for (int index = 0; index < this.gemsArray.length; ++index)
		{
			this.gemsArray[index].activity = 1;
		}
		this.gemSize = gemSize;
		this.currentDuration = 0;
	}

	@Override
	public void update(float delta)
	{
		// TODO Auto-generated method stub
		this.currentDuration += delta;
		if (this.currentDuration >= DisappearAnimation.totalDuration)
		{
			for (int index = 0; index < this.gemsArray.length; ++index)
			{
				this.gemsArray[index].activity = -1;
			}
			this.handler.onComplete(this);
		}
		else
		{
			float newSize = this.gemSize * (1 - this.currentDuration * DisappearAnimation.totalDurationInv);
			for (int index = 0; index < this.gemsArray.length; ++index)
			{
				((GameObject)this.gemsArray[index]).sizeY = ((GameObject)this.gemsArray[index]).sizeX = newSize;
			}
		}
	}
}