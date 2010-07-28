package jp.co.hybitz.otheris;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainView extends SurfaceView implements SurfaceHolder.Callback {
	private static final int MAX_X = 8;
	private static final int MAX_Y = 12;
	
	private Ball[][] balls = new Ball[MAX_X][MAX_Y];
	private ScheduledExecutorService executor;
	private Bitmap[] images;
	private Random rand = new Random();
	private Ball[] currentBalls;
	private Boolean locked = false;
	
	public MainView(Context context) {
		super(context);
		initView();
	}
	
	public MainView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public MainView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	
	private void initView() {
		getHolder().addCallback(this);
		Resources r = getContext().getResources();
		images = new Bitmap[]{
			BitmapFactory.decodeResource(r, R.drawable.black),
			BitmapFactory.decodeResource(r, R.drawable.white),
		};
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}
	
	public void moveLeft() {
		boolean canMove = canMoveBall(currentBalls[0].getX() - 1, currentBalls[0].getY(), currentBalls);
		canMove &= canMoveBall(currentBalls[1].getX() - 1, currentBalls[1].getY(), currentBalls);
		
		if (canMove) {
			clearBalls(currentBalls);
			currentBalls[0].moveLeft();
			currentBalls[1].moveLeft();
			storeBalls(currentBalls);
			repaint();
		}
	}
	
	public void moveRight() {
		boolean canMove = canMoveBall(currentBalls[0].getX() + 1, currentBalls[0].getY(), currentBalls);
		canMove &= canMoveBall(currentBalls[1].getX() + 1, currentBalls[1].getY(), currentBalls);

		if (canMove) {
			clearBalls(currentBalls);
			currentBalls[0].moveRight();
			currentBalls[1].moveRight();
			storeBalls(currentBalls);
			repaint();
		}
	}
	
	private void clearBall(Ball b) {
		if (b == null) {
			return;
		}
		
		balls[b.getX()][b.getY()] = null;
	}
	
	private void clearBalls(Ball[] balls) {
		if (balls == null) {
			return;
		}
		
		for (int i = 0; i < balls.length; i ++) {
			clearBall(balls[i]);
		}
	}

	private void storeBall(Ball b) {
		if (b == null) {
			return;
		}
		
		balls[b.getX()][b.getY()] = b;
	}
	
	private void storeBalls(Ball[] balls) {
		if (balls == null) {
			return;
		}
		
		for (int i = 0; i < balls.length; i ++) {
			storeBall(balls[i]);
		}
	}

	public void rotate() {
		repaint();
	}

	private boolean canMoveBalls(int diffX, int diffY, Ball[] balls) {
		for (int i = 0; i < balls.length; i ++) {
			Ball b = balls[i];
			if (!canMoveBall(b.getX() + diffX, b.getY() + diffY, balls)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean canMoveBall(int x, int y, Ball[] excludes) {
		if (x < 0 || x >= MAX_X) {
			return false;
		}
		
		if (y < 0 || y >= MAX_Y) {
			return false;
		}
		
		if (balls[x][y] == null) {
			return true;
		}
		else {
			if (excludes == null) {
				return false;
			}
			
			for (int i = 0; i < excludes.length; i ++) {
				if (balls[x][y] == excludes[i]) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean canMoveBall(int x, int y) {
		return canMoveBall(x, y, null);
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
	
	private void handleFallState() {
		clearLine(currentBalls[0].getY());
		repaint();
		sleep(500);
		fall(currentBalls[0].getY()-1);
		repaint();
		currentBalls = null;
	}
	
	private void moveDown(Ball[] balls) {
		clearBalls(balls);
		for (int i = 0; i < balls.length; i ++) {
			balls[i].moveDown();
		}
		storeBalls(balls);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				
				synchronized (locked) {
					if (locked) {
						return;
					}
				}
				
				if (currentBalls == null) {
					currentBalls = createNewBalls();
					if (canMoveBalls(0, 0, currentBalls)) {
						storeBalls(currentBalls);
						repaint();
					}
					else {
						gameOver();
					}
				}
				else if (canMoveBalls(0, 1, currentBalls)) {
					moveDown(currentBalls);
					repaint();
				}
				else if (isLined(currentBalls)) {
					synchronized (locked) {
						locked = true;
					}

					handleFallState();
					
					synchronized (locked) {
						locked = false;
					}
				}
				else {
					currentBalls = createNewBalls();
					if (canMoveBalls(0, 0, currentBalls)) {
						storeBalls(currentBalls);
						repaint();
					}
					else {
						gameOver();
					}
				}
			}
		}, 200, 200, TimeUnit.MILLISECONDS);
	}
	
	private void clearLine(int y) {
		for (int i = 0; i < MAX_X; i ++) {
			clearBall(balls[i][y]);
		}
	}
	
	private void moveLineDown(int y) {
		for (int i = 0; i < MAX_X; i ++) {
			Ball b = balls[i][y];
			if (b == null) {
				continue;
			}

			if (canMoveBall(b.getX(), b.getY() + 1)) {
				clearBall(b);
				b.moveDown();
				storeBall(b);
			}
		}
	}
	
	private void fall(int y) {
		for (int i = y; i >= 0; i --) {
			moveLineDown(i);
		}
		clearLine(0);
	}
	
	private boolean isLined(Ball b) {
		BallType type = b.getBallType();
		for (int i = 0; i < MAX_X; i ++) {
			if (balls[i][b.getY()] == null) {
				return false;
			}
			if (balls[i][b.getY()].getBallType() != type) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean isLined(Ball[] balls) {
		for (int i = 0; i < balls.length; i ++) {
			if (isLined(balls[i])) {
				return true;
			}
		}
		return false;
	}

	private Ball[] createNewBalls() {
		Ball[] ret = new Ball[2];
		ret[0] = new Ball(getRandomBallType(), images, 3, 0);
		ret[1] = new Ball(getRandomBallType(), images, 4, 0);
		return ret;
	}
	
	private BallType getRandomBallType() {
		if (true) return BallType.Black;
		int next = rand.nextInt(2);
		if (next == 1) {
			return BallType.Black;
		}
		else {
			return BallType.White;
		}
	}
	
	private void gameOver() {
		executor.shutdownNow();

		Canvas canvas = null;
		SurfaceHolder surfaceHolder = getHolder();
		try {
			canvas = surfaceHolder.lockCanvas();
			if (canvas == null) {
				return;
			}

			synchronized (surfaceHolder) {
				draw(canvas);
				Paint p = new Paint();
				p.setColor(Color.RED);
				canvas.drawLine(50, 50, 350, 550, p);
				canvas.drawLine(350, 50, 50, 550, p);
			}
		}
		finally {
			if (canvas != null) {
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		executor.shutdown();
	}
	
	@Override
	public void draw(Canvas canvas) {
		canvas.drawColor(Color.GRAY);
		for (int i = 0; i < MAX_X; i ++) {
			for (int j = 0; j < MAX_Y; j ++) {
				if (balls[i][j] != null) {
					balls[i][j].show(canvas);
				}
			}
		}
	}
	
	void repaint() {
		Canvas canvas = null;
		SurfaceHolder surfaceHolder = getHolder();
		try {
			canvas = surfaceHolder.lockCanvas();
			if (canvas == null) {
				return;
			}

			synchronized (surfaceHolder) {
				draw(canvas);
			}
		}
		finally {
			if (canvas != null) {
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}
}
