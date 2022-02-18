package indiv.park.breaktime.game.object;

import java.util.concurrent.atomic.AtomicInteger;

public class Position implements Comparable<Position> {

	public final AtomicInteger x;
	public final AtomicInteger y;
	
	private Position(int initX, int initY) {
		x = new AtomicInteger(initX);
		y = new AtomicInteger(initY);
	}
	
	public static Position createNewPosition(int initX, int initY) {
		return new Position(initX, initY);
	}
	
	public static Position createRandomPosition() {
		int initX = (int) (Math.random() * 800);
		
		return new Position(initX, 0);
	}

	@Override
	public int compareTo(Position other) {
		double a = Math.pow((other.x.intValue() - x.intValue()), 2);
		double b = Math.pow((other.y.intValue() - y.intValue()), 2);
		
		if (Math.sqrt(a + b) <= 50) {
			return 1;
		}
		
		return 0;
	}
}
