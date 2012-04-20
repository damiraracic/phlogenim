/**
 * 
 */
package phlogenim.sprite;

import java.awt.Color;
import java.awt.Graphics2D;

import phlogenim.State;

import rjones.game.engine.sound.SoundClip;
import rjones.game.engine.sprite.RectangleSprite;
import rjones.game.engine.sprite.Sprite;
import rjones.game.engine.util.BetterRectangle;
import rjones.game.engine.util.Vector2D;


/**
 * @author Damir Aracic
 *
 */

public class PExitSprite extends PSprite implements State {

	/**
	 * A delegate to provide default behaviors for this sprite.
	 */
	private RectangleSprite sprite_delegate;
		
	public PExitSprite(double x, double y, double w, double h, Color c)
	{
		sprite_delegate = new RectangleSprite(x, y, w, h, c);
	}
	
	public void collideWith(Sprite s)
	{
		if (s instanceof PHeroSprite)
		{
			manager.setGameState(LOAD_LEVEL);
		}
	}
	
	public BetterRectangle getCollisionBox()
	{
		return sprite_delegate.getCollisionBox();
	}

	public double getSurfaceNormal(double theta)
	{
		return sprite_delegate.getSurfaceNormal(theta);
	}

	public void paint(Graphics2D g)
	{
		sprite_delegate.paint(g);
	}

	public boolean update(long d_ms)
	{
		return sprite_delegate.update(d_ms);
	}
	
	public Vector2D getVelocity()
	{
		return sprite_delegate.getVelocity();
	}

}