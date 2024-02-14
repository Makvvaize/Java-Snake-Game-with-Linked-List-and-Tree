import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedList;
import java.util.TreeMap;

public class SnakeGame2 {
    private static final int SCALE = 10;
    private static final int WIDTH = 60;
    private static final int HEIGHT = 40;
    private static final int INITIAL_SPEED = 70;

    private LinkedList<Point> snake;
    private Point fruit;
    private int score;
    private boolean gameOver;

    private JFrame frame;
    private RenderPanel renderPanel;

    private TreeMap<Integer, String> scoreboard;

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private Direction direction;
    private Timer timer;

    public SnakeGame2() {
        snake = new LinkedList<>();
        fruit = new Point(0, 0);
        score = 0;
        gameOver = false;

        frame = new JFrame("Snake Game");
        renderPanel = new RenderPanel();
        scoreboard = new TreeMap<>();

        direction = Direction.RIGHT;

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH * SCALE, HEIGHT * SCALE);
        frame.setResizable(false);
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setVisible(true);
        frame.add(renderPanel);
        frame.addKeyListener(new KeyboardAdapter());

        startGame();
    }

    private void startGame() {
        snake.clear();
        snake.add(new Point(WIDTH / 2, HEIGHT / 2));
        generateFruit();
        score = 0;
        gameOver = false;
        direction = Direction.RIGHT;

        if (timer != null) {
            timer.stop();
        }

        timer = new Timer(INITIAL_SPEED, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    snakeMove();
                    checkCollision();
                    renderPanel.repaint();
                }
            }
        });

        timer.start();
    }

    private void snakeMove() {
        Point head = new Point(snake.getFirst());
        switch (direction) {
            case UP:
                head.y--;
                break;
            case DOWN:
                head.y++;
                break;
            case LEFT:
                head.x--;
                break;
            case RIGHT:
                head.x++;
                break;
        }

        snake.addFirst(head);
        if (!head.equals(fruit)) {
            snake.removeLast();
        } else {
            generateFruit();
            score++;
            timer.setDelay(Math.max(INITIAL_SPEED - score * 2, 50));
        }
    }

    private void generateFruit() {
        int x = (int) (Math.random() * (WIDTH-2));
        int y = (int) (Math.random() * (HEIGHT-2));
        fruit.setLocation(x, y);
    }

    private void checkCollision() {
        Point head = snake.getFirst();

        if (head.x < 0 || head.x >= WIDTH-1 || head.y < 0 || head.y >= HEIGHT-1) {
            gameOver = true;
            addScoreToScoreboard();
            return;
        }

        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver = true;
                addScoreToScoreboard();
                return;
            }
        }
    }

    private void addScoreToScoreboard() {
        scoreboard.put(score, "Player");

        if (scoreboard.size() > 7) {
            scoreboard.remove(scoreboard.firstKey());
        }
    }

    private class RenderPanel extends JPanel {
    	private static final int WIDTH = 60;
        private static final int HEIGHT = 40;
    	@Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);

            g.setColor(Color.BLUE);
            g.drawString("Score: " + score, 5, 15);

            g.setColor(Color.BLUE);
            g.drawString("Scoreboard:", 5, 35);
            int y = 55;
            for (Integer score : scoreboard.descendingKeySet()) {
                g.drawString(score + " - " + scoreboard.get(score), 5, y);
                y += 20;
            }

            if (gameOver) {
                g.setColor(Color.RED);
                g.drawString("Game Over!", WIDTH * SCALE / 2 - 30, HEIGHT * SCALE / 2);

                int treeX = WIDTH * SCALE / 2 - 50;
                int treeY = HEIGHT * SCALE / 2 + 20;

                g.setColor(Color.BLUE);
                drawTree(g, scoreboard, treeX, treeY, 120, 0, 30);
            }

            g.setColor(Color.GREEN);
            for (Point p : snake) {
                g.fillRect(p.x * SCALE, p.y * SCALE, SCALE, SCALE);
            }

            g.setColor(Color.RED);
            g.fillRect(fruit.x * SCALE, fruit.y * SCALE, SCALE, SCALE);
        }

        private void drawTree(Graphics g, TreeMap<Integer, String> tree, int x, int y, int dx, int dy, int lineHeight) {
            if (tree.isEmpty()) {
                return;
            }

            int mid = tree.size() / 2;
            Integer[] keys = tree.keySet().toArray(new Integer[0]);
            String[] values = tree.values().toArray(new String[0]);

            g.drawString(keys[mid] + " - " + values[mid], x, y);

            TreeMap<Integer, String> leftSubTree = new TreeMap<>();
            for (int i = 0; i < mid; i++) {
                leftSubTree.put(keys[i], values[i]);
            }
            drawTree(g, leftSubTree, x - dx, y + lineHeight, dx / 2, dy, lineHeight);

            TreeMap<Integer, String> rightSubTree = new TreeMap<>();
            for (int i = mid + 1; i < keys.length; i++) {
                rightSubTree.put(keys[i], values[i]);
            }
            drawTree(g, rightSubTree, x + dx, y + lineHeight, dx / 2, dy, lineHeight);
        }
    }

    private class KeyboardAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_UP && direction != Direction.DOWN) {
                direction = Direction.UP;
            } else if (key == KeyEvent.VK_DOWN && direction != Direction.UP) {
                direction = Direction.DOWN;
            } else if (key == KeyEvent.VK_LEFT && direction != Direction.RIGHT) {
                direction = Direction.LEFT;
            } else if (key == KeyEvent.VK_RIGHT && direction != Direction.LEFT) {
                direction = Direction.RIGHT;
            } else if (key == KeyEvent.VK_ENTER && gameOver) {
                startGame();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SnakeGame2::new);
    }
}
