package com.rubel.firstgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class Dock {

	Bitmap dockBitmap;
	int dockWidth, dockHeight;
	int leftmostpoint, rightmostpoint;

	Point topLeftPoint = new Point(0, 0), bottomCenterPoint;
	DrawingThread drawingThread;

	boolean movingLeftFlag = false;
	boolean movingRightFlag = false;

	public Dock(DrawingThread drawingThread, int bitmapID) {
		this.drawingThread = drawingThread;
		Bitmap tempBitmap = BitmapFactory.decodeResource(drawingThread.context.getResources(), bitmapID);
		tempBitmap = Bitmap.createScaledBitmap(tempBitmap, drawingThread.displayX,
				(drawingThread.displayX * tempBitmap.getHeight()) / tempBitmap.getWidth(), true);
		dockBitmap = tempBitmap;
		dockHeight = dockBitmap.getHeight();
		dockWidth = dockBitmap.getWidth();

		bottomCenterPoint = new Point((int) drawingThread.displayX / 2, (int) drawingThread.displayY);
		topLeftPoint.y = bottomCenterPoint.y - dockHeight;
		updateInfo();
	}

	private void updateInfo() {
		leftmostpoint = bottomCenterPoint.x - dockWidth / 2;
		rightmostpoint = bottomCenterPoint.x + dockWidth / 2;

		topLeftPoint.x = leftmostpoint;
	}

	public void moveDockToLeft() {
		bottomCenterPoint.x -= 4;
		updateInfo();
	}

	public void moveDockToRight() {
		bottomCenterPoint.x += 4;
		updateInfo();
	}

	public void startMovingLeft() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				movingLeftFlag = true;
				while (movingLeftFlag) {
					moveDockToLeft();
					try {
						sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};

		thread.start();
	}

	public void stopMovingLeft() {
		movingLeftFlag = false;
	}

	public void startMovingRight() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				movingRightFlag = true;
				while (movingRightFlag) {
					moveDockToRight();
					try {
						sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};

		thread.start();
	}

	public void stopMovingRight() {
		movingRightFlag = false;
	}

}
