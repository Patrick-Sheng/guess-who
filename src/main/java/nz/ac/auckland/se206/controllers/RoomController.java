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
 * interact with clues to investigate the crime scene.
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
    // Speaks introductory message to the player
    TextToSpeech.speak(
        "The thief of a family heirloom, an emerald, is aloof - and it's your job to find them out"
            + ", private detective!");

    // Initializes the state of clue interaction and visibility
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

    // Highlights the clicked clue by changing its style
    ImageView clickedImageView = (ImageView) event.getSource();
    clickedImageView.getStyleClass().remove("highlight-clue");
    clickedImageView.getStyleClass().add("lighter-clue");

    // Performs actions based on which clue was clicked
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

    // Dims the room pane and shows the clue pane
    paneRoom.setOpacity(0.2);
    paneClue.setVisible(true);

    // Updates the game state and checks if a button should be enabled
    App.getGameState().increaseObjects();
    isGuessReadyAndUpdate();
  }

  private void setClue(ImageView image) {
    // Hides all clue-related images and labels
    bagOpen.setVisible(false);
    letterCloseUp.setVisible(false);
    emeraldRoom.setVisible(false);
    reportCloseUp.setVisible(false);
    report.setVisible(false);
    yellowPaper.setVisible(false);
    revealLetterGrid.setVisible(false);
    roseLabel.setVisible(false);
    fabricLabel.setVisible(false);

    // Shows the specified clue image
    image.setVisible(true);
  }

  @FXML
  private void onExitClue() {
    // Hides the clue and restores the room pane's visibility
    bagOpen.setVisible(false);
    paneClue.setVisible(false);
    paneRoom.setOpacity(1);
    handleRectangleExited();
  }

  @FXML
  private void onMousePressed(MouseEvent event) {
    // Records the initial mouse position when the yellow paper is pressed
    mouseAnchorX = event.getX();
    mouseAnchorY = event.getY();

    // Makes adjustments to the yellow paper's appearance and the room pane
    yellowPaper.setOpacity(0.7);
    revealLetterGrid.setVisible(false);
    paneRoom.setOpacity(1);
  }

  @FXML
  private void onMouseDragged(MouseEvent event) {
    // Updates the yellow paper's position based on the mouse's movement
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
    // Restores the yellow paper's opacity and reveals the letter grid
    yellowPaper.setOpacity(1);
    revealLetterGrid.setVisible(true);

    // Checks if the yellow paper is dropped within the correct bounds
    if (326 < event.getSceneX() && event.getSceneX() < 326 + 112) {
      if (538 < event.getSceneY() && event.getSceneY() < 538 + 144) {
        // Reveals the letter if the drop is successful
        isLetterReveal = true;
        setClue(letterCloseUp);
      }
    }

    // Dims the room pane after dropping
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
