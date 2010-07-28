package jp.co.hybitz.otheris;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Ball {
	private static final int size = 50;
	
	private int x;
	private int y;
	private BallType ballType;
	private Bitmap black;
	private Bitmap white;
	
	public Ball(BallType ballType, Bitmap[] images, int x, int y) {
		this.x = x;
		this.y = y;
		this.ballType = ballType;
		this.black = images[0];
		this.white = images[1];
	}
	
	public void show(Canvas c) {
		c.drawBitmap(getImage(), x*size, y*size, null);
	}
	
	public void moveDown() {
		y ++;
	}
	
	public void moveLeft() {
		x --;
	}

	public void moveRight() {
		x ++;
	}
	
	public BallType getBallType() {
		return ballType;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

	private Bitmap getImage() {
		if (ballType == BallType.Black) {
			return black;
		}
		else if (ballType == BallType.White) {
			return white;
		}
		else {
			throw new IllegalStateException();
		}
	}
}
