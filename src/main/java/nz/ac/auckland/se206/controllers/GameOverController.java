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
    // Disable image when first enter the game over state
    imageAunt.setVisible(false);
    imageGardener.setVisible(false);
    imageNiece.setVisible(false);
    outOfTime.setVisible(false);
    feedbackTextArea.setVisible(false);
    feedbackTextArea.setText("Loading...");
  }

  public void setGameOverImage(Suspect suspect) {
    // Displays the corresponding image, shows the feedback text area if necessary, and updates the
    // subtitle.
    switch (suspect) {
      case AUNT:
        imageAunt.setVisible(true);
        feedbackTextArea.setVisible(true);

        continueOther.setVisible(false);
        continueAunt.setVisible(true);

        // Choosing the actual crime theft will give feedback on user's explanation
        labelSubtitle.setText("Here is some feedback based on your explanation");
        break;
      case GARDENER:
        imageGardener.setVisible(true);

        continueOther.setVisible(true);
        continueAunt.setVisible(false);

        // Choosing the gardener will not give feedback on user's explanation
        labelSubtitle.setText("Try again next time!");
        break;
      case NIECE:
        imageNiece.setVisible(true);

        continueOther.setVisible(true);
        continueAunt.setVisible(false);

        // Choosing the niece will not give feedback on user's explanation
        labelSubtitle.setText("Try again next time!");
        break;
      case OUT_OF_TIME:
        // Player lost staight away if the time run out
        outOfTime.setVisible(true);

        continueOther.setVisible(true);
        continueAunt.setVisible(false);

        labelSubtitle.setText("Try to be faster next time!");
        break;
      default:
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
