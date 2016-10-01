package com.rubel.firstgame;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.VelocityTracker;

public class Robot {
	float centerX, centerY;
	float velocityX, velocityY;
	int width, height;
	Bitmap robotBitmap;
	Paint robotPaint;
	boolean robotFellDownFlag = false;

	public Robot(Bitmap bitmap) {
		robotBitmap = bitmap;
		centerX = centerY = 0;
		height = robotBitmap.getHeight();
		width = robotBitmap.getWidth();
		robotPaint = new Paint();
		velocityX = velocityY = 0;

	}

	public Robot(Bitmap bitmap, int centerX, int centerY) {
		this(bitmap);
		this.centerX = centerX;
		this.centerY = centerY;
	}

	public Robot(Bitmap bitmap, Point center) {
		this(bitmap, center.x, center.y);
	}

	public void setCenter(Point centerPoint) {
		centerX = centerPoint.x;
		centerY = centerPoint.y;
	}

	public void setVelocity(VelocityTracker velocityTracker) {
		velocityX = velocityTracker.getXVelocity();
		velocityY = velocityTracker.getYVelocity();
	}

}
