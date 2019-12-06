package com.geekbrains.brains.cloud.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AuthService {
    private static Connection connection;
    private static Statement stmt;

    public static void connection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:mainDB.db");
            stmt = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getMaxID() {
        String sql = String.format("SELECT MAX(id) FROM main");

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
        String sql = String.format("SELECT id FROM main where nickname = '%s'", nick);

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


    public static int setNewUsers(String login, String nick, String pass) {
        int hash = pass.hashCode();
        int maxID =  Integer.parseInt(Objects.requireNonNull(getMaxID())) + 1;
        String sql = String.format("INSERT INTO main (id, login, password, nickname) VALUES (%s,'%s', %s ,'%s')", maxID, login, hash,nick);
        int rs = 0;
        try {
            rs = stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(sql);
            e.printStackTrace();
        }

        return rs;
    }


    public static String getNickByLoginAndPass(String login, String pass) {
        int hash = pass.hashCode();
        String sql = String.format("SELECT nickname FROM main where login = '%s' and password = '%s'", login, hash);

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

    public static String getLogin(String login) {
        String sql = String.format("SELECT nickname FROM main where login = '%s'", login);

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
