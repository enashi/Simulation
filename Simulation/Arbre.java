import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import java.awt.image.ImageObserver;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Arbre implements Surface{
	
	private Image treeSprite;
	private World myWorld;
	
	public Arbre(World myWorld_){
		myWorld=myWorld_;
		
	}
	
	public Image getImage(){
		try
		{
			
			treeSprite = ImageIO.read(new File("arbre1.png"));
			
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		
		return treeSprite;

	}


}
