package indiv.park.breaktime.game;

import java.util.Collection;
import java.util.TimerTask;

import indiv.park.breaktime.game.object.Character;
import indiv.park.breaktime.game.object.Barrage;
import indiv.park.breaktime.game.object.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainTask extends TimerTask {

	@Override
	public void run() {
		updateCharactersPosition();
		updateBarragePosition();
		updateStageToPlayer();
		checkCollision();
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
