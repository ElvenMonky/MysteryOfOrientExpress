package com.mystery_of_orient_express.game;

import java.util.Set;

public class GemFallAnimation implements IAnimation
{
	private static final float totalDuration = 0.0666666f;
	private static final float totalDurationInv = 15.0f;
	private IAnimationHandler handler;
	public Set<GemObject> gems;
	private GemObject[] gemsArray;
	private float currentDuration;
	private float fallLength;
	public GemFallAnimation(Set<GemObject> gems, float fallLength, IAnimationHandler handler)
	{
		this.handler = handler;
		this.gems = gems;
		this.gemsArray = gems.toArray(new GemObject[gems.size()]);
		this.fallLength = fallLength;
		this.currentDuration = 0;
	}

	@Override
	public void update(float delta)
	{
		// TODO Auto-generated method stub
		float currentDelta = Math.min(GemFallAnimation.totalDuration - this.currentDuration, delta);
		float deltaLength = this.fallLength * currentDelta * GemFallAnimation.totalDurationInv;
		for (int index = 0; index < this.gemsArray.length; ++index)
		{
			this.gemsArray[index].posY -= deltaLength;
		}
		this.currentDuration += delta;
		if (this.currentDuration >= GemFallAnimation.totalDuration)
		{
			this.handler.onComplete(this);
		}
	}
}
