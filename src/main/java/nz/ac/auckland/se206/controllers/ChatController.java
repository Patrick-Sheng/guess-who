package nz.ac.auckland.se206.controllers;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import nz.ac.auckland.se206.controllers.abstractions.MapController;

/**
 * Controller class for the chat view. Handles user interactions and communication with the GPT
 * model via the API proxy.
 */
public class ChatController extends MapController {
  public ImageView imageGardener;
  public ImageView imageNiece;
  public ImageView imageAunt;

  public Pane childRoom;
  public Pane auntieRoom;
  public Pane gardenerRoom;
  public Pane guessRoom;

  public GridPane gridMap;
  public Label labelMap;
}
