package nz.ac.auckland.se206.speech;

import nz.ac.auckland.apiproxy.tts.TextToSpeechRequest.Provider;
import nz.ac.auckland.apiproxy.tts.TextToSpeechRequest.Voice;

public class Text {
    private String text;
    private Voice voice;
    private Provider provider;

    public Text(String text, Voice voice, Provider provider) {
        this.text = text;
        this.voice = voice;
        this.provider = provider;
    }

    public String getText() {
        return text;
    }

    public Voice getVoice() {
        return voice;
    }

    public Provider getProvider() {
        return provider;
    }
}