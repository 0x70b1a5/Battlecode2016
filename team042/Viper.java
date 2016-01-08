package team042;

import java.awt.AWTException;
import java.awt.Robot;

import battlecode.common.Clock;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Viper extends Robot {

	public static RobotController rc;
	public static Utilities utils;
	public static RobotType rt;
	
	public Viper(RobotController robotController) throws AWTException {
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
