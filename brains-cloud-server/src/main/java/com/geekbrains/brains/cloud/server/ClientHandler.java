package com.geekbrains.brains.cloud.server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private MainApp server;
    private String nick;



    public String getNick() {
        return nick;
    }

    public ClientHandler(Socket socket, MainApp server) {
        try {
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            int length = in.available();
                            if (length > 0) {
                                byte[] buf = new byte[length];
                                in.readFully(buf);
                                ObjectInputStream in = null;
                                Files.write(Paths.get("tempSer.ser"), buf );
                                ObjectInputStream objectInputStream = null;
                                Message restMessage = null;
                                objectInputStream = new ObjectInputStream(new BufferedInputStream(
                                        new FileInputStream("tempSer.ser")));
                                restMessage = (Message)objectInputStream.readObject();
                                restMessage.saveFile();
                                objectInputStream.close();
                                break;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Клиент отключился");
                    server.unsubscribe(ClientHandler.this);
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
