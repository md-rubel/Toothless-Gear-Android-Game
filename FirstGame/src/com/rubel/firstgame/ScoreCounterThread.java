package com.rubel.firstgame;

public class ScoreCounterThread extends Thread {

	float maximumScore;
	DrawingThread drawingThread;
	boolean threadRunningFlag = false;

	public ScoreCounterThread(DrawingThread drawingThread) {
		this.drawingThread = drawingThread;
	}

	@Override
	public void run() {
		threadRunningFlag = true;
		while (threadRunningFlag) {
			float tempMax = 0;
			for (Robot robot : drawingThread.allRobots) {
				if (robot.centerY < tempMax) {
					tempMax = robot.centerY/2;
				}
			}

			drawingThread.maxScore = (int) (drawingThread.maxScore > (-tempMax) ? drawingThread.maxScore : (-tempMax));

			try {
				sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void stopThread() {
		threadRunningFlag = false;
	}

}
