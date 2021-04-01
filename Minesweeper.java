import java.util.Scanner;

public class Minesweeper {
    public static void main(String[] args) {
        Field f = new Field(5, 5, 2);
        Scanner stdin = new Scanner(System.in);
        String frame;
        String cmd;
        int x, y;
        do {
            frame = f.render();
            System.out.println(frame);
            System.out.printf("X: ");
            x = stdin.nextInt();
            System.out.printf("Y: ");
            y = stdin.nextInt();
            frame = f.render_select(x, y);
            System.out.println(frame);
            System.out.printf("(r)ight click/(l)eft click: ");
            stdin.nextLine();
            cmd = stdin.nextLine();
            if (cmd.charAt(0) == 'r') {
                f.rightclick(x, y);
            } else {
                f.leftclick(x, y);
            }
        } while(!f.is_gameset());
        frame = f.render();
        System.out.println(frame);
        if (f.is_win())
            System.out.println("You Win!");
        else
            System.out.println("You lose!");
    }
}
