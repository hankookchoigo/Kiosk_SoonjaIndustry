package Mode;
// 모드 클래스의 틀

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Mode {
    Scanner stdIn = new Scanner(System.in);
    
    String jdbcDriver = "com.mysql.cj.jdbc.Driver";
    String jdbcUrl = "jdbc:mysql://localhost/javadb";
    Connection conn;
    
    PreparedStatement pstmt;
    ResultSet rs;
    
    ArrayList<Product> productList = new ArrayList<>();
    ArrayList<Cart> cartList = new ArrayList<>();
    
    // 데이터베이스 연결 메서드
    public void connectDB() {
        try {
            // JDBC 드라이버 로드
            Class.forName(jdbcDriver);
            
            // 데이터베이스 연결
            conn = DriverManager.getConnection(jdbcUrl, "Kiosk", "Qhrb24244!!");
        } catch(Exception e) {
            System.out.println("연결 오류 발생(Fail to connect to database) - " + e.getMessage());
        }
    }
    
    // 데이터베이스 연결 해제 메서드
    public void closeDB() {
        try {
            pstmt.close();
            conn.close();
        } catch(Exception e) {
            System.out.println("연결 해제 중 오류 발생(Error occurs while disconnecting) - " + e.getMessage());
        }
    }
    
    // 상품을 위한 객체 생성
    public static class Product {
        String pname;
        int price;
        int quantity;
        public Product(String pname, int price, int quantity) {
            this.pname = pname;
            this.price = price;
            this.quantity = quantity;
        }
    }
    
    // 상품 목록 얻기
    public void getProductList() {
        try {
            pstmt = conn.prepareStatement("select * from productList");
            rs = pstmt.executeQuery();
            while(rs.next()) {
                Product p = new Product(rs.getString(1), rs.getInt(2), rs.getInt(3));
                productList.add(p);
            }
        } catch (SQLException e) {
            System.out.println("상품 목록 받아오기 실패(Fail to load product list) - " + e.getMessage());
        }
    }
    
    // 상품 목록 출력
    public void printProductList() {
        int i = 0;
        for (Product p : productList)
            System.out.printf("[ %-3d ] %-12s (가격: %-6d / 수량: %-4d)\n", ++i , p.pname, p.price, p.quantity);
    }
    
    // 장바구니를 위한 객체 생성
    public static class Cart {
        String pname;
        int price;
        int buyAmount;
        int restAmount;
        public Cart(String pname, int price, int buyAmount, int restAmount) {
            this.pname = pname;
            this.price = price;
            this.buyAmount = buyAmount;
            this.restAmount = restAmount;
        }
    }
    
    // 장바구니에 상품 추가
    public void addOnCartList(int idx, int buyAmount, int restAmount) {
        Cart c = new Cart(productList.get(idx).pname, productList.get(idx).price, buyAmount, restAmount);
        cartList.add(c);
        try {
            pstmt = conn.prepareStatement("insert into cart values(?, ?, ?, ?)");
            pstmt.setString(1, c.pname);
            pstmt.setInt(2, c.price);
            pstmt.setInt(3, buyAmount);
            pstmt.setInt(4, restAmount);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("장바구니에 항목 추가 실패(Fail to add the product selected) - " + e.getMessage());
        }
    }
    
    // 장바구니 출력
    public void printCart() {
        try {
            pstmt = conn.prepareStatement("select * from cart");
            rs = pstmt.executeQuery();
            
            int i = 0;
            int sum = 0;
            while (rs.next()) {
                int price = rs.getInt(2);
                int buyAmount = rs.getInt(3);
                System.out.printf("[ %-3d ] %-12s (가격: %-6d / 구매량: %-4d)\n", ++i, rs.getString(1), price, buyAmount);
                sum += price * buyAmount;
            }
            System.out.println("합계: " + sum);
            do {
                System.out.print("결제하시려면 [p], 이전으로 돌아가시려면 [b]를 입력해주세요: ");
                String sel = stdIn.next();
                if (sel.equals("p")) {
                    pay();
                    updateQuantity();
                    resetCart();
                    break;
                }
                else if (sel.equals("b"))
                    break;
                else
                    System.out.println("[b]나 [p] 중 하나로 다시 입력해주세요.");
            } while (true);
        } catch (SQLException e) {
            System.out.println("장바구니 출력 실패(Fail to load cart) - " + e.getMessage());
        }
    }
    
    // 결제하기(바뀐 상품 수량 반영)
    public void pay() {
        do {
            System.out.print("카드 결제는 [1], 삼성 페이는 [2], 카카오 페이는 [3], 뒤로 가기는 [4]를 입력해주세요: ");
            int payType = stdIn.nextInt();
            if (payType == 1 || payType == 2 || payType == 3 || payType == 4) {
                if (payType == 1)
                    System.out.println("카드로 결제되었습니다.");
                else if (payType == 2)
                    System.out.println("삼성 페이로 결제되었습니다.");
                else if (payType == 3)
                    System.out.println("카카오 페이로 결제되었습니다.");
                break;
            }
             else
                 System.out.println("[1], [2], [3] 중에서 다시 입력해주세요.");
        } while (true);
    }
    
    public void resetCart() {
        cartList.clear();
        try {
            pstmt = conn.prepareStatement("truncate cart");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("장바구니 초기화 실패(Fail to reset cart) - " + e.getMessage());
        }
    }
    
    public void updateQuantity() {
        try {
            for (Cart c : cartList) {
                pstmt = conn.prepareStatement("update productList set quantity = ? where pname = '?'");
                pstmt.setInt(1, c.restAmount);
                pstmt.setString(2, c.pname);
                
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("상품 수량 업데이트 실패(Fail to update productList) - " + e.getMessage());
        }
    }
}
