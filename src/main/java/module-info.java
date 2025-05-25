module epita.projectse {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;
    requires javafx.media;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;
    requires kotlin.stdlib;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires javafx.swing;

    opens epita.projectse to javafx.fxml;
    opens epita.projectse.application to javafx.fxml;
    opens epita.projectse.domain to org.hibernate.orm.core, com.fasterxml.jackson.databind;

    exports epita.projectse;
    exports epita.projectse.business_logic;
    exports epita.projectse.application to javafx.fxml;
    exports epita.projectse.domain to org.hibernate.orm.core;

}
