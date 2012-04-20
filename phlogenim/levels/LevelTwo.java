package phlogenim.levels;

import java.awt.Color;
import java.util.ArrayList;

import phlogenim.Manager;
import phlogenim.sprite.PEnemyOneSprite;
import phlogenim.sprite.PEnemyTwoSprite;
import phlogenim.sprite.PExitSprite;
import phlogenim.sprite.PStringSprite;
import phlogenim.sprite.PWallSprite;
import rjones.game.engine.sprite.Sprite;
import rjones.game.engine.sprite.StringSprite;

public class LevelTwo extends Level {

	public LevelTwo(Manager m, Sprite hero, double wt) {
		super(m, hero, wt);

		double left, up, height;
		left = viewer.getDisplay().getWidth() / 4;
		height = 10.00;
		
		up = 8 * viewer.getDisplay().getHeight() / 11;
		PWallSprite block1 = new PWallSprite(
							 left,
							 up,
						   	 viewer.getDisplay().getWidth() - left - 1.5*wall_thickness,
						     height,
						     Color.blue);
		up = 6 * viewer.getDisplay().getHeight() / 11;
		PWallSprite block2 = new PWallSprite(
							 viewer.getDisplay().getX() + wall_thickness,
							 up,
						   	 viewer.getDisplay().getWidth() - left,
						     height,
						     Color.blue);
		up = 4 * viewer.getDisplay().getHeight() / 11;
		PWallSprite block3 = new PWallSprite(
							 left,
							 up,
							 viewer.getDisplay().getWidth() - left - 1.5*wall_thickness,
							 height,
							 Color.blue);
		up = 2 * viewer.getDisplay().getHeight() / 11;
		PWallSprite block4 = new PWallSprite(
							 viewer.getDisplay().getX() + wall_thickness,
							 up,
							 viewer.getDisplay().getWidth() - left,
							 height,
							 Color.blue);
		PEnemyOneSprite e1 = new PEnemyOneSprite(
							 block1.getCollisionBox().getMaxX(),
	  						 block1.getCollisionBox().getMinY(),
	  						 20.00,
	  						 hero,
	  						 true);
		e1.setManager(manager);
		PEnemyTwoSprite e2 = new PEnemyTwoSprite(
				 			 block2.getCollisionBox().getCenterX(),
							 block2.getCollisionBox().getMinY(),
							 10.00,
							 hero,
							 false);
		e2.setManager(manager);
		PEnemyTwoSprite e3 = new PEnemyTwoSprite(
				 			 block3.getCollisionBox().getMaxX(),
							 block3.getCollisionBox().getMinY(),
							 15.00,
							 hero,
							 true);
		e3.setManager(manager);

		PStringSprite exit = new PStringSprite(block4.getCollisionBox().getMinX() + 5.00,
											   block4.getCollisionBox().getMinY() - 20.00,
											   20.00,
											   20.00,
											   Color.red);
		exit.setText("EXIT");
		exit.setManager(manager);

/*		PExitSprite exit = new PExitSprite(
				 block4.getCollisionBox().getMinX() + 5.00,
				 block4.getCollisionBox().getMinY() - 50.00,
				 20.00,
				 50.00,
				 Color.red);
		exit.setManager(manager);
*/		
		this.level_items.add(hero);
		this.level_items.add(block1);
		this.level_items.add(block2);
		this.level_items.add(block3);
		this.level_items.add(block4);
		this.level_items.add(e1);
		this.level_items.add(e2);
		this.level_items.add(e3);
		this.level_items.add(exit);
		this.initialize();
	}

}
