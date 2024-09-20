package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import nz.ac.auckland.se206.controllers.abstractions.MapController;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController extends MapController {
  public Label labelMap;

  public Button toGuessRoomButton;

  public ImageView letter;
  public ImageView door;
  public ImageView bag;
  public GridPane gridMap;

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
    isClueClick = false;
    isLetterReavel = false;
    super.initialize();
    paneClue.setVisible(false);
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
    handleRectangleEntered();
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
