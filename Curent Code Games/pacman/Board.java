package pacman;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Board extends JPanel implements ActionListener {

    private Dimension d;


    private Image ii;

    private Color mazecolor;

    private int pacanimcount = BoardConstants.pacanimdelay;
    private int pacanimdir = 1;
    private int pacmananimpos = 0;
    private int nrofghosts = 6;
    private int pacsleft, score;
    private int[] dx, dy;
    private int[] ghostx, ghosty, ghostdx, ghostdy, ghostspeed;


    private int pacmanx, pacmany, pacmandx, pacmandy;
    private int reqdx, reqdy, viewdx, viewdy;



    private int currentspeed = 3;
    private short[] screendata;
    private Timer timer;

    public Board() {

        PacmanImages.loadImages();
        initVariables();
        
        addKeyListener(new TAdapter());

        setFocusable(true);

        setBackground(Color.black);
        setDoubleBuffered(true);
    }

    private void initVariables() {

        screendata = new short[BoardConstants.nrofblocks * BoardConstants.nrofblocks];
        mazecolor = new Color(5, 100, 5);
        d = new Dimension(400, 400);
        ghostx = new int[BoardConstants.maxghosts];
        ghostdx = new int[BoardConstants.maxghosts];
        ghosty = new int[BoardConstants.maxghosts];
        ghostdy = new int[BoardConstants.maxghosts];
        ghostspeed = new int[BoardConstants.maxghosts];
        dx = new int[4];
        dy = new int[4];
        
        timer = new Timer(40, this);
        timer.start();
    }

    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (BoardGameStates.ingame) {
                if (key == KeyEvent.VK_LEFT) {
                    reqdx = -1;
                    reqdy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    reqdx = 1;
                    reqdy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    reqdx = 0;
                    reqdy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    reqdx = 0;
                    reqdy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                	BoardGameStates.ingame = false;
                } else if (key == KeyEvent.VK_PAUSE) {
                    if (timer.isRunning()) {
                        timer.stop();
                    } else {
                        timer.start();
                    }
                }
            } else {
                if (key == 's' || key == 'S') {
                	BoardGameStates.ingame = true;
                    initGame();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

            int key = e.getKeyCode();

            if (key == Event.LEFT || key == Event.RIGHT
                    || key == Event.UP || key == Event.DOWN) {
                reqdx = 0;
                reqdy = 0;
            }
        }
    }
    
    
    @Override
    public void addNotify() {
        super.addNotify();

        initGame();
    }

    private void doAnim() {

        pacanimcount--;

        if (pacanimcount <= 0) {
            pacanimcount = BoardConstants.pacanimdelay;
            pacmananimpos = pacmananimpos + pacanimdir;

            if (pacmananimpos == (BoardConstants.pacmananimcount - 1) || pacmananimpos == 0) {
                pacanimdir = -pacanimdir;
            }
        }
    }

    private void playGame(Graphics2D g2d) {

        if (BoardGameStates.dying) {

            death();

        } else {

            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }
    }

    private void showIntroScreen(Graphics2D g2d) {

        g2d.setColor(new Color(0, 32, 48));
        g2d.fillRect(50, BoardConstants.scrsize / 2 - 30, BoardConstants.scrsize - 100, 50);
        g2d.setColor(Color.white);
        g2d.drawRect(50, BoardConstants.scrsize / 2 - 30, BoardConstants.scrsize - 100, 50);

        String s = "Press s to start.";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);

        g2d.setColor(Color.white);
        g2d.setFont(small);
        g2d.drawString(s, (BoardConstants.scrsize - metr.stringWidth(s)) / 2, BoardConstants.scrsize / 2);
    }

    private void drawScore(Graphics2D g) {

        int i;
        String s;

        g.setFont(BoardConstants.smallfont);
        g.setColor(new Color(96, 128, 255));
        s = "Score: " + score;
        g.drawString(s, BoardConstants.scrsize / 2 + 96, BoardConstants.scrsize + 16);

        for (i = 0; i < pacsleft; i++) {
            g.drawImage(PacmanImages.pacman3left, i * 28 + 8, BoardConstants.scrsize + 1, this);
        }
    }

    private void checkMaze() {

        short i = 0;
        boolean finished = true;

        while (i < BoardConstants.nrofblocks * BoardConstants.nrofblocks && finished) {

            if ((screendata[i] & 48) != 0) {
                finished = false;
            }

            i++;
        }

        if (finished) {

            score += 50;

            if (nrofghosts < BoardConstants.maxghosts) {
                nrofghosts++;
            }

            if (currentspeed < BoardConstants.maxspeed) {
                currentspeed++;
            }

            initLevel();
        }
    }

    private void death() {

        pacsleft--;

        if (pacsleft == 0) {
        	BoardGameStates.ingame = false;
        }

        continueLevel();
    }

    private void moveGhosts(Graphics2D g2d) {

        short i;
        int pos;
        int count;

        for (i = 0; i < nrofghosts; i++) {
            if (ghostx[i] % BoardConstants.blocksize == 0 && ghosty[i] % BoardConstants.blocksize == 0) {
                pos = ghostx[i] / BoardConstants.blocksize + BoardConstants.nrofblocks * (int) (ghosty[i] / BoardConstants.blocksize);

                count = 0;

                if ((screendata[pos] & 1) == 0 && ghostdx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screendata[pos] & 2) == 0 && ghostdy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screendata[pos] & 4) == 0 && ghostdx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screendata[pos] & 8) == 0 && ghostdy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screendata[pos] & 15) == 15) {
                        ghostdx[i] = 0;
                        ghostdy[i] = 0;
                    } else {
                        ghostdx[i] = -ghostdx[i];
                        ghostdy[i] = -ghostdy[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    ghostdx[i] = dx[count];
                    ghostdy[i] = dy[count];
                }

            }

            ghostx[i] = ghostx[i] + (ghostdx[i] * ghostspeed[i]);
            ghosty[i] = ghosty[i] + (ghostdy[i] * ghostspeed[i]);
            drawGhost(g2d, ghostx[i] + 1, ghosty[i] + 1);

            if (pacmanx > (ghostx[i] - 12) && pacmanx < (ghostx[i] + 12)
                    && pacmany > (ghosty[i] - 12) && pacmany < (ghosty[i] + 12)
                    && BoardGameStates.ingame) {

            	BoardGameStates.dying = true;
            }
        }
    }

    private void drawGhost(Graphics2D g2d, int x, int y) {

        g2d.drawImage(PacmanImages.ghost, x, y, this);
    }

    private void movePacman() {

        int pos;
        short ch;

        if (reqdx == -pacmandx && reqdy == -pacmandy) {
            pacmandx = reqdx;
            pacmandy = reqdy;
            viewdx = pacmandx;
            viewdy = pacmandy;
        }

        if (pacmanx % BoardConstants.blocksize == 0 && pacmany % BoardConstants.blocksize == 0) {
            pos = pacmanx / BoardConstants.blocksize + BoardConstants.nrofblocks * (int) (pacmany / BoardConstants.blocksize);
            ch = screendata[pos];

            if ((ch & 16) != 0) {
                screendata[pos] = (short) (ch & 15);
                score++;
            }

            if (reqdx != 0 || reqdy != 0) {
                if (!((reqdx == -1 && reqdy == 0 && (ch & 1) != 0)
                        || (reqdx == 1 && reqdy == 0 && (ch & 4) != 0)
                        || (reqdx == 0 && reqdy == -1 && (ch & 2) != 0)
                        || (reqdx == 0 && reqdy == 1 && (ch & 8) != 0))) {
                    pacmandx = reqdx;
                    pacmandy = reqdy;
                    viewdx = pacmandx;
                    viewdy = pacmandy;
                }
            }

            // Check for standstill
            if ((pacmandx == -1 && pacmandy == 0 && (ch & 1) != 0)
                    || (pacmandx == 1 && pacmandy == 0 && (ch & 4) != 0)
                    || (pacmandx == 0 && pacmandy == -1 && (ch & 2) != 0)
                    || (pacmandx == 0 && pacmandy == 1 && (ch & 8) != 0)) {
                pacmandx = 0;
                pacmandy = 0;
            }
        }
        pacmanx = pacmanx + BoardConstants.pacmanspeed * pacmandx;
        pacmany = pacmany + BoardConstants.pacmanspeed * pacmandy;
    }

    private void drawPacman(Graphics2D g2d) {

        if (viewdx == -1) {
            drawPacnanLeft(g2d);
        } else if (viewdx == 1) {
            drawPacmanRight(g2d);
        } else if (viewdy == -1) {
            drawPacmanUp(g2d);
        } else {
            drawPacmanDown(g2d);
        }
    }

    private void drawPacmanUp(Graphics2D g2d) {

        switch (pacmananimpos) {
            case 1:
                g2d.drawImage(PacmanImages.pacman2up, pacmanx + 1, pacmany + 1, this);
                break;
            case 2:
                g2d.drawImage(PacmanImages.pacman3up, pacmanx + 1, pacmany + 1, this);
                break;
            case 3:
                g2d.drawImage(PacmanImages.pacman4up, pacmanx + 1, pacmany + 1, this);
                break;
            default:
                g2d.drawImage(PacmanImages.pacman1, pacmanx + 1, pacmany + 1, this);
                break;
        }
    }

    private void drawPacmanDown(Graphics2D g2d) {

        switch (pacmananimpos) {
            case 1:
                g2d.drawImage(PacmanImages.pacman2down, pacmanx + 1, pacmany + 1, this);
                break;
            case 2:
                g2d.drawImage(PacmanImages.pacman3down, pacmanx + 1, pacmany + 1, this);
                break;
            case 3:
                g2d.drawImage(PacmanImages.pacman4down, pacmanx + 1, pacmany + 1, this);
                break;
            default:
                g2d.drawImage(PacmanImages.pacman1, pacmanx + 1, pacmany + 1, this);
                break;
        }
    }

    private void drawPacnanLeft(Graphics2D g2d) {

        switch (pacmananimpos) {
            case 1:
                g2d.drawImage(PacmanImages.pacman2left, pacmanx + 1, pacmany + 1, this);
                break;
            case 2:
                g2d.drawImage(PacmanImages.pacman3left, pacmanx + 1, pacmany + 1, this);
                break;
            case 3:
                g2d.drawImage(PacmanImages.pacman4left, pacmanx + 1, pacmany + 1, this);
                break;
            default:
                g2d.drawImage(PacmanImages.pacman1, pacmanx + 1, pacmany + 1, this);
                break;
        }
    }

    private void drawPacmanRight(Graphics2D g2d) {

        switch (pacmananimpos) {
            case 1:
                g2d.drawImage(PacmanImages.pacman2right, pacmanx + 1, pacmany + 1, this);
                break;
            case 2:
                g2d.drawImage(PacmanImages.pacman3right, pacmanx + 1, pacmany + 1, this);
                break;
            case 3:
                g2d.drawImage(PacmanImages.pacman4right, pacmanx + 1, pacmany + 1, this);
                break;
            default:
                g2d.drawImage(PacmanImages.pacman1, pacmanx + 1, pacmany + 1, this);
                break;
        }
    }

    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;

        for (y = 0; y < BoardConstants.scrsize; y += BoardConstants.blocksize) {
            for (x = 0; x < BoardConstants.scrsize; x += BoardConstants.blocksize) {

                g2d.setColor(mazecolor);
                g2d.setStroke(new BasicStroke(2));

                if ((screendata[i] & 1) != 0) { 
                    g2d.drawLine(x, y, x, y + BoardConstants.blocksize - 1);
                }

                if ((screendata[i] & 2) != 0) { 
                    g2d.drawLine(x, y, x + BoardConstants.blocksize - 1, y);
                }

                if ((screendata[i] & 4) != 0) { 
                    g2d.drawLine(x + BoardConstants.blocksize - 1, y, x + BoardConstants.blocksize - 1,
                            y + BoardConstants.blocksize - 1);
                }

                if ((screendata[i] & 8) != 0) { 
                    g2d.drawLine(x, y + BoardConstants.blocksize - 1, x + BoardConstants.blocksize - 1,
                            y + BoardConstants.blocksize - 1);
                }

                if ((screendata[i] & 16) != 0) { 
                    g2d.setColor(BoardConstants.dotcolor);
                    g2d.fillRect(x + 11, y + 11, 2, 2);
                }

                i++;
            }
        }
    }

    private void initGame() {

        pacsleft = 3;
        score = 0;
        initLevel();
        nrofghosts = 2;
        currentspeed = 3;
    }

    private void initLevel() {

        int i;
        for (i = 0; i < BoardConstants.nrofblocks * BoardConstants.nrofblocks; i++) {
            screendata[i] = BoardConstants.leveldata[i];
        }

        continueLevel();
    }

    private void continueLevel() {

        short i;
        int dx = 1;
        int random;

        for (i = 0; i < nrofghosts; i++) {

            ghosty[i] = 4 * BoardConstants.blocksize;
            ghostx[i] = 4 * BoardConstants.blocksize;
            ghostdy[i] = 0;
            ghostdx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentspeed + 1));

            if (random > currentspeed) {
                random = currentspeed;
            }

            ghostspeed[i] = BoardConstants.validspeeds[random];
        }

        pacmanx = 7 * BoardConstants.blocksize;
        pacmany = 11 * BoardConstants.blocksize;
        pacmandx = 0;
        pacmandy = 0;
        reqdx = 0;
        reqdy = 0;
        viewdx = -1;
        viewdy = 0;
        BoardGameStates.dying = false;
    }



    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        drawMaze(g2d);
        drawScore(g2d);
        doAnim();

        if (BoardGameStates.ingame) {
            playGame(g2d);
        } else {
            showIntroScreen(g2d);
        }

        g2d.drawImage(ii, 5, 5, this);
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

  

    @Override
    public void actionPerformed(ActionEvent e) {

        repaint();
    }
}