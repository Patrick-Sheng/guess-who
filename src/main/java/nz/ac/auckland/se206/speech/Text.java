package nz.ac.auckland.se206.speech;

import nz.ac.auckland.apiproxy.tts.TextToSpeechRequest.Provider;
import nz.ac.auckland.apiproxy.tts.TextToSpeechRequest.Voice;

public record Text(String text, Voice voice, Provider provider) {}
