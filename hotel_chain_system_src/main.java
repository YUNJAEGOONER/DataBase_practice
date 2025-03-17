package hotel_chain_system;

import java.sql.*;
import java.util.Scanner;

public class main {

	public static final String GET_CUSTOMER = "SELECT * FROM customer where customer_id = ?";

	public static void main(String[] args) {
		try {
			Scanner sc = new Scanner(System.in);
			System.out.println("========== 호텔에 오신걸 환영합니다. ==========");
			System.out.print("ID : ");
			String user_id = sc.nextLine();

			if (user_id.equals("manager")) {
				managerProgram();
			} else if (user_id.equals("customer")) {
				System.out.print("Customer ID: ");
				int cid = sc.nextInt();
				Connection con = DBConnection.getConnection();
				if(avaiableID(con, cid)) {
					customerProgram(cid);
				}
				else {
					System.out.print("ID가 " + cid + "인 고객이 존재하지 않습니다.");
					return;
				}

			} else {
				System.out.print("잘못된 입력입니다.");
				return;
			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void managerProgram() throws SQLException {
		Scanner sc = new Scanner(System.in);
		while (true) {
			printManagerProgram();
			String cmd = sc.nextLine();
			switch (cmd) {
			case "1":
				show_occupied_rooms.main(null);
				break;
			case "2":
				check_in.main(null);
				break;
			case "3":
				check_out.main(null);
				break;
			case "4":
				allocate_houskeeper.main(null);
				break;
			case "5":
				show_housekeeping.main(null);
				break;
			case "6":
				mark_serviced.main(null);
				break;
			case "7":
				show_all_reservation.main(null);
				break;
	
			case "0":
				System.out.println("프로그램이 종료됩니다.");
				return;
			default:
				System.out.println("유효하지 않은 입력입니다. 다시 시도해주세요.");
				continue;
			}
			delayProgram();
			String delay = sc.nextLine();
			switch (delay) {
			case "1":
				break;
			default:
				System.out.println("프로그램이 종료됩니다.");
				return;
			}
		}
	}

	public static void printManagerProgram() {
		System.out.println("==========관리자 메뉴 ==========");
		System.out.println("1 : 현재 점유된 방을 본다."); // show_occupied_rooms(check_in 현황)
		System.out.println("2 : 체크인"); // check_in
		System.out.println("3 : 체크아웃"); // check_out
		System.out.println("4 : 하우스 키핑 배정"); // check_in 상태의 방에 배정
		System.out.println("5 : 하우스 키핑 할당 나열"); //
		System.out.println("6 : 객실 상태 정보 출력"); //
		System.out.println("7 : 모든 예약 정보 출력"); //
		System.out.println("0 : 프로그램 종료");
		System.out.print("메뉴 선택: ");
	}

	public static void customerProgram(int cid) throws SQLException {
		Scanner sc = new Scanner(System.in);
		while (true) {
			printCustomerProgram();
			String cmd = sc.nextLine();
			switch (cmd) {
			case "1":
				show_every_room.main(null);
				break;
			case "2":
				check_avaliable_room.main(null);
				break;
			case "3":
				make_reserve.main(cid);
				break;
			case "4":
				cancel_reservation.main(cid);
				break;
			case "5":
				show_my_reservation.main(cid);
				break;
			case "0":
				System.out.println("프로그램이 종료됩니다.");
				return;
			default:
				System.out.println("유효하지 않은 입력입니다. 다시 시도해주세요.");
				continue;
			}
			delayProgram();
			String delay = sc.nextLine();
			switch (delay) {
			case "1":
				break;
			default:
				System.out.println("프로그램이 종료됩니다.");
				return;
			}
		}
	}

	public static void printCustomerProgram() {
		System.out.println("========== 사용자 메뉴 ==========");
		System.out.println("1 : OO 호텔의 모든방을 조회하기"); // show every room
		System.out.println("2 : 예약가능한 객실 유형 및 비용 확인하기"); // check available room(by brand name)
		System.out.println("3 : 예약하기"); // make_reserve
		System.out.println("4 : 예약 취소하기"); // cancel reservation(by 예약번호)
		System.out.println("5 : 나의 예약 조회하기"); // show_my_reservation (by customer_id);
		System.out.println("0 : 프로그램 종료");
		System.out.print("메뉴 선택: ");

	}

	public static void delayProgram() {
		System.out.println(" ");
		System.out.println("1 : 상위 메뉴로 돌아가기");
		System.out.println("프로그램을 종료하려면 아무 버튼을 누르세요");
		System.out.print("메뉴 선택: ");
	}
	
	public static boolean avaiableID(Connection con, int uid) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(GET_CUSTOMER);
		stmt.setInt(1, uid);
		ResultSet rs = stmt.executeQuery();
		if(!rs.next())return false;
		return true;
	}

}
