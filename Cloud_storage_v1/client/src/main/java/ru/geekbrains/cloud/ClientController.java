package ru.geekbrains.cloud;


import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloud.messagemodel.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ResourceBundle;

@Slf4j
public class ClientController implements Initializable {

    @FXML
    public ListView<String> clientView, serverView;
    @FXML
    public Text textClient, textServer;
    @FXML
    public AnchorPane logIn, anchorPaneDelete;
    @FXML
    public TextField clientPath, serverPath,
            textSearchClient, textSearchServer,
            textLogin, textPassword;
    @FXML
    public Button buttonDownload, buttonUpload,
            buttonSearchClient, buttonSearchServer,
            buttonAuth, deleteClientFile;

    private Path clientDir;
    private Path serverDir;
    public static final int MB_8 = 8_000_000;

    void setAuthorized() {
        clientPath.setVisible(false);
        serverPath.setVisible(false);
        clientView.setVisible(false);
        serverView.setVisible(false);
        buttonDownload.setVisible(false);
        buttonUpload.setVisible(false);
        textClient.setVisible(false);
        textServer.setVisible(false);
        deleteClientFile.setVisible(false);
        anchorPaneDelete.setVisible(false);
        textSearchClient.setVisible(false);
        textSearchServer.setVisible(false);
        buttonSearchServer.setVisible(false);
        buttonSearchClient.setVisible(false);
    }

    void setAuthorizedIsOk() {
        logIn.setVisible(false);
        clientPath.setVisible(true);
        serverPath.setVisible(true);
        clientView.setVisible(true);
        serverView.setVisible(true);
        buttonDownload.setVisible(true);
        buttonUpload.setVisible(true);
        textClient.setVisible(true);
        textServer.setVisible(true);
        deleteClientFile.setVisible(true);
        textSearchClient.setVisible(true);
        textSearchServer.setVisible(true);
        buttonSearchServer.setVisible(true);
        buttonSearchClient.setVisible(true);
    }

    public void download(ActionEvent actionEvent) throws IOException {
        Path path = Paths.get(String.valueOf(serverDir.resolve(serverView.getSelectionModel().getSelectedItem())));
        NetworkClient.getOurInstance().getCurrentChannel().writeAndFlush(new FileRequest(serverDir));
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        Path path = Paths.get(String.valueOf(clientDir.resolve(clientView.getSelectionModel().getSelectedItem())));
        int marker = 0;
        try {
            byte[] data = Files.readAllBytes(Paths.get(String.valueOf(path)));
            byte[] dataTemp = new byte[MB_8];
            if (data.length < MB_8) {
                dataTemp = new byte[data.length];
            }
            for (int i = 0; i < data.length; i++) {
                dataTemp[marker] = data[i];
                marker++;
                if (marker == dataTemp.length) {
                    PackageFile packageFile = new PackageFile(Paths.get(String.valueOf(path)), false, dataTemp);
                    NetworkClient.getOurInstance().getCurrentChannel().writeAndFlush(packageFile);
                    marker = 0;
                    if (data.length - i < MB_8) {
                        dataTemp = new byte[data.length - i];

                    } else {
                        dataTemp = new byte[MB_8];

                    }
                }
            }
            PackageFile packageFile = new PackageFile(Paths.get(String.valueOf(path)), true, dataTemp);
            NetworkClient.getOurInstance().getCurrentChannel().writeAndFlush(packageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void deleteClientFile(ActionEvent actionEvent) throws IOException {
        Path path = Paths.get(String.valueOf(clientDir.resolve(clientView.getSelectionModel().getSelectedItem())));
        Files.delete(path);
        updateClientView();
    }

    public void deleteServerFile(ActionEvent actionEvent) throws IOException {

    }

    public void buttonSearchClient(ActionEvent actionEvent) {
        final String fileToFind = File.separator + textSearchClient.getText().trim();
        clientDir = Paths.get("D:\\WorkingMaterials\\Java.cloud_storage\\Cloud_storage_v1\\Cloud_storage_v1\\client\\client-cloud");
        try {
            Files.walkFileTree(clientDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String fileString = file.toAbsolutePath().toString();
                    if (fileString.endsWith(fileToFind)) {
                        clientPath.setText(String.valueOf(file.toAbsolutePath()));
                        clientView.getItems().clear();
                        clientView.getItems().add("...");
                        clientView.getItems().addAll(file.getParent().toFile().list());
                        System.out.println("file found at path: " + file.toAbsolutePath());
                        return FileVisitResult.TERMINATE;
                    }
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void buttonSearchServer(ActionEvent actionEvent) throws IOException {
        SearchMessage searchMessage = new SearchMessage(File.separator + textSearchServer.getText().trim());
        NetworkClient.getOurInstance().getCurrentChannel().writeAndFlush(searchMessage);

    }

    public void authClient(ActionEvent actionEvent) throws IOException {
        if (!textLogin.getText().trim().isEmpty() && !textPassword.getText().trim().isEmpty()) {
            AuthMessage authMessage = new AuthMessage(textLogin.getText().trim(), textPassword.getText().trim());
            NetworkClient.getOurInstance().getCurrentChannel().writeAndFlush(authMessage);
        }
    }

    private void serverNavigation() {
        serverDir = Paths.get("D:\\WorkingMaterials\\Java.cloud_storage\\Cloud_storage_v1\\Cloud_storage_v1\\server\\Server_cloud");
        serverView.setOnMouseClicked(mouseClicked -> {
            if (mouseClicked.getClickCount() == 2) {
                String item = serverView.getSelectionModel().getSelectedItem();
                if (item.equals("...")) {
                    serverDir = serverDir.getParent();
                    try {
                        updateServerNavigation(serverDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Path selected = serverDir.resolve(item);
                    if (selected.toFile().isDirectory()) {
                        serverDir = selected;
                        try {
                            updateServerNavigation(serverDir);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void clientNavigation() {
        clientDir = Paths.get("D:\\WorkingMaterials\\Java.cloud_storage\\Cloud_storage_v1\\Cloud_storage_v1\\client\\client-cloud");
        clientView.setOnMouseClicked(mouseClicked -> {
            if (mouseClicked.getClickCount() == 2) {
                String item = clientView.getSelectionModel().getSelectedItem();
                if (item.equals("...")) {
                    clientDir = clientDir.getParent();
                    updateClientView();
                } else {
                    Path selected = clientDir.resolve(item);
                    if (selected.toFile().isDirectory()) {
                        clientDir = selected;
                        updateClientView();
                    }
                }
            }
        });
    }

    void updateServerNavigation(Path path) throws IOException {
        NetworkClient.getOurInstance().getCurrentChannel().writeAndFlush(new UpdateServerNavigation(path));
    }

    public void updateClientView() {
        Platform.runLater(() -> {
            clientPath.setText(String.valueOf(clientDir.toAbsolutePath()));
            clientView.getItems().clear();
            clientView.getItems().add("...");
            clientView.getItems()
                    .addAll(clientDir.toFile().list());

        });
    }

    private void initNetwork() {
        new Thread(() -> NetworkClient.getOurInstance().start(ClientController.this)).start();


    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        initNetwork();
        setAuthorized();
        updateClientView();
        clientNavigation();
        serverNavigation();
    }
}
