package indiv.park.breaktime.game.object;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import indiv.park.breaktime.game.LoggerTemplate;
import indiv.park.breaktime.game.MainTask;
import indiv.park.breaktime.game.event.EventListener;
import indiv.park.breaktime.net.NetChannelGroup;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Stage {
	
	public static final Stage INSTANCE = new Stage();
	
	public final ConcurrentHashMap<ChannelId, Character> characterMap;
	public final ConcurrentHashMap<Integer, Barrage> barrageMap;
	
	public final AtomicInteger idCreator;
	
	public final AtomicBoolean isStart;
	public final AtomicLong startTime;
	
	private final EventListener barrageRemover = new EventListener() {
		@Override
		public boolean apply(Object key) { return removeBarrage((int) key); }
	};
	
	private final EventListener characterRemover = new EventListener() {
		@Override
		public boolean apply(Object key) { return removeCharacter((Channel) key); }
	};
	
	private final Timer mainTimer;
	
	private Timer createTimer;
	
	private Stage() {
		characterMap = new ConcurrentHashMap<>();
		barrageMap = new ConcurrentHashMap<>();
		
		idCreator = new AtomicInteger();
		
		mainTimer = new Timer();
		mainTimer.schedule(new MainTask(), 0, 10);
		
		isStart = new AtomicBoolean();
		startTime = new AtomicLong();
		
		createTimer = new Timer();
	}
	
	public synchronized void start() {
		int playerCnt = NetChannelGroup.INSTANCE.size();
		
		if (playerCnt != characterMap.size()) {
			NetChannelGroup.INSTANCE.writeAndFlush(new TextWebSocketFrame("E"));
			
			logger.info(LoggerTemplate.CURR_CONNECTION, NetChannelGroup.INSTANCE.size(), Stage.INSTANCE.characterMap.size());
			
			return;
		}
		
		isStart.set(true);
		startTime.set(System.currentTimeMillis() + 3000);
		
		NetChannelGroup.INSTANCE.writeAndFlush(new TextWebSocketFrame("S"));

		createTimer = new Timer();
		createTimer.schedule(new TimerTask() {
			
			@Override
			public void run() { createBarrage(); }
			
		}, 3000, 100);
		
		logger.info(LoggerTemplate.START_STAGE);
	}
	
	public synchronized void stop() {
		isStart.set(false);
		
		createTimer.cancel();
		idCreator.set(0);
		
		characterMap.clear();
		barrageMap.clear();
		
		logger.info(LoggerTemplate.STOP_STAGE);
	}
	
	public boolean createCharacter(Channel channel) {
		try {
			int id = idCreator.getAndIncrement();
			return characterMap.putIfAbsent(channel.id(), new Character(id, channel, characterRemover)) == null ? true : false;
			
		} finally {
			logger.info(LoggerTemplate.CURR_CONNECTION, NetChannelGroup.INSTANCE.size(), Stage.INSTANCE.characterMap.size());
		}
	}
	
	public boolean removeCharacter(Channel channel) {
		try {
			return characterMap.remove(channel.id()) != null ? true : false;
			
		} finally {
			logger.info(LoggerTemplate.CURR_CONNECTION, NetChannelGroup.INSTANCE.size(), Stage.INSTANCE.characterMap.size());
		}
	}
	
	public boolean createBarrage() {
		int id = idCreator.getAndIncrement();
		return barrageMap.putIfAbsent(id, new Barrage(id, barrageRemover)) == null ? true : false;
	}
	
	public boolean removeBarrage(int key) {
		return barrageMap.remove(key) != null ? true : false;
	}
	
	public long calcTime(long currentTimeMillis) {
		return currentTimeMillis - startTime.get();
	}

	public void sendData() {
		StringBuilder sb = new StringBuilder();
		for (Character character : characterMap.values()) {
			sb.append(character.returnData());
			sb.append(":");
		}
		for (Barrage barrage : barrageMap.values()) {
			sb.append(barrage.returnData());
			sb.append(":");
		}
		sb.deleteCharAt(sb.length() - 1);
		NetChannelGroup.INSTANCE.writeAndFlush(new TextWebSocketFrame(sb.toString()));
	}
}
