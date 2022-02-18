package indiv.park.breaktime.game.object;

import java.util.concurrent.atomic.AtomicInteger;

import indiv.park.breaktime.game.LoggerTemplate;
import indiv.park.breaktime.game.event.EventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Barrage {
	
	public final int id;
	public final Position position;
	
	public final AtomicInteger del;
	public final AtomicInteger acc;
	
	private final EventListener remover;
	
	public Barrage(int id, EventListener remover) {
		this.remover = remover;
		this.id = id;
		this.position = Position.createRandomPosition();
		
		this.del = new AtomicInteger(0);
		this.acc = new AtomicInteger(0);
		
		logger.debug(LoggerTemplate.CREATE_BARRAGE, id);
	}
	
	public void updatePosition() {
		position.y.addAndGet(1 * acc.intValue());
		if (del.intValue() % 6 == 0) {
			acc.incrementAndGet();
		}
		del.incrementAndGet();
	}
	
	public void isLast() {
		if (position.y.intValue() > 840) {
			remover.apply(id);
		}
	}
	
	public String returnData() {
		return String.format(LoggerTemplate.FORMAT_BARRAGE, id, position.x.intValue(), position.y.intValue());
	}
}