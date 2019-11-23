package net.patowen.songanalyzer.deck.beatmack;
/*
 * TODO before release:
 * Allow user to change sections of beat macks between linear and cubic
 * Properly handle linear vs cubic in export, raising the tolerance back to 5 milliseconds
 * Allow user to change the visualization shown in a beat mack
 * Allow export of tempo spline and move Beat Saber export to a separate command line program (move tolerance-handling here)
 * Add application title
 * SongAnalyzer -> TempoTool (package and project name)
 * Add license
 * 
 * TODO after release:
 * Add BPM labels to tempo graph
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
 */