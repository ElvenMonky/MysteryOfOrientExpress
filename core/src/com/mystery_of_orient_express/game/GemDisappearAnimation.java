package com.mystery_of_orient_express.game;

import java.util.Set;

public class GemDisappearAnimation implements IAnimation
{
	private static final float totalDuration = 0.1666666f;
	private static final float totalDurationInv = 6.0f;
	private IAnimationHandler handler;
	public Set<GemObject> gems;
	private GemObject[] gemsArray;
	private float currentDuration;
	private float gemSize;
	public GemDisappearAnimation(Set<GemObject> gems, float gemSize, IAnimationHandler handler)
	{
		this.handler = handler;
		this.gems = gems;
		this.gemsArray = gems.toArray(new GemObject[gems.size()]);
		this.gemSize = gemSize;
		this.currentDuration = 0;
	}

	@Override
	public void update(float delta)
	{
		// TODO Auto-generated method stub
		this.currentDuration += delta;
		if (this.currentDuration >= GemDisappearAnimation.totalDuration)
		{
			this.handler.onComplete(this);
		}
		else
		{
			float newSize = this.gemSize * (1 - this.currentDuration * GemDisappearAnimation.totalDurationInv);
			for (int index = 0; index < this.gemsArray.length; ++index)
			{
				this.gemsArray[index].sizeY = this.gemsArray[index].sizeX = newSize;
			}
		}
	}
}
