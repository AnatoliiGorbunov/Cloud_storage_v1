package ru.geekbrains.cloud;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.*;
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
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ResourceBundle;

@Slf4j
public class ClientController implements Initializable {

    @FXML
    public TextField clientPath, serverPath, textSearchClient, textSearchServer;
    @FXML
    public ListView<String> clientView, serverView;
    @FXML
    public Button buttonDownload, buttonUpload;
    @FXML
    public Text textClient,textServer;
    @FXML
    public Button deleteClientFile;
    @FXML
    public AnchorPane logIn;
    @FXML
    public TextField textLogin;
    @FXML
    public TextField textPassword;
    @FXML
    public AnchorPane anchorPaneDelete;
    @FXML
    public Button buttonSearchClient;
    @FXML
    public Button buttonSearchServer;
    @FXML
    public Button ButtonAuth;

    private Path clientDir;
    private Path searchDir;
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

    public void download(ActionEvent actionEvent) throws IOException {//посылаем запрос
//       oos.writeObject(new FileRequest(serverView.getSelectionModel().getSelectedItem()));
    }

    public void upload(ActionEvent actionEvent) throws IOException {//посылаем запрос

//        oos.writeObject(new FileMessage(clientDir.resolve(clientView.getSelectionModel().getSelectedItem())));

    }


    public void deleteClientFile(ActionEvent actionEvent) throws IOException {
        Path path = Paths.get(String.valueOf(clientDir.resolve(clientView.getSelectionModel().getSelectedItem())));
        Files.delete(path);
        updateClientView();
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

    private void updateServerView(){

        serverView.setOnMouseClicked(mouseClicked ->{
            if(mouseClicked.getClickCount() == 2){
                String item = serverView.getSelectionModel().getSelectedItem();
               if(item.equals("...")){


               }
            }
        });
    }


    private void updateClientView() {
        Platform.runLater(() -> {
            clientPath.setText(String.valueOf(clientDir.toAbsolutePath()));
            clientView.getItems().clear();
            clientView.getItems().add("...");
            clientView.getItems()
                    .addAll(clientDir.toFile().list());

        });
    }



    private void initNetwork() {//поднимаем соке
        new Thread(() -> NetworkClient.getOurInstance().start(ClientController.this)).start();
        clientDir = Paths.get("D:\\WorkingMaterials\\Java.cloud_storage\\Cloud_storage_v1\\Cloud_storage_v1\\client\\client-cloud");
        clientNavigation();

    }


    public void initialize(URL url, ResourceBundle resourceBundle) {
        initNetwork();
        setAuthorized();
        updateClientView();


    }


}
