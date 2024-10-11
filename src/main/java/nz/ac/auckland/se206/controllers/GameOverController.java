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

  /**
   * Initializes the GameOverController. Sets all images and feedback grids to invisible at the
   * start of the game over state.
   */
  @FXML
  public void initialize() {
    // Disable image when first enter the game over state
    imageAunt.setVisible(false);
    imageGardener.setVisible(false);
    imageNiece.setVisible(false);
    outOfTime.setVisible(false);
    feedbackGrid.setVisible(false);
  }

  /**
   * Sets the game over image and subtitle based on the selected suspect.
   *
   * @param suspect The suspect that triggered the game over state.
   */
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

  /**
   * Sets the feedback prompt based on the JSON feedback received after the game.
   *
   * @param feedback A JSON string containing the feedback information.
   */
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

      int correct = 0;
      int pointCount = points.length();

      for (int i = 0; i < pointCount; i++) {
        JSONObject point = points.getJSONObject(i);

        String typeString = point.getString("type");
        FeedbackType type = feedbackFromString(typeString);

        String statusString = point.getString("status");
        boolean status = successfulFromString(statusString) == SuccessfulPoint.Yes;

        if (status) {
          correct++;
        }

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

      if (pointCount == 5) {
        ratingAmount = correct;
        rating.setText(ratingAmount + " Out Of 5");
      }
    } catch (Exception e) {
      System.err.println("Error parsing JSON: " + e.getMessage());
      System.err.println("Full JSON: " + feedback);
      attempts += 1;
      App.getGameState().setAiProxyConfig();
    }
  }

  /**
   * Retrieves the corresponding Text object for a given FeedbackType.
   *
   * @param type The FeedbackType for which to retrieve the Text object.
   * @return The corresponding Text object, or null if none exists.
   */
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

  /**
   * Retrieves the corresponding ImageView for a given FeedbackType.
   *
   * @param type The FeedbackType for which to retrieve the ImageView.
   * @return The corresponding ImageView, or null if none exists.
   */
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

  /**
   * Converts a string input to the corresponding SuccessfulPoint enum.
   *
   * @param input The input string to convert.
   * @return The corresponding SuccessfulPoint enum.
   * @throws IllegalArgumentException if no matching enum constant is found.
   */
  public static SuccessfulPoint successfulFromString(String input) {
    for (SuccessfulPoint enumValue : SuccessfulPoint.values()) {
      if (enumValue.name().equalsIgnoreCase(input)) {
        return enumValue;
      }
    }
    throw new IllegalArgumentException("No success enum constant matches the input: " + input);
  }

  /**
   * Converts a string input to the corresponding FeedbackType enum.
   *
   * @param input The input string to convert.
   * @return The corresponding FeedbackType enum.
   * @throws IllegalArgumentException if no matching enum constant is found.
   */
  public static FeedbackType feedbackFromString(String input) {
    for (FeedbackType enumValue : FeedbackType.values()) {
      if (enumValue.name().equalsIgnoreCase(input)) {
        return enumValue;
      }
    }
    throw new IllegalArgumentException("No feedback enum constant matches the input: " + input);
  }

  /** Navigates back to the main menu. */
  @FXML
  public void backToMenu() {
    App.setRoot(SceneState.MAIN_MENU, "Going back to main menu...");
  }
}
