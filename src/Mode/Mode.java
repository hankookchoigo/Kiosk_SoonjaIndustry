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
        int id;
        String pname;
        int price;
        int quantity;
        public Product(int id, String pname, int price, int quantity) {
            this.id = id;
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
                Product p = new Product(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4));
                productList.add(p);
            }
        } catch (SQLException e) {
            System.out.println("상품 목록 받아오기 실패(Fail to load product list) - " + e.getMessage());
        }
    }
    
    // 상품 목록 출력
    public void printProductList() {
        for (int i = 0; i < productList.size(); i++) {
            Product p = productList.get(i);
            System.out.printf("[ %-3d ] %-12s (가격: %-6d / 수량: %-4d)\n", i + 1 , p.pname, p.price, p.quantity);
        }
    }
    
    // 장바구니를 위한 객체 생성
    public static class Cart {
        int id;
        String pname;
        int price;
        int buyQuantity;
        int restQuantity;
        public Cart(int id, String pname, int price, int buyQuantity, int restQuantity) {
            this.id = id;
            this.pname = pname;
            this.price = price;
            this.buyQuantity = buyQuantity;
            this.restQuantity = restQuantity;
        }
    }
    
    // 장바구니에 상품 추가
    public void addOnCartList(int id, String pname, int price, int buyQuantity, int restQuantity) {
        int commonidExist = 0;
        int oldBuyQuan = 0;
        int oldResQuan = 0;
        for (Cart c : cartList)
            if (id == c.id) {
                commonidExist = 1;
                break;
            }
        try {
            if (commonidExist == 1) {
                pstmt = conn.prepareStatement("select buyQuantity, restQuantity from cart where id = ?");
                pstmt.setInt(1, id);
                
                pstmt.executeQuery();
                
                while (rs.next()) {
                    oldBuyQuan = rs.getInt(1);
                    oldResQuan = rs.getInt(2);
                }
                
                pstmt = conn.prepareStatement("insert into cart(buyQuantity, restQuantity) values (?, ?)");
                pstmt.setInt(1, oldBuyQuan + buyQuantity);
                pstmt.setInt(2, oldResQuan - buyQuantity);
            } else {
                Cart c = new Cart(id, pname, price, buyQuantity, restQuantity);
                cartList.add(c);
                pstmt = conn.prepareStatement("insert into cart values(?, ?, ?, ?, ?)");
                pstmt.setInt(1, c.id);
                pstmt.setString(2, c.pname);
                pstmt.setInt(3, c.price);
                pstmt.setInt(4, c.buyQuantity);
                pstmt.setInt(5, c.restQuantity);
                
                pstmt.executeUpdate();
            }
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
                String pname = rs.getString(2);
                int price = rs.getInt(3);
                int buyQuantity = rs.getInt(4);
                int entirePrice = price * buyQuantity;
                System.out.printf("[ %-3d ] %-12s (%-6d원 X %-4d개 = %-6d원)\n", ++i, pname, price, buyQuantity, entirePrice);
                sum += entirePrice;
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
                 System.out.println("[1], [2], [3], [4] 중에서 다시 입력해주세요.");
        } while (true);
    }
    
    public void updateQuantity() {
        try {
            for (Cart c : cartList) {
                pstmt = conn.prepareStatement("update productList set quantity = ? where id = ?");
                pstmt.setInt(1, c.restQuantity);
                pstmt.setInt(2, c.id);
                
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("상품 수량 업데이트 실패(Fail to update productList) - " + e.getMessage());
        }
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
}
