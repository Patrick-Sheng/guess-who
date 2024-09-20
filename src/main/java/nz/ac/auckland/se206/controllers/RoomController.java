package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.abstractions.MapController;
import nz.ac.auckland.se206.enums.SceneState;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController extends MapController {
  @FXML private Button guessButton;
  @FXML private Button toGuessRoomButton;
  @FXML private Pane paneTimeIsUp;
  @FXML private Pane paneMap;
  @FXML private Pane paneRoom;
  @FXML private Pane paneClue;
  @FXML private Label timerLabel;
  @FXML private Label fabricLabel;
  @FXML private Label roseLabel;
  @FXML private ImageView bagOpen;
  @FXML private ImageView emeraldRoom;
  @FXML private ImageView letterCloseUp;
  @FXML private ImageView reportCloseUp;
  @FXML private ImageView report;
  @FXML private ImageView yellowPaper;
  @FXML private GridPane revealLetterGrid;

  private Boolean isClueClick;
  ;
  private Boolean isLetterReavel;
  private double mouseAnchorX;
  private double mouseAnchorY;

  @FXML
  public void initialize() {
    disableLabels();
    disableButton();
    enableButton();
    isClueClick = false;
    isLetterReavel = false;
    paneTimeIsUp.setVisible(false);
    paneMap.setVisible(false);
    paneClue.setVisible(false);
  }

  private void disableLabels() {}

  private void disableButton() {
    guessButton.setStyle("-fx-background-color: darkred; -fx-text-fill: #222;");
    guessButton.setDisable(true);
  }

  public void enableButton() {
    guessButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
    guessButton.setDisable(false);
  }

  public void updateLblTimer(int time, int red, int green, int blue) {
    if (time == 0) {
      App.stopTimer();
      paneTimeIsUp.setVisible(true);
    }

    int minutes = time / 60;
    int seconds = time % 60;
    timerLabel.setStyle(String.format("-fx-text-fill: rgb(%d, %d, %d);", red, green, blue));
    timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
  }

  @FXML
  private void onClue(MouseEvent event) {
    if (!isClueClick) {
      isClueClick = true;
    }
    ImageView clickedImageView = (ImageView) event.getSource();
    clickedImageView.getStyleClass().remove("highlight-clue");
    clickedImageView.getStyleClass().add("lighter-clue");
    String ImageViewID = clickedImageView.getId();

    switch (ImageViewID) {
      case "bag":
        setClue(bagOpen);
        report.setVisible(true);
        break;
      case "letter":
        if (!isLetterReavel) {
          setClue(yellowPaper);
          revealLetterGrid.setVisible(true);
        } else {
          setClue(letterCloseUp);
        }
        break;
      case "door":
        setClue(emeraldRoom);
        break;
      case "report":
        setClue(reportCloseUp);
        break;
    }
    paneRoom.setOpacity(0.2);
    paneClue.setVisible(true);
  }

  private void setClue(ImageView image) {
    bagOpen.setVisible(false);
    letterCloseUp.setVisible(false);
    emeraldRoom.setVisible(false);
    reportCloseUp.setVisible(false);
    report.setVisible(false);
    yellowPaper.setVisible(false);
    revealLetterGrid.setVisible(false);
    roseLabel.setVisible(false);
    fabricLabel.setVisible(false);
    image.setVisible(true);
  }

  @FXML
  private void onExitClue() {
    bagOpen.setVisible(false);
    paneClue.setVisible(false);
    paneRoom.setOpacity(1);
  }

  @FXML
  private void onMakeGuess() {
    App.stopTimer();
    App.resetColour();
    App.startTimer(60);
    App.setRoot(SceneState.START_GUESSING);
  }

  @FXML
  private void onMousePressed(MouseEvent event) {
    mouseAnchorX = event.getX();
    mouseAnchorY = event.getY();
    yellowPaper.setOpacity(0.7);
    revealLetterGrid.setVisible(false);
    paneRoom.setOpacity(1);
  }

  @FXML
  private void onMouseDragged(MouseEvent event) {
    yellowPaper.setLayoutX(event.getSceneX() - mouseAnchorX);
    if (yellowPaper.getLayoutX() < 0) {
      yellowPaper.setLayoutX(0);
    } else if (yellowPaper.getLayoutX() > 730) {
      yellowPaper.setLayoutX(730);
    }
    yellowPaper.setLayoutY(event.getSceneY() - mouseAnchorY);
    if (yellowPaper.getLayoutY() < 0) {
      yellowPaper.setLayoutY(0);
    } else if (yellowPaper.getLayoutY() > 540) {
      yellowPaper.setLayoutY(540);
    }
  }

  /**
   * Handles mouse drop events when the user releases the mouse button after dragging the shovel.
   *
   * @param event the MouseEvent triggered by the user releasing the mouse button
   */
  @FXML
  private void onMouseDropped(MouseEvent event) {
    yellowPaper.setOpacity(1);
    revealLetterGrid.setVisible(true);
    if (326 < event.getSceneX() && event.getSceneX() < 326 + 112) {
      if (538 < event.getSceneY() && event.getSceneY() < 538 + 144) {
        isLetterReavel = true;
        setClue(letterCloseUp);
      }
    }
    paneRoom.setOpacity(0.2);
  }

  @FXML
  private void rectangleClick(MouseEvent event) {
    Shape clickedRectangle = (Shape) event.getSource();
    String id = clickedRectangle.getId();
    switch (id) {
      case "fabricRec":
        fabricLabel.setVisible(true);
        break;
      case "roseRec":
        roseLabel.setVisible(true);
        break;
    }
  }
}
