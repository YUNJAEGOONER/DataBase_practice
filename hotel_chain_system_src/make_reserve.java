package hotel_chain_system;

import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class make_reserve {

	// room_id를 통해 예약을 가능하도록 하기

	// 해당 room_id에 대한 모든 예약을 조회하기
	public static final String GET_ROOM_AVAILABLE = "SELECT room_id, check_in_date, check_out_date "
			+ "FROM Reservation WHERE room_id = ? " + "UNION "
			+ "SELECT room_id, check_in AS check_in_date, check_out_date " + "FROM check_in WHERE room_id = ?";

	public static final String CREATE_RESERVE = "INSERT INTO reservation (customer_id, room_id, check_in_date, check_out_date, total_price) VALUES (?, ?, ?, ?, ?)";

	public static final String GET_ROOM_FEE = "SELECT price FROM Room WHERE room_id = ?";

	public static final String GET_ROOM = "SELECT * FROM room where room_id = ?";

	public static Date in;

	public static Date out;

	public static void main(int cid) throws SQLException {
		Connection con = null;
		try {
			con = DBConnection.getConnection();
			Scanner sc = new Scanner(System.in);

			System.out.println("1 : 예약가능한 객실 유형 및 비용 확인하기");
			System.out.println("2 : 예약진행하기");
			System.out.print("메뉴 선택: ");
			int cmd = sc.nextInt();

			if (cmd == 1) {
				check_avaliable_room.main(null);
			} else if (cmd == 2) {

				System.out.println("예약을 위해 객실ID, 체크인 날짜, 체크아웃 날짜를 입력해주세요");

				// transaction
				con.setAutoCommit(false);
				con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

				// room_id를 입력하고, check_in , check-out 입력하기
				System.out.print("ROOM_ID : ");
				int room_id = sc.nextInt();
				sc.nextLine();

				// int room_id = 3;
				int customer_id = cid; // 파라미터로 넘기기
				if (avaiableID(con, room_id)) {
					System.out.print("Check in Date : ");
					String check_in = sc.nextLine();

					while (!isValidDate(check_in)) {
						System.out.println("유효하지 않은 날짜 형식입니다. 다시 입력해주세요");
						System.out.print("Check in Date : ");
						check_in = sc.nextLine();
					}

					System.out.print("Check out Date : ");
					String check_out = sc.nextLine();

					while (!isValidDate(check_out)) {
						System.out.println("유효하지 않은 날짜 형식입니다. 다시 입력해주세요");
						System.out.print("Check out Date : ");
						check_out = sc.nextLine();
					}

					in = Date.valueOf(check_in);
					out = Date.valueOf(check_out);
					if (in.compareTo(out) == 0) {
						System.out.println("하루 이상 숙박이 필수 입니다");
						// System.out.println("Invalid input");

					} else if (in.compareTo(out) > 0) {
						System.out.println("체크인 날짜는 체크아웃 날짜보다 빨라야 합니다.");
						// System.out.println("Invalid input");
					} else {

						// 1. 해당 room_id에 대한 예약을 조회하고, 체크인 날짜, 체크아웃 날짜 확인하기

						ResultSet rs = getReservation(con, room_id);

						// System.out.println("Valid input");
						boolean flag = true;

						in = Date.valueOf(check_in);
						out = Date.valueOf(check_out);

						while (rs.next()) {
							if ((out.compareTo(rs.getDate("check_in_date")) <= 0)
									|| (in.compareTo(rs.getDate("check_out_date")) >= 0)) {
							} else {
								System.out.println("해당 기간에 대한 예약이 이미 존재합니다.");
								flag = false;
								break;
							}
						}
						;

						if (flag) {
							System.out.println("예약을 진행합니다");
							int nights = countDay(in, out);

							BigDecimal fee = calPrice(con, room_id, nights);
							System.out.println("예상 숙박 금액은 " + fee + "입니다.");
							// 2. 조건을 충족하면 예약을 생성하기
							createReservation(con, customer_id, room_id, fee);
						}
						con.commit();
					}
				} else {
					System.out.print("RoomID를 다시확인하고 예약해 주세요");
				}

			} else {
				System.out.println("잘못된 입력입니다.");

			}

		} catch (SQLException e) {
			System.out.print("RoomID를 다시확인하고 예약해 주세요");
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

	public static boolean isValidDate(String dateString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(false); // 엄격한 형식 검사
		try {
			sdf.parse(dateString); // 날짜를 파싱
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	public static ResultSet getReservation(Connection con, int room_id) throws SQLException {

		PreparedStatement stmt = con.prepareStatement(GET_ROOM_AVAILABLE);
		stmt.setInt(1, room_id);
		stmt.setInt(2, room_id);

		ResultSet rs = stmt.executeQuery();

		return rs;
	}

	// 예약을 생성한다.
	public static void createReservation(Connection con, int customer_id, int room_id, BigDecimal total_fee)
			throws SQLException {

		PreparedStatement stmt = con.prepareStatement(CREATE_RESERVE);

		stmt.setInt(1, customer_id);
		stmt.setInt(2, room_id);
		stmt.setDate(3, in);
		stmt.setDate(4, out);
		stmt.setBigDecimal(5, total_fee);

		stmt.executeUpdate();
		System.out.println(in + "-" + out + "기간에 " + room_id + " 번 방이 예약되었습니디.");
		stmt.close();
	}

	// 요금 계산하기
	public static BigDecimal calPrice(Connection con, int room_id, int nights) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(GET_ROOM_FEE);
		stmt.setInt(1, room_id);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int price = rs.getInt(1);
		BigDecimal fee = BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(nights));
		return fee;
	}

	public static int countDay(Date check_in, Date check_out) {
		long diffInMillis = check_out.getTime() - check_in.getTime();
		int diffInDays = (int) TimeUnit.MILLISECONDS.toDays(diffInMillis);
		System.out.println("숙박 기간은 " + diffInDays + "박 입니다.");
		return diffInDays;
	}

	public static boolean avaiableID(Connection con, int rid) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(GET_ROOM);
		stmt.setInt(1, rid);
		ResultSet rs = stmt.executeQuery();
		if (!rs.next())
			return false;
		return true;
	}

}
