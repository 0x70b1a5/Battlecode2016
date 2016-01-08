package team042;

import java.util.Random;

import battlecode.common.*;

public class Soldier {

	public static RobotController rc;
	public static Utilities utils;
	public static RobotType rt;
	public static Direction[] dirs = Utilities.dirs;

	public Soldier(RobotController robotController) {
		super();
		rc = robotController;
		utils = new Utilities(rc);
		rt = rc.getType();
	}

	public void run() {
		try {
			// Any code here gets executed exactly once at the beginning of the game.
			int myAttackRange = rt.attackRadiusSquared;

			while (true) {
				// This is a loop to prevent the run() method from returning. Because of the Clock.yield()
				// at the end of it, the loop will iterate once per game round.
				MapLocation myLoc = rc.getLocation();
				RobotInfo[] enemiesWithinRange = rc.senseHostileRobots(myLoc, myAttackRange);
				RobotInfo[] nearbyBots = rc.senseNearbyRobots();

				if (rc.isCoreReady()) {
					// If enemies within range, attack one
					if (enemiesWithinRange.length > 0) {		
						// Check if weapon is ready
						if (rc.isWeaponReady()) {
							// Find weakest enemy
							RobotInfo weakest = enemiesWithinRange[0];
							for (RobotInfo enemy : enemiesWithinRange) {
								RobotInfo current = enemy;
								if (current.health < weakest.health) {
									weakest = current;
								}
							}
							rc.attackLocation(weakest.location);
						} else {
							// weapon's not ready, but enemies near
							// Retreat
							for (RobotInfo enemy : enemiesWithinRange) {
								Direction dir = myLoc.directionTo(enemy.location).opposite();
							if (rc.canMove(dir)) {
								rc.move(dir);
							}
							}
						}
					} else { // no enemies, move around friends. 	
						Direction closerToBot;
						for (RobotInfo bot : nearbyBots) {
							closerToBot = myLoc.directionTo(bot.location);
							if (rc.canMove(closerToBot)) {
								rc.move(closerToBot);
							} 
						}
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

