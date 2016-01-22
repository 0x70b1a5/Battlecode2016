package team042;

import java.util.Random;

import battlecode.common.*;

public class Soldier {

	public static RobotController rc;
	public static Utilities utils;
	public static RobotType rt;
	public Direction[] dirs = Utilities.dirs;
	public int mySight = 35;

	public Soldier(RobotController robotController) {
		super();
		rc = robotController;
		utils = new Utilities(rc);
		rt = rc.getType();
		
	}

	public void run() {
		try {
			// Any code here gets executed exactly once at the beginning of the game.
			int myRange = rt.attackRadiusSquared;
			Random rand = new Random(rc.getID());
			MapLocation target = null;
			while (true) {
				Signal[] inSigs = rc.emptySignalQueue();
				for (Signal s:inSigs) {
					MapLocation enS = s.getLocation();
					MapLocation deS = utils.decryptCoords(enS.x);
					switch (enS.y) {
					case 611:
						// BIGZOMBIE
						// fall through to next case
					case 911:
						// DEN LOCATION
						if (target == null) {target = deS;}
						break;
					default:
						// anything less important
						break;
					}
					break;
				}
				
				if (rc.isCoreReady()) {
					MapLocation myLoc = rc.getLocation();
					RobotInfo[] enemies= rc.senseHostileRobots(myLoc, myRange);
					RobotInfo[] friends= rc.senseNearbyRobots(mySight);
					// If enemies within range, attack one
					int friendCount = friends.length;
					int enemyCount = enemies.length;
					if (rc.isCoreReady()&&enemyCount > 0 && rc.getWeaponDelay()<1) {
						// check high priority targets 
						for (RobotInfo e : enemies) {
							if (e.type == RobotType.ZOMBIEDEN || e.type == RobotType.ARCHON) {
								target = e.location;
								break;
							}
						}
						if (target != null && rc.canAttackLocation(target)) {rc.attackLocation(target);}
						else {// check weak targets
							target = utils.findWeakestLoc(enemies);
							if (target!=null){rc.attackLocation(target);}
							else {
								// just randomly attack
								rc.attackLocation(enemies[rand.nextInt(enemies.length)].location);
							}
						}
					} else if (rc.isCoreReady() && enemyCount > 0){
						//kite enemies
						MapLocation closestEnemyLoc = utils.closestHostile(enemies);
						if (myLoc.distanceSquaredTo(closestEnemyLoc) <= 12) {
							utils.tryMove(myLoc.directionTo(closestEnemyLoc).opposite());
						} else {
							utils.tryMove(myLoc.directionTo(closestEnemyLoc));
						}
					} else if(rc.isCoreReady() && friendCount>0){
						for (RobotInfo bot : friends) {
							// Group around archons first
							if (bot.type == RobotType.ARCHON) {
								utils.tryMove(myLoc.directionTo(bot.location));
							}
						}
						//otherwise group in general
						utils.tryMove(myLoc.directionTo(friends[rand.nextInt(friendCount)].location));
					} else {
						utils.tryMove(utils.intToDirection(rand.nextInt(8)));
					}
					//rubble
					// KEEP CRASHING
//					if (rc.isCoreReady()) {
//						utils.rubbleRouse(dirs[rand.nextInt(8)]);
//						break;
//					}
				}
				Clock.yield();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

