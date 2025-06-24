package epita.projectse.application;

import epita.projectse.domain.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.List;

enum RequestRole {
    ARTIST,
    COLLABORATOR,
    ADMIN
}

enum RequestReq {
    COLLABORATOR_REQUEST,
    ARTIST_REQUEST
}

public class AdminPanelController implements IController {

    private MainLayoutController mainLayoutController;

    @FXML
    private VBox collaboratorScrollPane, artistScrollPane, adminScrollPane, artistRequestScrollPane, collaboratorRequestScrollPane;


    @Override
    public void initializeContent(MainLayoutController mainLayoutController) {
        System.out.println("Initializing content...");
        this.mainLayoutController = mainLayoutController;

        loadRequestsInBackground(collaboratorScrollPane, RequestRole.COLLABORATOR);
        loadRequestsInBackground(artistScrollPane, RequestRole.ARTIST);
        loadRequestsInBackground(adminScrollPane, RequestRole.ADMIN);
        loadRequestsInBackground(artistRequestScrollPane, RequestReq.ARTIST_REQUEST);
        loadRequestsInBackground(collaboratorRequestScrollPane, RequestReq.COLLABORATOR_REQUEST);
    }

    private void loadRequestsInBackground(VBox vBox, RequestRole requestType) {
        Task<List<?>> task = new Task<>() {
            @Override
            protected List<?> call() throws Exception {
                switch (requestType) {
                    case COLLABORATOR:
                        return mainLayoutController.getDbAccess().select_request_to_collaborator_all();
                    case ARTIST:
                        return mainLayoutController.getDbAccess().select_request_to_artist_all();
                    case ADMIN:
                        return mainLayoutController.getDbAccess().select_request_to_admin_all();
                    default:
                        throw new IllegalArgumentException("Unknown request type");
                }
            }
        };

        task.setOnSucceeded(event -> {
            List<?> requests = task.getValue();
            loadRequests(vBox, requests, requestType);
        });

        task.setOnFailed(event -> {
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void loadRequestsInBackground(VBox vBox, RequestReq requestType) {
        Task<List<?>> task = new Task<>() {
            @Override
            protected List<?> call() throws Exception {
                switch (requestType) {
                    case COLLABORATOR_REQUEST:
                        return mainLayoutController.getDbAccess().select_collaborator_request_all();
                    case ARTIST_REQUEST:
                        return mainLayoutController.getDbAccess().select_artist_request_all();
                    default:
                        throw new IllegalArgumentException("Unknown request type");
                }
            }
        };

        task.setOnSucceeded(event -> {
            List<?> requests = task.getValue();
            loadRequests(vBox, requests, requestType);
        });

        task.setOnFailed(event -> {
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void loadRequests(VBox vBox, List<?> requests, RequestRole requestType) {
        vBox.getChildren().clear(); // Clear existing children
        if (requests != null && !requests.isEmpty()) {
            System.out.println(requestType + " requests: " + requests.size());
            for (Object request : requests) {
                long userId = getUserId(request);
                addRequestToVBox(vBox, userId, requestType);
            }
        }
    }

    private void loadRequests(VBox vBox, List<?> requests, RequestReq requestType) {
        vBox.getChildren().clear(); // Clear existing children
        if (requests != null && !requests.isEmpty()) {
            System.out.println(requestType + " requests: " + requests.size());
            for (Object request : requests) {
                long requestId = getRequestId(request);
                addRequestToVBox(vBox, requestId, requestType);
            }
        }
    }

    private long getUserId(Object request) {
        if (request instanceof RequestToCollaborator) {
            return ((RequestToCollaborator) request).getUser_id();
        } else if (request instanceof RequestToArtist) {
            return ((RequestToArtist) request).getUser_id();
        } else if (request instanceof RequestToAdmin) {
            return ((RequestToAdmin) request).getUser_id();
        }
        throw new IllegalArgumentException("Unknown request type");
    }

    private long getRequestId(Object request) {
        if (request instanceof CollaboratorRequest) {
            return ((CollaboratorRequest) request).getId();
        } else if (request instanceof ArtistRequest) {
            return ((ArtistRequest) request).getId();
        }
        throw new IllegalArgumentException("Unknown request type");
    }

    private void addRequestToVBox(VBox vBox, long userId, RequestRole requestType) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("adminViewRoleElement.fxml"));
        try {
            BorderPane viewElt = loader.load();
            AdminRoleElementController controller = loader.getController();
            controller.initializeContent(mainLayoutController);
            controller.initElt(userId, requestType);
            vBox.getChildren().add(viewElt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addRequestToVBox(VBox vBox, long requestId, RequestReq requestType) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("adminViewRequestElement.fxml"));
        try {
            BorderPane viewElt = loader.load();
            AdminRequestElementController controller = loader.getController();
            controller.initializeContent(mainLayoutController);
            controller.initElt(requestId, requestType);
            vBox.getChildren().add(viewElt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshVBox(RequestRole requestType) {
        switch (requestType) {
            case COLLABORATOR:
                loadRequestsInBackground(collaboratorScrollPane, RequestRole.COLLABORATOR);
                break;
            case ARTIST:
                loadRequestsInBackground(artistScrollPane, RequestRole.ARTIST);
                break;
            case ADMIN:
                loadRequestsInBackground(adminScrollPane, RequestRole.ADMIN);
                break;
        }
    }

    @FXML
    public void refreshAll(){
        loadRequestsInBackground(collaboratorScrollPane, RequestRole.COLLABORATOR);
        loadRequestsInBackground(artistScrollPane, RequestRole.ARTIST);
        loadRequestsInBackground(adminScrollPane, RequestRole.ADMIN);
        loadRequestsInBackground(artistRequestScrollPane, RequestReq.ARTIST_REQUEST);
        loadRequestsInBackground(collaboratorRequestScrollPane, RequestReq.COLLABORATOR_REQUEST);
    }

    public void refreshVBox(RequestReq requestType) {
        switch (requestType) {
            case COLLABORATOR_REQUEST:
                loadRequestsInBackground(collaboratorRequestScrollPane, RequestReq.COLLABORATOR_REQUEST);
                break;
            case ARTIST_REQUEST:
                loadRequestsInBackground(artistRequestScrollPane, RequestReq.ARTIST_REQUEST);
                break;
        }
    }
}
