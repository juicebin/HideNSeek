# TODO
- [ ] Set chat/tab color and prefix
- [ ] Glowing
- [ ] Trails
- [ ] Make sure that by the time the game time is at 00:00, the world border isn't too small
- [ ] Performance test and fixes
- [ ] Rounds ?
- [x] Make team config reload command (include reload teams each config reload)

# To Test
- Game winning message
- Game winning scoreboard display
- The entire game structure
- Individual GameListener functionality
- Glowing
- Team Management
- If players are active when they're offline
- Tagging
- Nameplate hiding
- Player joining and leaving again
- Multiple hiding teams winning

# Bugs
- [x] The time and countdowns are like one second apart
- [x] Actionbar seeker countdown doesn't display correct time
- Game ending
  - [x] Seekers won even though there was a hiding team left
  - [x] It didn't say who won in chat
  - [x] It didn't show who won in the scoreboard
  - [x] After return to lobby, the time should be removed

# Once the plugin is done
- Set up the world and worldguard zone to disable fall damage, hunger, etc. (BUT KEEP PVP)

# Future
- [ ] Add lang functionality w/ placeholders for ALL string messages
- [ ] Debug log functionality
- [ ] PlaceHolder API integration
- [ ] Make games world-based and allow for multiple at a time
- [ ] Make scoreboard entirely configurable
- [ ] Make the command system (original system you were working on that you gave up for times' sake)
- [ ] Make the games world-dependent instead of server-wide
- [ ] Strip of library components and make into GooseLibs
- [ ] Build the GUI menu library and then the game's GUI menu
