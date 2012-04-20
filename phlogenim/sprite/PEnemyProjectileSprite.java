package phlogenim.sprite;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;

import phlogenim.State;

import rjones.game.engine.sprite.RectangularShapeSprite;
import rjones.game.engine.sprite.Sprite;
import rjones.game.engine.util.BetterRectangle;
import rjones.game.engine.util.Vector2D;

public class PEnemyProjectileSprite extends PSprite implements Sprite, State {
	
	/**
	 * Projectile always knows who fired it (whether it was the hero or an enemy).
	 */
	private Sprite belongs_to;
	
	private RectangularShapeSprite sprite_delegate;
	
	public PEnemyProjectileSprite(double x, double y,
							 double width, double height,
							 Color c,
							 double m,
							 Sprite owner)
	{
		sprite_delegate =
		     new RectangularShapeSprite(x,
							            y,
							            width,
							            height,
							            true,
							            c,
							            (RectangularShape)new Ellipse2D.Double());
		mass = m;
		belongs_to = owner;
	}

	public void collideWith(Sprite s)
	{
		if (s instanceof PHeroSprite)
		{
			manager.wound(this.mass, this.getVelocity());
			PHeroSprite hero = (PHeroSprite)s;
			// rather random dampening coefficients
			double x = 0.1 * (this.mass / hero.getMass()) * this.getVelocity().getX();
    	    double y = 0.1 * (this.mass / hero.getMass()) * this.getVelocity().getY();
	    	hero.setVelocity(hero.getVelocity().add(new Vector2D(x, y)));
	    	manager.getSpriteManager().remove(this);
		}
	}

    public void setVelocity(Vector2D v)
    {
    	sprite_delegate.setVelocity(v);
    }
    
    public Vector2D getVelocity()
    {
    	return sprite_delegate.getVelocity();
    }
	
	public BetterRectangle getCollisionBox()
	{
		return sprite_delegate.getCollisionBox();
	}

	public void setCollisionBoxScale(double s)
	{
		sprite_delegate.setCollisionBoxScale(s);
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

}