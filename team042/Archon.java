package team042dev;

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
			//Random rand = new Random(rc.getID());
			Random rand = new Random(rc.getID()+167);
			
			int mySight = 35;
			
			Double probDoCore = 0.2;
			
			// Core-Ready probabilities (independent, so do not need to sum to 1).
			Double probBuilding = 0.6;
			Double probMoving = 0.9;
			
			// Building Probability distribution (this must sum to 1.0).
			Double probBuildTurret = 0.2;
			Double probBuildGuard = 0.1;
			Double probBuildSoldier = 0.7;
			
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
				
				// Attempt to run core tasks.
				Double randDoCore = rand.nextDouble();
				if (rc.isCoreReady() && randDoCore<=probDoCore) {
					// CORE-ONLY TASKS:
					// - Move 
					// - Construct bots
					// - Activate neuts
					// - Clear rubble (yeah right)

					// PRIORITIES:
					// 1. Build turrets
					// 2. Build soldiers/guards
					// 3. Style on zeds
					
					// Attempt to build stuff.
					Double randToBuild = rand.nextDouble();
					if (randToBuild <= probBuilding){
						
						Double randWhatToBuild = rand.nextDouble();
						
						for (Direction dir : dirs) {
							
							// If you can build in this direction, do so.
							if (rc.canBuild(dir, RobotType.TURRET) && rc.isCoreReady()) {
								
								if(randWhatToBuild <= probBuildTurret){
									rc.build(dir, RobotType.TURRET);
									break;
								}else{
									// Adjust the drawn rand to reflect failing this prob check.
									randWhatToBuild = randWhatToBuild - probBuildTurret;
								}
								
								if(randWhatToBuild <= probBuildGuard){
									rc.build(dir, RobotType.GUARD);
									break;
								}else{
									// Adjust the drawn rand to reflect failing this prob check.
									randWhatToBuild = randWhatToBuild - probBuildGuard;
								}
								
								if(randWhatToBuild <= probBuildSoldier){
									rc.build(dir, RobotType.SOLDIER);
									break;
								}else{
									// Adjust the drawn rand to reflect failing this prob check.
									randWhatToBuild = randWhatToBuild - probBuildSoldier;
								}
								
								// The code should never reach this point naturally.
							}
						}
					}
					
					// Attempt to move around.
					Double randToMove = rand.nextDouble();
					if(randToMove <= probMoving){
						for (RobotInfo bot : nearbyBots) {
							// Prioritize turrets
							if (bot.team == utils.myTeam && bot.type == RobotType.TURRET) {
								// Move toward it.
								Direction closerToBot = myLoc.directionTo(bot.location);
								utils.tryMove(closerToBot);
								break;
							} else if (bot.team == utils.myTeam) {
								// Move toward it.
								Direction closerToBot = myLoc.directionTo(bot.location);
								utils.tryMove(closerToBot);
								break;
							} else {
								// Move away.
								Direction awayFromBot = myLoc.directionTo(bot.location).opposite();
								utils.tryMove(awayFromBot);
								break;
							}
						}
					}
				} else {
					// CORE-FREE TASKS:
					// - Signaling 
					// - Repairing 

					// REPAIRING
					int numNearbyBots = nearbyBots.length;
					if (numNearbyBots > 0){
						
						// Find the weakest nearby bot.
						RobotInfo weakest = nearbyBots[0];
						for (RobotInfo friend : nearbyBots) {
							if (friend.team == myTeam && friend.health <= weakest.health ) {
								weakest = friend;
							}
						}
						
						// Repair the weakest bot.
						if (weakest.team == myTeam && rc.canAttackLocation(weakest.location)) {
							rc.repair(weakest.location);
						}
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
