package indiv.park.breaktime.game.object;

import java.util.concurrent.atomic.AtomicBoolean;

import indiv.park.breaktime.game.LoggerTemplate;
import indiv.park.breaktime.game.event.EventListener;
import indiv.park.breaktime.net.NetChannelGroup;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelMatchers;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Character {
	
	public final int id;
	public final Channel channel;
	public final EventListener remover;
	
	public final AtomicBoolean left;
	public final AtomicBoolean right;
	
	private final Position position;
	
	public Character(int id, Channel channel, EventListener remover) {
		this.id = id;
		this.channel = channel;
		this.remover = remover;
		
		left = new AtomicBoolean();
		right = new AtomicBoolean();
		
		position = Position.createNewPosition(400, 765);
		
		logger.info(LoggerTemplate.CREATE_CHARACTER, id);
		
		NetChannelGroup.INSTANCE.writeAndFlush(new TextWebSocketFrame(String.format(LoggerTemplate.FORMAT_ID, id)), ChannelMatchers.is(channel));
	}
	
	public void updatePosition() {
		if (right.get()) {
			if (position.x.addAndGet(13) > 770) {
				position.x.set(770);
			}
		}
		
		if (left.get()) {
			if (position.x.addAndGet(-13) < 30) {
				position.x.set(30);
			}
		}
	}
	
	public void isCollision(Position other) {
		if (position.compareTo(other) == 1) {
			remover.apply(channel);
			channel.writeAndFlush(new TextWebSocketFrame(String.format(LoggerTemplate.FORMAT_TIME, Stage.INSTANCE.calcTime(System.currentTimeMillis()))));
		}
	}
	
	public String returnData() {
		return String.format(LoggerTemplate.FORMAT_CHARACTER, id, position.x.intValue(), position.y.intValue());
	}
}
