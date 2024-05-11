package org.example.networkmathgame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.IOException;
import java.net.URL;

public class HelloApplication extends Application {
    private MediaPlayer mediaPlayer;

    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300); // Adjusted size
        stage.setTitle("Math Game Client");
        stage.setScene(scene);

        // Setup and play background music
        playBackgroundMusic();

        stage.show();
    }

    private void playBackgroundMusic() {
        // Find the audio file URL
        URL musicPath = HelloApplication.class.getResource("/background_music.mp3");
        if (musicPath == null) {
            System.out.println("Failed to load the background music file.");
            return;
        }

        // Create a Media object for the audio file
        Media media = new Media(musicPath.toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Set to loop indefinitely
        mediaPlayer.play(); // Start playing the music
    }

    public static void main(String[] args) {
        launch(args);
    }

}
