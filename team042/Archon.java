package team042;

import java.util.Random;

import battlecode.common.*;

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

		try{
			Team myTeam = rc.getTeam();
			Random rand = new Random(rc.getID());
			int mySight = 35;
		while (true) {
			// This is a loop to prevent the run() method from returning. Because of the Clock.yield()
			// at the end of it, the loop will iterate once per game round.
				MapLocation myLoc = rc.getLocation();
				RobotInfo[] enemies = rc.senseHostileRobots(myLoc,mySight);
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
					int typeToBuild = rand.nextInt();
					for (Direction dir : dirs) {
						if (rc.canBuild(dir, RobotType.TURRET) && rc.isCoreReady()) {
							if (typeToBuild % 2 == 0){
									rc.build(dir, RobotType.TURRET);
							} else if (typeToBuild % 3 == 0) {
								rc.build(dir, RobotType.GUARD);
							} else {
								rc.build(dir, RobotType.SOLDIER);
								// Can't build in this dir. 
							}
						} else{
							// can't build in this dir
						}
					}
					// MOVING AROUND
					for (RobotInfo bot : nearbyBots) {
						// Prioritize turrets
						if (bot.team == utils.myTeam && bot.type == RobotType.TURRET) {
							// Move toward it.
							Direction closerToBot = myLoc.directionTo(bot.location);
							utils.tryMove(closerToBot);
						} else if (bot.team == utils.myTeam) {
							// Move toward it.
							Direction closerToBot = myLoc.directionTo(bot.location);
							utils.tryMove(closerToBot);
						} else {
							// Move away.
							Direction awayFromBot = myLoc.directionTo(bot.location).opposite();
							utils.tryMove(awayFromBot);
						}
					}

				} else {
					// CORE-FREE TASKS:
					// - Signaling 
					// - Repairing 

					// REPAIRING
					RobotInfo weakest = nearbyBots[0];
					for (RobotInfo friend : nearbyBots) {
						if (friend.team == myTeam && friend.health <= weakest.health ) {
							weakest = friend;
						}
					}
					if (weakest.team == myTeam && rc.canAttackLocation(weakest.location)) {
						rc.repair(weakest.location);
					}

					// SIGNALING
					// TODO: anything at all here
				}
				
				Clock.yield();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
