package team042;

import battlecode.common.*;

public class RobotPlayer {

	public static void run(RobotController robotController) {

		//Robot robot = null; //Can do it this way if we rewrite Robot like 170 did. Would allow us to have common utility methods.
		// parse the current robot. Might remove type() and just replace with hardcode. We're going for efficiency not 'proper' looking code
		try {
			RobotType robotType = robotController.getType();

			switch(robotType){
			case ARCHON:
				Archon robot = new Archon(robotController);
				robot.run();
				break;
			case GUARD:
				Guard guard = new Guard(robotController);
				guard.run();
				break;
			case SCOUT:
				Scout scout = new Scout(robotController);
				scout.run();
				break;
			case VIPER:
				Viper viper = new Viper(robotController);
				viper.run();
				break;
			case TTM:
				TTM ttm = new TTM(robotController);
				ttm.run();
				break;
			case TURRET:
				Turret turret = new Turret(robotController);
				turret.run();
				break;
			case SOLDIER:
				Soldier soldier = new Soldier(robotController);
				soldier.run();
				break;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
