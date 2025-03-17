package hotel_chain_system;

import java.sql.*;
import java.util.ArrayList;

public class show_my_reservation {

	public static final String GET_SEARCH_QUERY = "SELECT * FROM reservation where customer_id = ? ";

	public static void main(int cid) {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			int id = cid;// parameter로 받아오기
			con = DBConnection.managerLogin();
			myReservation(con, id);
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

	public static void myReservation(Connection con, int customer_id) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(GET_SEARCH_QUERY);
		stmt.setInt(1, customer_id);
		ResultSet rs = stmt.executeQuery();
		printResult(rs, customer_id);
		stmt.close();
	}

	public static void printResult(ResultSet rs, int customer_id) throws SQLException {
		System.out.println("고객 번호가 " + customer_id + "인 고객님의 예약 내역을 확인합니다.");
		int resIdWidth = 20; // reservation_id
		int roomIdWidth = 10; // room_id
		int checkInWidth = 20;
		int checkOutWidth = 20;
		int priceWidth = 20;

		System.out.printf("%-" + resIdWidth + "s %-" + roomIdWidth + "s %-" + checkInWidth + "s %-" + checkOutWidth
				+ "s %-" + priceWidth + "s%n", "reservation_id", "room_id", "check_in", "check_out", "price");
		System.out.println("-".repeat(resIdWidth + roomIdWidth + checkInWidth + checkOutWidth + priceWidth));

		while (rs.next()) {

			System.out.printf(
					"%-" + resIdWidth + "s %-" + roomIdWidth + "s %-" + checkInWidth + "s %-" + checkOutWidth + "s %-"
							+ priceWidth + "s %n",
					rs.getString(1), rs.getString("room_id"), rs.getString("check_in_date"),
					rs.getString("check_out_date"), rs.getString("total_price"));
		}

		System.out.println("-".repeat(resIdWidth + roomIdWidth + checkInWidth + checkOutWidth + priceWidth));

	}

}
