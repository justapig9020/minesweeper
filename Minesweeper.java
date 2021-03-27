import java.util.Scanner;

public class Minesweeper {
    public static void main(String[] args) {
        Field f = new Field(5, 5);
        Scanner stdin = new Scanner(System.in);
        int x, y;
        do {
            f.print();
            System.out.printf("X: ");
            x = stdin.nextInt();
            System.out.printf("Y: ");
            y = stdin.nextInt();
        } while(f.leftclick(x, y));
        f.print();
    }
}
