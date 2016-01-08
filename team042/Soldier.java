package team042;

import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

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
		int myAttackRange = 0;
		try {
			// Any code here gets executed exactly once at the beginning of the game.
			myAttackRange = rt.attackRadiusSquared;
			Random rand = new Random(rc.getID());

			while (true) {
				// This is a loop to prevent the run() method from returning. Because of the Clock.yield()
				// at the end of it, the loop will iterate once per game round.
				MapLocation myLoc = rc.getLocation();
				RobotInfo[] nearbyBots = rc.senseNearbyRobots();
				RobotInfo[] enemiesWithinRange = rc.senseHostileRobots(myLoc, myAttackRange);

				// If enemies within range, attack one
				if (enemiesWithinRange.length > 0 && rc.isCoreReady()) {		
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
						// HIT EM HEAD ON
						Direction dir = myLoc.directionTo(enemiesWithinRange[rand.nextInt()].location);
						if (rc.canMove(dir)) {
							rc.move(dir);
						} else {//can't move toward it, so just try to move somewhere
							for (Direction d : dirs) {
								if (rc.canMove(d)) {
									rc.move(d);
								}
							}
						}
					}
				} else { // no enemies, move around. 						
					if (rc.isCoreReady()) { 
						// Spread out or close in, depending on a 50/50 die roll.
						int randBotNum = Math.min(rand.nextInt(), nearbyBots.length);
						Direction toMove = myLoc.directionTo(nearbyBots[randBotNum].location);
						if (randBotNum % 2 == 0) {
							toMove = toMove.opposite();
						}
						if (rc.canMove(toMove)) {
							rc.move(toMove);
						} else {//can't move toward it!
							for (Direction d : dirs) {
								 if (rc.canMove(d)) {
									 rc.move(d);
								 } else if (rc.canMove(d.opposite())) {
									 rc.move(d.opposite());
								 }
							}
						}
					} else {
						// core is not ready.
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

