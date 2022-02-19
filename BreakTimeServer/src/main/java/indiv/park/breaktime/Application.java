package indiv.park.breaktime;

import indiv.park.network.server.ServerModule;
import indiv.park.starter.ModuleStarter;

public class Application {

	public static int FRAME_WEIGHT = 1;
	
	public static void main(String[] args) {
		ModuleStarter.start(Application.class);
		
		ServerModule.INSTANCE.bind("break");
		
		if (args.length != 0) {
			FRAME_WEIGHT = Integer.parseInt(args[0]);
		}
	}
} 