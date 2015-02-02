package pacman;

import java.awt.Image;

import javax.swing.ImageIcon;

public class PacmanImages {
	
    protected static Image ghost;
    protected static Image pacman1, pacman2up, pacman2left, pacman2right, pacman2down;
    protected static Image pacman3up, pacman3down, pacman3left, pacman3right;
    protected static Image pacman4up, pacman4down, pacman4left, pacman4right;
	
	//returns a scaled version of an image in a program to its needed size.
    private static Image ImageIconScaler(String imageLocation){
    	
    	ImageIcon imageIcon = new ImageIcon(imageLocation);
        Image image = imageIcon.getImage(); // transform it 
        Image newimg = image.getScaledInstance(20,20,java.awt.Image.SCALE_SMOOTH); // scale it the smooth way 
        //can probably make this code more efficient with 
    	Image scaledImage = new ImageIcon(newimg).getImage();
    	return scaledImage;
    }
    
    protected static void loadImages() {

        ghost = ImageIconScaler("images/pacman/ghost.jpg");
        pacman1 = ImageIconScaler("images/pacman.png");
        pacman2up =  ImageIconScaler("images/up1.png");
        pacman3up =  ImageIconScaler("images/up2.png");
        pacman4up =  ImageIconScaler("images/up3.png");
        pacman2down =  ImageIconScaler("images/down1.png");
        pacman3down =  ImageIconScaler("images/down2.png");
        pacman4down =  ImageIconScaler("images/down3.png");
        pacman2left =  ImageIconScaler("images/left1.png");
        pacman3left =  ImageIconScaler("images/left2.png");
        pacman4left =  ImageIconScaler("images/left3.png");
        pacman2right =  ImageIconScaler("images/right1.png");
        pacman3right =  ImageIconScaler("images/right2.png");
        pacman4right = ImageIconScaler("images/right3.png");
    }
}
