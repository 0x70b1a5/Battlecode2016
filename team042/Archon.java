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
			//Random rand = new Random(rc.getID());
			Random rand = new Random(rc.getID()+167);

			//			int mySight = 35;

			Double probDoCore = 0.2;

			// Core-Ready probabilities (independent, so do not need to sum to 1).
			Double probBuilding = 0.6;
			Double probMoving = 0.9;

			// Building Probability distribution (this must sum to 1.0).
			Double probBuildTurret = 0.6;
			Double probBuildGuard = 0.2;
			Double probBuildSoldier = 0.2;

			while (true) {
				MapLocation myLoc = utils.myLoc;
				RobotInfo[] friends = utils.friends;
				RobotInfo[] zeds = utils.zeds;
				RobotInfo[] enemies = utils.enemies;
				boolean panic = utils.panic;
				
				// if we're panicking, FORGET EVERYTHING ELSE AND RUN!!!
				while (panic) {
					MapLocation closestHostileLoc = utils.closestHostile(enemies);
					if (closestHostileLoc != null) {utils.tryMove(myLoc.directionTo(closestHostileLoc).opposite());}
					else {
						utils.checkForCalmDown();
						utils.tryMove(dirs[rand.nextInt()%8]);
						// TODO FIXME ASAP: figure out how to set longterm vectors and STICK TO THEM.
					}
				}
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
					// 3. Process signals

					// Attempt to build stuff.
					Double randToBuild = rand.nextDouble();
					if (randToBuild <= probBuilding){

						Double randWhatToBuild = rand.nextDouble();
						
						for (Direction dir : dirs) {
							// If you can build in this direction, do so.
							if (rc.canBuild(dir, RobotType.SOLDIER) && rc.isCoreReady()) {

								if(randWhatToBuild <= probBuildTurret){
									if (rc.canBuild(dir, RobotType.TURRET)){rc.build(dir, RobotType.TURRET);}
									else{
										// can't build -- either the direction is blocked, or we don't have enough parts
										// TODO: convert to new building system w/flags
									}
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
						for (RobotInfo bot : friends) {
							// Prioritize turrets
							Direction closerToBot = myLoc.directionTo(bot.location);
							utils.tryMove(closerToBot);
							break;
						}
						// if we reach this point, we have no friends
						utils.checkForPanic();
						
					}
				} else {
					// CORE-FREE TASKS:
					// - Signaling 
					// - Repairing 

					// REPAIRING
					MapLocation weakestOne = utils.findWeakest(friends);
					if(weakestOne!=null){
						rc.repair(weakestOne);
						return;
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