package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.abstractions.ButtonController;
import nz.ac.auckland.se206.enums.FeedbackType;
import nz.ac.auckland.se206.enums.SceneState;
import nz.ac.auckland.se206.enums.SuccessfulPoint;
import nz.ac.auckland.se206.enums.Suspect;
import org.json.JSONArray;
import org.json.JSONObject;

public class GameOverController extends ButtonController {
  @FXML private ImageView continueOther;
  @FXML private ImageView continueAunt;
  @FXML private ImageView imageAunt;
  @FXML private ImageView imageGardener;
  @FXML private ImageView imageNiece;
  @FXML private ImageView outOfTime;
  @FXML private ImageView point1Image;
  @FXML private ImageView point2Image;
  @FXML private ImageView point3Image;
  @FXML private ImageView point4Image;
  @FXML private ImageView point5Image;
  @FXML private GridPane feedbackGrid;
  @FXML private Label labelSubtitle;
  @FXML private Text reasoning;
  @FXML private Text rating;
  @FXML private Text point1Text;
  @FXML private Text point2Text;
  @FXML private Text point3Text;
  @FXML private Text point4Text;
  @FXML private Text point5Text;

  private int attempts = 0;

  @FXML
  public void initialize() {
    // Disable image when first enter the game over state
    imageAunt.setVisible(false);
    imageGardener.setVisible(false);
    imageNiece.setVisible(false);
    outOfTime.setVisible(false);
    feedbackGrid.setVisible(false);
  }

  public void setGameOverImage(Suspect suspect) {
    // Displays the corresponding image, shows the feedback text area if necessary, and updates the
    // subtitle.
    switch (suspect) {
      case AUNT:
        imageAunt.setVisible(true);
        feedbackGrid.setVisible(true);

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
    if (attempts > 10) {
      System.err.println("Something went horribly wrong, returning!");
      return;
    }

    try {
      JSONObject jsonObject = new JSONObject(feedback);

      String reasoningStr = jsonObject.getString("reason");
      reasoning.setText(reasoningStr);

      int ratingAmount = jsonObject.getInt("rating");
      rating.setText(ratingAmount + " Out Of 5");

      JSONArray points = jsonObject.getJSONArray("points");

      Image metCriteriaImage =
          new Image(App.class.getResource("/images/buttons/checked.png").toExternalForm());
      Image notMetCriteriaImage =
          new Image(App.class.getResource("/images/buttons/unchecked.png").toExternalForm());

      for (int i = 0; i < points.length(); i++) {
        JSONObject point = points.getJSONObject(i);

        String typeString = point.getString("type");
        FeedbackType type = feedbackFromString(typeString);

        String statusString = point.getString("status");
        boolean status = successfulFromString(statusString) == SuccessfulPoint.Yes;

        String action = point.getString("action");

        Text text = getTextFromEnum(type);

        if (text != null) {
          text.setText(action);
        }

        ImageView image = getCheckFromEnum(type);

        if (image != null) {
          image.setImage(status ? metCriteriaImage : notMetCriteriaImage);
        }
      }
    } catch (Exception e) {
      System.err.println("Error parsing JSON: " + e.getMessage());
      System.err.println("Full JSON: " + feedback);
      attempts += 1;
      App.getGameState().setAiProxyConfig();
    }
  }

  public Text getTextFromEnum(FeedbackType type) {
    switch (type) {
      case PastLover:
        return point1Text;
      case FinanceProblem:
        return point2Text;
      case FramedGardener:
        return point3Text;
      case LoveLetter:
        return point4Text;
      case PerfumeScent:
        return point5Text;
      default:
        return null;
    }
  }

  public ImageView getCheckFromEnum(FeedbackType type) {
    switch (type) {
      case PastLover:
        return point1Image;
      case FinanceProblem:
        return point2Image;
      case FramedGardener:
        return point3Image;
      case LoveLetter:
        return point4Image;
      case PerfumeScent:
        return point5Image;
      default:
        return null;
    }
  }

  public static SuccessfulPoint successfulFromString(String input) {
    for (SuccessfulPoint enumValue : SuccessfulPoint.values()) {
      if (enumValue.name().equalsIgnoreCase(input)) {
        return enumValue;
      }
    }
    throw new IllegalArgumentException("No success enum constant matches the input: " + input);
  }

  public static FeedbackType feedbackFromString(String input) {
    for (FeedbackType enumValue : FeedbackType.values()) {
      if (enumValue.name().equalsIgnoreCase(input)) {
        return enumValue;
      }
    }
    throw new IllegalArgumentException("No feedback enum constant matches the input: " + input);
  }

  @FXML
  public void backToMenu() {
    App.setRoot(SceneState.MAIN_MENU, "Going back to main menu...");
  }
}
