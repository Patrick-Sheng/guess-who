package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.abstractions.MapController;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController extends MapController {
  public Label labelMap;

  public Pane gardenerRoom;
  public Pane auntieRoom;
  public Pane childRoom;
  public Pane guessRoom;

  public ImageView emeraldRoom;

  public ImageView letterCloseUp;
  public ImageView report;
  public ImageView exitClue;

  public Button toGuessRoomButton;

  public ImageView letter;
  public ImageView door;
  public ImageView bag;
  public GridPane gridMap;

  @FXML private Pane paneRoom;
  @FXML private Pane paneClue;
  @FXML private ImageView bagOpen;

  @FXML
  public void initialize() {
    super.initialize();
    paneClue.setVisible(false);
  }

  @FXML
  private void onClue(MouseEvent event) {
    ImageView clickedImageView = (ImageView) event.getSource();
    String ImageViewID = clickedImageView.getId();

    switch (ImageViewID) {
      case "bag":
        bagOpen.setVisible(true);
        break;
      case "letter":
        bagOpen.setVisible(true);
        break;
      case "door":
        bagOpen.setVisible(true);
        break;
      case "report":
        bagOpen.setVisible(true);
        break;
    }
    paneRoom.setOpacity(0.2);
    paneClue.setVisible(true);
    handleRectangleEntered();

    App.increaseObjects();
    checkButton();
  }

  @FXML
  private void onExitClue() {
    bagOpen.setVisible(false);
    paneClue.setVisible(false);
    paneRoom.setOpacity(1);
    handleRectangleExited();
  }
}
