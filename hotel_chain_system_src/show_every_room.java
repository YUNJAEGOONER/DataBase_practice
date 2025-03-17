package hotel_chain_system;

import java.sql.*;
import java.util.ArrayList;

public class show_every_room {

	public static final String GET_SEARCH_QUERY = "SELECT * FROM Room";

	public static final String GET_HOTLE_NAME = "SELECT * FROM Hotel WHERE hotel_id = ? ";

	public static void main(String[] args) {
		Connection con = null;
		try {
			con = DBConnection.managerLogin();
			// System.out.println("OO호텔의 모든 방을 조회합니다.");
			getEveryRoom(con);
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

	// 모든 객실의 정보를 가져온다.
	public static void getEveryRoom(Connection con) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(GET_SEARCH_QUERY);
		ResultSet rs = stmt.executeQuery();
		printResult(con, rs);
		stmt.close();
	}

	// hotel의 이름(hotel_name)을 가져오는 함수
	// hotel_id를 통해 hotel_name을 가져온다.
	public static ArrayList<String> getHotelName(Connection con, int id) throws SQLException {
		ArrayList<String> result = new ArrayList<>();
		PreparedStatement stmt = con.prepareStatement(GET_HOTLE_NAME);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		String name = rs.getString("name");
		String location = rs.getString("location");
		result.add(name);
		result.add(location);
		stmt.close();
		return result;
	}

	// 모든 객실의 정보를 출력하는 함수
	public static void printResult(Connection con, ResultSet rs) throws SQLException {
		System.out.println("현재 우리 호텔에서 운영중인 객실 정보입니다.");

		int roomIdWidth = 10;
		int hotelNameWidth = 30;
		int locationWidth = 30;
		int priceWidth = 15;
		int descriptionWidth = 50;

		System.out.printf("%-" + roomIdWidth + "s %-" + hotelNameWidth + "s %-" + locationWidth + "s %-" + priceWidth
				+ "s %-" + descriptionWidth + "s%n", "room_id", "hotel_name", "location", "price", "description");
		System.out.println("-".repeat(roomIdWidth + hotelNameWidth + locationWidth + priceWidth + descriptionWidth));

		while (rs.next()) {
			ArrayList<String> hotelinfo = getHotelName(con, rs.getInt("hotel_id"));
			System.out.printf(
					"%-" + roomIdWidth + "s %-" + hotelNameWidth + "s %-" + locationWidth + "s %-" + priceWidth
							+ ".2f %-" + descriptionWidth + "s%n",
					rs.getString(1), hotelinfo.get(0), hotelinfo.get(1), rs.getBigDecimal("price"),
					rs.getString("description"));
		}

		System.out.println("-".repeat(roomIdWidth + hotelNameWidth + locationWidth + priceWidth + descriptionWidth));
	}

}
