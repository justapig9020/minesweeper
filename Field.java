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
    public void rightclick(int x, int y) {
        Block block = this.blocks[x][y];
        block.rightclick();
    }
    public String render() {
        String ret = "";
        for(Block[] i: this.blocks) {
            for(Block j: i) {
                char s;
                if (j != null)
                    s = j.symbol();
                else
                    s = ' ';
                ret += "|" + s;
            }
            ret += "|\n";
            for(int j=0; j<i.length; j++) {
                ret += "+-";
            }
            ret += "+\n";
        }
        return ret;
    }
    private enum State {
        Unknow, Flaged, Suspected, Verified,
    }
    private class Block {
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
        public void rightclick() {
            switch (this.state) {
                case Unknow:
                    this.state = State.Flaged;
                    break;
                case Flaged:
                    this.state = State.Suspected;
                    break;
                case  Suspected:
                    this.state = State.Unknow;
                    break;
                default:
                    break;
            }
            System.out.println(this.state);
        }
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
    }
}
