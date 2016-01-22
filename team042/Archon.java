package team042;

import java.util.Random;

import battlecode.common.*;

public class Archon {

	public static RobotController rc;
	public static Utilities utils;
	public static Direction[] dirs = Utilities.dirs;
	public MapLocation myLoc;

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
			RobotType rt = RobotType.ARCHON;
			//Random rand = new Random(rc.getID());
			Random rand = new Random(rc.getID()+167);
			utils.myTeam = rc.getTeam();
			Team myTeam = utils.myTeam;
			int mySight = utils.mySight;

			// Core-Ready probabilities (independent, so do not need to sum to 1).

			// Building Probability distribution (this must sum to 1.0).
			Double probBuildTurret = 0.3;
			Double probBuildGuard = 0.2;
			Double probBuildSoldier = 0.5;
			Double probBuildScout = 0.0;

			while (true) {
				rc.setIndicatorString(1,"I entered my whlie loop");
				myLoc = rc.getLocation();
				utils.myLoc = myLoc;
				RobotInfo[] enemies = rc.senseHostileRobots(myLoc, mySight);
				RobotInfo[] friends = rc.senseNearbyRobots(mySight, myTeam);
				RobotInfo[] zeds = rc.senseNearbyRobots(mySight, Team.ZOMBIE);
				boolean panic = utils.panic;

				// if we're panicking, FORGET EVERYTHING ELSE AND RUN!!!
				while (panic) {
					rc.setIndicatorString(1,"I am panicking");
					MapLocation closestHostileLoc = utils.closestHostile(enemies);
					if (closestHostileLoc != null) {
						//determine direction from nearest enemy
						utils.fleeVector = myLoc.directionTo(closestHostileLoc).opposite(); 
					}
					//if we know which way to run, run that way.
					if (utils.fleeVector!=null&&rc.canMove(utils.fleeVector)){utils.tryMove(utils.fleeVector);}
					utils.checkForCalmDown(friends, zeds);
				}
				// Attempt to run core tasks.
				if (rc.isCoreReady()) {

					rc.setIndicatorString(2,"I entered the Core Tasks block");
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
					Double randWhatToBuild = rand.nextDouble();

					for (Direction dir : dirs) {
						rc.setIndicatorString(2,"I entered the Build Stuff block");
						// If you can build in this direction, do so.
						if (rc.isCoreReady()) { 
							while (utils.nextTypeToBuild == null) {
								if(randWhatToBuild <= probBuildTurret){
									utils.nextTypeToBuild = RobotType.TURRET;
									break;
								}else{
									// Adjust the drawn rand to reflect failing this prob check.
									randWhatToBuild = randWhatToBuild - probBuildTurret;
								}

								if(randWhatToBuild <= probBuildGuard){
									utils.nextTypeToBuild = RobotType.GUARD;
									break;
								}else{
									// Adjust the drawn rand to reflect failing this prob check.
									randWhatToBuild = randWhatToBuild - probBuildGuard;
								}

								if(randWhatToBuild <= probBuildSoldier){
									utils.nextTypeToBuild = RobotType.SOLDIER;
									break;
								}else{
									// Adjust the drawn rand to reflect failing this prob check.
									randWhatToBuild = randWhatToBuild - probBuildSoldier;
								}
								if(randWhatToBuild <= probBuildScout){
									utils.nextTypeToBuild = RobotType.SCOUT;
									break;
								}else{
									// Adjust the drawn rand to reflect failing this prob check.
									randWhatToBuild = randWhatToBuild - probBuildSoldier;
								}
							}
							if (utils.nextTypeToBuild != null){

								rc.setIndicatorString(2,"I found something to build");
								switch (utils.tryBuild(utils.nextTypeToBuild, dir, rt)){
								case 0:
									// Core not ready or not an Archon (?!)
									break;
								case 1:
									// Build succeeded
									utils.nextTypeToBuild = null;
									break;
								case 2:
									// not enough parts
									// => wait until we have enough parts
									break;
								case 3:
									// blocked by rubble
									if (utils.rubbleRouse(dir)) {
										// clear successful
										break;
									} else {
										// too much or too little rubble to bother clearing,
										// OR, no rubble at all.
										// TODO: check if anything needs to happen here
										break;
									}
								case 4:
									// blocked by friendly bot
									break;
								case 5:
									// blocked by ENEMY BOT??
									// see following case... 
								case 6:
									// blocked by ZED.
									// move away, and immediately check for panic
									utils.tryMove(dir.opposite());
									utils.checkForPanic();
									break;
								case 7:
									// blocked by neutral bot
									rc.activate(utils.dirToLoc(myLoc, dir));
									break;
								default:
									// ????
								}
							} else {
								// either we shouldn't build, or we've already built
								break;
							}
						} else {
							// core is not ready
							break;
						}
					}
					// should we move?
					utils.checkForPanic();
				} else {
					// CORE-FREE TASKS:
					// - Signaling 
					// - Repairing 

					rc.setIndicatorString(2,"I'm in the repair/signal Core-free block");
					// REPAIRING
					/*
					 * FIXME 
					 * FIXME
					 * TODO
					 * FIXME
					 * THIS KEEPS BREAKING 
					 * AAAAAAAAARGH 
					 */
//					MapLocation weakestOne = utils.findWeakest(friends);
//					if(weakestOne!=null&&rc.senseRobotAtLocation(weakestOne).type!=RobotType.ARCHON){//can't repair archons!
//						rc.repair(weakestOne);
//						return;
//					}

					// SIGNALING
					Signal[] incomingSignals = rc.emptySignalQueue();
					for (Signal s:incomingSignals) {
						MapLocation encryptedLoc = s.getLocation();
						MapLocation sigLoc = utils.decryptCoords(encryptedLoc.x);
						switch (encryptedLoc.y) {
						case 911:
							// DEN LOCATION - REBROADCAST TO TROOPS
							rc.broadcastMessageSignal(utils.encryptCoords(sigLoc.x, sigLoc.y), 911, 100);
							break;
						case 611:
							// BIGZOMBIE LOCATION
							break;
						case 104:
							// Turret tgt
							break;
						default: 
							// enemy archon ID# - rebroadcast
//							utils.enemyArchonLocs.add(new IdLocPair(encryptedLoc.y,sigLoc));
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