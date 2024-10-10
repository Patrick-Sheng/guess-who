package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.enums.SceneState;

public class IntroductionController {

  @FXML ImageView imageFamilyPhoto;
  @FXML ImageView imageEmeraldRoomBefore;
  @FXML ImageView imageEmeraldRoomAfter;
  @FXML ImageView imageDetectiveHouse;
  @FXML ImageView imageDetectiveManor;
  @FXML Label labelDescription;
  @FXML Label labelClickInstruction;

  @FXML
  public void initialize() {
    imageEmeraldRoomBefore.setVisible(false);
    imageEmeraldRoomAfter.setVisible(false);
    imageDetectiveHouse.setVisible(false);
    imageDetectiveManor.setVisible(false);

    imageFamilyPhoto.setVisible(true);
    labelDescription.setText("The family photo of the wealthy family who owns the emerald.");
    labelClickInstruction.setText("Click anywhere to go next");
  }

  @FXML
  public void nextScene() {
    if (imageFamilyPhoto.isVisible()) {
      imageFamilyPhoto.setVisible(false);
      imageEmeraldRoomBefore.setVisible(true);
      labelDescription.setText("The emerald room before the theft.");
    } else if (imageEmeraldRoomBefore.isVisible()) {
      imageEmeraldRoomBefore.setVisible(false);
      imageEmeraldRoomAfter.setVisible(true);
      labelDescription.setText("The emerald room after the theft.");
    } else if (imageEmeraldRoomAfter.isVisible()) {
      imageEmeraldRoomAfter.setVisible(false);
      imageDetectiveHouse.setVisible(true);
      labelDescription.setText("The detective's house.");
    } else if (imageDetectiveHouse.isVisible()) {
      imageDetectiveHouse.setVisible(false);
      imageDetectiveManor.setVisible(true);
      labelDescription.setText("The manor where the emerald was stolen.");
      labelClickInstruction.setText("Click anywhere to start the game!");
    } else if (imageDetectiveManor.isVisible()) {
      startGame();
    }
  }

  @FXML
  public void startGame() {
    App.setRoot(SceneState.START_GAME, "Starting game!");
    App.getGameState().startTimer(300);
  }
}
