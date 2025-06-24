package epita.projectse.application;

import epita.projectse.domain.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.List;

public class QueueController implements IController {

    @FXML
    private VBox songVbox;

    private MainLayoutController mainLayoutController;

    @Override
    public void initializeContent(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
    }

    public void initializeContent(){
        loadRequestsInBackground(songVbox);
    }

    private void loadRequestsInBackground(Pane box) {
        Task<List<Song>> task = new Task<>() {
            @Override
            protected List<Song> call() {
                return mainLayoutController.getSongQueue().subList(mainLayoutController.getSongIndex(), mainLayoutController.getSongQueue().size());
            }
        };

        task.setOnSucceeded(event -> {
            List<Song> requests = task.getValue();
            loadRequests(box, requests);
        });

        task.setOnFailed(event -> {
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void loadRequests(Pane box, List<Song> requests) {
        box.getChildren().clear(); // Clear existing children
        if (requests != null && !requests.isEmpty()) {
            for (Song request : requests) {
                addSongToBox((VBox) box, request);
            }
        }
    }

    private void addSongToBox(VBox vBox, Song song) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("songElement.fxml"));
        try {
            Pane viewElt = loader.load();
            SongElementController controller = loader.getController();
            controller.initializeContent(mainLayoutController);
            controller.initElt(song);
            if(vBox.getChildren().isEmpty())
            {
                controller.setFirstSongInQueue();
            }
            vBox.getChildren().add(viewElt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshVBox() {
        loadRequestsInBackground(songVbox);
    }
}


