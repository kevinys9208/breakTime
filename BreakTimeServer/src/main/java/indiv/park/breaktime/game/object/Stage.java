package indiv.park.breaktime.game.object;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import indiv.park.breaktime.game.LoggerTemplate;
import indiv.park.breaktime.game.MainRunnable;
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
	public final ConcurrentHashMap<Integer, Timer> timerMap;
	
	public final AtomicInteger idCreator;
	public final AtomicInteger stepCreator;
	
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
	
	private final Thread mainThread;
	
	private Timer createTimer;
	private Timer stepTimer;
	
	private Stage() {
		characterMap = new ConcurrentHashMap<>();
		barrageMap = new ConcurrentHashMap<>();
		timerMap = new ConcurrentHashMap<>();
		
		idCreator = new AtomicInteger();
		stepCreator = new AtomicInteger();
		
		mainThread = new Thread(new MainRunnable());
		mainThread.start();
		
		isStart = new AtomicBoolean();
		startTime = new AtomicLong();
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
		
		stepTimer = new Timer();
		stepTimer.schedule(new TimerTask() {
			
			@Override
			public void run() { createTimer(); }
			
		}, 13000, 10000);
		
		logger.info(LoggerTemplate.START_STAGE);
	}
	
	public synchronized void stop() {
		isStart.set(false);
		
		createTimer.cancel();
		stepTimer.cancel();
		idCreator.set(0);
		stepCreator.set(0);
		
		characterMap.clear();
		barrageMap.clear();
		
		timerMap.values().forEach(Timer::cancel);
		timerMap.clear();
		
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
	
	public void createTimer() {
		int step = stepCreator.incrementAndGet();
		
		Timer stepTimer = new Timer();
		
		Timer value = timerMap.putIfAbsent(step, stepTimer);
		if (value == null) {
			stepTimer.schedule(new TimerTask() {
				
				@Override
				public void run() { createBarrage(); }
				
			}, 100, 500);
			
			logger.info(LoggerTemplate.CREATE_STEPTIMER, step);
			
		} else {
			stepTimer.cancel();
		}
	}
	
	public long calcTime(long currentTimeMillis) {
		return currentTimeMillis - startTime.get();
	}

	public void sendData() {
		StringBuffer sb = new StringBuffer();
		for (Character character : characterMap.values()) {
			sb.append(character.returnData());
			sb.append(LoggerTemplate.SEPARATOR);
		}
		for (Barrage barrage : barrageMap.values()) {
			sb.append(barrage.returnData());
			sb.append(LoggerTemplate.SEPARATOR);
		}
		sb.deleteCharAt(sb.length() - 1);
		NetChannelGroup.INSTANCE.writeAndFlush(new TextWebSocketFrame(sb.toString()));
	}
}
