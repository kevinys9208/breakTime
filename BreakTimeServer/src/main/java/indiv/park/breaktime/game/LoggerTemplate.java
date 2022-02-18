package indiv.park.breaktime.game;

public class LoggerTemplate {

	public static final String CREATE_CHARACTER = "Character 객체가 생성되었습니다. #[ {} ]";
	public static final String REMOVE_CHARACTER = "Character 객체가 제거되었습니다. #[ {} ]";
	
	public static final String CREATE_BARRAGE = "Barrage 객체가 생성되었습니다. #[ {} ]";
	public static final String REMOVE_BRRAGE = "Barrage 객체가 제거되었습니다. #[ {} ]";
	
	public static final String START_STAGE = "Stage를 시작합니다.";
	public static final String STOP_STAGE = "Stage를 종료합니다.";
	
	public static final String NO_CHARACTER = "Character가 존재하지 않습니다.";
	
	public static final String FORMAT_CHARACTER = "C,%d,%d,%d";
	public static final String FORMAT_BARRAGE = "B,%d,%d,%d";
	public static final String FORMAT_ID = "I,%d";
	public static final String FORMAT_TIME = "R,%d";

	public static final String CURR_CONNECTION = "현재 Player 수: {}, 현재 Character 수: {}";
}
