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
		int myAttackRange = 0;
		try {
			// ONCE
			myAttackRange = rt.attackRadiusSquared;
			Random rand = new Random(rc.getID());
			//			Random rand = new Random(rc.getID());
			while (true) {
				MapLocation myLoc = rc.getLocation(); 
				RobotInfo[] enemies = rc.senseHostileRobots(myLoc, myAttackRange);
				int enemyCount = enemies.length;
				
				// If this robot type can attack, check for enemies within range and attack one
				if (rc.isCoreReady()&&enemyCount > 0&&rc.isWeaponReady()) {
					// Find weakest enemy
					RobotInfo weakest = enemies[0];
					for (RobotInfo enemy : enemies) {
						RobotInfo current = enemy;
						if (current.health < weakest.health) {
							weakest = current;
						}
					}
					if (rc.isWeaponReady()&&rc.canAttackLocation(weakest.location)) {
						rc.attackLocation(weakest.location);
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
