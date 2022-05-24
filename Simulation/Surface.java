import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import java.awt.image.ImageObserver;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract interface Surface{
	
	public abstract Image getImage();

}
