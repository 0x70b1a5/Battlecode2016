package team042;

//import java.util.Random;

import battlecode.common.*;

public class Turret {

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
		int mySight = rt.sensorRadiusSquared;
		RobotInfo[] enemies;
		if (rt == RobotType.TURRET) {
			enemies = rc.senseHostileRobots(myLoc, rt.attackRadiusSquared);
		} else {//rt==RobotType.TTM
			enemies = rc.senseHostileRobots(myLoc, mySight);}
		RobotInfo[] friends= rc.senseNearbyRobots(mySight);
		int enemyCount = enemies.length;

		// If this robot type can attack, check for enemies within range and attack one
		if (enemyCount > 0){
			if (rt == RobotType.TTM) {
				rc.unpack();}
			if (rc.isCoreReady() && rc.isWeaponReady()) {
				// Find weakest enemy
				MapLocation target = utils.findWeakest(enemies);
				/*
				 * 
				 * BIG FAT COMMENT NOTE
				 * 
				 * DO NOT FIX TURRET CODE ANY MORE
				 * 
				 * THIS IS DEPRECATED; 
				 * FINISH WRITING SCOUTS AND SIGNALS, THEN RENAME 'TurretwScout.java' TO 'Turret.java' AND 
				 * 
				 * DELETE
				 * 
				 * THIS 
				 * 
				 * FILE
				 * 
				 * !!!!!!
				 * 
				 * 
				 * 
				 * 
				 * 
				 */
				if (rc.isWeaponReady() && rc.canAttackLocation(target)) {
					rc.attackLocation(target);}}}
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
