package hotel_chain_system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	public final static String DB_DRIVER_CLASS = "org.postgresql.Driver";
	public final static String DB_URL = "jdbc:postgresql://localhost:5432/lee55650";
	public final static String DB_USERNAME = "yunjaelee";
	public final static String DB_PASSWORD = "love3928";
	
	public final static String CUSTOMER_DB_DRIVER_CLASS = "org.postgresql.Driver";
	public final static String CUSTOMER_DB_URL = "jdbc:postgresql://localhost:5432/lee55650";
	public final static String CUSTOMER_DB_USERNAME = "customer";
	public final static String CUSTOMER_DB_PASSWORD = "customer";

	public static Connection getConnection() throws ClassNotFoundException, SQLException {

		Connection con = null;

		// load the Driver Class
		Class.forName(DB_DRIVER_CLASS);

		// create the connection now
		con = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

		//System.out.println("DB Connection created successfully");
		return con;
	}

	public static Connection customerLogin() throws ClassNotFoundException, SQLException {

		Connection con = null;

		// load the Driver Class
		Class.forName(CUSTOMER_DB_DRIVER_CLASS);

		// create the connection now
		con = DriverManager.getConnection(CUSTOMER_DB_URL, CUSTOMER_DB_USERNAME, CUSTOMER_DB_PASSWORD);

		//System.out.println("사용자로 로그인 하였습니다.");
		return con;
	}

	public static Connection managerLogin() throws ClassNotFoundException, SQLException {

		Connection con = null;

		// load the Driver Class
		Class.forName(DB_DRIVER_CLASS);

		// create the connection now
		con = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

		//System.out.println("관리자로 로그인 하였습니다.");
		return con;
	}

	public static void main(String args[]) {

		Connection con = null;
		try {
			con = DBConnection.getConnection();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
}
