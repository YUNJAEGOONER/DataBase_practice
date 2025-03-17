package hotel_chain_system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class allocate_houskeeper {
	public static final String GET_HK_QUERY = "SELECT * FROM housekeeper where status = false";

	public static final String GET_ROOM_QUERY = "SELECT * FROM check_in WHERE housekeeper_id IS NULL";

	public static final String ALLOCATE_HK = "Update check_in SET housekeeper_id = ? WHERE room_id = ?";

	public static final String STATUS_CHANGE = "Update housekeeper SET status = true WHERE hk_id = ?";

	public static void main(String args[]) throws SQLException {
		Connection con = null;

		try {

			// 1. 쉬고 있는 하우스 키퍼 정보 가져오기
			con = DBConnection.managerLogin();
			getHousekeeper(con);

			// 2.check_in되어 있는 객실 중, 하우스 키퍼가 배정되지 않은 방의 정보를 가져온다.
			ArrayList<Integer> occupiedRooms = occupiedRoom(con);

			// 3.housekeeper를 선택
			System.out.print("하우스 커퍼의 ID를 입력하세요 : ");
			Scanner sc = new Scanner(System.in);
			int hkid = sc.nextInt();

			if (checkAvailableID(con, hkid)) {
				System.out.print("하우스키퍼를 배정할 객실의 ID를 입력하세요 :");
				int roomid = sc.nextInt();
				boolean checkroom = false;
				for (int i = 0; i < occupiedRooms.size(); i++) {
					if (occupiedRooms.get(i) == roomid) {
						checkroom = true;
						break;
					}
				}
				if (checkroom) {
					allocateHousekeeper(con, hkid, roomid);

				} else {
					System.out.println("해당 RoomID를 가진 객실에는 하우스키퍼 배정이 불가합니다.");
				}
			} else {
				System.out.println("해당 ID를 가진 하우스키퍼는 배정이 불가합니다.");
			}

		} catch (SQLException e) {
			// con.rollback();
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

	public static void getHousekeeper(Connection con) throws SQLException {
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(GET_HK_QUERY);
		printResult(rs);
	}

	public static void printResult(ResultSet rs) throws SQLException {

		System.out.println("현재 쉬고 있는 하우스키퍼 목록을 조회합니다.");

		int hkIdWidth = 10;
		int name = 20;

		System.out.printf("%-" + hkIdWidth + "s %-" + name + "s %n", "hkId", "name");
		System.out.println("-".repeat(hkIdWidth + name));

		while (rs.next()) {
			System.out.printf("%-" + hkIdWidth + "s %-" + name + "s %n", rs.getString(1), rs.getString(3));
		}

		System.out.println("-".repeat(hkIdWidth + name));
	}

	//입력한 하우스 키퍼ID에 대한 유효성 검사
	public static boolean checkAvailableID(Connection con, int id) throws SQLException {
		boolean result = false;
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(GET_HK_QUERY);
		while (rs.next()) {
			int target = rs.getInt("hk_id");
			if (target == id) {
				result = true;
				break;
			}
		}
		return result;
	}

	public static ArrayList<Integer> occupiedRoom(Connection con) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(GET_ROOM_QUERY);
		ResultSet rs = stmt.executeQuery();
		ArrayList<Integer> results = printOccupied(rs);
		stmt.close();
		return results;
	}

	public static ArrayList<Integer> printOccupied(ResultSet rs) throws SQLException {
		ArrayList<Integer> rooms = new ArrayList<>();

		System.out.println("청소가 되어있지 않은 모든 방을 조회합니다.");

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
					rs.getString("room_id"), rs.getString("check_in"), rs.getString("check_out_date"),
					rs.getString("housekeeper_id"));
			rooms.add(rs.getInt("room_id"));
		}

		System.out.println("-".repeat(housekeeper + roomIdWidth + checkInWidth + checkOutWidth));
		return rooms;
	}

	public static void allocateHousekeeper(Connection con, int hk_id, int room_id) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(ALLOCATE_HK);
		stmt.setInt(1, hk_id);
		stmt.setInt(2, room_id);
		stmt.executeUpdate();
		statusHousekeeper(con, hk_id);
		System.out.print("객실(객실ID: " + room_id + ")에 하우스키퍼(사번: " + hk_id + ")가 배정되었습니다.");
	}

	public static void statusHousekeeper(Connection con, int hk_id) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(STATUS_CHANGE);
		stmt.setInt(1, hk_id);
		stmt.executeUpdate();
	}
}
