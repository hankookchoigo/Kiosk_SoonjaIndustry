package Launching;
// 모드 선택

import java.util.Scanner;

import Mode.ManagerMode;
import Mode.UserMode;

public class ModeSelect {
    public ModeSelect(int i) {
        Scanner stdIn = new Scanner(System.in);
        label:
        do {
            if(i == 0) {
                System.out.print("비밀번호를 입력하세요(종료하려면 0을 입력하세요): ");
            }
            int pwd = stdIn.nextInt();
            switch (pwd) {
                case 11111:
                    i = 0;
                    new ManagerMode();
                    break;
                case 24244:
                    i = 0;
                    new UserMode();
                    break;
                case 0:
                    break label;
                default:
                    System.out.print("비밀번호를 다시 입력해주세요: ");
                    new ModeSelect(1);
                    break;
            }
        } while (true);
    }
}
