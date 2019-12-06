package com.geekbrains.brains.cloud.server;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Message implements Serializable {

        String command;
        String filename;
        MessageType messageType;
        private byte[] file;

        public void setFile(String filename) {
                try {
                        this.file =  Files.readAllBytes(Paths.get(filename));
                } catch (IOException e) {
                        e.printStackTrace();
                }
                this.filename = filename;

        }

        public void saveFile() {
                try {
                        Files.write(Paths.get( "savedMessage.txt"), file );
                } catch (IOException e) {
                        e.printStackTrace();
                }
               // return file;
        }

        public void setType (MessageType messageType) {
                this.messageType = messageType;
        }

        public void setCommand(String command) {
                this.command = command;
        }

}
