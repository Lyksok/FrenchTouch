package epita.projectse.application;

import epita.projectse.domain.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class AdminRoleElementController implements IController {
    @FXML
    private Label emailLabel;
    @FXML
    private Button acceptButton, denyButton;

    private long userId;
    private RequestRole requestType;

    private MainLayoutController mainLayoutController;

    public void initElt(long userId, RequestRole requestType) {
        this.userId = userId;
        this.requestType = requestType;
        User user = mainLayoutController.getDbAccess().select_user_by_id(userId);
        if (user == null) {
            System.out.println("User not found");
            return;
        }
        emailLabel.setText(user.getEmail());
        setupButtons();
    }

    private void setupButtons() {
        acceptButton.setOnAction(event -> {
            handleAccept();
            ((AdminPanelController)mainLayoutController.controllerCache.get("adminPanelView.fxml")).refreshVBox(requestType);
        });
        denyButton.setOnAction(event -> {
            handleDeny();
            ((AdminPanelController)mainLayoutController.controllerCache.get("adminPanelView.fxml")).refreshVBox(requestType);
        });
    }

    private void handleAccept() {
        switch (requestType) {
            case COLLABORATOR:
                Collaborator collaborator = new Collaborator(userId, false);
                mainLayoutController.getDbAccess().insert_collaborator(collaborator);
                mainLayoutController.getDbAccess().delete_request_to_collaborator(userId);
                break;
            case ARTIST:
                Artist artist = new Artist(userId, 0, null, null, false);
                mainLayoutController.getDbAccess().insert_artist(artist);
                mainLayoutController.getDbAccess().delete_request_to_artist(userId);
                break;
            case ADMIN:
                Admin admin = new Admin(userId);
                mainLayoutController.getDbAccess().insert_admin(admin);
                mainLayoutController.getDbAccess().delete_request_to_admin(userId);
                break;
        }
    }

    private void handleDeny() {
        switch (requestType) {
            case COLLABORATOR:
                mainLayoutController.getDbAccess().delete_request_to_collaborator(userId);
                break;
            case ARTIST:
                mainLayoutController.getDbAccess().delete_request_to_artist(userId);
                break;
            case ADMIN:
                mainLayoutController.getDbAccess().delete_request_to_admin(userId);
                break;
        }
    }

    @Override
    public void initializeContent(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
    }
}
