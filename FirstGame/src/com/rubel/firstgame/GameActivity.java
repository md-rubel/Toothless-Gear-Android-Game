package com.rubel.firstgame;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint("ClickableViewAccessibility")
public class GameActivity extends Activity {

	GameView gameView;
	SensorManager sensorManager;
	SensorEventListener sensorEventListener;
	Sensor accelerometerSensor;
	private static float gravityX, gravityY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// gameView = new GameView(this);
		initializeSensor();
		setContentView(R.layout.activity_second);
		gameView = (GameView) findViewById(R.id.myGameView);
		initializeButtons();
	}

	private void initializeButtons() {
		final Button moveLeftButton = (Button) findViewById(R.id.buttonLeft);
		final Button moveRightButton = (Button) findViewById(R.id.buttonRight);

		if (gameView.drawingThread.pauseFlag == false && gameView.drawingThread.infoFlag == false) {
			moveLeftButton.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						gameView.drawingThread.dock.startMovingLeft();
						moveLeftButton.getBackground().setAlpha(100);
						break;
					case MotionEvent.ACTION_UP:
						gameView.drawingThread.dock.stopMovingLeft();
						moveLeftButton.getBackground().setAlpha(255);
						break;

					default:
						break;
					}
					return false;
				}
			});

			moveRightButton.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						gameView.drawingThread.dock.startMovingRight();
						moveRightButton.getBackground().setAlpha(100);
						break;
					case MotionEvent.ACTION_UP:
						gameView.drawingThread.dock.stopMovingRight();
						moveRightButton.getBackground().setAlpha(255);
						break;

					default:
						break;
					}
					return false;
				}
			});
		}

	}

	public static float getGravityX() {
		return gravityX;
	}

	public static float getGravityY() {
		return gravityY;
	}

	private void initializeSensor() {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorEventListener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
					gravityX = -event.values[0];
					gravityY = event.values[1];

					if (gravityY < 0) {
						stopUsingSensors();
						gameView.drawingThread.animationThread.stopThread();
						gameView.drawingThread.scoreCounterThread.stopThread();

						alertMessage();
					}

				}
			}

			private void alertMessage() {
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(GameActivity.this);
				alertBuilder.setTitle("   NO CHEATING !!!");
				alertBuilder.setIcon(R.drawable.warning);
				alertBuilder.setMessage("You are shaking or holding your phone upside down!");

				alertBuilder.setPositiveButton("Restart", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						resetGame();
					}
				});

				alertBuilder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						stopGame(null);
					}
				});
				alertBuilder.show();

			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub

			}
		};
		accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		startUsingSensors();

	}

	private void startUsingSensors() {
		sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	private void stopUsingSensors() {
		sensorManager.unregisterListener(sensorEventListener);
	}

	@Override
	protected void onPause() {
		stopUsingSensors();
		super.onPause();
	}

	@Override
	protected void onResume() {
		startUsingSensors();
		super.onResume();
	}

	@Override
	protected void onStop() {
		stopUsingSensors();
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.second, menu);
		return true;
	}

	public void resetGame() {
		stopUsingSensors();
		startUsingSensors();

		gameView.drawingThread.stopThread();
		gameView.drawingThread = new DrawingThread(gameView, this);
		gameView.drawingThread.start();

		Toast.makeText(this, "Game Restarted", Toast.LENGTH_SHORT).show();
	}

	public void pauseGame(View view) {

		if (gameView.drawingThread.pauseFlag == false && gameView.drawingThread.infoFlag == false) {
			gameView.drawingThread.animationThread.stopThread();
			stopUsingSensors();
			gameView.drawingThread.pauseFlag = true;
			view.setBackgroundResource(R.drawable.unlock);

		} else if (gameView.drawingThread.pauseFlag == true && gameView.drawingThread.infoFlag == false) {
			gameView.drawingThread.animationThread = new AnimationThread(gameView.drawingThread);
			gameView.drawingThread.animationThread.start();
			startUsingSensors();
			view.setBackgroundResource(R.drawable.lock);
			gameView.drawingThread.pauseFlag = false;

		}
	}

	public void restartGame(View view) {
		if (gameView.drawingThread.pauseFlag == false && gameView.drawingThread.infoFlag == false) {
			gameView.drawingThread.allRobots.clear();
			gameView.drawingThread.maxScore = 0;
			gameView.drawingThread.scorePaint.setColor(Color.WHITE);
		}
	}

	public void stopGame(View view) {
		this.finish();
	}

	public void toothlessGear(View view) {

		if (gameView.drawingThread.infoFlag == false && gameView.drawingThread.pauseFlag == false) {
			gameView.drawingThread.animationThread.stopThread();
			stopUsingSensors();
			gameView.drawingThread.infoFlag = true;

		} else if (gameView.drawingThread.infoFlag == true && gameView.drawingThread.pauseFlag == false) {
			gameView.drawingThread.animationThread = new AnimationThread(gameView.drawingThread);
			gameView.drawingThread.animationThread.start();
			startUsingSensors();
			gameView.drawingThread.infoFlag = false;

		}
	}

}
