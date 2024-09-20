package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.abstractions.ButtonController;
import nz.ac.auckland.se206.enums.SceneState;
import nz.ac.auckland.se206.enums.Suspect;

public class GameOverController extends ButtonController {
  @FXML private ImageView continueOther;
  @FXML private ImageView continueAunt;
  @FXML private ImageView imageAunt;
  @FXML private ImageView imageGardener;
  @FXML private ImageView imageNiece;
  @FXML private ImageView outOfTime;
  @FXML private TextArea feedbackTextArea;
  @FXML private Label labelSubtitle;

  @FXML
  public void initialize() {
    imageAunt.setVisible(false);
    imageGardener.setVisible(false);
    imageNiece.setVisible(false);
    outOfTime.setVisible(false);
    feedbackTextArea.setVisible(false);
    feedbackTextArea.setText("Loading...");
  }

  public void setGameOverImage(Suspect suspect) {
    switch (suspect) {
      case AUNT:
        imageAunt.setVisible(true);
        feedbackTextArea.setVisible(true);

        continueOther.setVisible(false);
        continueAunt.setVisible(true);

        labelSubtitle.setText("Here is some feedback based on your explanation");
        break;
      case GARDENER:
        imageGardener.setVisible(true);

        continueOther.setVisible(true);
        continueAunt.setVisible(false);

        labelSubtitle.setText("Try again next time!");
        break;
      case NIECE:
        imageNiece.setVisible(true);

        continueOther.setVisible(true);
        continueAunt.setVisible(false);

        labelSubtitle.setText("Try again next time!");
        break;
      case OUT_OF_TIME:
        outOfTime.setVisible(true);

        continueOther.setVisible(true);
        continueAunt.setVisible(false);

        labelSubtitle.setText("Try to be faster next time!");
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
