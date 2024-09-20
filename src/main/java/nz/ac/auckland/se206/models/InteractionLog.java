package nz.ac.auckland.se206.models;

import nz.ac.auckland.se206.enums.Suspect;

public record InteractionLog(Suspect suspect, String message) {}
