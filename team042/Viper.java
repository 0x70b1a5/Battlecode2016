package team042;

import battlecode.common.*;

public class Viper {

	public static RobotController rc;
	public static Utilities utils;
	public static RobotType rt;
	
	public Viper(RobotController robotController) {
		super();
		rc = robotController;
		utils = new Utilities(rc);
		rt = rc.getType();
	}

	public void run() {
		while (true) {
			// TODO: VIPER STUFF
			Clock.yield();
		}
	}

}
