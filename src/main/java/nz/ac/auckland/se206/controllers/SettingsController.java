package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.abstractions.ButtonController;
import nz.ac.auckland.se206.enums.SceneState;

public class SettingsController extends ButtonController {
  @FXML private Slider volumeSlider;
  @FXML private Slider sfxSlider;
  @FXML private ToggleButton muteButton;

  private MediaPlayer music;
  private AudioClip sfx;

  @FXML
  public void initialize() {
    music = App.getMusic();
    sfx = App.getSfx();

    volumeSlider.setValue(music.getVolume() * 100);
    sfxSlider.setValue(sfx.getVolume() * 100);

    volumeSlider
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> music.setVolume(newValue.doubleValue() / 100.0));

    sfxSlider
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> sfx.setVolume(newValue.doubleValue() / 100.0));

    updateMuteButton();

    music.muteProperty().addListener((observable, oldValue, newValue) -> updateMuteButton());
  }

  @FXML
  private void onMuteToggle() {
    boolean newMute = !music.isMute();
    App.setMuted(newMute);
    music.setMute(newMute);
    updateMuteButton();
  }

  private void updateMuteButton() {
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
