

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.File;
import java.text.AttributedCharacterIterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;



public class SpriteDemo extends JPanel 
{
	public static JFrame frame;
	public static Graphics2D g2;
	
	private Image waterSprite;
	private Image grassSprite;
	private Image treeSprite;
	private Image tree_fire;
	private Image cendre;
	private Image poule;
	private Image renard;
	private Image bordure_bas;
	private Image bordure_gauche;
	private Image bordure_droite;
	private Image ombre;
	private Image lave ;
	private Image volcan;
	private Image sable;
	private Image nuage;
	private Image eclair;
	
	public static int spriteLength;
	
	private World myWorld_;
	
	public static int longueur;
	public static int largeur;
	public static int delai;

	public SpriteDemo()
	{	
		longueur=50;
		largeur=50;
		delai=500; //delai entre deux iterations
		spriteLength = 16;
		System.out.println(" la taille de sprite est : " + spriteLength);
		System.out.println(" debut de simulation !!!");
		try
		{
			waterSprite = ImageIO.read(new File("water.png"));
			treeSprite = ImageIO.read(new File("arbre1.png"));
			grassSprite = ImageIO.read(new File("grass.png"));
			tree_fire = ImageIO.read(new File("tree_fire.png"));
			cendre = ImageIO.read(new File("cendre.png"));
			poule = ImageIO.read(new File("poule.png"));
			renard = ImageIO.read(new File("renard.png"));
			bordure_bas = ImageIO.read(new File("bordure_bas.png"));
			bordure_gauche = ImageIO.read(new File("bordure_gauche.png"));
			bordure_droite = ImageIO.read(new File("bordure_droite.png"));
			ombre = ImageIO.read(new File("ombre.png"));
			lave = ImageIO.read(new File("lave.png"));
			volcan = ImageIO.read(new File("volcan.png"));
			sable  = ImageIO.read(new File("sable.png"));
			nuage = ImageIO.read(new File("Nuage.png"));
			eclair = ImageIO.read(new File("eclair.png"));
			
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}

		frame = new JFrame("World of Sprite");
		frame.add(this);
		frame.setSize((spriteLength+1)*2*longueur,(spriteLength+1)*2*largeur);
		frame.setVisible(true);
		
		double proportion_arbre_depart=0.1;
		double p1 = 0.00; // prob qu'un arbre prenne feu
		double p2 = 0.01; // prob qu'un arbre apparaisse //0.01
		int nb_poules=4;
		int nb_renards=1;
		int nb_nuages=6;

		
		myWorld_ = new World(this,longueur,largeur,p1,p2);
		
		myWorld_.init_terrain();
		myWorld_.init_surfaces(proportion_arbre_depart);
		myWorld_.init_agents(nb_poules,nb_renards);        // NBRE DE POULE ET NBRE DE RENARD
		myWorld_.init_nuages(nb_nuages); // NBRE DE NUAGES
	
				
	}
	
	public JFrame getframe() 
	{
		return frame;
	}
	public Graphics2D getG() 
	{
		return g2;
	}
	
	//0: herbe
	//2: sable
	//3: cendre
	//4: eau
	//5: lave
	
