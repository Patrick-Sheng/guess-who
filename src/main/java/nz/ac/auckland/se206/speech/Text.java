package nz.ac.auckland.se206.speech;

import nz.ac.auckland.apiproxy.tts.TextToSpeechRequest.Provider;
import nz.ac.auckland.apiproxy.tts.TextToSpeechRequest.Voice;

/** Represents a piece of text that can be spoken by a voice. */
public record Text(String text, Voice voice, Provider provider) {}
