package hotel_chain_system;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class check_out {

	public static final String GET_SEARCH_QUERY = "SELECT * FROM check_in WHERE room_id = ?";

	public static final String GET_PRICE = "SELECT * FROM room WHERE room_id = ?";

	public static final String DELETE_CHECK_IN = "DELETE FROM check_in WHERE room_id = ?";

	public static void main(String[] args) {
		Connection con = null;

		Scanner sc = new Scanner(System.in);
		try {
			con = DBConnection.managerLogin();

			System.out.print("객실 ID :");
			int room_id = sc.nextInt();
			
			int total_price = 0;

			// 1. room_id로 check_out이 가능한지 조회하고
			ResultSet check_in_info = getCheckIn(con, room_id);

			// transaction
			con.setAutoCommit(false);
			con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

			// 2-1.요금 계산
			int price = check_in_info.getInt("fee");

			LocalDate today = LocalDate.now();
			
			Date today_date = Date.valueOf(today);
			Date check_out = check_in_info.getDate("check_out_date");
			
			int day = refundDay(today_date, check_out);
			total_price = price + (getRoomPrice(con, room_id).intValue() * day);
			System.out.println("지불하실 금액은 " + total_price + "원 입니다.");

			// 2-2.check_in에서 데이터를 삭제하기
			deleteCheckIn(con, room_id);
			con.commit();

		} catch (SQLException e) {
			System.out.print("객실ID를 다시 확인해주세요");
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

	public static ResultSet getCheckIn(Connection con, int room_id) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(GET_SEARCH_QUERY);
		stmt.setInt(1, room_id);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		return rs;
	}

	public static BigDecimal getRoomPrice(Connection con, int room_id) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(GET_PRICE);
		stmt.setInt(1, room_id);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		BigDecimal price = rs.getBigDecimal("price");
		return price;
	}

	public static void deleteCheckIn(Connection con, int room_id) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(DELETE_CHECK_IN);
		stmt.setInt(1, room_id);
		stmt.executeUpdate();
		System.out.print("체크아웃이 완료되었습니다.");
	}

	public static int refundDay(Date today, Date check_out) {
		long diffInMillis = today.getTime() - check_out.getTime();
		int diffInDays = (int) TimeUnit.MILLISECONDS.toDays(diffInMillis);
		return diffInDays;
	}

}
