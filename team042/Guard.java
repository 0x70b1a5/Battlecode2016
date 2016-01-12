package team042;

import java.util.Random;

import battlecode.common.*;

public class Guard {

	public static RobotController rc;
	public static Utilities utils;
	public static RobotType rt;

	public Guard(RobotController robotController) {
		super();
		rc = robotController;
		utils = new Utilities(rc);
		rt = rc.getType();
	}

	public void run() {
		// TODO: make guards prefer attacking zeds
		try {
			// Any code here gets executed exactly once at the beginning of the game.
//			int myRange = rt.attackRadiusSquared;
			int mySight = rt.sensorRadiusSquared;
			Random rand = new Random(rc.getID());
			while (true) {
				// This is a loop to prevent the run() method from returning. Because of the Clock.yield()
				// at the end of it, the loop will iterate once per game round.
				if (rc.isCoreReady()) {
					MapLocation myLoc = rc.getLocation();
					RobotInfo[] enemies= rc.senseHostileRobots(myLoc, mySight);
					RobotInfo[] friends= rc.senseNearbyRobots(mySight);
					// If enemies within range, attack one
					int friendCount = friends.length;
					int enemyCount = enemies.length;
					if (rc.isCoreReady()&&enemyCount > 0 && rc.getWeaponDelay()<1&&rc.canAttackLocation(enemies[0].location)) {
						rc.attackLocation(enemies[0].location);						
					} else if(rc.isCoreReady() && friends.length>0){
						for (RobotInfo bot : friends) {
							// Group around archons first
							if (bot.type == RobotType.ARCHON) {
								utils.tryMove(myLoc.directionTo(bot.location));
							}
						}
						//otherwise group in general
						utils.tryMove(myLoc.directionTo(friends[rand.nextInt(friendCount)].location));
					} else {
						utils.tryMove(utils.intToDirection(rand.nextInt(8)));
					}
				}
				Clock.yield();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}
}