package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.enums.SceneState;

public class GameOverController {
    @FXML
    public void backToMenu() {
        App.setRoot(SceneState.MAIN_MENU);
    }
}
