module com.videoreg.videoreg {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires org.bytedeco.javacv;
    requires org.bytedeco.opencv;
    requires org.bytedeco.ffmpeg;
    requires javafx.swing;

    opens com.videoreg.videoreg to javafx.fxml;
    opens com.videoreg.videoreg.video to javafx.fxml;
    opens com.videoreg.videoreg.model to javafx.fxml;

    exports com.videoreg.videoreg;
}
