package hotel_chain_system;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class check_avaliable_room {
	// brand 입력을 통한 방을 조회하기

	// brand_name에 unique 속성 추가하기
	public static final String GET_BRAND_ID = "SELECT brand_id FROM Brand WHERE name = ?";

	// hotel_id = branch_id
	public static final String GET_ALL_BRANCH = "SELECT * FROM Hotel WHERE brand_id = ?";

	// get_room by hotel_id(branch_id)
	public static final String GET_ROOMS = "SELECT * FROM Room WHERE hotel_id = ?";

	// 날짜를 기준으로 예약 조회하기
	public static final String GET_RESERVE = "SELECT * FROM Reservation WHERE check_in_date <= ? AND check_out_date > ?"; // 안되는거
	
	public static final String GET_CHECKED = "SELECT * FROM check_in WHERE check_in <= ? AND check_out_date > ?"; // 안되는거

	public static final String PRINT_RESULT = "SELECT room_id, name, location, price, description "
			+ "FROM Room r, Hotel h WHERE r.hotel_id = h.hotel_id AND r.room_id = ?";

	public static void main(String[] args) {
		Connection con = null;
		PreparedStatement stmt = null;

		Scanner sc = new Scanner(System.in);
		try {
			con = DBConnection.managerLogin();

			// 1. brandname을 brandid로 바꾸기
			System.out.println("예약이 가능한 모든 방을 조회합니다.");

			System.out.print("호텔 브랜드 이름을 입력해주세요 : ");
			String bname = sc.nextLine();
			//String bname = "Luxury Stay";
			int result1 = get_brandID(con, bname);

			System.out.print("체크인 날짜를 입력해주세요 (연도-월-일): ");
			String today = sc.nextLine();

			// 2. 해당 brand_id를 갖는 branch_id를 모두 가져오기
			ResultSet rs2 = getAllBranch(con, result1);
			// print_rs(rs2);

			// 3. branch_id를 갖는 모든 room 가져오기
			ArrayList<Integer> result3 = getAllRoom(con, rs2);

			// 4. 예약이(날짜로 조건문을 작성해서)불가한 방 모두 가져오기
			// String today = "2023-12-14"; //오늘 날짜 기준
			while (!isValidDate(today)) {
				System.out.println("유효하지 않은 날짜 형식입니다. 다시 입력해주세요");
				today = sc.nextLine();
			}
				
			//예약된방
			ArrayList<Integer> result4 = getReserevedRooms(con, today);
			result3.removeAll(result4);
			
			//체크인된방
			ArrayList<Integer> result5 = getCheckedRooms(con, today);
			
			// 5. 3과 4,5에 대해서 차집합수행
			
			result3.removeAll(result5);
			
			// 6. 결과 출력하기
			if (result3.size() == 0) {
				System.out.print("해당 날짜에 이용가능한 호텔이 없습니다.");
			} else {
				printAvilableRoom(con, result3);
			}

		} catch (SQLException e) {
			System.out.print("존재하지 않는 브랜드입니다.");

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

	public static int get_brandID(Connection con, String bname) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(GET_BRAND_ID);
		stmt.setString(1, bname);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int branchID = rs.getInt(1);
		stmt.close();
		return branchID;
	}

	public static ResultSet getAllBranch(Connection con, int bid) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(GET_ALL_BRANCH);
		stmt.setInt(1, bid);
		ResultSet rs = stmt.executeQuery();
		return rs;
	}

	public static ArrayList<Integer> getAllRoom(Connection con, ResultSet all_branch) throws SQLException {
		ArrayList<Integer> rooms = new ArrayList<Integer>();
		while (all_branch.next()) {
			PreparedStatement stmt = con.prepareStatement(GET_ROOMS);
			stmt.setInt(1, all_branch.getInt("hotel_id"));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				rooms.add(rs.getInt(1));
			}
		}
		return rooms;
	}
	
	public static ArrayList<Integer> getReserevedRooms(Connection con, String today) throws SQLException {
		ArrayList<Integer> rooms = new ArrayList<Integer>();
		PreparedStatement stmt = con.prepareStatement(GET_RESERVE);
		stmt.setDate(1, Date.valueOf(today));
		stmt.setDate(2, Date.valueOf(today));
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			rooms.add(rs.getInt("room_id"));
		}
	
		return rooms;
	}
	
	public static ArrayList<Integer> getCheckedRooms(Connection con, String today) throws SQLException {
		ArrayList<Integer> rooms = new ArrayList<Integer>();
		PreparedStatement stmt = con.prepareStatement(GET_CHECKED);
		stmt.setDate(1, Date.valueOf(today));
		stmt.setDate(2, Date.valueOf(today));
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			rooms.add(rs.getInt("room_id"));
		}
		return rooms;
	}

	public static void printAvilableRoom(Connection con, ArrayList<Integer> result) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(PRINT_RESULT);

		int roomIdWidth = 10; // room_id는 최대 3자리, 여유를 두고 10
		int hotelNameWidth = 30; // hotel_name은 최대 15글자, 여유를 두고 20
		int locationWidth = 30;
		int priceWidth = 15; // price는 최대 4자리 + 2자리 소수점, 여유를 두고 15
		int descriptionWidth = 50; // description은 최대 20자, 여유를 두고 25

		System.out.printf("%-" + roomIdWidth + "s %-" + hotelNameWidth + "s %-" + locationWidth + "s %-" + priceWidth
				+ "s %-" + descriptionWidth + "s%n", "room_id", "hotel_name", "location", "price", "description");
		System.out.println("-".repeat(roomIdWidth + hotelNameWidth + locationWidth + priceWidth + descriptionWidth));

		for (int i = 0; i < result.size(); i++) {
			stmt.setInt(1, result.get(i));
			ResultSet rs = stmt.executeQuery();
			rs.next();

			System.out.printf(
					"%-" + roomIdWidth + "s %-" + hotelNameWidth + "s %-" + locationWidth + "s %-" + priceWidth
							+ ".2f %-" + descriptionWidth + "s%n",
					rs.getString(1), rs.getString(2), rs.getString(3), rs.getBigDecimal(4), rs.getString(5));
		}
	}

	public static boolean isValidDate(String dateString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(false);
		try {
			sdf.parse(dateString);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

}
