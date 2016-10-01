package com.rubel.firstgame;

import java.util.Random;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.VelocityTracker;

@SuppressLint("Recycle")
public class GameView extends SurfaceView implements Callback {
	Context context;
	SurfaceHolder surfaceHolder;
	DrawingThread drawingThread;
	VelocityTracker velocityTracker;

	public GameView(Context context) {
		super(context);
		this.context = context;
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		drawingThread = new DrawingThread(this, context);
		velocityTracker = VelocityTracker.obtain();

	}

	public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		drawingThread = new DrawingThread(this, context);
		velocityTracker = VelocityTracker.obtain();
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		drawingThread = new DrawingThread(this, context);
		velocityTracker = VelocityTracker.obtain();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			drawingThread.start();
		} catch (Exception e) {
			restartThread();
		}
	}

	private void restartThread() {
		drawingThread.stopThread();
		drawingThread = null;
		drawingThread = new DrawingThread(this, context);
		drawingThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		drawingThread.stopThread();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (drawingThread.pauseFlag == true || drawingThread.infoFlag == true) {
			return true;
		}

		Point touchPoint = new Point((int) event.getX(), (int) event.getY());
		Random random = new Random();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			drawingThread.touchedFlag = true;
			velocityTracker.addMovement(event);
			drawingThread.allRobots.add(new Robot(drawingThread.allPossibleRobots.get(random.nextInt(6)), touchPoint));
			break;
		case MotionEvent.ACTION_UP:
			velocityTracker.computeCurrentVelocity(30);
			drawingThread.allRobots.get(drawingThread.allRobots.size() - 1).setVelocity(velocityTracker);
			drawingThread.touchedFlag = false;
			break;
		case MotionEvent.ACTION_MOVE:
			velocityTracker.addMovement(event);
			drawingThread.allRobots.get(drawingThread.allRobots.size() - 1).setCenter(touchPoint);
			break;

		default:
			break;
		}
		return true;
	}

}