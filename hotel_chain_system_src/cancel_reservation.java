package hotel_chain_system;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class cancel_reservation {

	public static final String GET_RESERVATION = "SELECT * FROM Reservation WHERE reservation_id = ? AND customer_id = ?";

	public static final String DELETE_RESERVATION = "DELETE FROM Reservation WHERE reservation_id = ?";

	public static void main(int cid) throws SQLException {
		Connection con = null;
		Scanner sc = new Scanner(System.in);
		try {
			con = DBConnection.getConnection();

			System.out.print("취소하려는 예약의 예약번호를 입력하세요 :");
			// transacation
			con.setAutoCommit(false);
			con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			// 예약번호를 기준으로 예약을 취소하기
			int reservation_code = sc.nextInt();
			int customer_id = cid; // parameter로 받아오기

			LocalDate today = LocalDate.now();

			// 1. 예약 번호로 예약을 찾고
			if (checkReservation(con, reservation_code, customer_id)) {
				// 2. check_in 이전이면 delete
				if (checkCancleAvailbale(con, reservation_code, customer_id, Date.valueOf(today))) {
					// System.out.println("해당 예약 삭제 동작");
					deleteReservation(con, reservation_code);
				} else {
					System.out.println("취소가 불가능한 예약입니다.");
					System.out.println("체크인 날짜 이전에 취소가 가능합니다.");
				}
			} else {
				System.out.println("예약 내역이 존재하지 않습니다.");
			}
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

	// 예약 내역 확인
	public static boolean checkReservation(Connection con, int reservation_code, int customer_id) throws SQLException {
		boolean result = true;
		PreparedStatement stmt = con.prepareStatement(GET_RESERVATION);
		stmt.setInt(1, reservation_code);
		stmt.setInt(2, customer_id);
		ResultSet rs = stmt.executeQuery();
		if (!rs.next()) {
			result = false;
		}
		stmt.close();
		return result;
	}

	public static boolean checkCancleAvailbale(Connection con, int reservation_code, int customer_id, Date today)
			throws SQLException {
		boolean result = false;

		PreparedStatement stmt = con.prepareStatement(GET_RESERVATION);
		stmt.setInt(1, reservation_code);
		stmt.setInt(2, customer_id);
		ResultSet rs = stmt.executeQuery();

		if (rs.next() && (rs.getDate("check_in_date").compareTo(today) > 0)) {
			result = true;
		}
		stmt.close();
		return result;
	}

	public static void deleteReservation(Connection con, int reservation_code) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(DELETE_RESERVATION);
		stmt.setInt(1, reservation_code);
		stmt.executeUpdate();
		System.out.print("예약(예약번호: " + reservation_code + ")이 취소되었습니다.");
	}

}
