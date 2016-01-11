package team042dev;

import java.util.Random;

import battlecode.common.*;

public class Utilities {
	public static RobotController rc;
	public Team myTeam, enemyTeam;
	Random rand;
	public static Direction[] dirs = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
						Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	
	public Utilities(RobotController robotController){
		rc = robotController;
		rand = new Random(rc.getID());
		myTeam = rc.getTeam();
		enemyTeam = myTeam.opponent();
	}
	
	
	public int numNearbyZeds(RobotController rc) {
		RobotInfo[] nears = rc.senseNearbyRobots();
		int zeds = 0; 
		for (RobotInfo bot : nears) {
			if (bot.team == Team.ZOMBIE) {
				zeds++;
			}
		}
		return zeds;
	}
	public void tryMove(Direction d){
		try {
			int offsetIndex = 0;
			int[] offsets = {0,1,-1,2,-2};
			int dirint = directionToInt(d);
			while (offsetIndex < 5 && !rc.canMove(dirs[(dirint+offsets[offsetIndex]+8)%8])) {
				offsetIndex++;
			}
			if (offsetIndex < 5 && rc.isCoreReady()) {
				
					rc.move(dirs[(dirint+offsets[offsetIndex]+8)%8]);
			}
		} catch (GameActionException e) {
				e.printStackTrace();
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
}
