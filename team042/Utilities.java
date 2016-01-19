package team042;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import battlecode.common.*;

public class Utilities {
	public static RobotController rc;
	Random rand;
	public static Direction[] dirs = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
		Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};

	/*
	 * MAP INTELLIGENCE:
	 */
	public MapLocation base;
	public MapLocation[] denLocs;
	public MapLocation[] enemyArchonLocs;
	public MapLocation[] bigZedLocs;

	/*
	 * ARCHON BEHAVIOR FLAGS:
	 */
	public boolean panic = false;
	public RobotType nextTypeToBuild = null;
	public boolean kamikaZed = false;
	public double zedPanicRatio = 0.6;
	public Direction fleeVector = null;
	public int mySight = 35;
	public Team myTeam = null;
	public MapLocation myLoc = null; 
	
	public Utilities(RobotController robotController){
		rc = robotController;
		rand = new Random(rc.getID());
		myTeam = rc.getTeam();
		RobotType rt = rc.getType();
		Team enemyTeam = myTeam.opponent();
		int mySight;
		
		//set mySight manually by unit type
		switch (rt) {
		case ARCHON:
			mySight = 35;
			break;
		case SCOUT:
			mySight = 53;
			break;
		default:
			mySight = 24;
		}
	}

	public void tryMove(Direction d) throws GameActionException{
		int offsetIndex = 0;
		int[] offsets = {0,1,-1,2,-2};
		int dirint = directionToInt(d);
		while (offsetIndex < 5 && !rc.canMove(dirs[(dirint+offsets[offsetIndex]+8)%8])) {
			offsetIndex++;
		}
		if (offsetIndex < 5 && rc.isCoreReady()) {
			rc.move(dirs[(dirint+offsets[offsetIndex]+8)%8]);
		}
	}

	public static int directionToInt(Direction d) {
		switch(d) {
		case NORTH:
			return 0;
		case NORTH_EAST:
			return 1;
		case EAST:
			return 2;
		case SOUTH_EAST:
			return 3;
		case SOUTH:
			return 4;
		case SOUTH_WEST:
			return 5;
		case WEST:
			return 6;
		case NORTH_WEST:
			return 7;
		default:
			return -1;
		}
	}

	public Direction intToDirection(int d) {
		switch(d) {
		case 0:
			return Direction.NORTH;
		case 1:
			return Direction.NORTH_EAST;
		case 2:
			return Direction.EAST;
		case 3:
			return Direction.SOUTH_EAST;
		case 4:
			return Direction.SOUTH;
		case 5:
			return Direction.SOUTH_WEST;
		case 6:
			return Direction.WEST;
		case 7:
			return Direction.NORTH_WEST;
		default:
			return Direction.NONE;
		}
	}

	public MapLocation dirToLoc(MapLocation curMapLoc, Direction d) {
		MapLocation dirLoc;
		switch(d) {
		case NORTH:
			dirLoc = new MapLocation(curMapLoc.x,curMapLoc.y-1);
			return dirLoc;
		case NORTH_EAST:
			dirLoc = new MapLocation(curMapLoc.x+1,curMapLoc.y-1);
			return dirLoc;
		case EAST:
			dirLoc = new MapLocation(curMapLoc.x+1,curMapLoc.y);
			return dirLoc;
		case SOUTH_EAST:
			dirLoc = new MapLocation(curMapLoc.x+1,curMapLoc.y+1);
			return dirLoc;
		case SOUTH:
			dirLoc = new MapLocation(curMapLoc.x,curMapLoc.y-1);
			return dirLoc;
		case SOUTH_WEST:
			dirLoc = new MapLocation(curMapLoc.x-1,curMapLoc.y-1);
			return dirLoc;
		case WEST:
			dirLoc = new MapLocation(curMapLoc.x-1,curMapLoc.y);
			return dirLoc;
		case NORTH_WEST:
			dirLoc = new MapLocation(curMapLoc.x-1,curMapLoc.y+1);
			return dirLoc;
		default:
			dirLoc = new MapLocation(curMapLoc.x,curMapLoc.y);
			return dirLoc;
		}
	}
	
	public Direction locToDir(MapLocation curMapLoc, MapLocation destination) {
		Direction newDir = curMapLoc.directionTo(destination);
		return newDir;
	}

	/*
	 * Use this for adding data to their respective arrays 
	 * i.e.:
	 * 	Utilities.addLocation(denLocs, den.location);
	 * 	Utilities.addLocation(enemyArchonLocs, enemyArchon.location);
	 * etc.
	 */
	public boolean addLocation(MapLocation[] locArr, MapLocation loc) {
		if (Arrays.asList(locArr).contains(loc)) {
			return false;
		} else {
			locArr[locArr.length] = loc;
			return true;
		}
	}

	public int tryBuild(RobotType bot, Direction dir, RobotType rt) throws GameActionException {
		/*
		 * 0 = could not build
		 * 1 = build succeeded
		 * 2 = not enough parts
		 * 3 = direction blocked by rubble
		 * 4 = direction blocked by friendly robot
		 * 5 = direction blocked by enemy robot
		 * 6 = direction blocked by zed
		 * 7 = direction blocked by neutral robot
		 */
		MapLocation loc = dirToLoc(myLoc, dir); 
		// check: core, bot type. try to build the bot.
		if (!rc.isCoreReady() || rt != RobotType.ARCHON || loc == null){return 0;}
		else if (rc.canBuild(dir, bot)){
			rc.build(dir, bot);
			return 1;
		} else {//we are a core-ready Archon that can't build for some reason
			// is there rubble next to us?
			if (rc.senseRubble(loc)>99){
				return 3;
			}
			// is there a bot next to us?
			RobotInfo nearBot = rc.senseRobotAtLocation(loc);
			if (nearBot==null){
				// empty space, but we can't build for some reason
				return 0;
			}
			Team botTeam = nearBot.team;
			switch (botTeam) {
			case A:
				if (myTeam == Team.A) {return 4;} else {return 5;}
			case B:
				if (myTeam == Team.B) {return 4;} else {return 5;}
			case ZOMBIE:
				checkForPanic();
				return 6;
			case NEUTRAL:
				rc.activate(nearBot.location);
				return 7;
			default: 
				// somewhere, something went wrong...
				// a bad input or something for a location that 
				// wasn't sanitized. in that case,
				// just fail. 0 is a rare enough 
				// return from this function that 
				// we should probably debug it anyway.
				return 0;
			}
		}
	}

	public void checkForPanic() {
		/*
		 * Void function to determine whether or not our Archon should be panicking. 
		 * 
		 * If combined health of visible zeds versus the combined health of visible allies is greater than the ratio specified 
		 * above in zedPanicRatio, then the Archon enters Panic Mode, which is characterized by 
		 * running really fast away from the closest zed.
		 */
		// if we're already panicking, use checkForCalmDown() to see if we should return to normal behavior 
		if (panic){return;}
		else{//we're not panicking yet. good.....?	
			RobotInfo[] friends = rc.senseNearbyRobots(mySight, myTeam);
			RobotInfo[] zeds = rc.senseNearbyRobots(mySight, Team.ZOMBIE);
			//compare total health of visible zeds to health of visible teammates
			double totZedHealth = 0.0;
			for (RobotInfo zed:zeds) {
				totZedHealth += zed.health;
			}
			double totFriendHealth = 0.0;
			for (RobotInfo friend:friends) {
				totFriendHealth += friend.health;
			}

			if (totZedHealth >0 && totFriendHealth/totZedHealth >= zedPanicRatio) {
				rc.setIndicatorString(1, "I am panicking!!!!!");
				panic = true;
			}
		}
	}

	public void checkForCalmDown(RobotInfo[] friends, RobotInfo[] zeds) {
		// we should never run this unless we are panicking
		if (!panic) {return;}
		else {//we are panicking, good.....
			int friendsL = friends.length;
			int zedsL = zeds.length;
			//check surroundings
			if (friendsL > 0 && zedsL/friendsL < 1.0) {
				fleeVector = null;
				panic = false;
				rc.setIndicatorString(1, "I am not panicking.");
			}
			// TODO: add more criteria for ending panic
		}
	}

	public MapLocation findWeakest(RobotInfo[] robots) {
		double weakestSoFar = 0;
		MapLocation weakestLocation = null;
		for(RobotInfo r:robots){
			double weakness = r.maxHealth-r.health;
			if(weakness>weakestSoFar){
				weakestLocation = r.location;
				weakestSoFar=weakness;
			}
		}
		return weakestLocation;
	}

	public MapLocation closestHostile(RobotInfo[] enemies) {
		double closestDist=1000.0;
		MapLocation closestLocation = null;
		for (RobotInfo badguy:enemies) {
			double distance = myLoc.distanceSquaredTo(badguy.location);
			if (distance < closestDist) {
				closestDist = distance;
				closestLocation = badguy.location;
			}
		}
		return closestLocation;
	}

	public boolean rubbleRouse(Direction dir) throws GameActionException {
		MapLocation rubLoc = dirToLoc(myLoc, dir);
		if (rc.senseRubble(rubLoc) > 50 && rc.senseRubble(rubLoc) < 10000) {
			rc.clearRubble(dir);
			return true;
		} else {
			return false;
		}
	}
	
	public MapLocation setDestination(MapLocation curLoc, Direction dir, int spacesAway) {
		/*
		 * Intakes a unit's location, a direction to move, and the number of times to move in that direction.
		 * Returns a MapLocation at the end of the journey.
		 */
		MapLocation destination = curLoc;
		for (int spacesWalked=0; spacesWalked<spacesAway; spacesWalked++) {
			destination = dirToLoc(destination,dir);
		}
		return destination;
	}
	
	public MapLocation[] findBigZeds(RobotInfo[] zeds) {
		MapLocation[] bigZedLocs = {};
		for (RobotInfo zed:zeds) {
			if (zed.type == RobotType.BIGZOMBIE) {
				bigZedLocs[bigZedLocs.length] = zed.location;
			}
		}
		return bigZedLocs;
	}
	
	public MapLocation[] combineEnemyLocations(RobotInfo[] visibleEnemyArray, Signal[] incomingSignals) {
		ArrayList<MapLocation> attackableEnemyArray = new ArrayList<MapLocation>();
		for(RobotInfo r:visibleEnemyArray){
			attackableEnemyArray.add(r.location);
		}
		for(Signal s:incomingSignals){
			if(s.getTeam()==rc.getTeam().opponent()){
				MapLocation enemySignalLocation = s.getLocation();
				int distanceToSignalingEnemy = rc.getLocation().distanceSquaredTo(enemySignalLocation);
				if(distanceToSignalingEnemy<=rc.getType().attackRadiusSquared){
					attackableEnemyArray.add(enemySignalLocation);
				}
			}
		}
		MapLocation[] finishedArray = new MapLocation[attackableEnemyArray.size()];
		for(int i=0;i<attackableEnemyArray.size();i++){
			finishedArray[i]=attackableEnemyArray.get(i);
		}
		return finishedArray;
	}
}