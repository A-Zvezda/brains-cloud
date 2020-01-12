package com.geekbrains.brains.cloud.server;


import common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class MainHandler extends ChannelInboundHandlerAdapter {
    static String user = "user/";
    static String serverFolder = "server_storage/";
    static Boolean auth = false;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        try {
       //     if (!auth) {
                if (msg instanceof NewUser) {
                    NewUser newUser = (NewUser) msg;
                    if (DataBase.getLogin(newUser.getUserName()) == null) {
                        DataBase.setNewUsers(newUser.getUserName(), newUser.getPassword());
                        newUser.setAnswer("Registered");
                        createFolder(serverFolder + user);
                    } else {
                        newUser.setAnswer("User already exist");
                    }
                    ctx.writeAndFlush(newUser);
                } else if (msg instanceof AuthMessage) {
                    AuthMessage authMessage = (AuthMessage) msg;
                    if (DataBase.getUserByLoginAndPass(authMessage.getUserName(), authMessage.getPassword()) == null) {
                        authMessage.setAnswer("Wrong user name or password");
                    } else {
                        auth = true;
                        user = authMessage.getUserName() + "/";
                        authMessage.setAnswer("AuthOk");
                    }

                    ctx.writeAndFlush(authMessage);
                }
          //  } else {
                if (msg instanceof FileRequest) {
                    FileRequest fr = (FileRequest) msg;
                    if (Files.exists(Paths.get( serverFolder  + user + fr.getFilename()))) {
                        FileMessage fm = new FileMessage(Paths.get(serverFolder + user + fr.getFilename()));
                        ctx.writeAndFlush(fm);
                    }
                } else if (msg instanceof FileMessage) {
                    FileMessage fm = (FileMessage) msg;
                    Files.write(Paths.get(serverFolder + user + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                } else if (msg instanceof GetFileList) {
                    GetFileList getFileList = new GetFileList();
                    getFileList.setFileList(getFileList.getFileList(serverFolder + user));
                    ctx.writeAndFlush(getFileList);
                }
           // }
                else if  (msg instanceof CloseMessage) {
                CloseMessage closeMessage = new CloseMessage();
                ctx.writeAndFlush(closeMessage);
                System.out.println("!!!!!");
                ctx.close();
            }

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public void createFolder(String dirPath) {

        // Check If Directory Already Exists Or Not?
        Path dirPathObj = Paths.get(dirPath);
        boolean dirExists = Files.exists(dirPathObj);
        if(dirExists) {
            System.out.println("! Directory Already Exists !");
        } else {
            try {
                // Creating The New Directory Structure
                Files.createDirectories(dirPathObj);
                System.out.println("! New Directory Successfully Created !");
            } catch (IOException ioExceptionObj) {
                System.out.println("Problem Occured While Creating The Directory Structure= " + ioExceptionObj.getMessage());
            }
        }
    }

}