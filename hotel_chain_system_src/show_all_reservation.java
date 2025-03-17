package hotel_chain_system;

import java.sql.*;

public class show_all_reservation {

	public static final String GET_SEARCH_QUERY = "SELECT * FROM Reservation";

	public static void main(String[] args) {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = DBConnection.managerLogin();
			System.out.println("모든 예약을 조회합니다.");
			searchEveryRerservation(con);
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

	public static void searchEveryRerservation(Connection con) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(GET_SEARCH_QUERY);
		ResultSet rs = stmt.executeQuery();
		printResult(rs);
		stmt.close();
	}

	public static void printResult(ResultSet rs) throws SQLException {

		int resIdWidth = 20; // reservation_id
		int roomIdWidth = 10; // room_id
		int customerIdWidth = 15;
		int checkInWidth = 20;
		int checkOutWidth = 20;
		int priceWidth = 20;

		System.out.printf(
				"%-" + resIdWidth + "s %-" + roomIdWidth + "s %-" + customerIdWidth + "s %-" + checkInWidth + "s %-"
						+ checkOutWidth + "s %-" + priceWidth + "s%n",
				"reservation_id", "room_id", "customer_id", "check_in", "check_out", "price");
		System.out.println(
				"-".repeat(resIdWidth + roomIdWidth + checkInWidth + checkOutWidth + priceWidth + customerIdWidth));

		while (rs.next()) {

			System.out.printf(
					"%-" + resIdWidth + "s %-" + roomIdWidth + "s %-" + customerIdWidth + "s %-" + checkInWidth + "s %-"
							+ checkOutWidth + "s %-" + priceWidth + "s %n",
					rs.getString(1), rs.getString("room_id"), rs.getString("customer_id"),
					rs.getString("check_in_date"), rs.getString("check_out_date"), rs.getString("total_price"));
		}

		System.out.println("-".repeat(resIdWidth + roomIdWidth + checkInWidth + checkOutWidth + priceWidth
				+ customerIdWidth + customerIdWidth));
	}

}
