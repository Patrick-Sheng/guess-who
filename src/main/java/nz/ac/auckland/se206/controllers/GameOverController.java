package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.enums.SceneState;
import nz.ac.auckland.se206.enums.Suspect;

public class GameOverController {

  public Rectangle rectBackToMenu;
  public Label timerLabel;

  @FXML ImageView imageAunt;
  @FXML ImageView imageGardener;
  @FXML ImageView imageNiece;
  @FXML TextArea feedbackTextArea;
  @FXML Label labelSubtitle;

  @FXML
  public void initialize() {
    imageAunt.setVisible(false);
    imageGardener.setVisible(false);
    imageNiece.setVisible(false);
    feedbackTextArea.setVisible(false);
  }

  public void setGameOverImage(Suspect suspect) {
    switch (suspect) {
      case AUNT:
        imageAunt.setVisible(true);
        feedbackTextArea.setVisible(true);
        labelSubtitle.setText("Here are some feedback based on your gameplay");
        break;
      case GARDENER:
        imageGardener.setVisible(true);
        labelSubtitle.setText("Try again next time!");
        break;
      case NIECE:
        imageNiece.setVisible(true);
        labelSubtitle.setText("Try again next time!");
        break;
    }
  }

  public void setFeedbackPrompt(String feedback) {
    feedbackTextArea.setText(feedback);
  }

  @FXML
  public void backToMenu() {
    App.setRoot(SceneState.MAIN_MENU, "Going back to main menu...");
  }
}
