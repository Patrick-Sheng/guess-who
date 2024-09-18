package nz.ac.auckland.se206.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.enums.SceneState;
import nz.ac.auckland.se206.enums.Suspect;

public class GuessingController {

  @FXML private Pane paneTimeIsUp;
  @FXML private Label systemDescriptionLabel;
  @FXML private Button moveToNextScene;
  @FXML private Rectangle rectAunt;
  @FXML private Rectangle rectGardener;
  @FXML private Rectangle rectNiece;
  @FXML private Label timerLabel;

  private Suspect chosenSuspect;

  @FXML
  public void initialize() {
    paneTimeIsUp.setVisible(false);
  }

  public void updateLblTimer(int time, int red, int green, int blue) {
    if (time == 0) {
      timeIsUp();
    }

    int minutes = time / 60;
    int seconds = time % 60;
    timerLabel.setStyle(String.format("-fx-text-fill: rgb(%d, %d, %d);", red, green, blue));
    timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
  }

  private void timeIsUp() {
    App.stopTimer();
    paneTimeIsUp.setVisible(true);

    if (chosenSuspect == null) {
      systemDescriptionLabel.setText(
          "You did not select a suspect within time limit. Game is now over.");
      moveToNextScene.setText("Back to Main Menu");
    } else {
      systemDescriptionLabel.setText("Click submit to see if you are correct.");
      moveToNextScene.setText("Submit");
    }
  }

  private void highlightCharacterPane(String rectId) {
    switch (rectId) {
      case "rectAunt":
        rectAunt.setOpacity(0);
        rectGardener.setOpacity(0.5);
        rectNiece.setOpacity(0.5);
        break;
      case "rectGardener":
        rectAunt.setOpacity(0.5);
        rectGardener.setOpacity(0);
        rectNiece.setOpacity(0.5);
        break;
      case "rectNiece":
        rectAunt.setOpacity(0.5);
        rectGardener.setOpacity(0.5);
        rectNiece.setOpacity(0);
        break;
      default:
        rectAunt.setOpacity(0.5);
        rectGardener.setOpacity(0.5);
        rectNiece.setOpacity(0.5);
        break;
    }
  }

  @FXML
  private void chooseSuspect(MouseEvent event) {
    Rectangle rectangle = (Rectangle) event.getSource();
    String rectId = rectangle.getId();
    switch (rectId) {
      case "rectAunt":
        chosenSuspect = Suspect.AUNT;
        break;
      case "rectGardener":
        chosenSuspect = Suspect.GARDENER;
        break;
      case "rectNiece":
        chosenSuspect = Suspect.NIECE;
        break;
    }
    highlightCharacterPane(rectId);
  }

  @FXML
  private void enterChoosing(MouseEvent event) {
    Rectangle rectangle = (Rectangle) event.getSource();
    String rectId = rectangle.getId();
    if (chosenSuspect == null) {
      highlightCharacterPane(rectId);
    }
  }

  @FXML
  private void exitChoosing() {
    if (chosenSuspect == null) {
      rectAunt.setOpacity(0.5);
      rectGardener.setOpacity(0.5);
      rectNiece.setOpacity(0.5);
    }
  }

  @FXML
  private void onSubmitFeedback() {
    App.stopTimer();

    Task<Void> task =
        new Task<Void>() {
          @Override
          protected Void call() {
            App.setSuspect(chosenSuspect);
            switch (chosenSuspect) {
              case AUNT:
                App.setRoot(SceneState.END_GAME_WON);
                break;
              case GARDENER:
                App.setRoot(SceneState.END_GAME_LOST);
                break;
              case NIECE:
                App.setRoot(SceneState.END_GAME_LOST);
                break;
              default:
                return null;
            }
            return null;
          }
        };

    new Thread(task).start();
  }

  @FXML
  public void onMoveToNextScene() {
    App.stopTimer();
    if (chosenSuspect == null) {
      App.setRoot(SceneState.MAIN_MENU);
    } else {
      onSubmitFeedback();
    }
  }
}
