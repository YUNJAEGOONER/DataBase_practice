package hotel_chain_system;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class check_in {

	public static final String GET_SEARCH_QUERY = "SELECT * FROM reservation where reservation_id = ?";

	public static final String CREATE_CHECK_IN = "INSERT INTO check_in (customer_id, room_id, fee, check_in, check_out_date) VALUES (?, ?, ?, ?, ?)";

	public static final String DELETE_RESERVE = "DELETE FROM reservation where reservation_id = ?";

	public static void main(String[] args) throws SQLException {
		Connection con = null;
		Scanner sc = new Scanner(System.in);
		try {
			con = DBConnection.managerLogin();

			System.out.print("예약 번호를 입력하세요 : ");

			con.setAutoCommit(false);
			con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

			int reservation_id = sc.nextInt();
			LocalDate today = LocalDate.now();

			// 1. 예약 조회 확인 by reservation_id
			// 2. 오늘 날짜랑 check_in 날짜가 동일하면 check_in
			ResultSet reservation = getReservation(con, reservation_id);

			if (reservation.getDate("check_in_date").equals(Date.valueOf(today))) {
				System.out.println("체크인이 가능합니다.");
				createCheckIn(con, reservation);
				deleteReservation(con, reservation_id);
				System.out.println("체크인이 완료되었습니다.");
			} else {
				System.out.println("체크인이 불가능합니다.");
			}
			con.commit();

		} catch (SQLException e) {
			con.rollback();
			System.out.println("해당 번호에 대한 예약이 존재하지 않습니다.");

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

	public static ResultSet getReservation(Connection con, int reservation_id) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(GET_SEARCH_QUERY);
		stmt.setInt(1, reservation_id);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		return rs;
	}

	// reservation에 있는 데이터 삭제하기
	public static void deleteReservation(Connection con, int reservation_id) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(DELETE_RESERVE);
		stmt.setInt(1, reservation_id);
		stmt.executeUpdate();
		stmt.close();
	}

	public static void createCheckIn(Connection con, ResultSet reservation) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(CREATE_CHECK_IN);
		stmt.setInt(1, reservation.getInt("customer_id"));
		stmt.setInt(2, reservation.getInt("room_id"));
		stmt.setInt(3, reservation.getInt("total_price")); 
		stmt.setDate(4, reservation.getDate("check_in_date"));
		stmt.setDate(5, reservation.getDate("check_out_date"));
		stmt.executeUpdate();
		stmt.close();
	}

}
