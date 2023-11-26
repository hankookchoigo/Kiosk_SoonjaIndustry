package Mode;
// 사용자 모드

public class UserMode extends Mode {
    public UserMode() {
        connectDB();
        kioskMain();
        closeDB();
    }
    
    // 사용자 모드로 키오스크 사용하기
    public void kioskMain() {
        do {
            if (!productList.isEmpty())
                productList.clear();
            getProductList();
            System.out.println();
            System.out.println("반갑습니다! 순자 키오스크입니다!");
            System.out.println();
            System.out.println("<< 상품 목록 >>");
            
            printProductList();
            
            System.out.println("[] 안의 번호를 선택해 상품을 선택하거나 [c]를 눌러 장바구니 보기");
            System.out.print("선택: ");
            String sel = stdIn.next();
            
            if (sel.equals("c"))
                printCart();
            else if (sel.equals("관리자_종료"))
                break;
            else {
                int bIdx = Integer.parseInt(sel) - 1;   // 구매 상품의 인덱스
                Product buyPro = productList.get(bIdx); // 구매 상품 정보를 위해 상품 가져오기
                int thisQuantity = buyPro.quantity; // 구매 상품의 수량 가져오기
                
                if (thisQuantity <= 0)
                    System.out.println("해당 상품은 품절되었습니다. 다른 상품을 선택해주세요.");
                else {
                    System.out.print("구매할 상품 개수 입력(남은 수량: " + thisQuantity + " 개): ");
                    int buyQuantity = stdIn.nextInt();  // 구매자가 원하는 수량
                    int lackQuantity = buyQuantity - thisQuantity;    // 구매 시 남는 상품 수량
                    
                    if (lackQuantity > 0) {
                        System.out.println("해당 상품의 수량이 " + lackQuantity + "개 부족합니다. 수량을 줄이거나 다른 상품을 선택해주세요.");
                    }
                    else {
                        addOnCartList(bIdx, buyQuantity, -lackQuantity);
                        System.out.printf("[ %-3d ] %-12s (가격: %-6d / 구매량: %-4d) 상품이 장바구니에 담겼습니다.\n", bIdx + 1, buyPro.pname, buyPro.price, buyQuantity);
                    }
                }
            }
        } while (true);
    }
}
