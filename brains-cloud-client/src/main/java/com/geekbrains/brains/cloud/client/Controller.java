package com.geekbrains.brains.cloud.client;


import common.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Stream;


public class Controller implements Initializable {
    @FXML
    ListView<String> clientFiles, serverFiles, log;
    @FXML
    TextField loginField;
    @FXML
    PasswordField passwordField;
    @FXML
    Button signIn, register, logout;



    private String serverIP;
    private int serverPort;
    private String clientFolder;
    //private boolean auth = false;

    protected static final Logger logger = LoggerFactory.getLogger(Controller.class);
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshLocalFileList();
        String filePath = "client.properties";
        Properties prop = new Properties();
        try (InputStream input = Controller.class.getClassLoader().getResourceAsStream(filePath)) {
            prop.load(input);

        } catch (IOException ex) {
            System.err.println("Unable to load properties file : " + filePath);
            ex.printStackTrace();
        }

        this.serverIP = prop.getProperty("server.ip");
        this.serverPort = Integer.parseInt(prop.getProperty("server.port"));
        this.clientFolder = prop.getProperty("client.folder");

        Network.start(this.serverIP, this.serverPort);
        Thread t = new Thread(() -> {
            boolean auth = false;
            try {

                while (true) {
                    AbstractMessage am = Network.readObject();
//                    if (auth) {
                        if (am instanceof FileMessage) {
                            FileMessage fm = (FileMessage) am;
                            Files.write(Paths.get(this.clientFolder + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                            refreshLocalFileList();
                        } else if (am instanceof GetFileList) {
                            GetFileList getFileList = (GetFileList) am;
                            refreshServerFileList(getFileList);
                        }  else if (am instanceof CloseMessage) {
                   //         break;
                        }
                            
//                    } else {
                    else if (am instanceof NewUser) {
                            NewUser newUser = (NewUser) am;
                            log.getItems().addAll(newUser.getAnswer());
                            
                        } else if (am instanceof AuthMessage) {
                            AuthMessage authMessage = (AuthMessage) am;
                            log.getItems().addAll(authMessage.getAnswer());
                            auth = true;
                            sendRefreshFileList();
                            aut();
                        }
                    }
             //   }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                //auth = false;
                Network.closeConnection();
            }
        });
        t.setDaemon(true);
        t.start();

    }

    private void refreshLocalFileList() {
        Platform.runLater(() -> {
            clientFiles.getItems().clear();
            Set <String> fileList;
            try {
                fileList =  new GetFileList().getFileList(this.clientFolder );
                clientFiles.getItems().addAll(fileList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void refreshServerFileList(GetFileList fileList) {
        Platform.runLater(() -> {
        serverFiles.getItems().clear();
        serverFiles.getItems().addAll(fileList.getFileList());
        });
    }


    public void btnClickRefreshLocalFileList(ActionEvent actionEvent) {
        refreshLocalFileList();

    }

    public void btnClickRefreshServerFileList(ActionEvent actionEvent) {
        sendRefreshFileList();
    }

    public void sendRefreshFileList () {
        //label.setText(serverFiles.getSelectionModel().getSelectedItem());
        Network.sendMsg(new GetFileList());
        logger.debug("Refresh file list message sent");
    }

    public void signIn(ActionEvent actionEvent) {

        AuthMessage authMessage = new AuthMessage();
        authMessage.setUserName(loginField.getText());
        authMessage.setPassword(passwordField.getText());
        Network.sendMsg(authMessage);

    }
    public void aut () {
        passwordField.setDisable(true);
        loginField.setDisable(true);
        signIn.setDisable(true);
        register.setDisable(true);
        logout.setDisable(false);
        logout.setVisible(true);
    }
    public void register(ActionEvent actionEvent) {
        NewUser newUser = new NewUser();
        newUser.setUserName(loginField.getText());
        newUser.setPassword(passwordField.getText());
        Network.sendMsg(newUser);

    }

    public void logout(ActionEvent actionEvent) {
        passwordField.setDisable(false);
        loginField.setDisable(false);
        signIn.setDisable(false);
        register.setDisable(false);
        logout.setDisable(true);
        logout.setVisible(false);
    }

    public void btnClickSendFile(ActionEvent actionEvent) {
        String fileName = clientFiles.getSelectionModel().getSelectedItem();
        try {
            Network.sendMsg(new FileMessage(Paths.get(this.clientFolder + fileName)));
            logger.debug("File sent");
            sendRefreshFileList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void Dispose() {
        logger.debug("File closing message");

        if (Network.getSocket() != null) {
            CloseMessage closeMessage = new CloseMessage();
            Network.sendMsg(closeMessage);
            Network.closeConnection();

        }
    }

    public void btnClickGetFile(ActionEvent actionEvent) {
        String fileName = serverFiles.getSelectionModel().getSelectedItem();
        Network.sendMsg(new FileRequest(fileName));
        logger.debug("Send file request");
        }

}
   