package Managermode;

import java.sql.*;
import java.util.Scanner;
import Launching.Modeselect;

public class Managermode {
    Scanner scan = new Scanner(System.in);
    
    String jdbcDriver = "com.mysql.cj.jdbc.Driver";
    String jdbcUrl = "jdbc:mysql://localhost/javadb";
    Connection conn;
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    public Managermode() {
        System.out.println("## 이벤트 등록을 위해 상품 정보를 등록하세요 ##");
        System.out.print("이름(띄어쓰기 시 오류 발생, 띄어쓰기 없이 입력): ");
        String pname = scan.nextLine();
        System.out.print("가격: ");
        int price = scan.nextInt();
        System.out.print("수량: ");
        int quantity = scan.nextInt();
        
        connectDB();
        regProduct(pname, price, quantity);
        printList();
        closeDB();
    }
    
    public void connectDB() {
        try {
            // 1단계 : JDBC 드라이버 로드
            Class.forName(jdbcDriver);
            
            // 2단계 : 데이터베이스 연결
            conn = DriverManager.getConnection(jdbcUrl, "Kiosk", "Qhrb24244!!");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void regProduct(String pname, int price, int quantity) {
        String sql = "insert into productList(pname, price, quantity) values(?, ?, ?)";
        try {
            // 3단계 : Statement 생성
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, pname);
            pstmt.setInt(2, price);
            pstmt.setInt(3, quantity);
            
            // 4단계 : SQL 문 전송
            pstmt.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void printList() {
        System.out.println("# 상품 목록 정보 #");
        String sql = "select * from productList";
        try {
            pstmt = conn.prepareStatement(sql);
            
            // 5단계 : 결과받기
            rs = pstmt.executeQuery();
            while(rs.next()) {
                System.out.println("상품 이름 / 가격 / 수량: " + rs.getString(2) + " / " + rs.getInt(3) + " / " + rs.getInt(4));
            }
            System.out.print("추가 상품 등록은 p, 나가시려면 e를 눌러주세요: ");
            String next = scan.next();
            switch(next) {
                case "p":
                    new Managermode();
                case "e":
                    new Modeselect(0);
                default:
                    System.out.println("p 혹은 e 키 중 하나를 눌러 다시 선택해주세요.");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void closeDB() {
        try {
            // 6단계 : 연결 해제
            pstmt.close();
            conn.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
