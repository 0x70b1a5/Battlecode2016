package team042;

import java.awt.AWTException;
import java.awt.Robot;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class Guard extends Robot {

	public static RobotController rc;
	public static Utilities utils;
	public static RobotType rt;
	
	public Guard(RobotController robotController) throws AWTException {
		super();
		rc = robotController;
		utils = new Utilities(rc);
		rt = rc.getType();
	}

	public void run() {
		// TODO: make guards prefer attacking zeds
		int myAttackRange = 0;
		try {
			// Any code here gets executed exactly once at the beginning of the game.
			myAttackRange = rt.attackRadiusSquared;
		} catch (Exception e) {
			// Throwing an uncaught exception makes the robot die, so we need to catch exceptions.
			// Caught exceptions will result in a bytecode penalty.
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		while (true) {
			// This is a loop to prevent the run() method from returning. Because of the Clock.yield()
			// at the end of it, the loop will iterate once per game round.
			try {
				MapLocation myLoc = rc.getLocation();
				RobotInfo[] nearbyBots = rc.senseNearbyRobots();
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
							rc.attackLocation(weakest.location);
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
							Direction nearDir = myLoc.directionTo(nearest.location);
							if (rc.canMove(nearDir)) {
								rc.move(nearDir);
							} else {
								// TODO: what do do if can't move nearer to enemies
								// (probably move closer to friends lol)
							}
						} else {
							// No enemies. 
							for (RobotInfo bot : nearbyBots) {
								if (bot.team == utils.myTeam) {
									// Move toward it.
									Direction closerToBot = myLoc.directionTo(nearbyBots[0].location);
									if (rc.canMove(closerToBot)) {
										rc.move(closerToBot);
									} else {//can't move toward it!
										for (int i=0; i<8; i++) {
											Direction strafeDir = closerToBot.rotateRight();
											if (rc.canMove(strafeDir)) {
												rc.move(strafeDir);
											} 
										}
									}
								} else {
									// Move away.
									Direction awayFromBot = myLoc.directionTo(nearbyBots[0].location).opposite();
									if (rc.canMove(awayFromBot)) {
										rc.move(awayFromBot);
									} else { //can't move away!
										for (int i=0; i<8; i++) {
											Direction strafeDir = awayFromBot.rotateRight();
											if (rc.canMove(strafeDir)) {
												rc.move(strafeDir);
											} 
										}
									}
								}
							}
						}

					} else {
						// core is not ready.
					}
				}

				Clock.yield();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
