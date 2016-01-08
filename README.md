# Project BLEAKFORTUNE: Hope past hope.

As time and entropy, we are the inexorable.

## Changelog
* 2016/01/06:
  * Initial A.I. built and pushed.
    * Two-Word Strategy Summary: *[TURTLE TOWER!](https://www.youtube.com/watch?v=P96Ne_CkFuQ)*
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

## Match History (recent first):
### Overall Ranked: 0-0
#### Ranked:
1. N/A

### Overall Scrims: 1-1 
#### Scrims:
1. See Sharper (W,L,L) (closequarters, desert, helloworld) (LOSS)
2. Teh Devs 1-0 (W) (closequarters) (WIN)

## What 

MIT 2016 Battlecode competition.

## Who

Doesn't matter.

## When

January 2016. Total prize pool: $50,000.[1]

[1] We're ineligible. So it goes.
