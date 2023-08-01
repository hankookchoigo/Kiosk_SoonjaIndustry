package Launching;

import java.util.Scanner;
import Usermode.Usermode;
import Managermode.Managermode;

public class Modeselect {
    public Modeselect(int i) {
        Scanner sc = new Scanner(System.in);
        if(i == 0) {
            System.out.print("비밀번호를 입력하세요: ");
        }
        int pwd = sc.nextInt();
        switch(pwd) {
            case 11111:
                new Managermode();
                break;
            case 24244:
                new Usermode();
                break;
            default:
                System.out.print("비밀번호를 다시 입력해주세요: ");
                new Modeselect(1);
        }
    }
}
