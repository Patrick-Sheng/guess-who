package nz.ac.auckland.se206.controllers.abstractions;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;

import java.io.IOException;

public abstract class ClickableController {
    private static GameStateContext context;

    @FXML
    public void initialize() {
        App.playMusic(getMusic());

        context = App.getGameStateContext();

        postInitialize();
    }

    public abstract String getMusic();

    public void postInitialize() {}

    @FXML
    private void handleRectangleClick(MouseEvent event) throws IOException {
        ImageView clickedRectangle = (ImageView) event.getSource();
        context.handleRectangleClick(event, clickedRectangle.getId());
    }
}
