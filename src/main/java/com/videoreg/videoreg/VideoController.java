package com.videoreg.videoreg;

import com.videoreg.videoreg.model.User;
import com.videoreg.videoreg.model.UserRepository;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class VideoController {

    @FXML private ImageView cameraView;
    @FXML private Label statusLabel;
    @FXML private Slider sizeSlider;
    @FXML private Circle recordIndicator;

    private User user;
    private volatile boolean running = false;
    private FrameGrabber grabber;
    private FFmpegFrameRecorder recorder;
    private String recordedVideoPath;

    public void setUser(User user) {
        this.user = user;
    }

    @FXML
    private void initialize() {
        try {
            grabber = new OpenCVFrameGrabber(0);
            grabber.start();
            startPreviewThread();

            sizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                cameraView.setFitWidth(newVal.doubleValue());
                cameraView.setFitHeight(newVal.doubleValue() * 3 / 4);
            });
        } catch (Exception e) {
            statusLabel.setText("Ошибка запуска камеры: " + e.getMessage());
        }
    }

    private void startPreviewThread() {
        Thread t = new Thread(() -> {
            Java2DFrameConverter converter = new Java2DFrameConverter();
            while (true) {
                try {
                    Frame frame = grabber.grab();
                    if (frame != null && frame.image != null) {
                        BufferedImage img = converter.getBufferedImage(frame);
                        Image fxImage = SwingFXUtils.toFXImage(img, null);
                        Platform.runLater(() -> cameraView.setImage(fxImage));

                        if (running && recorder != null) {
                            recorder.record(frame);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void onStartRecording() {
        try {
            File tempFile = File.createTempFile("recording_", ".mp4");
            tempFile.deleteOnExit();
            recordedVideoPath = tempFile.getAbsolutePath();

            recorder = new FFmpegFrameRecorder(recordedVideoPath,
                    grabber.getImageWidth(), grabber.getImageHeight());
            recorder.setFormat("mp4");
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFrameRate(30);
            recorder.start();

            running = true;
            recordIndicator.setFill(Color.RED);
            statusLabel.setText("Запись идёт…");
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Ошибка при запуске записи: " + e.getMessage());
        }
    }

    @FXML
    private void onStopRecording() {
        try {
            running = false;
            recordIndicator.setFill(Color.GRAY);

            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить видео как...");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("MP4 files (*.mp4)", "*.mp4"));
            Window window = cameraView.getScene().getWindow();
            File chosenFile = fileChooser.showSaveDialog(window);

            if (chosenFile != null && recordedVideoPath != null) {
                Files.move(
                        Paths.get(recordedVideoPath),
                        Paths.get(chosenFile.getAbsolutePath()),
                        StandardCopyOption.REPLACE_EXISTING);

                recordedVideoPath = chosenFile.getAbsolutePath();
                statusLabel.setText("Запись сохранена: " + recordedVideoPath);

                // Сохраняем путь в БД
                if (user != null) {
                    UserRepository.save(user, recordedVideoPath);
                }
            } else {
                statusLabel.setText("Запись остановлена (не сохранено)");
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Ошибка остановки записи: " + e.getMessage());
        }
    }
}
