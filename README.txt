Purpose:

This program should help you find the tempo of any song, especially if the tempo is not constant. Convenience is traded for potential accuracy, as it is a manual tool.

How to use:

 * After downloading or compiling, run the program in a directory that does not require administrative privileges to modify.
 * Ctrl-N starts a new project. (If this is your first time running it, you shouldn't need to press this)
 * To choose a sound file, click on "<no audio selected>" on the top-right to choose a sound file. Only "wav" and "mp3" are currently supported. If you have another format (like "ogg"), converting to "wav" is recommended over "mp3", as converting to "mp3" can introduce an undesired offset of a few milliseconds, enough to be noticeable.
 * To play or pause, press "space". To seek, click on the topmost row (mack) in the main area below the header (deck). The topmost mack is called the seek mack.
 * To zoom in or out in time, use the scroll-wheel. If you zoom in close enough, you will see the waveform of the sound file (left and right stereo channels are added together).
 * To play at a different speed, click and drag left or right on the part of the header that says "1/1". This is a fraction that represents the play speed.
 * You will notice that a metronome is playing (and is probably way off). You can silence the metronome by clicking on the tab to the left of the bottommost mack (the beat mack), and pressing the "T" key. The tab appears white when a mack is selected, and pressing "T" toggles whether the selected mack is allowed to tick. There is a small green square on each tab indicating whether a mack is making a sound or not.
 * The middle mack is called the marker mack. This lets you mark arbitrary points in time, and, assuming it isn't muted, a tick will play at those points. Right click to place a mark. Shift-right click to delete a mark. Select the mack and press "N" to place a mark where the play cursor is.
 * Undo and redo is supported (but don't rely on it). Ctrl-Z map to undo, and both Ctrl-Y and Ctrl-Shift-Z map to redo.
 * To save, use Ctrl-S. To save to a new file, use Ctrl-Shift-S. To open a file, use Ctrl-O. The extension is "tpt" for "tempotool". Note that "tpt" files do not store the audio directly, so if you delete or move the sound file it references, you may have to point the program to a replacement file.

How to use the beat mack:

 * The vertical gray lines represent the beats of the song. Your task is to get these to line up with the actual beat of the song. The short cyan vertical lines are called handles (knots in the codebase) and are moved around manually. The actual beats follow their lead.
 * Click and drag the short cyan vertical lines left and right to spread the beats apart or compress them together, decreasing or increasing the tempo respectively.
 * Right click on a beat to create a new handle. Shift-right click on a handle to delete it. There cannot be fewer than two handles.
 * To add or remove beats between two handles, hold Ctrl and scroll in between these two handles.
 * By default, the tempo changes continuously to smoothly fit all the handles. It is possible to declare a section between two handles to be of constant tempo. This is done by holding Ctrl and right clicking between two handles. The line between these handles will switch from cyan to yellow to indicate that the tempo is constant. Ctrl-right-clicking again will switch the section back to normal.
 * By default, the beat mack shows all the beats as vertical gray lines. You can switch the visualization type by selecting the beat mack and pressing "V". The second representation is a scatterplot that shows each beat and its signed distance from the closest marker mack with vertical bounds set to 200 milliseconds (hardcoded). The third representation is a graph of the tempo, by default set to a range between 0 BPM and 300 BPM. This can be changed by holding shift and scrolling to zoom in and out vertically.
 * It is possible to move marks in such a way that interpolation creates a negative tempo. This will make most of the visualizations and ticking unreliable. To avoid this, either use constant tempo sections or avoid having the tempo change too drastically between two handles.
 * Use Ctrl-B to export the resulting tempo information to a format that gives the information you need for a Beat Saber custom map. This file format shows the average tempo (which the song will need to be set to) and the list of BPM changes in JSON format which can be copied and pasted into each difficulty file.

Suggested workflow for constant-tempo songs:
 * The handles by default are 8 beats apart. Move the earlier handle to the first beat of the song, and the later handle to the 8th beat of the song. Find these locations either by listening or zooming in and looking at the waveform (Note: the second approach can be misleading). Check your work by listening to the beginning of the song and determining if the metronome sounds synchronized.
 * Move further along the song to see if the metronome still sounds synchronized (don't move so far along the song that the metronome could be more than about half a beat off). If it's a little off, place a handle there and delete the previously-later handle (that is now in between two other handles). Now click and drag the new handle left or right to make the metronome sync up again.
 * Repeat this step until you have reached the end of the song, and the metronome is still synchronized.
 * Check your work by listening to the whole song with the metronome.

Suggested workflow for songs that change tempo:

 * Prepare in advance by selecting the marker mack, playing the song slowly (not so slow as to lose any sense of rhythm), and pressing "N" on the beat. Listen to the song at normal speed with the marker mack as a metronome (mute the beat mack and unmute the marker mack) to check your work, redoing any sections that are too far off. Be sure to mute the marker mack and unmute the beat mack afterwards.
 * For each part of roughly constant tempo, follow the instructions above.
 * If the tempo changes suddenly, place a handle at the point where it changes and make the sections on either side constant tempo.
 * If the tempo changes subtly, place handles at the points where the beat is most off and adjust. Go by ear or use the scatterplot representation to assist.
 * Keep adjusting until it sounds right and/or the scatterplot does not appear to have any major trends up or down.
 * Check your work by listening to the whole song with the metronome.

The following potentially useful features have not been completed. They may be included in a future version:

 * Support ogg
 * Add BPM labels to tempo graph
 * Allow export of tempo spline and move Beat Saber export to a separate command line program (move tolerance-handling here)
 * Consider allowing a linear transformation (destructive or saved as a transformation) to all marks at once (within a mack or between macks)
 * Consider allowing the user to round a constant-tempo section (possibly keeping the tempo fixed)
 * Consider allowing the use of rational numbers in tempo export
 * Potentially allow import of tempo spline (Possible compatibility issues with broader export format)
 * Allow user to create macks
 * Allow user to change the metronome marker mack and main beat mack (adding indicators to the tab)
 * Add vertical scrolling for the deck
 * Add asterisk to application title when unsaved (ask for confirmation before closing unsaved work)
 * Allow reduction of click-and-drag sensitivity when moving knots.
 * Allow user to move marks in the marker mack.
 * Add support for thicker grid borders
 * Enforce a minimum size for grids (avoid negative center row/column sizes)
 * Save maximization state of window in config
 * Make seek mack mutable (muting the actual sound file)
 * Always show the waveform. Possibly allow better handling of stereo.
