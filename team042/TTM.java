package team042dev;

import battlecode.common.*;

public class TTM {

	public static RobotController rc;
	public static Utilities utils;
	public static RobotType rt;
	public static Direction[] dirs = Utilities.dirs;

	public TTM(RobotController robotController) {
		super();
		rc = robotController;
		utils = new Utilities(rc);
		rt = rc.getType();
	}

	public void run() {
		// DO, OR DO NOT; THERE IS NO...
		try {
			while (true) {
				// move to an open space nearby
				for (Direction dir : dirs) {
					if (rc.canMove(dir)) {
						rc.move(dir);
					}
				}
				rc.unpack();
				Clock.yield();
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}
}