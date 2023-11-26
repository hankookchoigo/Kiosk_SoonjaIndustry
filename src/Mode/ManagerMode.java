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
                System.out.print("추가 상품 등록은 r, 상품 삭제는 d, 나가기는 e를 눌러주세요: ");
                String next = stdIn.next();
                switch (next) {
                    case "r":
                        regProduct();
                        break label1;
                    case "d":
                        System.out.print("삭제할 번호: ");
                        int delIdx = stdIn.nextInt() - 1;
                        delProduct(delIdx);
                        break label1;
                    case "e":
                        break label;
                    default:
                        System.out.println("r / c / d / e 에서 선택해주세요.\n");
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
            System.out.println("## 이벤트 등록을 위해 상품 정보를 등록하세요 ##");
            System.out.print("이름: ");   String s = stdIn.nextLine();    String pname = stdIn.nextLine();
            System.out.print("가격: ");   int price = stdIn.nextInt();
            System.out.print("수량: ");   int quantity = stdIn.nextInt();
            String sql = "insert into productList(pname, price, quantity) values(?, ?, ?)";
            try {
                // Statement 생성
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, pname);
                pstmt.setInt(2, price);
                pstmt.setInt(3, quantity);
                
                // SQL 문 전송
                pstmt.executeUpdate();
                System.out.println("등록 완료");
                Thread.sleep(1000);
            } catch(Exception e) {
                System.out.println("상품 등록 실패(Fail to register the products) - " + e.getMessage());
            }
        }
    }
    
    public void delProduct(int delIdx) {
        String targetPname = productList.get(delIdx).pname;
        try {
            pstmt = conn.prepareStatement("delete from productList where pname = ?");
            pstmt.setString(1, targetPname);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("상품 삭제 실패(Fail to delete product) - " + e.getMessage());
        }
    }
}
