import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import java.awt.image.ImageObserver;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ArbreEnFeu implements Surface{
	
	private Image tree_fireSprite;
	private World myWorld;
	
	public ArbreEnFeu(World myWorld_){
		myWorld=myWorld_;
		
	}
	
	public Image getImage(){
		try
		{
			
			tree_fireSprite = ImageIO.read(new File("tree_fire.png"));
			
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		
		return tree_fireSprite;

	}
	


}
