
public abstract class Agent {

	World _world;
	
	
	int _sexe;  // 0= fille, 1 = garÃ§on
	int 	_x;
	int 	_y;
	int 	_orient;
	int 	_etat;
	int _vie;
	
	int S_x; // position x du Sprite 
	int S_y; // position y du Sprite 
	int p_S_x; // position x du Sprite prÃ©cÃ©dent 
	int p_S_y; // position y du Sprite prÃ©cÃ©dent 
	public Agent( int __x, int __y, World __w )
	{
		_x = __x;
		_y = __y;
		_world = __w;
		_world.add(this);
		
		
		_vie=100;
		_orient = 0;
		
		_sexe = (int) Math.random()*2;
		
		
		S_x=_x;
		S_y=_y;
		p_S_x=S_x;
		p_S_y=S_y;
		
		
	}
	
	abstract public void step( );
	
	abstract public boolean destination_valide( int x_dest, int y_dest, int orientation);
	
	public void move_Sprite()
	{
		if((p_S_x!=S_x) || (p_S_y!=S_x))
		{
			if(p_S_x<S_x){
				p_S_x ++;
				
			}
			else if(p_S_y<S_x){
				p_S_y ++;
			}
			else if(p_S_x>S_x){
				p_S_x --;
			}
			else if(p_S_y>S_x){
				p_S_y --;
			}
		}
		else
		{
			p_S_x=S_x;
			p_S_y=S_y;
		}
		
			
		
	} // DÃ©caler de 1 pixel le sprite
}


