package ocean;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Tertris extends JFrame implements KeyListener {
    // 游戏行列数
    private static final int GAME_X = 26;
    private static final int GAME_Y = 12;

    JTextArea[][] text; // 文本域数组
    int[][] data; // 数据

    // 显示游戏状态、分数的标签
    JLabel label1;
    JLabel label;

    boolean isRunning; // 游戏是否结束

    int[] allRect; // 存储所有方块
    int rect; // 当前方块

    int time = 1000; // 休眠时间
    int x, y; // 当前方块坐标
    int score = 0; // 分数

    boolean gamePause = false; // 暂停
    int pauseTimes = 0;


    public void initWindow(){ // 初始化窗口
        this.setSize(600,850);
        this.setVisible(true);
        this.setLocationRelativeTo(null); // 窗口居中
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //
        this.setResizable(false);
        this.setTitle("俄罗斯方块");
    }

    public void initGamePanel(){
        JPanel gameMain = new JPanel();
        gameMain.setLayout(new GridLayout(GAME_X, GAME_Y,1,1)); // 网格布局

        for (int i = 0; i < text.length; i++) { // 初始化面板
            for (int j = 0; j < text[i].length; j++) {
                text[i][j] = new JTextArea(GAME_X, GAME_Y); // 文本域行列数
                text[i][j].setBackground(Color.WHITE); // 背景色
                text[i][j].addKeyListener(this); // （文本域）添加键盘监听事件

                if (j == 0 || j == text[i].length-1 || i == text.length - 1){ // 初始化游戏边界
                    text[i][j].setBackground(Color.GREEN);
                    data[i][j] = 1;
                }

                text[i][j].setEditable(false); // 文本域不可编辑
                gameMain.add(text[i][j]); // 文本域添加到主面板
            }
        }
        // 添加到窗口
        this.setLayout(new BorderLayout());
        this.add(gameMain,BorderLayout.CENTER);
    }

    public void initExplainPanel(){
        JPanel explainLeft = new JPanel(); // 创建游戏的左右说明面板
        JPanel explainRight = new JPanel();

        explainLeft.setLayout(new GridLayout(6,1));
        explainRight.setLayout(new GridLayout(2,1));

        // 左面板添加说明文字
        explainLeft.add(new JLabel("空格 -> 变形"));
        explainLeft.add(new JLabel("左箭头 -> 左移"));
        explainLeft.add(new JLabel("右箭头 -> 右移"));
        explainLeft.add(new JLabel("下箭头 -> 下落"));
        explainLeft.add(new JLabel(" g -> 开挂"));
        explainLeft.add(new JLabel(" p -> 暂停"));


        label1.setForeground(Color.RED); // 标签内容颜色

        // 右面板添加游戏状态、分数标签
        explainRight.add(label);
        explainRight.add(label1);

        // 左右说明面板添加到窗口的左右侧
        this.add(explainLeft,BorderLayout.WEST);
        this.add(explainRight,BorderLayout.EAST);

    }


    public Tertris(){
        text = new JTextArea[GAME_X][GAME_Y];
        data = new int[GAME_X][GAME_Y];

        label1 = new JLabel("游戏状态：正在游戏中...");
        label = new JLabel("游戏得分为: 0 ");

        isRunning = true; //

        //初始化存放方块的数组
        allRect = new int[]{
                0x000f,0x8888,
                0x00cc,
                0x002e,0x088c,0x00e8,0x0311,
                0x004e,0x08c8,0x00e4,0x04c4,
                0x008e,0x0c88,0x00e2,0x044c,
                0x00c6,0x04c8,
                0x006c,0x08c4
        };

        initGamePanel();
        initExplainPanel();
        initWindow();
    }


    public static void main(String[] args) {
        Tertris t = new Tertris(); // 静态面板
        t.gameBegin(); // 动态活动
    }

    public void gameBegin(){
        while (true){

            if (!isRunning) break;

            gameRun(); // 进行
        }

        label1.setText("游戏状态：游戏结束!"); //
    }

    public void ranRect(){ // 随机生成下落方块
        Random random = new Random();
        rect = allRect[random.nextInt(19)]; // [0,19)
    }

    public void gameRun() {
        ranRect();

        x = 0; // 方块下落位置
        y = 5;

        for (int i = 0; i < GAME_X; i++) {
            try {
                Thread.sleep(time); // 下落

                if (gamePause){
                    i--;
                }
                else {
                    if (!canFall(x, y)) {
                        changData(x, y); // 将data置为1,表示有方块占用

                        for (int j = x; j < x + 4; j++) { // 循环遍历4层,看是否有行可以消除
                            int sum = 0;

                            for (int k = 1; k <= (GAME_Y - 2); k++) {
                                if (data[j][k] == 1) {
                                    sum++;
                                }
                            }

                            if (sum == (GAME_Y - 2)) {
                                removeRow(j); // 消除j这一行
                            }
                        }

                        for (int j = 1; j <= (GAME_Y - 2); j++) { // 游戏是否失败
                            if (data[3][j] == 1) {
                                isRunning = false;
                                break;
                            }
                        }
                        break;

                    } else {
                        x++; // 层数+1
                        fall(x, y); // 方块下落一行
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean canFall(int m, int n){
        int temp = 0x8000; //
        for (int i = 0; i < 4; i++) { // 4*4
            for (int j = 0; j < 4; j++) {
                if ((temp & rect) != 0) {
                    // 该位置下一行是否有方块
                    if (data[m+1][n] == 1) return false;
                }
                n++;
                temp >>= 1;
            }

            m++;
            n -= 4;
        }

        return true; // fall
    }

    public void changData(int m,int n) {
        int temp = 0x8000; //
        for (int i = 0;i < 4;i++) { // 4*4
            for (int j = 0;j < 4;j++) {
                if ((temp & rect) != 0) {
                    data[m][n] = 1; //
                }
                n++;
                temp >>= 1;
            }
            m++;
            n = n - 4;
        }
    }

    public void removeRow(int row) { // 消行
        int temp = 100;
        for (int i = row;i >= 1;i--) {
            // 自上而下覆盖
            System.arraycopy(data[i - 1], 1, data[i], 1, GAME_Y - 2);
        }
        //刷新游戏区域
        reFresh(row);

        //方块加速
        if (time > temp) {
            time -= (temp/10);
        }

        score += temp;

        //显示变化后的分数
        label.setText("游戏得分: " + score);
    }

    public void reFresh(int row) { // 消行
        for (int i = row;i >= 1;i--) {  // 遍历row行以上的游戏区域
            for (int j = 1; j <= (GAME_Y -2); j++) {
                if (data[i][j] == 1) {
                    Color last = text[i-1][j].getBackground(); // （该行有方块）该行覆盖下一行
                    text[i][j].setBackground(last);
                }else {
                    text[i][j].setBackground(Color.WHITE); //  （该行无方块）下一行设为白色
                }
            }
        }
    }

    public int currentIndex(){
        //定义变量,存储目前方块的索引
        int old;

        for (old = 0;old < allRect.length;old++) {
            //判断是否是当前方块
            if (rect == allRect[old]) {
                break;
            }
        }
        return old;
    }

    public void color(int old, int m,int n){
        if (old >= 0 && old <= 1){
            text[m][n].setBackground(Color.RED); //
        }else if (old == 2){
            text[m][n].setBackground(Color.ORANGE); //

        }else if (old >= 3 && old <= 6){
            text[m][n].setBackground(Color.YELLOW); //

        }else if (old >= 7 && old <= 10){
            text[m][n].setBackground(Color.BLACK); //
        }else if (old >= 11 && old <=14){
            text[m][n].setBackground(Color.BLUE); //

        }else if (old >= 15 && old <=16){
            text[m][n].setBackground(Color.CYAN); //

        }else if (old >= 17 && old <= 18){
            text[m][n].setBackground(Color.PINK); //

        }
    }

    public void fall(int m, int n){
        if (m > 0) clear(m-1, n);
        draw(m, n);
    }

    public void clear(int m, int n) { // 清色
        int temp = 0x8000; // temp
        for (int i = 0;i < 4;i++) {
            for (int j = 0;j < 4;j++) {
                if ((temp & rect) != 0) {
                    text[m][n].setBackground(Color.WHITE);
                }
                n++;
                temp >>= 1;
            }
            m++;
            n = n - 4;
        }
    }

    public void draw(int m,int n) {
        int old = currentIndex();

        //定义变量
        int temp = 0x8000;
        for (int i = 0;i < 4;i++) {
            for (int j = 0;j < 4;j++) {
                if ((temp & rect) != 0) { // 上色
                    color(old, m, n);
                }
                n++;
                temp >>= 1;
            }
            m++;
            n = n - 4;
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'p'){
            if (!isRunning) {
                return;
            }

            pauseTimes++;

            if (pauseTimes == 1) {
                gamePause = true;
                label1.setText("游戏状态：暂停中！");
            }

            if (pauseTimes == 2) {
                gamePause = false;
                pauseTimes = 0; // 归零
                label1.setText("游戏状态: 正在进行中！");
            }
        }

        if (e.getKeyChar() == 'g'){  // 开挂（加分&&消底行）
            if (!isRunning) return;
            removeRow(data.length-2);
        }

        if (e.getKeyChar() == KeyEvent.VK_SPACE) { // shape

            if (!isRunning) return;

            //定义变量,存储目前方块的索引
            int old = currentIndex();

            //定义变量,存储变形后方块
            int next;

            //判断是方块
            if (old == 2) {
                return;
            }

            //清除当前方块
            clear(x,y);

            if (old == 0 || old == 1) { // 长条
                next = allRect[old == 0 ? 1 : 0];

                if (canTurn(next,x,y)) {
                    rect = next;
                }
            }

            if (old >= 3 && old <= 6) {
                next = allRect[old + 1 > 6 ? 3 : old + 1];

                if (canTurn(next,x,y)) {
                    rect = next;
                }
            }

            if (old >= 7 && old <= 10) {
                next = allRect[old + 1 > 10 ? 7 : old + 1];

                if (canTurn(next,x,y)) {
                    rect = next;
                }
            }

            if (old >= 11 && old <= 14) {
                next = allRect[old + 1 > 14 ? 11 : old + 1];

                if (canTurn(next,x,y)) {
                    rect = next;
                }
            }

            if (old == 15 || old == 16) {
                next = allRect[old == 15 ? 16 : 15];

                if (canTurn(next,x,y)) {
                    rect = next;
                }
            }


            if (old == 17 || old == 18) {
                next = allRect[old == 17 ? 18 : 17];

                if (canTurn(next,x,y)) {
                    rect = next;
                }
            }


            //重新绘制变形后方块
            draw(x,y);
        }



    }

    public boolean canTurn(int a, int m, int n){
        int temp = 0x8000; // temp

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if ((a & temp) != 0){
                    if (data[m][n] == 1){
                        return false;
                    }
                }
                n++;
                temp >>= 1;
            }
            m++;
            n -= 4;
        }

        return true; // can
    }

    @Override
    public void keyPressed(KeyEvent e) {

        int temp = 0x8000; // temp

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT : { // left
                if (!isRunning) return;
                if (gamePause) return;

                if (y <= 1) return;
                // 碰左壁
                for (int i = x; i < x + 4; i++) {
                    for (int j = y; j < y + 4; j++) {
                        if ((temp & rect) != 0) {
                            if (data[i][j - 1] == 1) {
                                return;
                            }
                        }
                        temp >>= 1;
                    }
                }
                clear(x, y); // 清除当前方块
                y--;
                draw(x, y);
                break;
            }

            case KeyEvent.VK_RIGHT : { // right
                if (!isRunning) return;
                if (gamePause) return;

                // 最右边坐标值
                int m = x;
                int n = y;
                int num = 1;

                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        if ((temp & rect) != 0) {
                            if (n > num) {
                                num = n;
                            }
                        }
                        n++;
                        temp >>= 1;
                    }
                    m++;
                    n = n - 4;
                }

                //碰右墙壁
                if (num >= (GAME_Y - 2)) {
                    return;
                }

                //判断右移途中是否有方块
                temp = 0x8000;
                for (int i = x; i < x + 4; i++) {
                    for (int j = x; j < y + 4; j++) {
                        if ((temp & rect) != 0) {
                            if (data[i][j + 1] == 1) {
                                return;
                            }
                        }
                        temp >>= 1;
                    }
                }

                //清除当前方块
                clear(x, y);
                y++;
                draw(x, y);
                break;
            }

            case KeyEvent.VK_DOWN : { // down
                if (!isRunning) return;
                if (gamePause) return;

                if (!canFall(x, y)) {
                    return;
                }
                clear(x, y);

                //改变方块的坐标
                x++;
                draw(x, y);
                break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
