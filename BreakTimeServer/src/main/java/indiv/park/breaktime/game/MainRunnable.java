package indiv.park.breaktime.game;

import java.util.Collection;

import indiv.park.breaktime.Application;
import indiv.park.breaktime.game.object.Barrage;
import indiv.park.breaktime.game.object.Character;
import indiv.park.breaktime.game.object.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainRunnable implements Runnable {

	@Override
	public void run() {
		long currStart = System.currentTimeMillis();
		long nowStart = 0;
		
		int targetDef = 7 * Application.FRAME_WEIGHT;
		int def = 0;

		while (true) {
			try {
				updateCharactersPosition();
				updateBarragePosition();

				nowStart = System.currentTimeMillis();

				def = (int) (nowStart - currStart);
//				logger.info(LoggerTemplate.FRAME_DEF, def);
				
				if (def <= targetDef) {
					Thread.sleep(targetDef - def);
				}

				currStart = System.currentTimeMillis();
				
				updateStageToPlayer();
				checkCollision();

				Thread.sleep(3);

			} catch (Exception e) {
				//
			}
		}
	}

	private void updateCharactersPosition() {
		for (Character character : Stage.INSTANCE.characterMap.values()) {
			character.updatePosition();
		}
	}

	private void updateBarragePosition() {
		for (Barrage barrage : Stage.INSTANCE.barrageMap.values()) {
			barrage.updatePosition();
			barrage.isLast();
		}
	}

	private void checkCollision() {
		for (Barrage barrage : Stage.INSTANCE.barrageMap.values()) {
			Collection<Character> characters = Stage.INSTANCE.characterMap.values();
			for (Character character : characters) {
				character.isCollision(barrage.position);
			}
		}
	}

	private void updateStageToPlayer() {
		if (Stage.INSTANCE.characterMap.isEmpty()) {
			if (Stage.INSTANCE.isStart.get()) {
				logger.info(LoggerTemplate.NO_CHARACTER);
				Stage.INSTANCE.stop();
			}
			return;
		}

		Stage.INSTANCE.sendData();
	}
}
