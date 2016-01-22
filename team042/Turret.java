package team042;

//import java.util.Random;

import battlecode.common.*;

public class Turret{

	public static RobotController rc;
	public static Utilities utils;
	public static RobotType rt;

	public Turret(RobotController robotController){
		super();
		rc = robotController;
		utils = new Utilities(rc);
	}

	public void run() {
		try {
			// ONCE
			//			Random rand = new Random(rc.getID());
			while (true) {
				rt = rc.getType();
				attack(true);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}


	private void attack(boolean weakest) throws GameActionException {
		MapLocation myLoc = rc.getLocation();
		int myRange = rt.sensorRadiusSquared;
		MapLocation[] enemyLocArray = null;
		RobotInfo [] seenEnemies = rc.senseHostileRobots(myLoc, myRange);
		Signal[] incomingSignals = rc.emptySignalQueue();
		enemyLocArray = utils.combineEnemyLocations(seenEnemies,incomingSignals);
		RobotInfo[] friends= rc.senseNearbyRobots(myRange);
		int enemyCount = enemyLocArray.length;

		// If this robot type can attack, check for enemies within range and attack one
		if (enemyCount > 0){
			if (rt == RobotType.TTM) {
				rc.unpack();}
			if (rc.isCoreReady() && rc.isWeaponReady()) {
				for (MapLocation target:enemyLocArray){
					if (rc.isWeaponReady() && rc.canAttackLocation(target)) {
						rc.attackLocation(target);}}}}
		else {
			for (RobotInfo bot : friends) {
				// Group around archons first
				if (bot.type == RobotType.ARCHON) {
					utils.tryMove(myLoc.directionTo(bot.location));
					break;}
				else{
					utils.tryMove(myLoc.directionTo(bot.location).rotateRight());}}
			if (rt == RobotType.TURRET && rc.getRoundNum() % 50 == 0) {//only pack every 30 rounds
				rc.pack();}}

		Clock.yield();}
}
