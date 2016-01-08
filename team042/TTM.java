package team042;

import java.awt.AWTException;
import java.awt.Robot;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class TTM extends Robot {

	public static RobotController rc;
	public static Utilities utils;
	public static RobotType rt;
	
	public TTM(RobotController robotController) throws AWTException {
		super();
		rc = robotController;
		utils = new Utilities(rc);
		rt = rc.getType();
	}

	public void run() {
		// DO, OR DO NOT; THERE IS NO "try"
		int myAttackRange = 0;
		try {
			rc.unpack();
		}
		catch (Exception e) {
			// Throwing an uncaught exception makes the robot die, so we need to catch exceptions.
			// Caught exceptions will result in a bytecode penalty.
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		myAttackRange = rt.attackRadiusSquared;
		while (true) {
			MapLocation myLoc = rc.getLocation();
			RobotInfo[] nearbyBots = rc.senseNearbyRobots();
			Direction ttmDir = myLoc.directionTo(nearbyBots[0].location).opposite();
			if (rc.isCoreReady()) {
				for (Direction d : utils.dirs) {
					if (rc.canMove(ttmDir)) {
						try {
							rc.move(ttmDir);
						} catch (GameActionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			try {
				rc.unpack();
			}
			catch (Exception e) {
				// Throwing an uncaught exception makes the robot die, so we need to catch exceptions.
				// Caught exceptions will result in a bytecode penalty.
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			Clock.yield();
		}
	}
}