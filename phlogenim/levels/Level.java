/**
 * 
 */
package phlogenim.levels;

import java.awt.Color;
import java.util.ArrayList;

import phlogenim.Manager;
import phlogenim.Viewer;
import phlogenim.items.Item;
import phlogenim.sprite.PTemporaryStringSprite;


import rjones.game.engine.SpriteManager;
import rjones.game.engine.sprite.Sprite;

/**
 * @author: Damir Aracic
 *
 */
public abstract class Level {
	
	protected Sprite hero;
	
	protected double wall_thickness;

	/**
	 * All level-specific sprites are contained in here.
	 */
	protected ArrayList level_items;
	
	/**
	 * Presumably, when item count reaches zero, another level needs to be loaded.
	 */
	protected int item_count;

	/**
	 * Level-specific sprites have to be registered with the sprite manager.
	 */
	protected Viewer viewer;
	
	protected SpriteManager sprite_manager;
	
	protected Manager manager;
	
	/**
	 * Generic constructor;
	 * 
	 * @param sm: (see above)
	 */
	public Level(Manager m)
	{
		this.manager = m;
		this.sprite_manager = m.getSpriteManager();
		this.viewer = m.getViewer();
		this.level_items = new ArrayList();
	}
	
	public Level(Manager m, Sprite hero)
	{
		this(m);
		this.hero = hero;
	}
	
	public Level(Manager m, Sprite hero, double wt)
	{
		this(m, hero);
		this.wall_thickness = wt;
	}
	
	public void initialize()
	{
		item_count = level_items.size();
		
		for (int i = 0; i < level_items.size(); i++) {
			if (level_items.get(i) instanceof Item) {
				((Item)level_items.get(i)).registerItem();
			}
			else if (level_items.get(i) instanceof Sprite) {
				sprite_manager.add((Sprite)level_items.get(i));
			}
		}
	}
	
	/**
	 * Add a sprite to the list of level-specific sprites.
	 */
	public void addLevelItem(Item item)
	{
		level_items.add(item);
		item_count++;
	}
	
	/**
	 * Remove a sprite from the list of level-specific sprites.
	 */
	public void removeLevelItem(Item item)
	{
		level_items.remove(item);
		item_count--;
/*		if (item_count == 0) {
			manager.loadLevel();
			
			PTemporaryStringSprite bonus_display = new PTemporaryStringSprite(
	  									400,
										400,
										0,
										0,
										System.currentTimeMillis(),
										1000,
										manager.getSpriteManager(),
										Color.red);
			bonus_display.setText("N E W   L E V E L !!!");
			
			manager.getSpriteManager().add(bonus_display);
			manager.addHealth();
		}
*/	}

}