
package pacman;

import java.awt.Event;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


/*
 * 
 * 
 * This class alters the state of pacmans direction depending on the key pressed.
 * It turns pacman right or left or up/down depending on key pressed.
 * Uses matrix format, so pressing UP actually lowers the state of the matrix.
 * It also sets the state of INGAME. If during the intro screen, pressing S starts the game.
 * May want to change it to any key.
 */
/*
class TAdapter extends KeyAdapter {

    @Override
    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        if (ingame) {
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
                ingame = false;
            } else if (key == KeyEvent.VK_PAUSE) {
                if (timer.isRunning()) {
                    timer.stop();
                } else {
                    timer.start();
                }
            }
        } else {
            if (key == 's' || key == 'S') {
                ingame = true;
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
*/
