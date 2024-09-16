package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.abstractions.ButtonController;
import nz.ac.auckland.se206.enums.SceneState;

public class SettingsController extends ButtonController {
    public Slider volumeSlider;
    public ToggleButton muteButton;
    public ImageView backButton;

    private MediaPlayer music;

    @FXML
    public void initialize() {
        super.initialize();

        music = App.getMusic();

        volumeSlider.setValue(music.getVolume() * 100);

        volumeSlider
                .valueProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            music.setVolume(newValue.doubleValue() / 100.0);
                        });

        updateMuteButton();

        music
                .muteProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            updateMuteButton();
                        });
    }

    @FXML
    private void onMuteToggle() {
        music.setMute(!music.isMute());
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
        App.setRoot(SceneState.MAIN_MENU);
    }
}