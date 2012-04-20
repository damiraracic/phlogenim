/**
 * 
 */
package phlogenim.sprite;

import phlogenim.Manager;
import rjones.game.engine.sound.SoundClip;
import rjones.game.engine.sprite.Sprite;

/**
 * @author Damir
 *
 */
public abstract class PSprite implements Sprite {
	
	protected boolean first_update;
	
	protected double mass;

	protected SoundClip collision_sound;
	
	protected Manager manager;
	
	
	public double getMass()
	{
		return mass;
	}
	
	public void setMass(double m)
	{
		mass = m;
	}

	public void setCollisionSound(SoundClip s)
	{
		collision_sound = s;
	}
	
	public Manager getManager()
	{
		return manager;
	}
	
	public void setManager(Manager mng)
	{
		manager = mng;
	}
	
	public void setCollisionBoxScale(double scale) {}
}
