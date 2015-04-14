package com.mystery_of_orient_express.match3_engine.controller;

import com.mystery_of_orient_express.match3_engine.model.GameObject;
import com.mystery_of_orient_express.match3_engine.model.IAnimation;

public class SwapAnimation implements IAnimation
{
	private static final float totalDuration = 0.1f;
	private static final float totalDurationInv = 10.0f;
	private IAnimationHandler handler;
	public GameObject gem1;
	public GameObject gem2;
	private float currentDuration;
	private float distanceX;
	private float distanceY;
	public boolean swapBack;
	public SwapAnimation(GameObject gem1, GameObject gem2, boolean swapBack, IAnimationHandler handler)
	{
		this.handler = handler;
		this.gem1 = gem1;
		this.gem2 = gem2;
		this.gem1.activity = 2;
		this.gem2.activity = 2;
		this.swapBack = swapBack;
		this.distanceX = this.gem2.posX - this.gem1.posX;
		this.distanceY = this.gem2.posY - this.gem1.posY;
		this.currentDuration = 0;
	}

	@Override
	public void update(float delta)
	{
		// TODO Auto-generated method stub
		float currentDelta = Math.min(SwapAnimation.totalDuration - this.currentDuration, delta);
		float deltaX = this.distanceX * currentDelta * SwapAnimation.totalDurationInv;
		float deltaY = this.distanceY * currentDelta * SwapAnimation.totalDurationInv;
		this.gem1.posX += deltaX;
		this.gem1.posY += deltaY;
		this.gem2.posX -= deltaX;
		this.gem2.posY -= deltaY;
		this.currentDuration += delta;
		if (this.currentDuration >= SwapAnimation.totalDuration)
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
				this.gem1.activity = -1;
				this.gem2.activity = -1;
				this.handler.onComplete(this);
			}
		}
	}
}