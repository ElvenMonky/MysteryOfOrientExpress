package com.mystery_of_orient_express.match3_engine.controller;

import java.util.Set;

import com.mystery_of_orient_express.match3_engine.model.CellObject;
import com.mystery_of_orient_express.match3_engine.model.GameObject;
import com.mystery_of_orient_express.match3_engine.model.IAnimation;

public class FallAnimation implements IAnimation
{
	private static final float totalDuration = 0.0666666f;
	private static final float totalDurationInv = 15.0f;
	private IAnimationHandler handler;
	public Set<CellObject> gems;
	private CellObject[] gemsArray;
	private float currentDuration;
	private float fallLength;
	public FallAnimation(Set<CellObject> gems, float fallLength, IAnimationHandler handler)
	{
		this.handler = handler;
		this.gems = gems;
		this.gemsArray = gems.toArray(new CellObject[gems.size()]);
		for (int index = 0; index < this.gemsArray.length; ++index)
		{
			this.gemsArray[index].activity = 0;
		}
		this.fallLength = fallLength;
		this.currentDuration = 0;
	}

	@Override
	public void update(float delta)
	{
		// TODO Auto-generated method stub
		float currentDelta = Math.min(FallAnimation.totalDuration - this.currentDuration, delta);
		float deltaLength = this.fallLength * currentDelta * FallAnimation.totalDurationInv;
		for (int index = 0; index < this.gemsArray.length; ++index)
		{
			((GameObject)this.gemsArray[index]).posY -= deltaLength;
		}
		this.currentDuration += delta;
		if (this.currentDuration >= FallAnimation.totalDuration)
		{
			for (int index = 0; index < this.gemsArray.length; ++index)
			{
				this.gemsArray[index].activity = -1;
			}
			this.handler.onComplete(this);
		}
	}
}