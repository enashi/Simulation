public class Poule extends Agent
{
    // VERIFIER QUE LA POURSUITE/FUITE SE FAIT INTELLIGEMENT
    
    public boolean _alive;
    public int vie = _vie;
    
    public Poule( int __x, int __y, World __w )
    {
        super(__x,__y,__w);
        
        _alive = true;
    }
    
    public void step()
    {
        // met a jour l'agent
        
        // ... A COMPLETER
        
        boolean a_bouge=false;
        
        boolean a_fui=false;
        
        for ( int i = 0; i <_world.agents.size(); i++ )
        {
            
            Agent a = _world.agents.get(i);
            
            if (  a instanceof PredatorAgent)
            {
                if (((a._x == this._x+1) && (a._y == this._y)) && (destination_valide(_x,_y,6))) //est
                {
                    _orient = 6;
                    a_fui = true;
                }
                else
                {
                    if (((a._x == this._x) && (a._y == this._y+1)) && (destination_valide(_x,_y,0))) //sud
                    {
                        _orient = 0;
                        a_fui = true;
                    }
                    else
                    {
                        if (((a._x == this._x-1) && (a._y == this._y)) && (destination_valide(_x,_y,2))) //ouest
                        {
                            _orient = 2;
                            a_fui = true;
                        }
                        else
                        {
                            if (((a._x == this._x) && (a._y == this._y-1)) && (destination_valide(_x,_y,4))) //nord
                            {
                                _orient = 4;
                                a_fui = true;
                            }
                            else
                            {
                                if (((a._x == this._x+1) && (a._y == this._y-1)) && (destination_valide(_x,_y,5))) //nord-est
                                {
                                    _orient = 5;
                                    a_fui = true;
                                }
                                else
                                {
                                    if (((a._x == this._x+1) && (a._y == this._y+1)) && (destination_valide(_x,_y,7))) //sud-est
                                    {
                                        _orient = 7;
                                        a_fui = true;
                                    }
                                    else
                                    {
                                        if (((a._x == this._x-1) && (a._y == this._y+1)) && (destination_valide(_x,_y,1))) //sud-ouest
                                        {
                                            _orient = 1;
                                            a_fui = true;
                                        }
                                        else
                                        {
                                            if (((a._x == this._x+1) && (a._y == this._y-1)) && (destination_valide(_x,_y,3))) //nord-ouest
                                            {
                                                _orient = 3;
                                                a_fui = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // JE CONSIDERE QUE EN 20 ( + 1 pour l'initialisation ) TENTATIVES ALEATOIRES LE RANDOM AURA SELECTIONNE TOUTES LES 4 VALEURS POSSIBLES POUR _orient !
        
        if (!a_fui)
        {
            for(int tmp=0;tmp<20;tmp++)
            {
                if (Math.random() > 0.5) // au hasard
                    _orient = (_orient + 1) % 8;
                else
                    _orient = (_orient - 1 + 8) % 8;
                
                if(destination_valide(_x,_y,_orient))
                {
                    a_bouge=true;
                    break;
                }
            }
        }
        
        if ( (a_bouge) || (a_fui) )
        {
            
            // met a jour: la position de l'agent (depend de l'orientation)
            
            switch (_orient)
            {
                case 0: // nord
                    _y = (_y - 1 + _world.getLargeur()) % _world.getLargeur();
                    break;
                    
                case 1: // nord-est
                    _x = (_x + 1 + _world.getLongueur()) % _world.getLongueur();
                    _y = (_y - 1 + _world.getLargeur()) % _world.getLargeur();
                    break;
                    
                case 2: // est
                    _x = (_x + 1 + _world.getLongueur()) % _world.getLongueur();
                    break;
                    
                case 3: // sud-est
                    _x = (_x + 1 + _world.getLongueur()) % _world.getLongueur();
                    _y = (_y + 1 + _world.getLargeur()) % _world.getLargeur();
                    break;
                    
                case 4: // sud
                    _y = (_y + 1 + _world.getLargeur()) % _world.getLargeur();
                    break;
                    
                case 5: // sud-ouest
                    _x = (_x - 1 + _world.getLongueur()) % _world.getLongueur();
                    _y = (_y + 1 + _world.getLargeur()) % _world.getLargeur();
                    break;
                    
                case 6: // ouest
                    _x = (_x - 1 + _world.getLongueur()) % _world.getLongueur();
                    break;
                    
                case 7: // nord-ouest
                    _x = (_x - 1 + _world.getLongueur()) % _world.getLongueur();
                    _y = (_y - 1 + _world.getLargeur()) % _world.getLargeur();
                    break;
            }
            vie = vie-1;
        }
        
        if(vie == 0)
        {
            _world.agents.remove(this);
        }
    }
    
    public boolean destination_valide( int x_dest, int y_dest, int orientation)
    {
        switch (orientation)
        {
                
            case 0: //nord
                if(((_world.terrain[x_dest][(y_dest - 1 + _world.getLargeur()) % _world.getLargeur()] == 0) || (_world.terrain[x_dest][(y_dest - 1 + _world.getLargeur()) % _world.getLargeur()]==3) || (_world.terrain[x_dest][(y_dest - 1 + _world.getLargeur()) % _world.getLargeur()] == 2)) && (!(_world.surfaces[x_dest][(y_dest - 1 + _world.getLargeur()) % _world.getLargeur()] instanceof Surface)))
                {
                    return true;
                }
                break;
                
            case 1: //nord-est
                if(((_world.terrain[(x_dest + 1 + _world.getLongueur()) % _world.getLongueur()][(y_dest - 1 + _world.getLargeur()) % _world.getLargeur()] == 0) || (_world.terrain[(x_dest + 1 + _world.getLongueur()) % _world.getLongueur()][(y_dest - 1 + _world.getLargeur()) % _world.getLargeur()] == 3) || (_world.terrain[(x_dest + 1 + _world.getLongueur()) % _world.getLongueur()][(y_dest - 1 + _world.getLargeur()) % _world.getLargeur()] == 2)) && (!(_world.surfaces[(x_dest + 1 + _world.getLongueur()) % _world.getLongueur()][(y_dest - 1 + _world.getLargeur()) % _world.getLargeur()] instanceof Surface)))
                {
                    return true;
                }
                break;
                
            case 2: //est
                if(((_world.terrain[(x_dest + 1 + _world.getLongueur()) % _world.getLongueur()][y_dest] == 0) || (_world.terrain[(x_dest + 1 + _world.getLongueur() )% _world.getLongueur()][y_dest] == 3) || (_world.terrain[(x_dest + 1 + _world.getLongueur() )% _world.getLongueur()][y_dest] == 2)) && (!(_world.surfaces[(x_dest + 1 + _world.getLongueur() )% _world.getLongueur()][y_dest] instanceof Surface)))
                {
                    return true;
                }
                break;
                
            case 3: //sud-est
                if (((_world.terrain[(x_dest + 1 + _world.getLongueur()) % _world.getLongueur()][(y_dest + 1 + _world.getLargeur()) % _world.getLargeur()] == 0) || (_world.terrain[(x_dest + 1 + _world.getLongueur()) % _world.getLongueur()][(y_dest + 1 + _world.getLargeur()) % _world.getLargeur()] == 3) || (_world.terrain[(x_dest + 1 + _world.getLongueur()) % _world.getLongueur()][(y_dest + 1 + _world.getLargeur()) % _world.getLargeur()] == 2)) && (!(_world.surfaces[(x_dest+ 1 + _world.getLongueur()) % _world.getLongueur()][(y_dest + 1 + _world.getLargeur()) % _world.getLargeur()] instanceof Surface)))
                {
                    return true;
                }
                break;
                
            case 4: //sud
                if (((_world.terrain[x_dest][(y_dest + 1 + _world.getLargeur()) % _world.getLargeur()] == 0)||(_world.terrain[x_dest][(y_dest + 1 + _world.getLargeur()) % _world.getLargeur()] == 3)||(_world.terrain[x_dest][(y_dest + 1 + _world.getLargeur()) % _world.getLargeur()] == 2)) && (!(_world.surfaces[x_dest][(y_dest + 1 + _world.getLargeur()) % _world.getLargeur()] instanceof Surface)))
                {
                    return true;
                }
                break;
                
            case 5: //sud-ouest
                if (((_world.terrain[(x_dest - 1 + _world.getLongueur()) % _world.getLongueur()][(y_dest + 1 + _world.getLargeur()) % _world.getLargeur()] == 0) || (_world.terrain[(x_dest - 1 + _world.getLongueur()) % _world.getLongueur()][y_dest] == 3) || (_world.terrain[(x_dest - 1 + _world.getLongueur()) % _world.getLongueur()][y_dest] == 2)) && (!(_world.surfaces[(x_dest - 1 + _world.getLongueur()) % _world.getLongueur()][(y_dest + 1 + _world.getLargeur()) % _world.getLargeur()] instanceof Surface)))
                {
                    return true;
                }
                break;
                
            case 6: //ouest
                if (((_world.terrain[(x_dest - 1 + _world.getLongueur()) % _world.getLongueur()][y_dest] == 0) || (_world.terrain[(x_dest - 1 + _world.getLongueur()) % _world.getLongueur()][y_dest] == 3) || (_world.terrain[(x_dest - 1 + _world.getLongueur()) % _world.getLongueur()][y_dest] == 2)) && (!(_world.surfaces[(x_dest - 1 + _world.getLongueur()) % _world.getLongueur()][y_dest] instanceof Surface)))
                {
                    return true;
                }
                break;
                
            case 7: //nord-ouest
                if(((_world.terrain[(x_dest - 1 + _world.getLongueur()) % _world.getLongueur()][(y_dest - 1 + _world.getLargeur()) % _world.getLargeur()] == 0) || (_world.terrain[(x_dest - 1 + _world.getLongueur()) % _world.getLongueur()][(y_dest - 1 + _world.getLargeur()) % _world.getLargeur()] == 3) || (_world.terrain[(x_dest - 1 + _world.getLongueur()) % _world.getLongueur()][(y_dest - 1 + _world.getLargeur()) % _world.getLargeur()] == 2)) && (!(_world.surfaces[(x_dest - 1 + _world.getLongueur()) % _world.getLongueur()][(y_dest - 1 + _world.getLargeur()) % _world.getLargeur()] instanceof Surface)))
                {
                    return true;
                }
                break;
                
        }
        
        return false;
    }
}
