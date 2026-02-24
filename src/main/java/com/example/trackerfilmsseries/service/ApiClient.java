package com.example.trackerfilmsseries.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ApiClient {

    private static final String URL =
            "jdbc:postgresql://ep-summer-heart-ai4mfa71-pooler.c-4.us-east-1.aws.neon.tech/neondb?user=neondb_owner&password=npg_l6fzI4aZbuHU&sslmode=require&channelBinding=require";

    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_l6fzI4aZbuHU";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
