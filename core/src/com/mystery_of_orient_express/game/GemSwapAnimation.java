package com.mystery_of_orient_express.game;

public class GemSwapAnimation implements IAnimation
{
	private static final float totalDuration = 0.1f;
	private static final float totalDurationInv = 10.0f;
	private IAnimationHandler handler;
	private GemObject gem1;
	private GemObject gem2;
	private float currentDuration;
	private float distanceX;
	private float distanceY;
	private boolean swapBack;
	public GemSwapAnimation(GemObject gem1, GemObject gem2, boolean swapBack, IAnimationHandler handler)
	{
		this.handler = handler;
		this.gem1 = gem1;
		this.gem2 = gem2;
		this.swapBack = swapBack;
		this.distanceX = this.gem2.posX - this.gem1.posX;
		this.distanceY = this.gem2.posY - this.gem1.posY;
		this.currentDuration = 0;
	}

	@Override
	public void update(float delta)
	{
		// TODO Auto-generated method stub
		float currentDelta = Math.min(GemSwapAnimation.totalDuration - this.currentDuration, delta);
		float deltaX = this.distanceX * currentDelta * GemSwapAnimation.totalDurationInv;
		float deltaY = this.distanceY * currentDelta * GemSwapAnimation.totalDurationInv;
		this.gem1.posX += deltaX;
		this.gem1.posY += deltaY;
		this.gem2.posX -= deltaX;
		this.gem2.posY -= deltaY;
		this.currentDuration += delta;
		if (this.currentDuration >= GemSwapAnimation.totalDuration)
		{
			if (this.swapBack)
			{
				this.swapBack = false;
				this.currentDuration = 0;
				this.distanceX = -this.distanceX;
				this.distanceY = -this.distanceY;
				this.update(delta - currentDelta);
			}
			else
			{
				this.handler.onComplete(this);
			}
		}
	}
}
