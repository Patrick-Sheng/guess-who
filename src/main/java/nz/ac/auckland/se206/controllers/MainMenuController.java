package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;

public class MainMenuController {

  @FXML private Rectangle rectStartGame;
  @FXML private Rectangle rectSettings;
  @FXML private Rectangle rectExit;

  private static GameStateContext context;

  @FXML
  public void initialize() {
    context = App.getGameStateContext();
  }

  @FXML
  private void handleRectangleClick(MouseEvent event) throws IOException {
    Rectangle clickedRectangle = (Rectangle) event.getSource();
    context.handleRectangleClick(event, clickedRectangle.getId());
  }
}
