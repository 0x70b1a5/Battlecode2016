package team042;

import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import team042.Utilities;

public class Scout {

	public static RobotController rc;
	public static Utilities utils;
	public static RobotType rt;
	
	public Scout(RobotController robotController) {
		super();
		rc = robotController;
		utils = new Utilities(rc);
		rt = rc.getType();
	}

	public void run() {
		try{
			Random rand = new Random(rc.getID());
			int senseRange = rt.sensorRadiusSquared;
			while(true){
				RobotInfo enemyArchon = null;
				MapLocation myLoc = rc.getLocation();
				RobotInfo[] enemies= rc.senseHostileRobots(myLoc, senseRange);
				RobotInfo[] friends= rc.senseNearbyRobots(senseRange);
				int friendCount = friends.length;
				int enemyCount = enemies.length;
				
				//Search for enemy archons. Broadcast the x,y location if found.
				//TODO: Adjust broadcast range.
				for(RobotInfo bot : enemies){
					if(bot.type == RobotType.ARCHON){
						MapLocation archLoc = bot.location;
						rc.broadcastMessageSignal(archLoc.x, archLoc.y, 500);
						enemyArchon = bot;
						break;
					}
					else if(myLoc.distanceSquaredTo(bot.location) < 25){
						utils.tryMove(myLoc.directionTo(bot.location).opposite());
					}
				}
				
				//IF core is ready and an enemy Archon was detected:
				//Initiate stake-out protocols
				if(rc.isCoreReady() && enemyArchon != null){
					if(myLoc.distanceSquaredTo(enemyArchon.location) > 45)
						utils.tryMove(myLoc.directionTo(enemyArchon.location));
					else
						utils.tryMove(myLoc.directionTo(enemyArchon.location).opposite());
				}
				
				else if(rc.isCoreReady()){
					utils.tryMove(myLoc.directionTo(friends[rand.nextInt(friendCount)].location).opposite());
				}
				
				
				Clock.yield();
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
	}

}
