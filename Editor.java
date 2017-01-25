package editor;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;


public class Editor extends Application {
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 500;
    private static final ArrayList<Character> chars = new ArrayList<>();
    static String fileName = null;

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Group textRoot = new Group();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        /* Initialize scrollBar, set dimensions and orientation */
        ScrollBar scrollBar = new ScrollBar();
        scrollBar.setPrefHeight(WINDOW_HEIGHT);
        scrollBar.setOrientation(Orientation.VERTICAL);
        scrollBar.setLayoutX(WINDOW_WIDTH - scrollBar.getLayoutBounds().getWidth());
        root.getChildren().add(scrollBar);

        /* Initiailze new textBody and populate with text, if a file has been opened */
        TextBody text = new TextBody(textRoot, WINDOW_WIDTH - (int) Math.round(scrollBar.getLayoutBounds().getWidth()));
        for (char chr : chars) {
            text.insert(chr);
        }
        text.clearUndo();
        text.render();
        root.getChildren().add(textRoot);

        /* Set parameters of scrollBar, and add a listener */
        scrollBar.setMin(WINDOW_HEIGHT);
        scrollBar.setMax(Math.max(text.getTextHeight(), WINDOW_HEIGHT));
        scrollBar.valueProperty().addListener((observable, oldValue, newValue) ->
                textRoot.setLayoutY(WINDOW_HEIGHT - newValue.doubleValue()));

        /* Initialize eventHandlers */
        KeyEventHandler keyEventHandler = new KeyEventHandler(text, scrollBar);
        MouseEventHandler mouseEventHandler = new MouseEventHandler(text);
        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);
        scene.setOnMouseClicked(mouseEventHandler);

        scene.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
            int width = newWidth.intValue() - (int) Math.round(scrollBar.getLayoutBounds().getWidth());
            scrollBar.setLayoutX(width);
            text.updateWidth(width);
            text.render();
        });
        if (fileName != null) {
            primaryStage.setTitle(fileName);
        } else {
            primaryStage.setTitle("Editor");
        }
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void read(String fileName) {
        int read;

        /* Attempts to open a file if a fileName is provided */
        try {
            File file = new File(fileName);
            if (file.exists()) {
                FileReader reader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(reader);
                while ((read = bufferedReader.read()) != -1) {
                    chars.add((char) read);
                }
                reader.close();
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found. Exception:" + fileNotFoundException);
        } catch (IOException ioException) {
            System.out.println("Error reading file. Exception: " + ioException);
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("No filename provided");
        } else {
            fileName = args[0];
            read(fileName);
        }
        launch(args);
    }
}