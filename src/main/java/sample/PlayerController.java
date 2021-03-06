package sample;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

/**
 * @author Sudhansu
 */
public class PlayerController {
    @FXML
    private Button buttonStop, buttonPlay, buttonPause;
    private File mediaFile;
    private MediaPlayer mediaPlayer;
    @FXML
    private Label currentTime, totalDuration;
    @FXML
    private Slider progressBar, volumeSlider;

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
            mediaPlayer.seek(mediaPlayer.getStartTime());
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
        if (mediaPlayer != null
                && (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING))
            stopMusic();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Media File");
        mediaFile = fileChooser.showOpenDialog(new Stage());
        initializePlayer();
    }

    private String getTimeString(Duration duration) {
        if ((int) duration.toHours() == 0) {
            return String.format("%02d:%02d",
                    (int) duration.toMinutes(),
                    (int) duration.toSeconds() % 60);
        } else {
            return String.format("%4d:%02d:%02d",
                    (int) duration.toHours(),
                    (int) duration.toMinutes() % 60,
                    (int) duration.toSeconds() % 3600);
        }
    }

    private void initializePlayer() {
        try {
            mediaPlayer = new MediaPlayer(new Media(mediaFile.toURI().toString()));
            mediaPlayer.setOnReady(() -> {
                volumeSlider.setValue(100);
                volumeSlider.lookup(".track")
                        .setStyle("-fx-background-color: #3ba8e1;");
                mediaPlayer.setVolume(volumeSlider.getValue() / 100);
                currentTime.textProperty().bind(
                        Bindings.createStringBinding(() -> {
                            Duration time = mediaPlayer.getCurrentTime();
                            return getTimeString(time);
                        }, mediaPlayer.currentTimeProperty()));
                totalDuration.setText(getTimeString(mediaPlayer.getTotalDuration()));
                progressBar.maxProperty().bind(
                        Bindings.createDoubleBinding(
                                () -> mediaPlayer.getTotalDuration().toSeconds(),
                                mediaPlayer.totalDurationProperty()
                        )
                );
                final double MIN_CHANGE = 0.5;
                progressBar.valueChangingProperty().addListener((
                        observable, wasChanging, isChanging
                ) -> {
                    if (!isChanging) {
                        mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
                    }
                });
                progressBar.valueProperty().addListener((
                        observable, oldTime, newTime
                ) -> {
                    if (!progressBar.isValueChanging()) {
                        double currentTimeSecs = mediaPlayer
                            .getCurrentTime().toSeconds();
                        if (Math.abs(currentTimeSecs - newTime.doubleValue())
                                > MIN_CHANGE) {
                            mediaPlayer.seek(Duration.seconds(newTime.doubleValue()));
                        }
                    }
                    int newTimeStyle =
                        (int) (newTime.doubleValue() / mediaPlayer
                        .getTotalDuration().toSeconds() * 100);
                    String style = String.format(
                            "-fx-background-color: " +
                            "linear-gradient(to right, #3ba8e1 %d%%, #969696 %d%%);",
                            newTimeStyle, newTimeStyle
                    );
                    progressBar.lookup(".track").setStyle(style);
                });
                mediaPlayer.currentTimeProperty().addListener((
                        observable, oldTime, newTime
                ) -> {
                    if (!progressBar.isValueChanging()) {
                        progressBar.setValue(newTime.toSeconds());
                    }
                });
                volumeSlider.valueChangingProperty().addListener((
                        observableValue, wasChanging, isChanging
                ) -> {
                    if (!isChanging) {
                        mediaPlayer.setVolume(volumeSlider.getValue() * 0.01d);
                    }
                });
                volumeSlider.valueProperty().addListener((
                        observable, oldValue, newValue
                ) -> volumeSlider.lookup(".track").setStyle(String.format(
                        "-fx-background-color: " +
                        "linear-gradient(to top, #3ba8e1 %d%%, #969696 %d%%);",
                        newValue.intValue(), newValue.intValue()
                )));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
