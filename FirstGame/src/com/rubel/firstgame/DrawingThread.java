package com.rubel.firstgame;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class DrawingThread extends Thread {
	private Canvas canvas;
	GameView gameView;
	Context context;
	boolean threadFlag = false;
	boolean touchedFlag = false;
	boolean pauseFlag = false;
	boolean infoFlag = false;
	int maxScore = 0;
	Bitmap backgroundBitmap;
	Bitmap infoBitmap;
	int displayX, displayY;
	ArrayList<Robot> allRobots;
	ArrayList<Bitmap> allPossibleRobots;
	AnimationThread animationThread;
	ScoreCounterThread scoreCounterThread;
	Paint scorePaint;
	Dock dock;

	public DrawingThread(GameView gameView, Context context) {
		super();
		this.gameView = gameView;
		this.context = context;
		initializeAll();

	}

	private void initializeAll() {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display defaultDisplay = windowManager.getDefaultDisplay();
		Point displayDimension = new Point();
		defaultDisplay.getSize(displayDimension);
		displayX = displayDimension.x;
		displayY = displayDimension.y;
		backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
		backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, displayX, displayY, true);

		infoBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.about_us);
		infoBitmap = Bitmap.createScaledBitmap(infoBitmap, displayX / 2, displayY / 2, true);

		initializeAllPossibleRobots();
		scoreCounterThread = new ScoreCounterThread(this);
		dock = new Dock(this, R.drawable.dock);
		ScorePainter();
	}

	private void ScorePainter() {
		scorePaint = new Paint();
		scorePaint.setColor(Color.WHITE);
		scorePaint.setTextAlign(Align.CENTER);
		scorePaint.setTextSize(displayX / 15);

	}

	private void initializeAllPossibleRobots() {
		allRobots = new ArrayList<Robot>();
		allPossibleRobots = new ArrayList<Bitmap>();

		allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.robo1));
		allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.robo2));
		allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.robo3));
		allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.robo4));
		allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.robo5));
		allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.robo6));

	}

	private Bitmap giveResizedRobotBitmap(int resourceID) {
		Bitmap tempBitmap = BitmapFactory.decodeResource(context.getResources(), resourceID);
		tempBitmap = Bitmap.createScaledBitmap(tempBitmap, displayX / 5,
				(tempBitmap.getHeight() / tempBitmap.getWidth()) * (displayX / 5), true);
		return tempBitmap;
	}

	@Override
	public void run() {
		threadFlag = true;
		animationThread = new AnimationThread(this);
		animationThread.start();
		scoreCounterThread.start();

		while (threadFlag) {
			canvas = gameView.surfaceHolder.lockCanvas();

			try {
				synchronized (gameView.surfaceHolder) {
					updateDisplay();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (canvas != null) {
					gameView.surfaceHolder.unlockCanvasAndPost(canvas);
				}

			}

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		animationThread.stopThread();
		scoreCounterThread.stopThread();

	}

	private void updateDisplay() {
		canvas.drawBitmap(backgroundBitmap, 0, 0, null);
		drawDock();

		Robot tempRobot;
		for (int i = 0; i < allRobots.size(); i++) {
			tempRobot = allRobots.get(i);
			canvas.drawBitmap(tempRobot.robotBitmap, tempRobot.centerX - (tempRobot.width / 2),
					tempRobot.centerY - (tempRobot.height / 2), tempRobot.robotPaint);
		}
		if (pauseFlag) {
			pauseStateDraw();
		} else if (infoFlag) {
			infoStateDraw();
		}
		if (!infoFlag) {
			drawScore();
		}

	}

	private void infoStateDraw() {
		canvas.drawARGB(255, 0, 0, 0);
		canvas.drawBitmap(infoBitmap, (displayX / 4), (displayY / 4), null);

	}

	private void pauseStateDraw() {
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTextSize(80);
		paint.setTextAlign(Align.CENTER);
		paint.setAlpha(150);
		canvas.drawARGB(150, 0, 0, 0);
		canvas.drawText("PAUSED", displayX / 2, displayY / 2, paint);
	}

	public void stopThread() {
		threadFlag = false;
	}

	private void drawDock() {
		canvas.drawBitmap(dock.dockBitmap, dock.topLeftPoint.x, dock.topLeftPoint.y + (dock.dockHeight / 5), null);
	}

	private void drawScore() {
		if (maxScore > 1000) {
			scorePaint.setColor(Color.GREEN);
			if (maxScore > 10000) {
				scorePaint.setColor(Color.YELLOW);
				if (maxScore > 100000) {
					scorePaint.setColor(Color.RED);
				}
			}
		}

		canvas.drawText("Score : " + maxScore, displayX / 2, displayY / 10, scorePaint);
	}

}
