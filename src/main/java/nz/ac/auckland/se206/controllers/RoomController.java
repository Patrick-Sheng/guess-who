package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.controllers.abstractions.MapController;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * interact with clues to investigate the crime scene.
 */
public class RoomController extends MapController {
  @FXML private Pane paneRoom;
  @FXML private Pane paneClue;
  @FXML private Label shoePrintLabel;
  @FXML private Label roseLabel;
  @FXML private ImageView bagOpen;
  @FXML private ImageView emeraldRoom;
  @FXML private ImageView emeraldRoomBackdrop;
  @FXML private ImageView letterCloseUp;
  @FXML private ImageView reportCloseUp;
  @FXML private ImageView medicineBill;
  @FXML private ImageView report;
  @FXML private ImageView yellowPaper;
  @FXML private ImageView arrowRight;
  @FXML private ImageView arrowLeft;
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
    onClue(event);
    setClue(bagOpen);
    report.setVisible(true);
    TextToSpeech.speak(
        "Opening the leather bag up, you see documents neatly tucked in the case, a book, and a"
            + " pair of gloves.");
  }

  @FXML
  public void letterReveal(MouseEvent event) {
    onClue(event);
    if (!isLetterReveal) {
      setClue(yellowPaper);
      revealLetterGrid.setVisible(true);
      TextToSpeech.speak(
          "You find a blank page lying on the ground, which you suspect contains a message - of"
              + " what, you're unsure.");
    } else {
      setClue(letterCloseUp);
    }
  }

  @FXML
  public void doorOpen(MouseEvent eventMouse) {
    onClue(eventMouse);
    setClue(emeraldRoom);

    GameState state = App.getGameState();
    emeraldRoomBackdrop.setVisible(true);
    roseLabel.setVisible(state.hasFoundRose());
    shoePrintLabel.setVisible(state.hasFoundFootprint());

    TextToSpeech.speak(
        "The oak doors open wide to reveal the room containing the once-treasured necklace.");

    Circle clip = new Circle(65);

    RadialGradient gradient =
        new RadialGradient(
            0,
            0,
            0.5,
            0.5,
            0.5,
            true,
            CycleMethod.NO_CYCLE,
            new Stop(0, Color.BLACK),
            new Stop(1, Color.TRANSPARENT));
    clip.setFill(gradient);

    emeraldRoom.setClip(clip);

    clip.setCenterX(eventMouse.getX() - emeraldRoom.getLayoutX());
    clip.setCenterY(eventMouse.getY() - emeraldRoom.getLayoutY());

    Scene scene = emeraldRoom.getScene();

    scene.addEventFilter(
        MouseEvent.MOUSE_MOVED,
        event -> {
          clip.setCenterX(event.getX() - emeraldRoom.getLayoutX());
          clip.setCenterY(event.getY() - emeraldRoom.getLayoutY());
        });

    Image torch = new Image(App.class.getResource("/images/clue/torch.png").toExternalForm());

    scene.setCursor(new ImageCursor(torch));

    ColorAdjust colorAdjust = new ColorAdjust();
    colorAdjust.setBrightness(-0.90);
    emeraldRoomBackdrop.setEffect(colorAdjust);
  }

  @FXML
  public void reportClose(MouseEvent event) {
    onClue(event);
    App.playCustomSoundEffect("paper.mp3");
    setClue(reportCloseUp);
    arrowLeft.setVisible(false);
    arrowRight.setVisible(true);
  }

  @FXML
  public void arrowClick(MouseEvent event) {
    ImageView clickedArrow = (ImageView) event.getSource();
    if (clickedArrow.getId().equals("arrowLeft")) {
      setClue(reportCloseUp);
      App.playCustomSoundEffect("paper.mp3");
      arrowLeft.setVisible(false);
      arrowRight.setVisible(true);
    } else {
      setClue(medicineBill);
      App.playCustomSoundEffect("paper.mp3");
      arrowLeft.setVisible(true);
      arrowRight.setVisible(false);
    }
  }

  private void onClue(MouseEvent event) {
    TextToSpeech.stopSpeak();

    if (!isClueClick) {
      isClueClick = true;
    }

    // Highlights the clicked clue by changing its style
    ImageView clickedImageView = (ImageView) event.getSource();
    clickedImageView.getStyleClass().remove("highlight-clue");
    clickedImageView.getStyleClass().add("lighter-clue");

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
    emeraldRoomBackdrop.setVisible(false);
    reportCloseUp.setVisible(false);
    medicineBill.setVisible(false);
    report.setVisible(false);
    yellowPaper.setVisible(false);
    revealLetterGrid.setVisible(false);
    roseLabel.setVisible(false);
    shoePrintLabel.setVisible(false);
    arrowLeft.setVisible(false);
    arrowRight.setVisible(false);

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

    Scene scene = paneRoom.getScene();
    scene.setCursor(Cursor.DEFAULT);
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
        App.playCustomSoundEffect("hiss.mp3");
      }
    }

    // Dims the room pane after dropping
    paneRoom.setOpacity(0.2);
  }

  @FXML
  private void clickShoePrint() {
    shoePrintLabel.setVisible(true);
    App.playCustomSoundEffect("mud.mp3");
    App.getGameState().setFoundFootprint(true);
  }

  @FXML
  private void clickRose() {
    roseLabel.setVisible(true);
    App.playCustomSoundEffect("prick.mp3");
    App.getGameState().setFoundRose(true);
  }
}
