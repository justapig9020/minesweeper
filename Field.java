import java.lang.Math;

public class Field {
    private Block[][] blocks;
    private int safe_amount;
    private int mine_amount;
    private int flaged_mine_amount;
    private int verified_amount;
    private boolean gameset;
    private int total, xsize, ysize;
    public Field(int xsize, int ysize, int mine_amount) {
        this.total = xsize * ysize;
        this.xsize = xsize;
        this.ysize = ysize;
        this.safe_amount = this.total - mine_amount;
        this.mine_amount = mine_amount;
        this.flaged_mine_amount = 0;
        this.verified_amount = 0;
        this.gameset = false;
        this.blocks = new Block[xsize][ysize];

        for (int i=0; i<mine_amount; i++) {
            int x = (int)(Math.random() * xsize);
            int y = (int)(Math.random() * ysize);
            Block block = this.blocks[x][y];
            if (block == null) {
                block = new Mine();
            } else {
                block = block.set_as_mine();
            }
            if (block != null) {
                for (int x_off=-1; x_off<=1; x_off++) {
                    for (int y_off=-1; y_off<=1; y_off++) {
                        int curr_x = x + x_off;
                        int curr_y = y + y_off;
                        if (x_off == 0 && y_off == 0)
                            continue;
                        if (this.valid_coordinate(curr_x, curr_y)) {
                            Block modify = this.blocks[curr_x][curr_y];
                            if (modify == null)
                                this.blocks[curr_x][curr_y] = new Sign(1);
                            else
                                modify.add_surrounding_mine();
                        }
                    }
                }
                this.blocks[x][y] = block;
            } else {
                /* Already a mine there. */
                i -= 1;
            }
        }
        for (int i=0; i<this.xsize; i++) {
            for (int o=0; o<this.ysize; o++) {
                Block check = this.blocks[i][o];
                if (check == null)
                    this.blocks[i][o] = new Space();
            }
        }
    }
    private boolean valid_coordinate(int x, int y) {
        boolean x_valid = (x >= 0) && (x < this.xsize);
        boolean y_valid = (y >= 0) && (y < this.ysize);
        return x_valid && y_valid;
    }
    private void spread(int x, int y) {
        Node[] stack = new Node[this.total];
        int top = 0;
        stack[top] = new Node(x, y);
        top += 1;
        while (top > 0) {
            top -= 1;
            Node curr = stack[top];
            while (curr.next_step()) {
                int next_x = curr.get_x();
                int next_y = curr.get_y();
                if (!this.valid_coordinate(next_x, next_y)) {
                    continue;
                }
                Block next_block = this.blocks[next_x][next_y];
                if (next_block.try_spread()) {
                    this.verified_amount += 1;
                    if (next_block.keep_going()) {
                        stack[top] = new Node(next_x, next_y);
                        top += 1;
                    }
                }
            }
        }
    }
    public void leftclick(int x, int y) {
        Block block = this.blocks[x][y];
        if (!block.is_verified()) {
            /* leftclick function return false on the click failed.
             * Which means the block is mine.
             */
            boolean is_mine = !block.leftclick();
            if (is_mine) {
                this.gameset = true;
            } else {
                this.verified_amount += 1;
                this.spread(x, y);
                if (this.verified_amount == this.safe_amount)
                    this.gameset = true;
            }
        }
    }
    public boolean is_gameset() {
        return this.gameset;
    }
    public boolean is_win() {
        boolean all_verified = this.verified_amount == this.safe_amount;
        boolean all_flaged = this.flaged_mine_amount == this.mine_amount;
        return all_verified || all_flaged;
    }
    public void rightclick(int x, int y) {
        Block block = this.blocks[x][y];
        this.flaged_mine_amount += block.rightclick();
        if (this.flaged_mine_amount == this.mine_amount)
            this.gameset = true;
    }
    public String render_select(int x, int y) {
        String ret = "";
        for (int i=0; i<this.ysize; i++) {
            ret += "+-";
        }
        ret += "+\n";
        for (int i=0; i<this.xsize; i++) {
            for (int j=0; j<this.ysize; j++) {
                if (i == x && j == y) {
                    ret += "|O";
                    continue;
                }
                Block curr = this.blocks[i][j];
                char s;
                if (curr != null)
                    s = curr.symbol();
                else
                    s = ' ';
                ret += "|" + s;
            }
            ret += "|\n";
            for (int j=0; j<this.ysize; j++) {
                ret += "+-";
            }
            ret += "+\n";
        }
        return ret;
    }
    public String render() {
        String ret = "";
        for (int i=0; i<this.ysize; i++) {
            ret += "+-";
        }
        ret += "+\n";
        for (Block[] i: this.blocks) {
            for (Block j: i) {
                char s;
                if (j != null)
                    s = j.symbol();
                else
                    s = ' ';
                ret += "|" + s;
            }
            ret += "|\n";
            for (int j=0; j<i.length; j++) {
                ret += "+-";
            }
            ret += "+\n";
        }
        return ret;
    }
    private enum State {
        Unknow, Flaged, Suspected, Verified,
    }
    private class Node {
        private int orient, x, y, x_off, y_off;
        public Node(int x, int y) {
            this.x = x;
            this.y = y;
            this.x_off = 0;
            this.y_off = 0;
            this.orient = 0;
        }
        public boolean next_step() {
            this.orient += 1;
            if (this.orient > 4)
                return false;
            switch(this.orient) {
            case 1:
                this.x_off = 1;
                this.y_off = 0;
                break;
            case 2:
                this.x_off = 0;
                this.y_off = 1;
                break;
            case 3:
                this.x_off = -1;
                this.y_off = 0;
                break;
            case 4:
                this.x_off = 0;
                this.y_off = -1;
                break;
            }
            return true;
        }
        public int get_x() {
            return this.x + this.x_off;
        }
        public int get_y() {
            return this.y + this.y_off;
        }
    }
    private abstract class Block {
        protected State state;
        public Block() {
            this.state = State.Unknow;
        }
        public char symbol() {
            /* All types of block shared the same symbols for indicating states,
             * except verified state.
             */
            switch (this.state) {
                case Flaged:
                    return 'F';
                case Suspected:
                    return '?';
                case Unknow:
                    return ' ';
                default:
                    return ' ';
            }
        }
        public boolean is_verified() {
            return this.state == State.Verified;
        }
        public boolean leftclick() {
            /* Both of Flaged/Suspected state should protect blocks from
             * accidentally leftclick.
             */
            if (this.state == State.Unknow) {
                this.state = State.Verified;
                return true;
            }
            return false;
        }
        public int rightclick() {
            switch (this.state) {
                case Unknow:
                    this.state = State.Flaged;
                    /* Flag a safe block, step away from success.
                     */
                    return -1;
                case Flaged:
                    this.state = State.Suspected;
                    /* Unflag a safe block, step forward to success.
                     */
                    return 1;
                case Suspected:
                    this.state = State.Unknow;
                    break;
                default:
                    break;
            }
            return 0;
        }
        public boolean try_spread() {
            if (this.state != State.Verified) {
                this.state= State.Verified;
                return true;
            }
            return false;
        }
        public boolean keep_going() {
            return false;
        }
        public Block set_as_mine() {
            return new Mine();
        }
        public void add_surrounding_mine() {}
    }
    private class Mine extends Block {
        public char symbol() {
            if (this.state == State.Verified) {
                return '*';
            }
            return super.symbol();
        }
        public boolean leftclick() {
            boolean verified = super.leftclick();
            /* Indicate mission failed only if the leftclick successed
             * Which means leftclick a unknow mine which are neither
             * flaged nor suspected.
             */
            return !verified;
        }
        public int rightclick() {
            /* Only if flaging a mine is step forward to success.
             */
            int ret = super.rightclick();
            return ret * -1;
        }
        public boolean try_spread() {
            return false;
        }
        public Block set_as_mine() {
            return null;
        }
    }
    /* The Space blocks are always away from any mine blocks.
     * In the other hand, the Space block is Sign block with zero value of "num".
     */
    private class Space extends Block {
        public char symbol() {
            if (this.state == State.Verified) {
                return '#';
            }
            return super.symbol();
        }
        public boolean leftclick() {
            super.leftclick();
            /* leftclick on Space block will always make the game continue. */
            return true;
        }
        public boolean keep_going() {
            return true;
        }
    }
    /* The Sign blocks are always near by mine blocks
     * and indicating how many mine blocks are nearing by.
     */
    private class Sign extends Block {
        /* The amount of nearing mine blocks,
         * the value should be initialized during the construction process. */
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
            super.leftclick();
            /* leftclick on Sign block will always make the game continue. */
            return true;
        }
        public void add_surrounding_mine() {
            this.num += 1;
        }
    }
}
