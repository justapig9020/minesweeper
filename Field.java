public class Field {
    public enum Type {
        Mine, Space, Sign
    }
    private Block[][] blocks;
    public Field(int xsize, int ysize) {
        this.blocks = new Block[xsize][ysize];

        this.blocks[0][0] = new Space();
        this.blocks[0][1] = new Space();
        this.blocks[0][2] = new Space();
        this.blocks[0][3] = new Space();
        this.blocks[0][4] = new Space();

        this.blocks[1][0] = new Space();
        this.blocks[1][1] = new Sign(1);
        this.blocks[1][2] = new Sign(1);
        this.blocks[1][3] = new Sign(1);
        this.blocks[1][4] = new Space();

        this.blocks[2][0] = new Sign(1);
        this.blocks[2][1] = new Sign(2);
        this.blocks[2][2] = new Mine();
        this.blocks[2][3] = new Sign(1);
        this.blocks[2][4] = new Space();

        this.blocks[3][0] = new Sign(1);
        this.blocks[3][1] = new Mine();
        this.blocks[3][2] = new Sign(2);
        this.blocks[3][3] = new Sign(1);
        this.blocks[3][4] = new Space();

        this.blocks[4][0] = new Sign(1);
        this.blocks[4][1] = new Sign(1);
        this.blocks[4][2] = new Sign(1);
        this.blocks[4][3] = new Space();
        this.blocks[4][4] = new Space();
    }
    public boolean leftclick(int x, int y) {
        Block block = this.blocks[x][y];
        return block.leftclick();
    }
    public void print() {
        for(Block[] i: this.blocks) {
            for(Block j: i) {
                char s;
                if (j != null)
                    s = j.symbol();
                else
                    s = ' ';
                System.out.printf("|%c", s);
            }
            System.out.println("|");
            for(int j=0; j<i.length; j++) {
                System.out.printf("+-");
            }
            System.out.println("+");
        }
    }
    private enum State {
        Unknow, Flaged, Doubtful, Verified,
    }
    private abstract class Block {
        protected State state;
        public Block() {
            this.state = State.Unknow;
        }
        public char symbol() {
            switch (this.state) {
                case Flaged:
                    return 'F';
                case Doubtful:
                    return '?';
                case Unknow:
                    return ' ';
                default:
                    return ' ';
            }
        }
        abstract public boolean leftclick();
    }
    private class Mine extends Block {
        public char symbol() {
            if (this.state == State.Verified) {
                return '*';
            }
            return super.symbol();
        }
        public boolean leftclick() {
            this.state = State.Verified;
            return false;
        }
    }
    private class Space extends Block {
        public char symbol() {
            if (this.state == State.Verified) {
                return '#';
            }
            return super.symbol();
        }
        public boolean leftclick() {
            this.state = State.Verified;
            return true;
        }
    }
    private class Sign extends Block {
        private int num;
        public Sign(int num) {
            this.num = num;
        }
        public char symbol() {
            if (this.state == State.Verified) {
                return (char)(this.num + '0');
            }
            return super.symbol();
        }
        public boolean leftclick() {
            this.state = State.Verified;
            return true;
        }
    }
}
