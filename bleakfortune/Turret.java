package bleakfortune;

import java.awt.AWTException;
import java.awt.Robot;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class Turret extends Robot {

	public static RobotController rc;
	public static Utilities utils;
	public static RobotType rt;
	
	public Turret(RobotController robotController) throws AWTException {
		super();
		rc = robotController;
		utils = new Utilities(rc);
		rt = rc.getType();
	}

	public void run() {
		int myAttackRange = 0;
		try {
			// ONCE
			myAttackRange = rt.attackRadiusSquared;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		while (true) {
			// TODO: TURRET STUFF
			RobotInfo[] nearbyBots = rc.senseNearbyRobots();
			MapLocation myLoc = rc.getLocation(); 
			boolean shouldAttack = false;
			RobotInfo[] enemiesWithinRange = rc.senseHostileRobots(myLoc, myAttackRange);

			// If this robot type can attack, check for enemies within range and attack one
			if (myAttackRange > 0) {
				if (enemiesWithinRange.length > 0) {
					shouldAttack = true;						
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
						try {
							rc.attackLocation(weakest.location);
						} catch (GameActionException e) {
							// TODO Auto-generated catch block
							System.out.println(e.getMessage());
							e.printStackTrace();
						}
					}
				} 
			}

			if (!shouldAttack) {
				if (rc.isCoreReady()) {
					// Are there enemies?
					if (enemiesWithinRange.length > 0) {
						// Find nearest enemy
						RobotInfo nearest = enemiesWithinRange[0];
						for (RobotInfo enemy : enemiesWithinRange) {
							RobotInfo current = enemy;
							if (myLoc.distanceSquaredTo(current.location) < 
									myLoc.distanceSquaredTo(nearest.location)) {
								nearest = current;
							}
						}
						// no enemies within range. I'm a turret!
						// For now, just wait. Our time will come.
						Clock.yield();
					} else {
						// I'm still a turret!
						// DO NOTHING.
					}
				} else {
					// core is not ready.
				}
			}

			Clock.yield();
		}
		
	}

}
