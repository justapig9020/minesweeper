import java.util.Scanner;

public class Minesweeper {
    public static void main(String[] args) {
        Field f = new Field(5, 5);
        Scanner stdin = new Scanner(System.in);
        String frame;
        String cmd;
        int x, y;
        boolean unsolve = true;
        do {
            frame = f.render();
            System.out.println(frame);
            System.out.printf("X: ");
            x = stdin.nextInt();
            System.out.printf("Y: ");
            y = stdin.nextInt();
            System.out.println("R/L");
            stdin.nextLine();
            cmd = stdin.nextLine();
            if (cmd.charAt(0) == 'r') {
                f.rightclick(x, y);
            } else {
                unsolve = f.leftclick(x, y);
            }
        } while(unsolve);
        frame = f.render();
        System.out.println(frame);
    }
}
