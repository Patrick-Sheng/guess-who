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

  /**
   * Initializes the room controller, setting up initial states and speaking the introductory
   * message.
   */
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

  /**
   * Handles the event of clicking on the bag to reveal clues inside.
   *
   * @param event the MouseEvent triggered by the user clicking on the bag
   */
  @FXML
  public void bagOpen(MouseEvent event) {
    // Call onClue method to handle any clue-related actions for the event
    onClue(event);
    setClue(bagOpen);
    // Make the report visible to the player
    report.setVisible(true);
    TextToSpeech.speak(
        "Opening the leather bag up, you see documents neatly tucked in the case, a book, and a"
            + " pair of gloves.");
  }

  /**
   * Reveals the letter clue when clicked, and displays its close-up view if already revealed.
   *
   * @param event the MouseEvent triggered by the user clicking on the letter
   */
  @FXML
  public void letterReveal(MouseEvent event) {
    onClue(event);
    // Check if the letter has not been revealed yet
    if (!isLetterReveal) {
      setClue(yellowPaper);
      revealLetterGrid.setVisible(true);
      // Use text-to-speech to describe the found clue to the player
      TextToSpeech.speak(
          "You find a blank page lying on the ground, which you suspect contains a message - of"
              + " what, you're unsure.");
    } else {
      // If the letter has already been revealed, set the clue to the close-up of the letter
      setClue(letterCloseUp);
    }
  }

  /**
   * Handles the event of opening the door to reveal the emerald room, applying a spotlight effect.
   *
   * @param eventMouse the MouseEvent triggered by the user clicking on the door
   */
  @FXML
  public void doorOpen(MouseEvent eventMouse) {
    // Trigger clue interaction logic
    onClue(eventMouse);
    // Set the current clue to the emerald room
    setClue(emeraldRoom);
    // Announce the opening of the door using text-to-speech
    TextToSpeech.speak(
        "The oak doors open wide to reveal the room containing the once-treasured necklace.");

    Circle clip = new Circle(50);
    // Define a radial gradient for the clip, transitioning from black to transparent
    RadialGradient gradient =
        new RadialGradient(
            0,
            0,
            0.5,
            0.5,
            0.5,
            true,
            CycleMethod.NO_CYCLE, // No repeating of the gradient
            new Stop(0, Color.BLACK),
            new Stop(1, Color.TRANSPARENT));
    clip.setFill(gradient); // Apply the gradient to the clip
    // Set the clipping mask on the emerald room
    emeraldRoom.setClip(clip);

    clip.setCenterX(eventMouse.getX() - emeraldRoom.getLayoutX());
    clip.setCenterY(eventMouse.getY() - emeraldRoom.getLayoutY());

    Scene scene = emeraldRoom.getScene();

    // Get the current scene for further mouse event handling
    // Add a mouse move event filter to update the clip's position
    scene.addEventFilter(
        MouseEvent.MOUSE_MOVED,
        event -> {
          // Update the center position of the clip with the mouse coordinates
          clip.setCenterX(event.getX() - emeraldRoom.getLayoutX());
          clip.setCenterY(event.getY() - emeraldRoom.getLayoutY());
        });
    // Load the torch image to use as a cursor
    Image torch = new Image(App.class.getResource("/images/clue/torch.png").toExternalForm());

    scene.setCursor(new ImageCursor(torch));

    // Create a color adjustment effect to darken the emerald room backdrop
    ColorAdjust colorAdjust = new ColorAdjust();
    colorAdjust.setBrightness(-0.90);
    emeraldRoomBackdrop.setEffect(colorAdjust);
  }

  /**
   * Switches between pages of the report document when an arrow is clicked.
   *
   * @param event the MouseEvent triggered by clicking on the left or right arrow
   */
  @FXML
  public void reportClose(MouseEvent event) {
    onClue(event);
    App.playCustomSoundEffect("paper.mp3");
    setClue(reportCloseUp);
    arrowLeft.setVisible(false);
    arrowRight.setVisible(true);
  }

  /**
   * Switches between pages of the report document when an arrow is clicked.
   *
   * @param event the MouseEvent triggered by clicking on the left or right arrow
   */
  @FXML
  public void arrowClick(MouseEvent event) {
    ImageView clickedArrow = (ImageView) event.getSource();

    // Check if the left arrow was clicked
    if (clickedArrow.getId().equals("arrowLeft")) {
      // Set the clue to a close-up report when the left arrow is clicked
      setClue(reportCloseUp);
      App.playCustomSoundEffect("paper.mp3");
      arrowLeft.setVisible(false);
      arrowRight.setVisible(true);
    } else {
      // If the left arrow was not clicked, set the clue to the medicine bill
      setClue(medicineBill);
      App.playCustomSoundEffect("paper.mp3");
      // Show the left arrow and hide the right arrow
      arrowLeft.setVisible(true);
      arrowRight.setVisible(false);
    }
  }

  /**
   * Handles the event when a clue is clicked. Stops any ongoing speech, highlights the selected
   * clue, and dims the room pane to focus on the clue. Also updates the game state to reflect that
   * a clue has been interacted with.
   *
   * @param event the MouseEvent triggered by clicking on a clue
   */
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

  /**
   * Sets the specified clue image as visible while hiding all other clue-related images and labels.
   * Also displays the backdrop for the emerald room if the selected clue is the emerald.
   *
   * @param image the ImageView of the clue to be displayed
   */
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
    if (image == emeraldRoom) {
      emeraldRoomBackdrop.setVisible(true);
    }
  }

  /** Shows the room and hides the clue pane, restoring the default cursor and room visibility. */
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

  /**
   * Records the initial position when the user presses on the yellow paper clue.
   *
   * @param event the MouseEvent triggered by pressing the yellow paper
   */
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

  /**
   * Drags the yellow paper clue across the screen as the mouse moves, within specified bounds.
   *
   * @param event the MouseEvent triggered by dragging the yellow paper
   */
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
   * Handles mouse drop events, revealing the letter grid if the yellow paper is dropped correctly.
   *
   * @param event the MouseEvent triggered by releasing the yellow paper
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

  /** Displays a label and plays a sound effect when the shoe print clue is clicked. */
  @FXML
  private void clickShoePrint() {
    shoePrintLabel.setVisible(true);
    App.playCustomSoundEffect("mud.mp3");
  }

  /** Displays a label and plays a sound effect when the rose clue is clicked. */
  @FXML
  private void clickRose() {
    roseLabel.setVisible(true);
    App.playCustomSoundEffect("prick.mp3");
  }
}
