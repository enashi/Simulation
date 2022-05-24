public class PouleFemelle extends Poule
{
    // VERIFIER QUE LA POURSUITE/FUITE SE FAIT INTELLIGEMENT
    
    public PouleFemelle( int __x, int __y, World __w )
    {
        super(__x,__y,__w);
    }
    
    /*public void Reproduction()
    {
        for ( int i = 0; i <_world.getLongueur(); i++ )
        {
            Agent a = _world.agents.get(i);
            
            for(int j=0; j < _world.getLargeur(); j++)
            {
                int c = (int)Math.random() * 2;
                
                if(i == j)
                {
                    if(a instanceof PouleMale)
                    {
                        if((c%2) == 0)
                        {
                            Agent n = new PouleMale(a._x, a._y, _world);
                            _world.agents.add(n);
                        }
                        else
                        {
                            Agent n = new PouleFemelle(a._x, a._y, _world);
                            _world.agents.add(n);
                        }
                    }
                }
            }
        }
    }*/
}
