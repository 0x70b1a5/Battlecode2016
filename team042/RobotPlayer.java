package team042dev;

import battlecode.common.*;
import battlecode.common.RobotController;

public class RobotPlayer {
	
	public static void run(RobotController robotController) {
		
		//Robot robot = null; //Can do it this way if we rewrite Robot like 170 did. Would allow us to have common utility methods.
		
		// parse the current robot. Might remove type() and just replace with hardcode. We're going for efficiency not 'proper' looking code

		if (robotController.getType() == RobotType.ARCHON) {

			Archon robot = new Archon(robotController);
			robot.run();									
			
		} else if (robotController.getType() == RobotType.GUARD) {
			
			Guard robot = new Guard(robotController);
			robot.run();
			
		} else if (robotController.getType() == RobotType.SCOUT) {

			Scout robot = new Scout(robotController);
			robot.run();
			
		} else if (robotController.getType() == RobotType.SOLDIER) {
			
			Soldier robot = new Soldier(robotController);
			robot.run();
			
		} else if (robotController.getType() == RobotType.TURRET) {  //Merge this with TTM???
			
			Turret robot = new Turret(robotController);
			robot.run();
			
		} else if (robotController.getType() == RobotType.TTM) {		//^^^^^^^^^^^^^^
			
			TTM robot = new TTM(robotController);
			robot.run();
			
		} else if (robotController.getType() == RobotType.VIPER) {
			
			Viper robot = new Viper(robotController);
			robot.run();
			
		} 

	}
}
