package team042;

import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class Archon {

	public static RobotController rc;
	public static Utilities utils;
	public static Direction[] dirs = Utilities.dirs;
	
	public Archon(RobotController robotController) {
		super();
		rc = robotController;
		utils = new Utilities(rc);
	}

	public void run() {
		// ARCHON ALGO: 
		// - build towers
		// - repair nearby bots 
		// - attack zeds when {rc.numNearbyZeds() > OMFG_RUN_THRESH} zeds nearby
		// - TODO: spawn ratios
		// --- TODO: spawn ratios respond to map/team status

//		Random rand = new Random(rc.getID());

		while (true) {
			// This is a loop to prevent the run() method from returning. Because of the Clock.yield()
			// at the end of it, the loop will iterate once per game round.
			try {
				MapLocation myLoc = rc.getLocation();
				RobotInfo[] nearbyBots = rc.senseNearbyRobots();
				//					Signal[] signals = rc.emptySignalQueue();
				//					if (signals.length > 0) {
				//						// TODO: interpret signals
				//					}
				if (rc.isCoreReady()) {
					// CORE-ONLY TASKS:
					// - Move 
					// - Construct bots
					// - Activate neuts
					// - Clear rubble (yeah right)

					// PRIORITIES:
					// 1. Build turrets
					// 2. Build soldiers/guards
					// 3. Style on zeds

					// BUILDING STUFF 
					for (Direction dir : dirs) {
						if (rc.canBuild(dir, RobotType.TURRET)) {
							if (rc.getTeamParts() > 82) {
								// Lots of parts. TURRET.
								rc.build(dir, RobotType.TURRET);
							} else {
								// Not so many parts.
								// Randomly build soldiers or guards, biased slightly toward soldiers.
								if (nearbyBots.length % 2 == 0) {
									rc.build(dir, RobotType.SOLDIER);
								} else {
									rc.build(dir, RobotType.GUARD);
								}
							}
						} else {
							// Can't build in this dir. Next dir!
						}
					}
					// MOVING AROUND
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

				} else {
					// CORE-FREE TASKS:
					// - Signaling 
					// - Repairing 

					// REPAIRING
					// 		FORGET THIS NOISE

					// SIGNALING
					// TODO: anything at all here
				}
				
				Clock.yield();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
