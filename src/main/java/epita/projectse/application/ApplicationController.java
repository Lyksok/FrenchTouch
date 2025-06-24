package epita.projectse.application;

import epita.projectse.domain.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ProgressBar;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.text.DecimalFormat;

public class ApplicationController implements IController {

    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label startTimer, endTimer, titleLabel, artistLabel;
    @FXML
    private Slider volume, timeSlider;
    @FXML
    private ImageView coverView;
    @FXML
    private Circle circleShape;

    private Thread rotationThread;

    private User artist;
    private MainLayoutController mainLayoutController;


    public User getCurrentArtist(){
        return artist;
    }
    public void setCurrentArtist(User artist){
        this.artist = artist;
    }

    /*
     * Helper to convert duration into desire String format
     * @param Duration to convert
     */
    private String durationToString (Duration duration){
        DecimalFormat decimalFormat = new DecimalFormat("##00");
        double rawSec = duration.toSeconds();
        double sec = (rawSec - (rawSec % 1)) % 60;
        double min = ((rawSec - (rawSec % 1)) / 60 - 0.49);
        min = min < 0 ? -min : min;
        return (decimalFormat.format(min) + ":" + decimalFormat.format(sec));
    }

    void initializeSlider() {
        volume.setMax(1);
        volume.setMin(0);
        volume.setValue(0.5);
    }

    void updateMusic(Song song) {
        if (this.circleShape != null) {
            circleShape.setVisible(true);
        }
        Song currentSong = song;
        titleLabel.setText(song.getTitle());
        String artistName = artist.getUsername();
        artistLabel.setText("by : " + artistName);
        if (currentSong != null) {
            Image image = currentSong.getCoverView();
            coverView.setImage(image);

            // Create a Circle for clipping
            double radius = Math.min(coverView.getFitWidth(), coverView.getFitHeight()) / 2;
            Circle coverCircle = new Circle(radius);
            coverCircle.setCenterX(radius);
            coverCircle.setCenterY(radius);

            // Set the clip to the ImageView
            coverView.setClip(coverCircle);
        }
    }

    @Override
    public void initializeContent(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
        mainLayoutController.getMediaPlayer().currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration duration, Duration t1) {
                progressBar.setProgress(mainLayoutController.getMediaPlayer().getCurrentTime().toSeconds() / mainLayoutController.getMediaPlayer().getTotalDuration().toSeconds());
                startTimer.setText(durationToString(mainLayoutController.getMediaPlayer().getCurrentTime()));

                //UPDATE each times but at least it works
                //If you find another place where it works please contact me at hugo.cohen@epita.fr
                endTimer.setText(durationToString(mainLayoutController.getMediaPlayer().getMedia().getDuration()));

            }
        });
        mainLayoutController.getMediaPlayer().statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == MediaPlayer.Status.PLAYING) {
                mainLayoutController.setPlaying(true);
                startRotationThread();
            } else {
                mainLayoutController.setPlaying(false);
            }
        });
    }

    private void startRotationThread() {
        if (rotationThread == null || !rotationThread.isAlive()) {
            rotationThread = new Thread(() -> {
                while (mainLayoutController.isPlaying()) {
                    try {
                        // Rotate the coverView
                        Platform.runLater(() -> coverView.setRotate(coverView.getRotate() + 2));
                        // Sleep for a short duration to control the rotation speed
                        Thread.sleep(20); // Adjust the sleep duration to control speed
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
            rotationThread.setDaemon(true);
            rotationThread.start();
        }
    }

    /*
     * Initialize each element in the applicationView
     * @param Duration to convert
     */
    public void initialize() {
        startTimer.setText("00:00");
        endTimer.setText("00:00");
        initializeSlider();

        volume.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (mainLayoutController.getMediaPlayer() != null) {
                    mainLayoutController.getMediaPlayer().setVolume(t1.doubleValue());
                }
            }
        });

        timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (mainLayoutController.getMediaPlayer() != null) {
                    mainLayoutController.getMediaPlayer().seek(new Duration (t1.doubleValue() / 100 * mainLayoutController.getMediaPlayer().getMedia().getDuration().toMillis()));
                }
            }
        });
    }

    /*
     * Bind to play button
     */
    @FXML
    void onPlayButtonClicked(ActionEvent event) {
        if (mainLayoutController != null) {
            mainLayoutController.getMediaPlayer().setVolume(volume.getValue());
            mainLayoutController.switchPauseSong();
        }
    }
    @FXML
    void onPrevButtonClicked(ActionEvent event) {
        if (mainLayoutController != null) {
            mainLayoutController.previousSong();
        }
    }

    @FXML
    void onNextButtonClicked(ActionEvent event) {
        if (mainLayoutController != null) {
            mainLayoutController.nextSong();
        }
    }

}
