/**
 * 
 */
package phlogenim.sprite;

import java.awt.Color;

import rjones.game.engine.SpriteManager;
import rjones.game.engine.sprite.Sprite;
import rjones.game.engine.sprite.StringSprite;

/**
 * @author: Damir Aracic
 *
 */
public class PTemporaryStringSprite extends StringSprite implements Sprite {

	private long creation_time;
	private long life_time;
	
	private SpriteManager sprite_manager;
	
	public PTemporaryStringSprite(double x, double y,
								 double width, double height,
								 long create, long life,
								 SpriteManager sm)
	{
		super(x, y, width, height);
		creation_time = create;
		life_time = life;
		sprite_manager = sm;
	}

	public PTemporaryStringSprite(double x, double y,
								 double width, double height,
								 long create, long life,
								 SpriteManager sm,
								 Color color)
	{
		super(x, y, width, height, color);
		creation_time = create;
		life_time = life;
		sprite_manager = sm;
	}
	
	public boolean update(long dms)
	{
		long time = System.currentTimeMillis();
		if (time - creation_time >= life_time) {
			sprite_manager.remove(this);
		}
		return true;
	}

}
