module com.example.pollutiontracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires java.logging;
    requires java.sql;
    requires java.net.http;
    requires org.json;
    requires org.postgresql.jdbc;

    opens com.example.pollutiontracker to javafx.fxml;
    exports com.example.pollutiontracker;
}


