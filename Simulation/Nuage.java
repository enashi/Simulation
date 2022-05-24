import java.util.ArrayList;
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


public class Nuage {
	private World _world;
	private int 	_x;
	private int 	_y;
	private boolean eclair= false;
	
	public Nuage(int __x, int __y, World __w)
	{
		_x = __x;
		_y = __y;
		_world = __w;
		
	}
	
	public void step(int orient_vent){
		if(eclair==true) {
			eclair = false;
		}
		switch(orient_vent) {
			case 0: //nord
				stepNord();
				break;
			case 1: // est 
				stepEst();
				break;
			case 2: //sud
				stepSud();
				break;
			case 3: //ouest 
				stepOuest();
				break;
			default:
				System.out.println(" Il y a un problème avec la valeur de l'orientation du vent, dans le step de Nuage");
				break;
		}
		
		eclair(0.3);
	}
	
	public void eclair( double prob) {    // PROB CORRESPOND A LA PROBABILITE QU'UN NUAGE LANCE UN ECLAIR
		double rand= Math.random();
		if(rand< prob) {
			eclair=true;
			if(_world.surfaces[_x][_y] instanceof Arbre) {
				_world.surfaces[_x][_y]=new ArbreEnFeu(_world);
			}
		}
	}
	public World getW(){
		return _world;
	}
	public int getX(){
		return _x;
	}
	
	public int getY(){
		return _y;
	}
	public boolean getEclair(){
		return eclair;
	}
	
	private void stepSud(){
		if(eclair==true) {
			eclair = false;
		}
		_x= (_x +( (int) (Math.random()*3)-1)+_world.getLongueur())%_world.getLongueur(); // varie de -1 à 1
		_y= (_y +( (int) (Math.random()*2))+_world.getLargeur())%_world.getLargeur(); //varie de 0 à 1
		
	}
	private void stepEst(){
		if(eclair==true) {
			eclair = false;
		}
		_x= (_x +( (int) (Math.random()*2))+_world.getLongueur())%_world.getLongueur(); // varie de 0 à 1
		_y= (_y +( (int) (Math.random()*3)-1)+_world.getLargeur())%_world.getLargeur(); //varie de -1 à 1
		
	}
	
	private void stepNord(){
		if(eclair==true) {
			eclair = false;
		}
		_x= (_x +( (int) (Math.random()*3)-1)+_world.getLongueur())%_world.getLongueur(); // varie de -1 à 1
		_y= (_y +( (int) (Math.random()*2)-1)+_world.getLargeur())%_world.getLargeur(); //varie de -1 à 0
		
	}
	private void stepOuest(){
		if(eclair==true) {
			eclair = false;
		}
		_x= (_x +( (int) (Math.random()*2)-1)+_world.getLongueur())%_world.getLongueur(); // varie de -1 à 0
		_y= (_y +( (int) (Math.random()*3)-1)+_world.getLargeur())%_world.getLargeur(); //varie de -1 à 1

	}
	
}
