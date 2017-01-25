package editor;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class MouseEventHandler implements EventHandler<MouseEvent> {
    private TextBody text;

    MouseEventHandler(TextBody text) {
        this.text = text;
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        int mouseX = (int) Math.round(mouseEvent.getX());
        int mouseY = (int) Math.round(mouseEvent.getY());
        text.placeCursor(Math.round(mouseX), Math.round(mouseY));
        text.render();
    }
}
