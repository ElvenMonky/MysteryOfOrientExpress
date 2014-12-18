package com.mystery_of_orient_express.match3_engine.controller;

import java.util.Set;

import com.mystery_of_orient_express.match3_engine.model.GameObject;

public class FallAnimation implements IAnimation
{
	private static final float totalDuration = 0.0666666f;
	private static final float totalDurationInv = 15.0f;
	private IAnimationHandler handler;
	public Set<GameObject> gems;
	private GameObject[] gemsArray;
	private float currentDuration;
	private float fallLength;
	public FallAnimation(Set<GameObject> gems, float fallLength, IAnimationHandler handler)
	{
		this.handler = handler;
		this.gems = gems;
		this.gemsArray = gems.toArray(new GameObject[gems.size()]);
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
			this.gemsArray[index].posY -= deltaLength;
		}
		this.currentDuration += delta;
		if (this.currentDuration >= FallAnimation.totalDuration)
		{
			this.handler.onComplete(this);
		}
	}
}