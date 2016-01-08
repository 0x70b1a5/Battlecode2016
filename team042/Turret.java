package team042;

import java.util.Random;

import battlecode.common.*;

public class Turret {

	public static RobotController rc;
	public static Utilities utils;
	public static RobotType rt;

	public Turret(RobotController robotController){
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
			//			Random rand = new Random(rc.getID());
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
							// There are enemies nearby, but our core is ready. 
							// I'm a turret! BIDE TIME.
							Clock.yield();
						} else {
							// no enemies within range. I'm still a turret!
							// Check if there are too many friendlies nearby.
							if (nearbyBots.length > 5) {
								try {
									rc.pack();
								} catch (GameActionException e) {
									e.printStackTrace();
								}
							} else {
								// not too many people nearby. chill
							}
						}
					} else {
						// core is not ready. can't do much
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
