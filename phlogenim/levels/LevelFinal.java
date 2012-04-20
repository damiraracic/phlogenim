package phlogenim.levels;

import java.awt.Color;
import java.util.ArrayList;

import phlogenim.Manager;
import phlogenim.sprite.PEnemyBossSprite;
import phlogenim.sprite.PEnemyOneSprite;
import phlogenim.sprite.PEnemyTwoSprite;
import phlogenim.sprite.PExitSprite;
import phlogenim.sprite.PWallSprite;
import rjones.game.engine.sprite.Sprite;
import rjones.game.engine.util.Vector2D;

public class LevelFinal extends Level {

	public LevelFinal(Manager m, Sprite hero, double wt) {
		super(m, hero, wt);

		double left, up, height;
		left = viewer.getDisplay().getWidth() / 5;
		height = 10.00;
		
		up = 6 * viewer.getDisplay().getHeight() / 11;
		PWallSprite block1 = new PWallSprite(
							 left,
							 up,
							 100,
							 height,
							 Color.blue);
		up = 8 * viewer.getDisplay().getHeight() / 11;
		PWallSprite block2 = new PWallSprite(
							 3 * left,
							 up,
							 100,
							 height,
							 Color.blue);
		up = 7 * viewer.getDisplay().getHeight() / 11;
		PWallSprite block3 = new PWallSprite(
							 2 * left,
							 up,
							 100,
							 height,
							 Color.blue);
		
		PEnemyBossSprite boss = new PEnemyBossSprite(
							 block1.getCollisionBox().getMaxX(),
	  						 block1.getCollisionBox().getMinY(),
	  						 20.00,
	  						 hero,
	  						 true);
		boss.setManager(manager);
		
		this.level_items.add(hero);
		this.level_items.add(block1);
		this.level_items.add(block2);		
		this.level_items.add(block3);
		this.level_items.add(boss);
		this.initialize();
	}

}
