package bleakfortune;

import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;

public class Utilities {
	public static RobotController rc;
	public Team myTeam, enemyTeam;
	Random rand;
	Direction[] dirs = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
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
}
