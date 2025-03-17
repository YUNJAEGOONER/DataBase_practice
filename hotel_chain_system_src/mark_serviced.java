package hotel_chain_system;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class mark_serviced {
	public static final String GET_SERVICED = "select r.room_id, ci.customer_id, ci.check_out_date "
			+ "from room r left join check_in ci "
			+ "ON r.room_id = ci.room_id "
			+ "order by r.room_id" ;

	public static void main(String args[]) throws SQLException {
		Connection con = null;
		try {
			con = DBConnection.managerLogin();
			con.setAutoCommit(false);
			con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

			System.out.println("모든 객실의 상태를 출력합니다.");
			getRoomStatus(con);
			con.commit();
		} catch (SQLException e) {
			con.rollback();

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

	public static void getRoomStatus(Connection con) throws SQLException {
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(GET_SERVICED);
		printCleaningRoom(rs);
		stmt.close();
	}

	public static void printCleaningRoom(ResultSet rs) throws SQLException {
		int roomIdWidth = 10;
		int customerIdWidth = 20;
		int checkOutWidth = 20;

		System.out.printf("%-" + roomIdWidth + "s %-" + customerIdWidth + "s %-" + checkOutWidth + "s %n", "room_id", "customer_id", "check_out_date");

		System.out.println("-".repeat(roomIdWidth + customerIdWidth + checkOutWidth));

		while (rs.next()) {
			System.out.printf(
					"%-" + roomIdWidth + "s %-" + customerIdWidth + "s %-" + checkOutWidth + "s %n",
					rs.getString("room_id"), rs.getString("customer_id"), rs.getDate("check_out_date"));
		}
		System.out.println("-".repeat(roomIdWidth + customerIdWidth + checkOutWidth));
	}

}