	public void paint_terrain(Graphics g )
	{
		Graphics2D g2 = (Graphics2D)g;
		for ( int i = 0 ; i < myWorld_.terrain.length ; i++ )
		{
			for ( int j = 0 ; j < myWorld_.terrain[0].length ; j++ )
			{
				if ( myWorld_.terrain[i][j] == 0 )                 
					g2.drawImage(grassSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				else
					if ( myWorld_.terrain[i][j] == 3 )
					g2.drawImage(cendre,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
					else
						if ( myWorld_.terrain[i][j] == 4 )
						g2.drawImage(waterSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);	
						else
							if(( myWorld_.terrain[i][j] == 5 ))
								g2.drawImage(lave,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
							else
								if(( myWorld_.terrain[i][j] == 2 ))
									g2.drawImage(sable,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
									else
										if(( myWorld_.terrain[i][j] == 6 ))
											g2.drawImage(volcan,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				
			}	
		}
		
	}
	
	public void paint_altitude(Graphics g )
	{	int nb_pixels_bordure_bas = (int) (spriteLength/8); //car le sprite de bordure fait un 8e de l'image
		int nb_pixels_ombre= (int) (spriteLength/32); //car le sprite d'ombre  fait un 32e de l'image
		//System.out.println(nb_pixels_ombre);
		Graphics2D g2 = (Graphics2D)g;
		for ( int i = 0 ; i < myWorld_.altitude.length ; i++ )
		{
			for ( int j = 0 ; j < myWorld_.altitude[0].length ; j++ )
			{
					int alt =myWorld_.altitude[i][j];  
					int alt_case_dessous = myWorld_.altitude[i][(j+1 + myWorld_.altitude.length)%myWorld_.altitude[0].length];	
					if (alt_case_dessous < alt)
					{
						int difference_alt= alt - alt_case_dessous;
						for(int z=0; z<difference_alt ; z++)
						{	
							g2.drawImage(bordure_bas,spriteLength*i,spriteLength*j-nb_pixels_bordure_bas*z,spriteLength,spriteLength, frame);
						}	
					}
					if (myWorld_.altitude[(i-1+myWorld_.altitude.length)%myWorld_.altitude.length][j]< alt) 
							g2.drawImage(bordure_gauche,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
							
					if ( myWorld_.altitude[(i+1+myWorld_.altitude.length)%myWorld_.altitude.length][j]<alt)
							g2.drawImage(bordure_droite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
					
					
					if (alt_case_dessous > alt)
					{
						int difference_alt= alt_case_dessous -alt;
						for(int z=0; z<difference_alt ; z++)
						{	
							g2.drawImage(ombre,spriteLength*i,spriteLength*j+nb_pixels_ombre*z,spriteLength,spriteLength, frame);
						}
					}
				
			}	
		}
		
	}	


	public void paint_surface(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		for ( int i = 0 ; i < myWorld_.surfaces.length ; i++ )
		{
			for ( int j = 0 ; j < myWorld_.surfaces[0].length ; j++ )
			{
				int decalage=myWorld_.altitude[i][j]+ ( (int) (spriteLength/8) );
				
				if ( myWorld_.surfaces[i][j] instanceof Arbre )
						g2.drawImage(treeSprite,spriteLength*i,spriteLength*j-decalage,spriteLength,spriteLength, frame);
					else
						if ( myWorld_.surfaces[i][j] instanceof ArbreEnFeu )	
							g2.drawImage(tree_fire,spriteLength*i,spriteLength*j-decalage,spriteLength,spriteLength, frame);					
			}
		}
	}
		
	public void paint_agents(Graphics g)
	{
		
		Graphics2D g2 = (Graphics2D)g;
		
		for (int j = 0 ; j < myWorld_.agents.size() ; j++) 
			{	
				Agent a = myWorld_.agents.get(j);
				int decalage=myWorld_.altitude[a._x][a._y]+ (int) (spriteLength/8);;
				if( a instanceof Poule ) g2.drawImage(poule,spriteLength*a._x,spriteLength*a._y-decalage,spriteLength,spriteLength, frame);
				if( a instanceof PredatorAgent ) g2.drawImage(renard,spriteLength*a._x,spriteLength*a._y-decalage,spriteLength,spriteLength, frame);
				
			}
	
	}
	
	public void paint_nuages(Graphics g)
	{
		
		Graphics2D g2 = (Graphics2D)g;
		
		for (int j = 0 ; j < myWorld_.getNuages().size() ; j++) 
			{	
				Nuage n = myWorld_.getNuages().get(j);
				g2.drawImage(nuage,spriteLength*n.getX(),spriteLength*n.getY(),spriteLength,spriteLength, frame);
				if(n.getEclair()) {
					g2.drawImage(eclair,spriteLength*n.getX(),spriteLength*n.getY(),spriteLength,spriteLength,frame);

				}
				
			}
	}
	

	public void paint(Graphics g )
	{
		Graphics2D g2 = (Graphics2D)g;
		
		paint_terrain(g);
		
		paint_altitude(g);
		
		paint_surface(g);
		
		paint_agents(g);
		
		paint_nuages(g);
		

	}

	
	public static void main(String[] args) 
	{
		SpriteDemo s= new SpriteDemo();
		
		
		
		int nbIterations = 200;
		int it = 0;	
		while(it < nbIterations)
		{	
			try 
			{
				Thread.sleep(delai);
			} 
		
			catch (InterruptedException e) 
			{
				
			}
	
			s.myWorld_.step();
			
			//System.out.println(it);
			
			s.repaint();
			it++;
			
		}
		
	}
}




