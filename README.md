# ConcurrentComputing
A simulation of a swimming medley relay.

<p style="text-align:left;">
    Razeen Brey
    <span style="float:right;">
        BRYRAZ002
    </span>
</p>

## Simulation Rules

### <span style='color:#CC1963'>1. The Start Button Starts the Simulation.</span>

**MedleySimulation**

- The <span style='color:magenta'>view</span>, <span style='color:magenta'>results</span> and <span style='color:magenta'>teams[]</span> thread initialisations were moved from the <span style='color:gold'>main()</span> method to <span style='color:gold'>actionPerformed()</span> of <span style='color:magenta'>startB</span> (the start button).

### <span style='color:#CC1963'>2. The Quit Button terminates the simulation.</span>

Given.

### <span style='color:#CC1963'>3. Only one Swimmer is allowed on a GridBlock at a time.</span>

**GridBlock**

- <span style='color:magenta'>isOccupied</span> was changed from <span style='color:green'>int</span> to <span style='color:green'>AtomicInteger</span>.
  - Ensures that only one thread can access a <span style='color:green'>GridBlock</span> at a time.
  - This is achieved due to the thread-safe java <span style='color:green'>AtomicInteger</span>.

### <span style='color:#CC1963'>4.Swimmers move block by block, simultaneously to ensure liveliness.</span>

**GridBlock**

- <span style='color:gold'>get()</span>, <span style='color:gold'>release()</span> and <span style='color:gold'>occupied()</span> were synchronized to safeguard against data races.

**StadiumGrid**

- <span style='color:magenta'>currentBlock</span> and <span style='color:magenta'>newBlock</span> in <span style='color:gold'>moveTowards()</span> and <span style='color:gold'>jumpTo()</span> were synchronised, in that order, to allow for simultaneous forward movement of swimmers without data races involving accessing <span style='color:green'>GridBlocks</span>.

### <span style='color:#CC1963'>5. After Start button is pressed, Swimmers enter through entrance one at a time in race order.</span>

**SwimTeam**

- <span style='color:green'>AtomicInteger</span> <span style='color:magenta'>swimOrder</span> created to manage order of <span style='color:green'>Swimmer</span> objects.
  - This is set to 1 for BackStroke.

**Swimmer**

- Added a <span style='color:green'>SwimTeam</span> object - <span style='color:magenta'>swimTeam</span> -  to the <span style='color:green'>Swimmer</span> object to access <span style='color:magenta'>swimTeam.swimOrder</span>.
- Synchronised <span style='color:magenta'>swimTeam</span> to make swimmers wait until it is their turn to enter:
  - While the thread's stroke  (<span style='color:magenta'>swimStroke.order</span>) is not the next in line (is not = to <span style='color:magenta'>swimTeam.swimOrder</span>)...
    - wait
  - If it is the next required stroke of the team...
    - enter the stadium
    - increment <span style='color:magenta'>swimTeam.swimOrder</span>
    - notify all other threads.

**StadiumGrid**

- Synchronised <span style='color:magenta'>entrance</span> in <span style='color:gold'>enterStadium()</span> to prevent data races.

### <span style='color:#CC1963'>6. Swimmers line up in race order.</span>

**Swimmer**

- A <span style='color:green'>CountDownLatch</span>, <span style='color:magenta'>blackLatch</span> was created to hold all BackStroke swimmers from starting the race.
- <span style='color:green'>Swimmers</span> are in order based on point 5 above.
- Points 3 and 4 ensure that each <span style='color:green'>GridBlock</span> only has 1 <span style='color:green'>Swimmer</span> in it and that they move block by block.
  - Therefore, all other swimmers will line up and wait behind their BackStroke Swimmer in their team.

### <span style='color:#CC1963'>7. The race only begins when all Backstroke swimmers arrive.</span>

**Swimmer**

- <span style='color:magenta'>blackLatch</span> was made <span style='color:teal'>static</span> so that all <span style='color:green'>Swimmer</span> objects can read it.
- If a <span style='color:green'>Swimmer</span> is a backstroke Swimmer and has reached the <span style='color:magenta'>entrance</span> the <span style='color:green'>CountDownLatch</span> is counted down.
- Once all 10 backstroke Swimmers arrive, the latch is released and the race begins.

### <span style='color:#CC1963'>8. Other members of the team can only start once their previous team member has finished.</span>

**SwimTeam**

- <span style='color:green'>CountDownLatch</span>-es were created for orange, magenta and red swimmers.

**Swimmer**

- Once the black swimmer finishes, it triggers the countdown of the orange swimmer through the shared swimTeam.
- The orange Swimmer swims the race and triggers the magenta swimmers countdown, etc.

### <span style='color:#CC1963'>9. The team with the first freestyle swimmer to complete wins.</span>

Given.
