package com.geekbrains.brains.cloud.client;

import com.geekbrains.brains.cloud.server.Message;

import java.io.*;
import java.net.Socket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;


public class ConsoleMainClient {
    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    final String IP_ADRESS = "localhost";
    final int PORT = 8189;
    public static void main(String[] args) {
        new ConsoleMainClient("1");
    }
    public ConsoleMainClient(String st) {

        try {
            socket = new Socket(IP_ADRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Message msg = new Message();
            msg.setFile("1.txt");
            msg.setCommand("aaa");
            sendMsg(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendMsg(Message msg) {
        ObjectOutputStream outputStream = null;
        try {
            outputStream = new ObjectOutputStream(new BufferedOutputStream(
                    new FileOutputStream("temp.ser")));
            outputStream.writeObject(msg);
            outputStream.close();
            out.write(Files.readAllBytes(Paths.get("temp.ser")));
            System.out.println("Сообщение отрпавленно");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
