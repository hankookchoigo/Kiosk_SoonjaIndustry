package Mode;
// 관리자 모드

import java.sql.SQLException;

public class ManagerMode extends Mode {
    
    // 생성자 메서드
    public ManagerMode() {
        connectDB();
        manageList();
        closeDB();
    }
    
    // 리스트 관리 메서드
    public void manageList() {
        label:
        do {
            if (!productList.isEmpty())
                productList.clear();
            getProductList();
            System.out.println("<< 상품 목록 >>");
            printProductList();
            
            label1:
            do {
                System.out.print("추가 상품 등록은 r, 상품 수정은 m, 상품 삭제는 d, 나가기는 e를 눌러주세요: ");
                String next = stdIn.next();
                switch (next) {
                    case "r":
                        regProduct();
                        break label1;
                    case "m":
                        System.out.print("변경할 상품 개수: ");
                        modProduct(stdIn.nextInt());
                        break label1;
                    case "d":
                        System.out.print("삭제할 상품 개수: ");
                        delProduct(stdIn.nextInt());
                        break label1;
                    case "e":
                        break label;
                    default:
                        System.out.println("r / m / d / e 에서 선택해주세요.\n");
                        break;
                }
            } while (true);
        } while (true);
    }
    
    // 상품 등록 메서드
    public void regProduct() {
        System.out.print("등록할 상품 개수를 입력해주세요: ");
        int pcount = stdIn.nextInt();   // 입력 횟수
        for (int i = 0; i < pcount; i++) {  // pcount만큼 이벤트 등록 반복 실행
            System.out.println("## 상품 정보를 등록하세요 ##");
            System.out.print("상품 아이디: ");     int id = stdIn.nextInt();
            System.out.print("이름: ");   String s = stdIn.nextLine(); String pname = stdIn.nextLine();
            System.out.print("가격: ");   int price = stdIn.nextInt();
            System.out.print("수량: ");   int quantity = stdIn.nextInt();
            String sql = "insert into productList values(?, ?, ?, ?)";
            try {
                // Statement 생성
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, id);
                pstmt.setString(2, pname);
                pstmt.setInt(3, price);
                pstmt.setInt(4, quantity);
                
                // SQL 문 전송
                pstmt.executeUpdate();
                System.out.println("등록 완료");
                Thread.sleep(1000);
            } catch(Exception e) {
                System.out.println("상품 등록 실패(Fail to register the products) - " + e.getMessage());
            }
        }
    }
    
    public void modProduct(int count) {
        for (int i = 0; i < count; i++) {  // count만큼 이벤트 수정 반복 실행
            System.out.println("## 상품 정보를 수정하세요 ##");
            System.out.println();
            printProductList();
            System.out.println();
            System.out.print("상품 아이디: ");     int id = stdIn.nextInt();
            System.out.print("이름: ");   String s = stdIn.nextLine(); String pname = stdIn.nextLine();
            System.out.print("가격: ");   int price = stdIn.nextInt();
            System.out.print("수량: ");   int quantity = stdIn.nextInt();
            String sql = "update productList set pname = ?, price = ?, quantity = ? where id = ?";
            try {
                // Statement 생성
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, pname);
                pstmt.setInt(2, price);
                pstmt.setInt(3, quantity);
                pstmt.setInt(4, id);
                
                
                // SQL 문 전송
                pstmt.executeUpdate();
                System.out.println("등록 완료");
                Thread.sleep(1000);
            } catch(Exception e) {
                System.out.println("상품 정보 수정 실패(Fail to modify the information of the product) - " + e.getMessage());
            }
        }
    }
    
    public void delProduct(int count) {
        try {
            pstmt = conn.prepareStatement("delete from productList where id = ?");
            
            for (int i = 0; i < count; i++) {
                System.out.println();
                printProductList();
                System.out.println();
                System.out.print("삭제할 아이디: ");
                int delId = stdIn.nextInt();
                pstmt.setInt(1, delId);
                pstmt.executeUpdate();
                System.out.println("삭제 완료");
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.out.println("상품 삭제 실패(Fail to delete product) - " + e.getMessage());
        }
    }
    
    @Override
    public void printProductList() {
        for (Product p : productList) {
            System.out.printf("[ %-4d ] %-12s (가격: %-6d / 수량: %-4d)\n", p.id, p.pname, p.price, p.quantity);
        }
    }
}
