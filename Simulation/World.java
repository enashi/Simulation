import java.util.ArrayList;
import java.util.Random;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.imageio.*; 
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

import java.awt.image.ImageObserver;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class World {

	SpriteDemo s;
	private int longueur;
	private int largeur;
	
	int[][] terrain;
	int[][] altitude;
	private int[][] nv_liquide;
	private int duree_evaporation = 10;
	private int duree_eruption=40; // l'intervalle de temps entre deux eruptions
	private int cpt_volcan=0;
	private int duree_vent=20; // determine l'intervalle de temps avant que le vent change de sens ( ou non si par hasard on retrouve la meme valeur que precedemment )
	private int cpt_vent=0;
	private int orient_vent=0; // la maj de orient_vent se fait automatiquement dans le step
	private int x_volcan;
	private int y_volcan;
	private int disparite_altitude=5; // nbre de niveau d'altitude differents
	private int heterogeneite_altitude = 0; // plus cette valeur est elevee plus la quantite d'espace pour chaque niveau d'altitude sera disparate (attention: une valeur trop grande donnera une generation incoherente)
	private int niveau_mer=90;   // de 0 a 256 (exclu)
	Surface[][] surfaces;
	
	private double p1 = 0; // prob qu'un arbre prenne feu
	private double p2 = 0; // prob qu'un arbre apparaisse

	ArrayList<Agent> agents;
	private ArrayList<Nuage> nuages;

	public World ( SpriteDemo _s, int longueur_, int largeur_, double p1_, double p2_ )
	{ 
			s=_s;
			p1=p1_;
			p2=p2_;
			longueur=longueur_;
			largeur=largeur_;
			agents= new ArrayList<Agent>();
			
			terrain = new int[longueur][largeur];     //ON INITIALISE LA MAP
			altitude = new int[longueur][largeur];  
			nv_liquide = new int[longueur][largeur];
			surfaces = new Surface[longueur][largeur];
			nuages = new ArrayList<Nuage>();
	}
	
	
	
	// CONVERTIT UNE IMAGE EN BUFFERD IMAGE
	public BufferedImage toBufferedImage(Image image) 
	{ 
        /** On teste si l'image n'est pas deja une instance de BufferedImage */ 
        if( image instanceof BufferedImage ) 
        { 
                return( (BufferedImage)image ); 
        } 
        else 
        { 
        	//On s'assure que l'image est completement chargee
        	image = new ImageIcon(image).getImage(); 
        	
        	//On cree la nouvelle image
        	BufferedImage bufferedImage = new BufferedImage( 
        						image.getWidth(null), 
        						image.getHeight(null), 
        						BufferedImage.TYPE_INT_RGB ); 
        		
        	Graphics g = bufferedImage.createGraphics(); 
  			g.drawImage(image,0,0,null); 
            g.dispose(); 
            return( bufferedImage ); 
        }  
	}

	public void init_terrain()
	{
		int selection =2;
		
		switch(selection)
		{
			case 1:
				for ( int i = 0 ; i != longueur ; i++ )
				{
					for ( int j = 0 ; j != largeur ; j++ )
					{
						switch((int)(Math.random()*2.0))
						{
							case 0: terrain[i][j] = 0;
								break;
							case 1: terrain[i][j] = 4;
								break;
						}
					}		

				}
				break;
				
			case 2 :
				Image perlin_cloud = null ;
				int choix_terrain=(int) (Math.random()*4)+1;
				
				try
				{	
					switch(choix_terrain)
					{
						case 1:
							perlin_cloud = ImageIO.read(new File("perlin_50x50.png"));
						break;
						
						case 2:
							perlin_cloud = ImageIO.read(new File("perlin_128x128.png"));
						break;
						
						case 3:
							perlin_cloud = ImageIO.read(new File("perlin1_100x100.png"));
						break;
						
						case 4:
							perlin_cloud = ImageIO.read(new File("perlin4_50x50.png"));
						break;
					}
				}
				
				catch(Exception e)
				{
					e.printStackTrace();
					System.exit(-1);
				}
				
				BufferedImage buff = toBufferedImage(perlin_cloud);
				
				for ( int i = 0 ; i != longueur ; i++ )
				{
					for ( int j = 0 ; j != largeur ; j++ )
					{
						int rgb = buff.getRGB(i,j); 
						int red = (rgb >> 16) & 0x000000FF;
						//red=red%disparite_altitude;
						
						//int borne_mer = (int) (niveau_mer*255);
						//System.out.println(borne_mer);
						
						if(red< niveau_mer )  //ICI ON INITIALISE LE TERRAIN
						{
							terrain[i][j] = 4; // mer
						}
						else
						{
							terrain[i][j] = 0; // herbe
						}
						
						int type_de_generation=2;
						switch(type_de_generation) 
						{
							case 1:
								if(red<120)  //ICI ON INITIALISE LE TERRAIN  							
								{
									altitude[i][j]=0;
								}
								else if (red < 140)
								{
									altitude[i][j] = 1;
								}
								else if (red < 160)
								{
									altitude[i][j] = 2;
								}
								else if (red < 180)
								{
									altitude[i][j] = 3;
								}
								else if (red < 210)
								{
									altitude[i][j] = 4;
								}
								else if (red < 240)
								{
									altitude[i][j] = 5;
								}
								break;
							
							case 2:								
								int [] tab_borne_sup=getBorneSupAlt();
								//System.out.println("niveau altitude de red"+getNiveauAltitude(tab_borne_sup,red));
								altitude[i][j]=getNiveauAltitude(tab_borne_sup,red);
								break;
						}
					}
				}
				break;
				
			case 3:  //PYRAMIDE 10x10 (pour les test d'ecoulements)
				for ( int i = 0 ; i != longueur ; i++ )
				{
					for ( int j = 0 ; j != largeur ; j++ )
					{
						switch((int)(Math.random()*1.0))
						{
							case 0: terrain[i][j] = 0;
								break;
							case 1: terrain[i][j] = 4;
								break;
						}
					}	
				}
				
				nv_liquide[5][5] = duree_evaporation;
				int [] [] tmp= {
						{0,0,0,0,0,0,0,0,0,0},
						{0,3,0,0,0,1,0,0,0,0},
						{0,0,0,0,1,1,1,0,0,0},
						{0,0,0,1,1,2,1,1,0,0},
						{0,0,1,0,2,3,2,1,1,0},
						{0,1,0,0,3,4,3,2,1,1},
						{0,0,1,0,2,3,2,1,1,0},
						{0,0,0,1,1,2,1,1,0,0},
						{0,0,0,0,1,1,1,0,0,0},
						{0,0,0,0,0,1,0,0,0,0}	};
				altitude= tmp;
				break;
		}
		//CALCUL DE POSITION DU VOLCAN + initialisation des cellules de sable
		this.x_volcan=0;
		this.x_volcan=0;
		
		for( int i=0; i<longueur; i++)
		{
			for ( int j=0; j<largeur; j++) 
			{
				if(altitude[i][j]>altitude[x_volcan][y_volcan]) 
				{
					x_volcan=i;
					y_volcan=j;
				}
				
				if(terrain[i][j]==0)
				{     //sable
					if ( (terrain[(i+1)%longueur][j]==4)|| (terrain[i][(j+1)%largeur]==4 ) ||(terrain[(i-1+longueur)%longueur][j]==4 ) || (terrain[i][(j-1+largeur)%largeur]==4 ) ) 
					{
						terrain[i][j] = 2;
					}
				}	
			}
		}
	}
	
	public void init_surfaces( double proportion_arbres)
	{
		for ( int i = 0 ; i != longueur ; i++ )
		{
			for ( int j = 0 ; j != largeur ; j++ )
			{
				if(terrain[i][j]==0)
				{
					if(Math.random()<proportion_arbres)
					{	
						Arbre a= new Arbre(this);
						surfaces[i][j]=a;
					}
				}
			}		
		}
		
	}
	
	public void init_agents(int nb_poule, int nb_renard)
	{
		int i;
		
		for(i=0; i<nb_poule; i++)
		{
			int l=(int)(Math.random()*longueur);
			int b=(int)(Math.random()*largeur);
			int c=(int)(Math.random()*2);
            
            while (!( (this.terrain[l][b] == 0) && ( this.surfaces[l][b] == null) ) )
            {
                l = (int)(Math.random()*longueur);
                b = (int)(Math.random()*largeur);
            }
            
            //ON INTEGRE UNE POULE !!
            if(c == 0)
            {
                Poule poule = new PouleFemelle(l,b,this);
                //this.add(poule);
            }
            else
            {
                Poule poule = new PouleMale(l,b,this);
                //this.add(poule);
            }
        }
		
		for(i=0; i<nb_renard; i++)
		{
			int l=(int)(Math.random()*longueur);
			int b=(int)(Math.random()*largeur);
			int c=(int)(Math.random()*2);
		
			while (!( (this.terrain[l][b] == 0) && ( this.surfaces[l][b] == null) ) )
			{
				l = (int)(Math.random()*longueur);
				b = (int)(Math.random()*largeur);
			}
		
			//ON INTEGRE UN RENARD !!
			if(c == 0)
            {
                PredatorAgent renard = new PredatorAgentFemelle(l,b,this);
                //this.add(renard);
            }
            else
            {
                PredatorAgent renard = new PredatorAgentMale(l,b,this);
                //this.add(renard);
            }
		}
	}

	public void init_nuages(int nb_nuages)
	{
		int i;
		
		for(i=0; i<nb_nuages; i++)
		{
			int l=(int)(Math.random()*longueur);
			int b=(int)(Math.random()*largeur);
		
			Nuage n = new Nuage(l,b,this);
			this.add(n);
		}
	}
	
	public void step( )
	{
		if (cpt_vent==duree_vent)
		{
			cpt_vent=0;
		}
			
		if(cpt_vent==0) 
		{
			orient_vent = (int) (Math.random()*4);
			System.out.println(" valeur de orient_vent: "+ orient_vent+ "  !!!!!!!!!!");
		}
		stepTerrain();
		stepSurface(orient_vent);
		stepAgents();
		stepNuages(orient_vent);
		//renouveler l'affichage apres avec un appel a  paint

		cpt_vent++;
	}

	public int getLongueur()
	{
		return longueur;
	}
	
	public int getLargeur()
	{
		return largeur;
	}
		
	public int[][] getAltitude()
	{
		return altitude;
	}
	
	public void add (Agent agent)
	{
		agents.add(agent);
	}
		
	public void add (Nuage n)
	{
		nuages.add(n);
	}
	


	public void stepTerrain() // terrain THEN surfaces THEN agents
	{
		int[][] tableauCourant=terrain;
		int[][] nouveauTableau= new int[longueur][largeur];
		
		//maj de l'automate cellulaire pour feu de forets

		for ( int x = 0 ; x != tableauCourant.length ; x++ ) 
		{
			for ( int y = 0 ; y != tableauCourant[0].length ; y++ )
			{	
				if (( tableauCourant[x][y] == 0) || (tableauCourant[x][y] == 3) || (tableauCourant[x][y]==2))
				{
					if ( (tableauCourant[(x+1)%longueur][y]==4)|| (tableauCourant[x][(y+1)%largeur]==4 ) ||(tableauCourant[(x-1+longueur)%longueur][y]==4 ) || (tableauCourant[x][(y-1+largeur)%largeur]==4 ) ) 
					{
						nouveauTableau[x][y] = 2;
					}
					else
					{
						nouveauTableau[x][y] = 0;		
					}
				}
					
				if ( ( surfaces[x][y] instanceof ArbreEnFeu )||(tableauCourant[x][y] == 5 ) )
				{
					nouveauTableau[x][y] = 3;	
				}
					
				if (tableauCourant[x][y] == 4)//ecoulement liquide (eau)
				{
					if(nv_liquide[x][y]<0) 
					{
						nouveauTableau[x][y] = 0;
					}
					else 
					{
						nouveauTableau[x][y] = 4;
					}
				}
					
				if (tableauCourant[x][y] == 5)//ecoulement liquide (lave)
				{
					if(nv_liquide[x][y]<0) 
					{
						nouveauTableau[x][y] = 3; //cendre
					}
					else 
					{
						nouveauTableau[x][y] = 5;
						nv_liquide[x][y]--;
					}
				}
				else 
				{
						//S'il n'y a pas de lave a cette case on checke les cases environnantes
					if (((tableauCourant[(x+1)%longueur][y]==5)&& (altitude[(x+1)%longueur][y]>altitude[x][y])) || ((tableauCourant[x][(y+1)%largeur]==5 )&& (altitude[x][(y+1)%largeur]>altitude[x][y])) ||((tableauCourant[(x-1+longueur)%longueur][y]==5 ) && (altitude[(x-1+longueur)%longueur][y]>altitude[x][y]))|| ((tableauCourant[x][(y-1+largeur)%largeur]==5 )&& (altitude[x][(y-1+largeur)%largeur]>altitude[x][y]))) 
					{
						nouveauTableau[x][y]=5;
						nv_liquide[x][y]= duree_evaporation;
					}
				}
					
				if (((tableauCourant[(x+1)%longueur][y]==4)&& (altitude[(x+1)%longueur][y]>altitude[x][y])) || ((tableauCourant[x][(y+1)%largeur]==4 )&& (altitude[x][(y+1)%largeur]>altitude[x][y])) ||((tableauCourant[(x-1+longueur)%longueur][y]==4 ) && (altitude[(x-1+longueur)%longueur][y]>altitude[x][y]))|| ((tableauCourant[x][(y-1+largeur)%largeur]==4 )&& (altitude[x][(y-1+largeur)%largeur]>altitude[x][y]))) 
				{
					nouveauTableau[x][y]=4;
					nv_liquide[x][y]= duree_evaporation;
					System.out.println("x "+x+" y "+y+ " a ete renouvele !");
				}
			}				
		}

			if(cpt_volcan==duree_eruption) 
			{
				nouveauTableau[x_volcan][y_volcan]=5;
				cpt_volcan=0;
			}
			else 
				if(tableauCourant[x_volcan][y_volcan]!=5)
				{
					nouveauTableau[x_volcan][y_volcan]=6;
				}
			
			cpt_volcan++;

			// 2 - met a jour le tableau affichable
				
			for ( int x = 0 ; x != tableauCourant.length ; x++ ) 
			{
				for ( int y = 0 ; y != tableauCourant[0].length ; y++ ) 
				{
					tableauCourant[x][y] = nouveauTableau[x][y];		
				}
			}
		terrain=tableauCourant;
	}


	public void stepSurface(int orient_vent)
	{
		Surface[][] tableauCourant=surfaces;
		Surface[][] nouveauTableau= new Surface[longueur][largeur];
		
		
		///maj de l'automate cellulaire pour feu de forets
		
		for ( int x = 0 ; x != surfaces.length ; x++ ) 
		{
			for ( int y = 0 ; y != surfaces[0].length ; y++ ) 
			{
				if(terrain[x][y]==0) 
				{
					if (tableauCourant[x][y] instanceof Arbre) 
					{
						switch(orient_vent) 
						{
							case 0: //nord
								stepFeuNord(x,y,tableauCourant,nouveauTableau);
								break;
								
							case 1: // est
								stepFeuEst(x,y,tableauCourant,nouveauTableau);
								break;
								
							case 2: //sud
								stepFeuSud(x,y,tableauCourant,nouveauTableau);
								break;
					
							case 3: //ouest
								stepFeuOuest(x,y,tableauCourant,nouveauTableau);
								break;
							default:
								System.out.println(" mauvaise valeur de orient_vent dans le stepSurface()");
								break;
						}
					}
					if (p2 > Math.random()) 
					{
						if ( terrain[x][y] == 0) 
						{
							boolean agent_a_cette_position =false;
							
							if(agents.size()>0) 
							{    // on verifie qu'il n'y a pas d'agent a cette position
								for (int j = 0 ; j <agents.size() ; j++) 
								{
									Agent a=agents.get(j);
									
									if( (x==a._x) && (y==a._y) )
									{
										agent_a_cette_position = true;
										break;
									}
								}
							}
							
							if(!agent_a_cette_position)
							{
								nouveauTableau[x][y] = new Arbre(this);	
							}
						}
					}
						
					else if (p1 > Math.random())
					{
						if ( nouveauTableau[x][y] instanceof Arbre) 
						{
							nouveauTableau[x][y] = new ArbreEnFeu(this);
						}
					}
				}	
			}
		}
			
		// 2 - met a jour le tableau affichable
				
		for ( int x = 0 ; x != tableauCourant.length ; x++ ) 
		{
			for ( int y = 0 ; y != tableauCourant[0].length ; y++ ) 
			{
				tableauCourant[x][y] = nouveauTableau[x][y];		
			}
		}
		surfaces=tableauCourant;	
	}

	private void stepFeuNord(int x, int y,Surface[][] tableauCourant,Surface[][] nouveauTableau )
	{
		// test de lave a proximite: (terrain[(x+1)%longueur][y]==5) || (terrain[x][(y+1)%largeur]==5) || terrain[(x-1+longueur)%longueur][y] ==5   || (terrain[x][(y-1+largeur)%largeur]==5) 
		if( (tableauCourant[(x-1+longueur)%longueur][(y+1+largeur)%largeur] instanceof ArbreEnFeu ) || (tableauCourant[(x+longueur)%longueur][(y+1+largeur)%largeur] instanceof ArbreEnFeu ) || (tableauCourant[(x+1+longueur)%longueur][(y+1+largeur)%largeur] instanceof ArbreEnFeu ) || (terrain[(x+1)%longueur][y]==5) || (terrain[x][(y+1)%largeur]==5) || terrain[(x-1+longueur)%longueur][y] ==5   || (terrain[x][(y-1+largeur)%largeur]==5))
		{
			 nouveauTableau[x][y] = new ArbreEnFeu(this);		 
		}
		else 
		{
			nouveauTableau[x][y] = new Arbre(this);		
		}
	}
	
	private void stepFeuEst(int x, int y,Surface[][] tableauCourant,Surface[][] nouveauTableau)
	{
		// test de lave a proximite: (terrain[(x+1)%longueur][y]==5) || (terrain[x][(y+1)%largeur]==5) || terrain[(x-1+longueur)%longueur][y] ==5   || (terrain[x][(y-1+largeur)%largeur]==5) 
		if ( (tableauCourant[(x-1+longueur)%longueur][(y-1+largeur)%largeur] instanceof ArbreEnFeu ) || (tableauCourant[(x-1+longueur)%longueur][(y+largeur)%largeur] instanceof ArbreEnFeu ) || (tableauCourant[(x+1+longueur)%longueur][(y+1+largeur)%largeur] instanceof ArbreEnFeu ) || (terrain[(x+1)%longueur][y]==5) || (terrain[x][(y+1)%largeur]==5) || terrain[(x-1+longueur)%longueur][y] ==5   || (terrain[x][(y-1+largeur)%largeur]==5))
		{
			 nouveauTableau[x][y] = new ArbreEnFeu(this);	
			}				
		else 
		{		
			nouveauTableau[x][y] = new Arbre(this);	
		}
	}
	
	private void stepFeuSud(int x, int y,Surface[][] tableauCourant,Surface[][] nouveauTableau)
	{
		// test de lave a proximite: (terrain[(x+1)%longueur][y]==5) || (terrain[x][(y+1)%largeur]==5) || terrain[(x-1+longueur)%longueur][y] ==5   || (terrain[x][(y-1+largeur)%largeur]==5) 
		if( (tableauCourant[(x-1+longueur)%longueur][(y-1+largeur)%largeur] instanceof ArbreEnFeu ) || (tableauCourant[(x+longueur)%longueur][(y-1+largeur)%largeur] instanceof ArbreEnFeu ) || (tableauCourant[(x+1+longueur)%longueur][(y-1+largeur)%largeur] instanceof ArbreEnFeu ) || (terrain[(x+1)%longueur][y]==5) || (terrain[x][(y+1)%largeur]==5) || terrain[(x-1+longueur)%longueur][y] ==5   || (terrain[x][(y-1+largeur)%largeur]==5))
		{
			 nouveauTableau[x][y] = new ArbreEnFeu(this);	
		}				
		else 
		{		
			nouveauTableau[x][y] = new Arbre(this);		
		}
	}
	
	private void stepFeuOuest(int x, int y,Surface[][] tableauCourant,Surface[][] nouveauTableau)
	{
		// test de lave a proximite: (terrain[(x+1)%longueur][y]==5) || (terrain[x][(y+1)%largeur]==5) || terrain[(x-1+longueur)%longueur][y] ==5   || (terrain[x][(y-1+largeur)%largeur]==5) 
		if( (tableauCourant[(x+1+longueur)%longueur][(y-1+largeur)%largeur] instanceof ArbreEnFeu ) || (tableauCourant[(x+1+longueur)%longueur][(y+largeur)%largeur] instanceof ArbreEnFeu ) || (tableauCourant[(x+1+longueur)%longueur][(y+1+largeur)%largeur] instanceof ArbreEnFeu ) || (terrain[(x+1)%longueur][y]==5) || (terrain[x][(y+1)%largeur]==5) || terrain[(x-1+longueur)%longueur][y] ==5   || (terrain[x][(y-1+largeur)%largeur]==5))
		{
			 nouveauTableau[x][y] = new ArbreEnFeu(this);	
		}				
		else 
		{		
			nouveauTableau[x][y] = new Arbre(this);		
		}
	}

	public void stepAgents() // world THEN agents
    {	
    	
        for ( int i = 0 ; i < agents.size() ; i++ )
        {
            agents.get(i).step();
        }
        
        ArrayList<Agent> tmp = new ArrayList<> (agents); // la liste qu'on va parcourir
        
        for (int i = 0 ; i < tmp.size() ; i++) // gere la naissance
        {
            Agent a1 = tmp.get(i);
            int c = (int)Math.random() * 2;
            
            for (int j = 0 ; j < tmp.size() ; j++)
            {
                Agent a2 = tmp.get(j);
                
                if ( (a1._x==a2._x) && (a1._y==a2._y) ) 
                {
                	if (a1 instanceof PouleFemelle)
                	{
                		if (a2 instanceof PouleMale)
                		{
                			if(c == 0)
                			{
                				Poule poule = new PouleMale(a1._x,a1._y, this);
                			}
                			else
                			{
                				Poule poule1 = new PouleFemelle(a1._x, a1._y, this);
                			}
                		}
                	}
                }
            }
        }
        
        ArrayList<Agent> ren = new ArrayList<> (agents);
        
        for (int i = 0 ; i < ren.size() ; i++) // gere la naissance
        {
            Agent a1 = ren.get(i);
            int c = (int)Math.random() * 2;
            
            for (int j = 0 ; j < ren.size() ; j++)
            {
                Agent a2 = ren.get(j);
                
                if ( (a1._x == a2._x) && (a1._y == a2._y) ) 
                {
                	if (a1 instanceof PredatorAgentFemelle)
                	{
                		if (a2 instanceof PredatorAgentMale)
                		{
                			if(c == 0)
                			{
                				PredatorAgent renard = new PredatorAgentMale(a1._x,a1._y, this);
                			}
                			else
                			{
                				PredatorAgent renard1 = new PredatorAgentFemelle(a1._x, a1._y, this);
                			}
                		}
                	}
                }
            }
        }
    }
		
	public void stepNuages(int orient_vent)
	{
		for ( int i = 0 ; i < nuages.size() ; i++ )
		{
			nuages.get(i).step(orient_vent);
		}
	}
	
	private int getNiveauAltitude( int [] tab_borne_sup, int red) 
	{
		for(int z=0; z<disparite_altitude; z++) 
		{
			//System.out.println(tab_borne_sup[z]);
			if(red<=tab_borne_sup[z]) 
			{
				return z;
			}
		}
		return disparite_altitude;
	}
	
	private int[] getBorneSupAlt() 
	{ 
		int [] res = new int[disparite_altitude];
		int plage_parfaite= (int) (255/disparite_altitude);
		int difference_plage = (int) (Math.random()*heterogeneite_altitude);
		int cpt=0;
		
		for(int i =0; i< disparite_altitude ; i++) 
		{
			int plage_alloue = plage_parfaite + getRandomSign()*difference_plage ;
			//System.out.println(" borne sup "+cpt);
			cpt+=plage_alloue;
			res[i]=cpt;
		}
		return res;
	}
	
	public int getRandomSign() 
	{
	    Random rand = new Random();
	    if(rand.nextBoolean())
	        return -1;
	    else
	        return 1;
	}
		
	public ArrayList<Nuage> getNuages() 
	{
		return nuages;
	}
	
	public ArrayList<Agent>  getAgents()
	{
		return agents;
	}
}
