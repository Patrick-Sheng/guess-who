package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.enums.SceneState;
import nz.ac.auckland.se206.enums.Suspect;

public class GameOverController {

  @FXML ImageView imageAunt;
  @FXML ImageView imageGardener;
  @FXML ImageView imageNiece;

  @FXML
  public void initialize() {
    imageAunt.setVisible(false);
    imageGardener.setVisible(false);
    imageNiece.setVisible(false);
  }

  public void setGameOverImage(Suspect suspect) {
    switch (suspect) {
      case AUNT:
        imageAunt.setVisible(true);
        break;
      case GARDENER:
        imageGardener.setVisible(true);
        break;
      case NIECE:
        imageNiece.setVisible(true);
        break;
    }
  }

  @FXML
  public void backToMenu() {
    App.setRoot(SceneState.MAIN_MENU);
  }
}
