package hotel_chain_system;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class show_housekeeping {
	public static final String GET_CLEANING = "SELECT * FROM check_in where housekeeper_id is NOT NULL";

	public static void main(String args[]) throws SQLException {
		Connection con = null;
		try {
			con = DBConnection.managerLogin();
			cleaningRoom(con);
		} catch (SQLException e) {

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				// should close connections when done;
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	//public static final String GET_CLEANING = "SELECT * FROM check_in where housekeeper_id is NOT NULL";

	public static void cleaningRoom(Connection con) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(GET_CLEANING);
		ResultSet rs = stmt.executeQuery();
		printCleaningRoom(rs);
		stmt.close();
	}

	public static void printCleaningRoom(ResultSet rs) throws SQLException {
		System.out.println("하우스 키퍼가 관리하고 있는 방을 조회합니다.");

		int roomIdWidth = 10;
		int checkInWidth = 20;
		int checkOutWidth = 20;
		int housekeeper = 10;

		System.out.printf(
				"%-" + roomIdWidth + "s %-" + checkInWidth + "s %-" + checkOutWidth + "s %-" + housekeeper + "s %n",
				"room_id", "check_in_date", "check_out_date", "housekeeper");

		System.out.println("-".repeat(housekeeper + roomIdWidth + checkInWidth + checkOutWidth));

		while (rs.next()) {
			System.out.printf(
					"%-" + roomIdWidth + "s %-" + checkInWidth + "s %-" + checkOutWidth + "s %-" + housekeeper + "s%n",
					rs.getString("room_id"), rs.getString("check_in"), rs.getString("check_out_date"), rs.getString("housekeeper_id"));
		}
		System.out.println("-".repeat(housekeeper + roomIdWidth + checkInWidth + checkOutWidth));
	}

}
