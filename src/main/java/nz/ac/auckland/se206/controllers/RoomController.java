package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.abstractions.MapController;
import nz.ac.auckland.se206.enums.Clues;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController extends MapController {
  @FXML private Pane paneRoom;
  @FXML private Pane paneClue;
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

  private Boolean isLetterReveal;
  private double mouseAnchorX;
  private double mouseAnchorY;

  @FXML
  public void initialize() {
    super.initialize();
    TextToSpeech.speak("The thief of a family heirloom, an emerald, is aloof - and it's your job to find them out - mister detective!");

    isClueClick = false;
    isLetterReveal = false;

    paneClue.setVisible(false);
  }

  @FXML
  public void bagOpen(MouseEvent event) {
    onClue(event, Clues.BAG);
  }

  @FXML
  public void letterReveal(MouseEvent event) {
    onClue(event, Clues.LETTER);
  }

  @FXML
  public void doorOpen(MouseEvent event) {
    onClue(event, Clues.DOOR);
  }

  @FXML
  public void reportClose(MouseEvent event) {
    onClue(event, Clues.REPORT);
  }

  private void onClue(MouseEvent event, Clues clue) {
    if (!isClueClick) {
      isClueClick = true;
    }

    ImageView clickedImageView = (ImageView) event.getSource();
    clickedImageView.getStyleClass().remove("highlight-clue");
    clickedImageView.getStyleClass().add("lighter-clue");

    switch (clue) {
      case BAG:
        setClue(bagOpen);
        report.setVisible(true);
        break;
      case LETTER:
        if (!isLetterReveal) {
          setClue(yellowPaper);
          revealLetterGrid.setVisible(true);
        } else {
          setClue(letterCloseUp);
        }
        break;
      case DOOR:
        setClue(emeraldRoom);
        break;
      case REPORT:
        setClue(reportCloseUp);
        break;
    }

    paneRoom.setOpacity(0.2);
    paneClue.setVisible(true);
    handleRectangleEntered();

    App.getGameState().increaseObjects();
    checkButton();
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
    handleRectangleExited();
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
        isLetterReveal = true;
        setClue(letterCloseUp);
      }
    }
    paneRoom.setOpacity(0.2);
  }

  @FXML
  private void clickFabric() {
    fabricLabel.setVisible(true);
  }

  @FXML
  private void clickRose() {
    roseLabel.setVisible(true);
  }
}
