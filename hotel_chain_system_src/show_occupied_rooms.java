package hotel_chain_system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

//체크인 상태에 있는 모든 방을 조회하기
public class show_occupied_rooms {
	public static final String GET_SEARCH_QUERY = "SELECT * FROM check_in";

	public static void main(String[] args) throws SQLException{
		Connection con = null;
		try {
			// 1. 쉬고 있는 하우스 키퍼 정보 가져오기
			con = DBConnection.managerLogin();
			occupiedRoom(con);
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

	public static void occupiedRoom(Connection con) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(GET_SEARCH_QUERY);
		ResultSet rs = stmt.executeQuery();
		printOccupied(rs);
		stmt.close();
	}

	public static void printOccupied(ResultSet rs) throws SQLException {
		System.out.println("현재 고객님이 이용중인 모든 방을 조회합니다.");

		int customerIdWidth = 20;
		int roomIdWidth = 10;
		int checkInWidth = 20;
		int checkOutWidth = 20;

		System.out.printf(
				"%-" + customerIdWidth + "s %-" + roomIdWidth + "s %-" + checkInWidth + "s %-" + checkOutWidth + "s %n",
				"customer_id", "room_id", "check_in_date", "check_out_date");
		System.out.println("-".repeat(customerIdWidth + roomIdWidth + checkInWidth + checkOutWidth));

		while (rs.next()) {
			System.out.printf(
					"%-" + customerIdWidth + "s %-" + roomIdWidth + "s %-" + checkInWidth + "s %-" + checkOutWidth + "s%n",
					rs.getString("customer_id"), rs.getString("room_id"), rs.getString("check_in"), rs.getString("check_out_date"));
		}

		System.out.println("-".repeat(customerIdWidth + roomIdWidth + checkInWidth + checkOutWidth));

		while (rs.next()) {
			System.out.print(rs.getString(1) + " " + rs.getString(2) + '\n');
		}
	}
}
