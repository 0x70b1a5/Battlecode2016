package team042_old;

import battlecode.common.*;

import java.util.Random;

public class RobotPlayer {
	/**
	 * run() is the method that is called when a robot is instantiated in the Battlecode world.
	 * If this method returns, the robot dies!
	 **/
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

	@SuppressWarnings("unused")
	public static void run(RobotController rc) throws GameActionException {
		// You can instantiate variables here.
		Direction[] dirs = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
				Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
		Random rand = new Random(rc.getID());
		int myAttackRange = 0;
		Team myTeam = rc.getTeam();
		Team enemyTeam = myTeam.opponent();
		RobotType rt = rc.getType();

		if (rt == RobotType.ARCHON) {

			// ARCHON ALGO: 
			// - build towers
			// - repair nearby bots 
			// - attack zeds when {rc.numNearbyZeds() > OMFG_RUN_THRESH} zeds nearby
			// - TODO: spawn ratios
			// --- TODO: spawn ratios respond to map/team status

			try {
				// Any code here gets executed exactly once at the beginning of the game.
			} catch (Exception e) {
				// Throwing an uncaught exception makes the robot die, so we need to catch exceptions.
				// Caught exceptions will result in a bytecode penalty.
				System.out.println(e.getMessage());
				e.printStackTrace();
			}

			while (true) {
				// This is a loop to prevent the run() method from returning. Because of the Clock.yield()
				// at the end of it, the loop will iterate once per game round.
				try {
					MapLocation myLoc = rc.getLocation();
					RobotInfo[] nearbyBots = rc.senseNearbyRobots();
					//					Signal[] signals = rc.emptySignalQueue();
					//					if (signals.length > 0) {
					//						// TODO: interpret signals
					//					}
					if (rc.isCoreReady()) {
						// CORE-ONLY TASKS:
						// - Move 
						// - Construct bots
						// - Activate neuts
						// - Clear rubble (yeah right)

						// PRIORITIES:
						// 1. Build turrets
						// 2. Build soldiers/guards
						// 3. Style on zeds

						// BUILDING STUFF 
						for (Direction dir : dirs) {
							if (rc.canBuild(dir, RobotType.TURRET)) {
								if (rc.getTeamParts() > 82) {
									// Lots of parts. TURRET.
									rc.build(dir, RobotType.TURRET);
								} else {
									// Not so many parts.
									// Randomly build soldiers or guards, biased slightly toward soldiers.
									if (nearbyBots.length % 2 == 0) {
										rc.build(dir, RobotType.SOLDIER);
									} else {
										rc.build(dir, RobotType.GUARD);
									}
								}
							} else {
								// Can't build in this dir. Next dir!
							}
						}
						// MOVING AROUND
						for (RobotInfo bot : nearbyBots) {
							if (bot.team == myTeam) {
								// Move toward it.
								Direction closerToBot = myLoc.directionTo(nearbyBots[0].location);
								if (rc.canMove(closerToBot)) {
									rc.move(closerToBot);
								} else {//can't move toward it!
									for (int i=0; i<8; i++) {
										Direction strafeDir = closerToBot.rotateRight();
										if (rc.canMove(strafeDir)) {
											rc.move(strafeDir);
										} 
									}
								}
							} else {
								// Move away.
								Direction awayFromBot = myLoc.directionTo(nearbyBots[0].location).opposite();
								if (rc.canMove(awayFromBot)) {
									rc.move(awayFromBot);
								} else { //can't move away!
									for (int i=0; i<8; i++) {
										Direction strafeDir = awayFromBot.rotateRight();
										if (rc.canMove(strafeDir)) {
											rc.move(strafeDir);
										} 
									}
								}
							}
						}

					} else {
						// CORE-FREE TASKS:
						// - Signaling 
						// - Repairing 

						// REPAIRING
						//	TURRETS FIRST
						RobotInfo[] nearbyTurrets = new RobotInfo[0];
						// find turrets
						for (RobotInfo bot : nearbyBots) {
							if (bot.type == RobotType.TURRET) {
								nearbyTurrets[nearbyTurrets.length] = bot;
							}
						}
						// Are there turrets?
						if (nearbyTurrets.length > 0) {// Yes.
							// find weakest turret
							RobotInfo weakest = nearbyTurrets[0];
							for (RobotInfo tur : nearbyTurrets) {
								if (tur.health < weakest.health) {
									weakest = tur;
								}
							}

							rc.repair(weakest.location);
						} else {// No turrets.
							// repair another bot instead.
							// Find weakest near bot
							RobotInfo weakest = nearbyBots[0]; 
							for (RobotInfo bot : nearbyBots) {
								if (bot.health < weakest.health) {
									weakest = bot;
								}
							}

							rc.repair(weakest.location);
						}

						// SIGNALING
						// TODO: anything at all here
					}

					Clock.yield();
				} catch (Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		} else if (rt == RobotType.SOLDIER || rt == RobotType.GUARD) {
			// TODO: make guards prefer attacking zeds
			try {
				// Any code here gets executed exactly once at the beginning of the game.
				myAttackRange = rt.attackRadiusSquared;
			} catch (Exception e) {
				// Throwing an uncaught exception makes the robot die, so we need to catch exceptions.
				// Caught exceptions will result in a bytecode penalty.
				System.out.println(e.getMessage());
				e.printStackTrace();
			}

			while (true) {
				// This is a loop to prevent the run() method from returning. Because of the Clock.yield()
				// at the end of it, the loop will iterate once per game round.
				try {
					MapLocation myLoc = rc.getLocation();
					RobotInfo[] nearbyBots = rc.senseNearbyRobots();
					boolean shouldAttack = false;
					RobotInfo[] enemiesWithinRange = rc.senseHostileRobots(myLoc, myAttackRange);

					// If this robot type can attack, check for enemies within range and attack one
					if (myAttackRange > 0) {
						if (enemiesWithinRange.length > 0) {
							shouldAttack = true;						
							// Check if weapon is ready
							if (rc.isWeaponReady()) {
								// Find weakest enemy
								RobotInfo weakest = enemiesWithinRange[0];
								for (RobotInfo enemy : enemiesWithinRange) {
									RobotInfo current = enemy;
									if (current.health < weakest.health) {
										weakest = current;
									}
								}
								rc.attackLocation(weakest.location);
							}
						} 
					}

					if (!shouldAttack) {
						if (rc.isCoreReady()) {
							// Are there enemies?
							if (enemiesWithinRange.length > 0) {
								// Find nearest enemy
								RobotInfo nearest = enemiesWithinRange[0];
								for (RobotInfo enemy : enemiesWithinRange) {
									RobotInfo current = enemy;
									if (myLoc.distanceSquaredTo(current.location) < 
											myLoc.distanceSquaredTo(nearest.location)) {
										nearest = current;
									}
								}
								Direction nearDir = myLoc.directionTo(nearest.location);
								if (rc.canMove(nearDir)) {
									rc.move(nearDir);
								} else {
									// TODO: what do do if can't move nearer to enemies
									// (probably move closer to friends lol)
								}
							} else {
								// No enemies. 
								for (RobotInfo bot : nearbyBots) {
									if (bot.team == myTeam) {
										// Move toward it.
										Direction closerToBot = myLoc.directionTo(nearbyBots[0].location);
										if (rc.canMove(closerToBot)) {
											rc.move(closerToBot);
										} else {//can't move toward it!
											for (int i=0; i<8; i++) {
												Direction strafeDir = closerToBot.rotateRight();
												if (rc.canMove(strafeDir)) {
													rc.move(strafeDir);
												} 
											}
										}
									} else {
										// Move away.
										Direction awayFromBot = myLoc.directionTo(nearbyBots[0].location).opposite();
										if (rc.canMove(awayFromBot)) {
											rc.move(awayFromBot);
										} else { //can't move away!
											for (int i=0; i<8; i++) {
												Direction strafeDir = awayFromBot.rotateRight();
												if (rc.canMove(strafeDir)) {
													rc.move(strafeDir);
												} 
											}
										}
									}
								}
							}

						} else {
							// core is not ready.
						}
					}

					Clock.yield();
				} catch (Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		} else if (rt == RobotType.VIPER) {
			while (true) {
				// TODO: VIPER STUFF
				Clock.yield();
			}
		} else if (rt == RobotType.TURRET) {
			try {
				// ONCE
				myAttackRange = rt.attackRadiusSquared;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}

			while (true) {
				// TODO: TURRET STUFF
				RobotInfo[] nearbyBots = rc.senseNearbyRobots();
				MapLocation myLoc = rc.getLocation(); 
				boolean shouldAttack = false;
				RobotInfo[] enemiesWithinRange = rc.senseHostileRobots(myLoc, myAttackRange);

				// If this robot type can attack, check for enemies within range and attack one
				if (myAttackRange > 0) {
					if (enemiesWithinRange.length > 0) {
						shouldAttack = true;						
						// Check if weapon is ready
						if (rc.isWeaponReady()) {
							// Find weakest enemy
							RobotInfo weakest = enemiesWithinRange[0];
							for (RobotInfo enemy : enemiesWithinRange) {
								RobotInfo current = enemy;
								if (current.health < weakest.health) {
									weakest = current;
								}
							}
							try {
								rc.attackLocation(weakest.location);
							} catch (GameActionException e) {
								// TODO Auto-generated catch block
								System.out.println(e.getMessage());
								e.printStackTrace();
							}
						}
					} 
				}

				if (!shouldAttack) {
					if (rc.isCoreReady()) {
						// Are there enemies?
						if (enemiesWithinRange.length > 0) {
							// Find nearest enemy
							RobotInfo nearest = enemiesWithinRange[0];
							for (RobotInfo enemy : enemiesWithinRange) {
								RobotInfo current = enemy;
								if (myLoc.distanceSquaredTo(current.location) < 
										myLoc.distanceSquaredTo(nearest.location)) {
									nearest = current;
								}
							}
							// no enemies within range. I'm a turret!
							// For now, just wait. Our time will come.
							Clock.yield();
						} else {
							// I'm still a turret!
							// DO NOTHING.
						}
					} else {
						// core is not ready.
					}
				}

				Clock.yield();
			}
		} else if (rt == RobotType.TTM) {
			// DO, OR DO NOT; THERE IS NO "try"
			rc.unpack();
			myAttackRange = rt.attackRadiusSquared;
			while (true) {
				MapLocation myLoc = rc.getLocation();
				RobotInfo[] nearbyBots = rc.senseNearbyRobots();
				Direction ttmDir = myLoc.directionTo(nearbyBots[0].location).opposite();
				if (rc.isCoreReady()) {
					for (Direction d : dirs) {
						if (rc.canMove(ttmDir)) {
							try {
								rc.move(ttmDir);
							} catch (GameActionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				rc.unpack();
				Clock.yield();
			}
		}
	}
}

