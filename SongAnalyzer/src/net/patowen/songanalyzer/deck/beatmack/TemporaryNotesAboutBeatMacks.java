package net.patowen.songanalyzer.deck.beatmack;
/*
 * Constant tempo section operations:
 * Shift phase (drag beats together left or right)
 * Fix a point on-beat and continuously change tempo (drag left and right to resize)
 * Fix a point on-beat and set tempo to a rational value (to take advantage of integer tempo)
 * Fix an off-beat point iff there are no on-beat points in the section
 * 
 * How operations propagate:
 * Changed tempo or phase shift propagates to continuous neighbors but are absorbed by tempo ramps
 * 
 * 
 * Use cases of tempo variance:
 * Uneven tempo due to imperfection
 * Sudden tempo changes or tempo ramps for effect
 * 
 * 
 * Idea (for when there are no constant tempo sections):
 * Let tempo be piecewise-linear with user defined marks as knots
 * Workflow:
 * Drag right where tick sounds are too early
 * Drag left where tick sounds are too late
 * Fix phase at points, NOT TEMPO
 * Think about phase function during implementation, not tempo function
 * Use cubic interpolation. Don't do anything crazy for extrapolation.
 */