# Operation BLEAKFORTUNE: Hope past hope.
*Battlecode 2016 | Team #42*

As time and entropy, we are the inexorable.

## Changelog
### 2016/01/13: Sprint Tournament Postmortem
  * [Dark times for the Operation](https://www.youtube.com/watch?v=19rv-48qERA). Having faced an avalanche of more sophisticated and coordinated foes, our "blind" bots have begun to show their shortcomings, in a big way.
  * New strategy is being considered. On the table are several much-needed improvements to our existing system:
    * **System I: Signalling architecture to communicate local and global objectives to units.**
      * *Subsystem A: Scout code that measures the following:*
        * Map size (see below)
        * Enemy Archon locations
          * Early game rushes
          * Archon KamikaZeds
            * i.e. friendly Archons that rush toward enemy units when they reach below a certain health level **and** will be infected for a significant period of time.
        * Hostiles in range of friendly turrets
      * *Subsystem B: Database for storing information round-to-round.*
        * Long term objectives
          * High-priority targets 
            * Big Zeds
              * Big Zeds are **slower than normal troops.** Ergo: we can [kite](http://lmgtfy.com/q?=kiting+moba) them instead of fist-fighting and sustaining damage.
            * Den locations
              * Eliminating nearby dens early is a consistent win condition
            * Enemy Archon locations (see KamikaZeds above)
          * Blind Firing (see: [kyle.java](http://s3.amazonaws.com/battlecode-releases-2016/lectures/kyle.zip) and [Lecture 4](http://www.battlecode.org/contestants/lectures/))
            * Turrets cannot fire up close. It's crucial that we take davantage of their full attack range.
    * **System II: Improved Archon code.**
      * Adaptive spawn rates for differently-sized maps.
        * We consistently lose smaller maps to early soldier rushes.
        * Conversely, we tend to lose on larger maps due to disorganized and outnumbered units.
          * Group and coordinate more.
          * Spawn more and smarter.
      * When accosted by hostiles, ***RUN.***
        * We've forfeited a couple games just because our Archon decided a tower was nice to build in front of an oncoming wave.
        * Several times an Archon, being approached by a wave, has waited until it randomly decides to move (error #1), and instead of moving away, strafed (error #2) or even approached (error #3) the enemies.
  * We were eliminated in the first round of the sprint tourney to small-scale problems like the above. However, the fundamental issue is simply a ***lack of refined logic*** underlying the player. 
    * If we build something cohesive, we will taste victory.

### 2016/01/06: Beginnings
    * Two-Word Strategy Summary: **[TURTLE TOWER!](https://www.youtube.com/watch?v=P96Ne_CkFuQ)**
    * Movement
      * All units will move toward other friendly units.
      * If a unit sees a hostile (zed OR enemy), it will move toward it and attack.
      * When surrounded by multiple enemies, units will always attack the weakest.
        * Except Archons, which will simply retreat toward other friendlies.
      * Turrets never pack; they are set in place forever.
        * Unpacking was hard.
    * Building
      * Archons will build Turrets until Parts falls below 90 (or so; see code in case it's changed recently...).
      * Then, they will construct either soldiers or guards, depending on a 50/50 die roll.
        * Viper/Scout AI was also hard. Also, screw Signals. 
    * Signals
      * Did you not just read the above line?
    * Repairing
      * Archons are _supposed_ to repair any adjacent injured bots, with turrets taking priority. The code is probably broken, since it doesn't ever end up happening.
    * Rubble
      * We don't do anything with rubble yet.
      * Planned functionality: Archons should clear rubble when it's blocking a build.

## Rankings:
* Our (awesome,abysmal,ambivalent)*rand.nextInt(3)%3 record can be found [here](http://www.battlecode.org/scrimmage/).

## What 

MIT 2016 Battlecode competition.

## Who

Doesn't matter.

## When

January 2016. Total prize pool: $50,000.[1][We're ineligible. So it goes.]