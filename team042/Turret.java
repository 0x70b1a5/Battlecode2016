package team042;

import java.util.Random;

import battlecode.common.*;

public class Turret {

	public static RobotController rc;
	public static Utilities utils;
	public static RobotType rt;

	public Turret(RobotController robotController){
		super();
		rc = robotController;
		utils = new Utilities(rc);
		rt = rc.getType();
	}

	public void run() {
		try {
			// ONCE
			Random rand = new Random(rc.getID());
			//			Random rand = new Random(rc.getID());
			while (true) {
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
		RobotInfo[] enemies = rc.senseHostileRobots(myLoc, rt.attackRadiusSquared);
		RobotInfo[] friends= rc.senseNearbyRobots(myRange);
		int enemyCount = enemies.length;
		
		// If this robot type can attack, check for enemies within range and attack one
		if (enemyCount > 0){
			if (rt == RobotType.TTM) {
				rc.unpack();
				rt = RobotType.TURRET;
			}
			if (rc.isCoreReady() && rc.isWeaponReady()) {
				// Find weakest enemy
				RobotInfo target = enemies[0];
	
				for (RobotInfo enemy : enemies) {
					RobotInfo current = enemy;
					if(weakest){
						if (current.health < target.health) {
							target = current;
						}
					}else{
						if (current.health > target.health) {
							target = current;
						}
					}
				}
				if (rc.isWeaponReady() && rc.canAttackLocation(target.location)) {
					rc.attackLocation(target.location);
				}
			}
		}
		else if(enemyCount == 0){
			if(rt == RobotType.TURRET){
				rc.pack();
				rt = RobotType.TTM;
			}
			for (RobotInfo bot : friends) {
				// Group around archons first
				if (bot.type == RobotType.ARCHON) {
					utils.tryMove(myLoc.directionTo(bot.location).rotateLeft());
				}
				else{
					utils.tryMove(myLoc.directionTo(bot.location).rotateRight());
				}
			}
		}
		
		Clock.yield();
	}
}
