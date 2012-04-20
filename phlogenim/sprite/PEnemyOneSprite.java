/**
 * 
 */
package phlogenim.sprite;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.RectangularShape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import rjones.game.engine.graphics.ImageLoader;
import rjones.game.engine.sound.SoundClip;
import rjones.game.engine.sprite.RectangularShapeSprite;
import rjones.game.engine.sprite.Sprite;
import rjones.game.engine.util.BetterRectangle;
import rjones.game.engine.util.Vector2D;

/**
 * @author: Damir Aracic
 *
 */

public class PEnemyOneSprite extends PSprite implements Sprite {
	
	/**
	 * Enemy can take a number of shots before dying.
	 */
	private int lives;
	
	private long timer;
	
	/**
     * Two arrays of images store the animations of the hero sprite.
     * Current image to be displayed is pointed to by the index variable.
     */
    private int index;
   
    private BufferedImage[] enemy_images;

    private static BufferedImage[] enemy_images_right
		= {new ImageLoader().loadImage("/phlogenim/images/v2R2.gif"),
		   new ImageLoader().loadImage("/phlogenim/images/v2R1.gif")};

    private static BufferedImage[] enemy_images_left
	 	= {new ImageLoader().loadImage("/phlogenim/images/v2L2.gif"),
		   new ImageLoader().loadImage("/phlogenim/images/v2L1.gif")};

    private static SoundClip shot_sound = new SoundClip("/phlogenim/sounds/snap-keys.wav");
    /**
     * Two variable to control frequency of hero sprite update.
     * Without limiting the speed of updating, hero sprite animation would look
     * unbelievable and quite comic.
     */   
    private long update_last;
   
    private static long animation_delay = 200;
	
    /**
     * Enemy sprites always targets the user sprite, thus referenced.
     */
	private Sprite target;
	
	private Vector2D shot_direction;
	
	private RectangularShapeSprite sprite_delegate;
	
	public PEnemyOneSprite(double x,
						   double y,
				  		   double m,
				  		   Sprite hero,
				  		   boolean facing)
	{
		sprite_delegate =
	   		 new RectangularShapeSprite(x - enemy_images_left[0].getWidth(),
	   									y - enemy_images_left[0].getHeight(),
							            (double)enemy_images_left[0].getWidth(),
							            (double)enemy_images_left[0].getHeight(),
							            true,
							            Color.white,
							            (RectangularShape)new Rectangle2D.Double());
		mass = m;
		lives = 10;
		target = hero;
		update_last = System.currentTimeMillis();
		if (facing) {
			enemy_images = enemy_images_left;
		} else {
			enemy_images = enemy_images_right;
		}
		index = 0;
	}
	
	public void setTarget(Sprite s)
	{
		target = s;
	}
	
	public void shoot(double mass)
	{
		PEnemyProjectileSprite pps =
		    new PEnemyProjectileSprite(
		        this.getCollisionBox().getCenterX(),
		        this.getCollisionBox().getCenterY(),
		        15.0, 10.0,
		        Color.red,
		        mass,
		        this);
		shot_sound.play();
		pps.setManager(manager);
		pps.setCollisionSound(collision_sound);
		Vector2D src = new Vector2D(sprite_delegate.getCollisionBox().getCenterX(),
			  		   				sprite_delegate.getCollisionBox().getCenterY());
		Vector2D dest = new Vector2D(target.getCollisionBox().getCenterX(),
									 target.getCollisionBox().getCenterY());
		dest.subtract(src);
	    pps.setVelocity(new Vector2D(dest));
	    manager.getSpriteManager().add(pps);
	    index = 1;
	    update_last = System.currentTimeMillis();
	}
	
	public Vector2D getVelocity()
	{
		return sprite_delegate.getVelocity();
	}
	
	public void setVelocity(Vector2D v)
	{
		sprite_delegate.setVelocity(v);
	}
	
	public void collideWith(Sprite s)
	{
		if (s instanceof PHeroProjectileSprite)
		{
			lives--;
			if (lives == 0) {
				manager.getSpriteManager().remove(this);
			}
			PHeroProjectileSprite hps = (PHeroProjectileSprite)s;
			double x = 0.1 * (hps.getMass() / this.mass) * this.getVelocity().getX();
    	    double y = 0.1 * (hps.getMass() / this.mass) * this.getVelocity().getY();
	    	this.setVelocity(this.getVelocity().add(new Vector2D(x, y)));
		}
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
		   BetterRectangle br = getCollisionBox();
		   g.drawImage(enemy_images[index], new AffineTransform(
					   						1.0, 0.0,
					   						0.0, 1.0,
					   						sprite_delegate.getCollisionBox().getMinX(),
					   						sprite_delegate.getCollisionBox().getMinY()),
					   						null);
		   if (System.currentTimeMillis() - update_last >= animation_delay) {
			   index = 0;
		   }
	}

	public boolean update(long d_ms)
	{
		timer += d_ms;
		if (timer % 147 == 0)
		{
//			System.err.println("Timer: " + timer);
			shoot(15.0);
		}

		// update shot direction
		if (target.getCollisionBox().getCenterX() - this.getCollisionBox().getX() > 0.0) {
			enemy_images = enemy_images_right;
		} else {
			enemy_images = enemy_images_left;
		}
			
		return false;
	}

}