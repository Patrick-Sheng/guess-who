package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.abstractions.ButtonController;
import nz.ac.auckland.se206.enums.SceneState;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class SettingsController extends ButtonController {
  @FXML private Slider volumeSlider;
  @FXML private Slider sfxSlider;
  @FXML private ToggleButton muteButton;

  private MediaPlayer music;
  private AudioClip sfx;

  @FXML
  public void initialize() {
    // Get the current music and sound effects instances from the application
    music = App.getMusic();
    sfx = App.getSfx();

    // Set the sliders to the current volume levels (0 to 100 scale)
    volumeSlider.setValue(music.getVolume() * 100);
    sfxSlider.setValue(sfx.getVolume() * 100);

    // Add listeners to the sliders to update the volume in real-time when the user adjusts them
    volumeSlider
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> music.setVolume(newValue.doubleValue() / 100.0));

    sfxSlider
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> sfx.setVolume(newValue.doubleValue() / 100.0));

    // Initialize the mute button state based on the current mute state of the music
    updateMuteButton();

    // Add a listener to the music's mute property to update the mute button whenever the mute state
    // changes
    music.muteProperty().addListener((observable, oldValue, newValue) -> updateMuteButton());
  }

  @FXML
  private void onMuteToggle() {
    // Toggle the mute state of the music and update the application's mute state
    boolean newMute = !music.isMute();
    App.setMuted(newMute);
    TextToSpeech.voiceState(newMute);
    music.setMute(newMute);
    // Update the mute button text and style based on the new mute state
    updateMuteButton();
  }

  private void updateMuteButton() {
    // Update the mute button text and style
    if (music.isMute()) {
      muteButton.setText("On");
      muteButton.setStyle("-fx-text-fill: darkred;");
    } else {
      muteButton.setText("Off");
      muteButton.setStyle("-fx-text-fill: green;");
    }
  }

  @FXML
  private void onBackButton() {
    App.setRoot(SceneState.MAIN_MENU, "Going back to home page...");
  }
}
