package nz.ac.auckland.se206.models;

import nz.ac.auckland.se206.enums.Suspect;

/** Interaction log for the game. Stores message for each suspect. */
public record InteractionLog(Suspect suspect, String message) {}
