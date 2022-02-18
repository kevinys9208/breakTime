package indiv.park.breaktime;

import indiv.park.network.server.ServerModule;
import indiv.park.starter.ModuleStarter;

public class Application {

	public static void main(String[] args) {
		ModuleStarter.start(Application.class);
		
		ServerModule.INSTANCE.bind("break");
	}
}