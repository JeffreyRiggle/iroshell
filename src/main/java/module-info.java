module ilusr.iroshell {
    requires java.base;
    requires java.xml;
    requires java.logging;
    requires javafx.controls;
    requires javafx.swing;
    requires javafx.fxml;
    requires ilusr.core;
    requires ilusr.logrunner;
    requires ilusr.persistencelib;
    requires spring.core;

    exports ilusr.iroshell.core;
    exports ilusr.iroshell.dockarea;
    exports ilusr.iroshell.dockarea.overlay;
    exports ilusr.iroshell.documentinterfaces;
    exports ilusr.iroshell.documentinterfaces.sdi;
    exports ilusr.iroshell.documentinterfaces.mdi;
    exports ilusr.iroshell.features;
    exports ilusr.iroshell.main;
    exports ilusr.iroshell.persistence;
    exports ilusr.iroshell.services;
    exports ilusr.iroshell.statusbar;
    exports ilusr.iroshell.toolbar;
}