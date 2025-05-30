module cz.cvut.fel.pjv {
    requires java.datatransfer;
    requires java.desktop;
    requires java.logging;
    requires com.fasterxml.jackson.databind;
    exports cz.cvut.fel.pjv;
    exports cz.cvut.fel.pjv.logic;
    exports cz.cvut.fel.pjv.gui;
}