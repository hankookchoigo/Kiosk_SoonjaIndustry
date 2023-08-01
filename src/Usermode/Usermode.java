package Usermode;

import java.sql.*;
import java.util.Scanner;

public class Usermode {
    Scanner scan = new Scanner(System.in);
    
    String jdbcDriver = "com.mysql.cj.jdbc.Driver";
    String jdbcUrl = "jdbc:mysql://localhost/javadb";
    Connection conn;
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    public Usermode() {
        connectDB();
        printListAndSelect();
        closeDB();
    }
    
    public void connectDB() {
        try {
            Class.forName(jdbcDriver);
            
            conn = DriverManager.getConnection(jdbcUrl, "Kiosk", "Qhrb24244!!");
        } catch(Exception e) {
            System.out.println("연결 오류 발생(fail to connect to database)" + e.getMessage());
        }
    }
    
    public void printListAndSelect() {
        int keyNum = 0;
        System.out.println("상품 목록");
        String sql = "select * from productList";
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while(rs.next()) {
                System.out.printf("[%d] %s (가격: %d, 수량: %d)\n", rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4));
            }
            System.out.println("[] 안의 번호를 선택해 장바구니에 상품 담기");
            System.out.println("혹은 [c]를 눌러 장바구니 및 결제하기 창 띄우기");
            System.out.print("선택: ");
            keyNum = scan.nextInt();
        } catch(SQLException e) {
            System.out.println("정보 로딩 실패(fail to load data)" + e.getMessage());
        }
        
        try {
            pstmt = conn.prepareStatement("select pname, price, quantity from productList where id = ?");
            pstmt.setInt(1, keyNum);
            rs = pstmt.executeQuery();
            
            pstmt = conn.prepareStatement("insert into cart(pname, price, quantity) values(?, ?, ?)");
            while(rs.next()) {
                pstmt.setString(1, rs.getString(1));
                pstmt.setInt(2, rs.getInt(2));
                pstmt.setInt(3, rs.getInt(3));
                pstmt.executeUpdate();
            }
            printListAndSelect();
        } catch(SQLException e) {
            System.out.println("장바구니에 항목 추가 실패(fail to add the product selected)" + e.getMessage());
        }
    }
    
    public void closeDB() {
        try {
            pstmt.close();
            conn.close();
        } catch(Exception e) {
            System.out.println("연결 해제 중 오류 발생(find an error while disconnecting)" + e.getMessage());
        }
    }
}
