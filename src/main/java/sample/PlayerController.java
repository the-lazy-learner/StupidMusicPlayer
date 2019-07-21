package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
/**
 *
 * @author Sudhansu
 */
public class PlayerController {
    @FXML
    private Button buttonStop, buttonPlay, buttonPause;
    private File mediaFile;
    private MediaPlayer mediaPlayer;

    public PlayerController() {
        try {
            openFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void stopMusic() {
        if (mediaPlayer.getCurrentTime() != Duration.ZERO) {
            mediaPlayer.stop();
            buttonStop.setDisable(true);
            buttonPause.setDisable(true);
            buttonPlay.setDisable(false);
        }
    }

    @FXML
    public void playMusic() {
        if ((mediaPlayer.getStatus() == MediaPlayer.Status.READY ||
                mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED ||
                mediaPlayer.getStatus() == MediaPlayer.Status.STOPPED)) {
            if (mediaPlayer.getCurrentTime() == mediaPlayer.getTotalDuration()) {
                mediaPlayer.seek(mediaPlayer.getStartTime());
            }
            mediaPlayer.play();
            buttonPlay.setDisable(true);
            buttonPause.setDisable(false);
            buttonStop.setDisable(false);
        }
    }

    @FXML
    public void pauseMusic() {
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            buttonPlay.setDisable(false);
            buttonPause.setDisable(true);
        }
    }

    @FXML
    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Media File");
        mediaFile = fileChooser.showOpenDialog(new Stage());
        initializePlayer();
    }

    private void initializePlayer() {
        try {
            mediaPlayer = new MediaPlayer(new Media(mediaFile.toURI().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
