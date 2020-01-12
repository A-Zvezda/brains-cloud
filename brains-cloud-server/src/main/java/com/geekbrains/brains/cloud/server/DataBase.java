package com.geekbrains.brains.cloud.server;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataBase {
    private static Connection connection;
    private static Statement stmt;

    public static void connection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:cloudDB.db");
            stmt = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getMaxID() {
        String sql = String.format("SELECT MAX(id) FROM users");

        try {
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                String str = rs.getString(1);
                return rs.getString(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getUserIDbyNick(String nick) {
        String sql = String.format("SELECT id FROM user where users = '%s'", nick);

        try {
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                String str = rs.getString(1);
                return rs.getString(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }



    public static int setNewUsers(String login, int pass) {

        String sql = String.format("INSERT INTO users (user, password) VALUES ('%s', %s)", login, pass);
        int rs = 0;
        try {
            rs = stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(sql);
            e.printStackTrace();
        }

        return rs;
    }


    public static String getUserByLoginAndPass(String login, int pass) {

        String sql = String.format("SELECT user FROM users where user = '%s' and password = %s", login, pass);

        try {
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                String str = rs.getString(1);
                return rs.getString(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getLogin(String user) {
        String sql = String.format("SELECT user FROM users where user = '%s'", user);

        try {
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                String str = rs.getString(1);
                return rs.getString(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
