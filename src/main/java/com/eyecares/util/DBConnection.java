package com.eyecares.util;

import java.sql.Connection;
import java.sql.DriverManager;


public class DBConnection {
	  private static Connection con = null;

	    public static Connection getConnection() {

	        try {
	            // Load MySQL JDBC Driver
	            Class.forName("com.mysql.cj.jdbc.Driver");

	            // Create connection
	            con = DriverManager.getConnection(
	                "jdbc:mysql://localhost:3306/e_eyecares",
	                "root",
	                "ruchi@sakshi10"
	            );

	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return con;
	    }
	    public static void main(String[] args) {
	        if(getConnection() != null) {
	            System.out.println("Connected to MySQL successfully!");
	        } else {
	            System.out.println("Connection failed!");
	        }
	    }

}
