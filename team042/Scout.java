package team042;

import java.util.Random;

import battlecode.common.*;
import team042.Utilities;

public class Scout {

	public static RobotController rc;
	public static Utilities utils;
	public static RobotType rt;
	public int mySight = 53;
	public Team myTeam;
	public Random rand;

	public Scout(RobotController robotController) {
		super();
		rc = robotController;
		utils = new Utilities(rc);
		rt = rc.getType();
	}

	public void run() {
		try{
			rand = new Random(rc.getID());
			int FATE = rand.nextInt(3);
			myTeam = rc.getTeam();

			while(true){
				switch (FATE) {
				case 0:
					enemyArchonScouter();
					break;
				case 1:
					turretBuddyScouter();
					break;
				case 2:
					mapSizeScouter();
					break;
				default:
					rc.setIndicatorString(1, "I don't know what I'm doing with my life!");
					break;
				}
				Clock.yield();
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

	public void enemyArchonScouter() throws Exception {
		//Search for enemy archons. Broadcast the x,y location if found.
		MapLocation myLoc = rc.getLocation();
		RobotInfo[] enemies= rc.senseHostileRobots(myLoc, mySight);
		boolean searchedArchons = false;
		MapLocation[] enemyArchonLocs = null;

		// find enemy archons
		if (!searchedArchons) {
			enemyArchonLocs = rc.getInitialArchonLocations(myTeam.opponent());
			searchedArchons = true;}

		// navigate towards enemy archons
		if (enemyArchonLocs != null) {
			MapLocation closestEnArch = utils.closest(enemyArchonLocs);
			int distToEnemy = myLoc.distanceSquaredTo(closestEnArch);
			if (distToEnemy >= mySight) {
				utils.tryMove(myLoc.directionTo(closestEnArch));
			} else {
				// now, we have arrived at range to the "initial" location 
				// likely the position has changed.
				// so, clear the cache of locations so that we 
				// skip this block in the future, and now on rely
				// on realtime location data only.
				enemyArchonLocs = null;
			}
		}

		// scan visible enemies, broadcast locations if archon
		for(RobotInfo bot : enemies){
			if(bot.type == RobotType.ARCHON){
				MapLocation archLoc = bot.location;
				int archID = bot.ID;
				rc.broadcastMessageSignal(utils.encryptCoords(archLoc.x, archLoc.y), archID, 50);
			}else if (bot.type == RobotType.ZOMBIEDEN) {
				// alarm code: attack here at all costs
				rc.broadcastMessageSignal(utils.encryptCoords(bot.location.x, bot.location.y), 911, 50);
			}else if(myLoc.distanceSquaredTo(bot.location) < 40){
				utils.tryMove(myLoc.directionTo(bot.location).opposite());
				break;
			}
		}
	}

	public void mapSizeScouter() throws Exception {
		MapLocation myLoc = rc.getLocation();
		int CORNER_FATE = rand.nextInt(4);
		MapLocation destination;
		switch(CORNER_FATE) {
		case 0:
			destination = new MapLocation(0,0);
			break;
		case 1:
			destination = new MapLocation(10000,0);
			break;
		case 2:
			destination = new MapLocation (0,10000);
			break;
		case 3: 
			destination = new MapLocation(10000,10000);
			break;
		default:
			destination = new MapLocation(rand.nextInt(10000), rand.nextInt(10000));
		}

		utils.tryMove(myLoc.directionTo(destination));
		/*
		 *  TODO- finish the rest of this bot
		 *  - detecting when a corner has been reached
		 *  - broadcasting the corner coords to other bots
		 */
	}

	public void turretBuddyScouter() throws Exception {
		MapLocation myLoc = rc.getLocation();
		RobotInfo[] friends = rc.senseNearbyRobots(mySight, myTeam);
		RobotInfo[] enemies = rc.senseNearbyRobots(mySight, myTeam.opponent());
		for (RobotInfo bot:friends) {
			if (bot.type == RobotType.TURRET) {
				if (myLoc.distanceSquaredTo(bot.location) > 9) {
					utils.tryMove(myLoc.directionTo(bot.location));
					break;
				} else {
					MapLocation weakestBot = utils.findWeakestLoc(enemies);
					rc.broadcastMessageSignal(weakestBot.x, weakestBot.y,10);
					break;
				}
			}
		}
	}

}
