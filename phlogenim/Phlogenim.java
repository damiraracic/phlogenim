package phlogenim;

/*
 * Created on Aug 7, 2003
 */
 
/**
 * This is the main class of the phlogenim game.  In the interests of composability, it doesn't do much.
 * It merely sets the size of the game screen, creates a phlogenim manager, and tells the phlogenim manager
 * to go about its business.  The idea is that this is just a shell of a main program, allowing us
 * to run the game as a Java application.  If you want to embed the Phlogenim code in some other type of
 * program, like an Applet, it shouldn't be difficult to do.
 * 
 * Following the model-view-controller paradigm, there are three main classes that provide the
 * structure for the Phlogenim game.  
 * 
 * The PongManager class is the "model", keeping track of internal game information, and providing
 * communication pathways for all the other components.
 * 
 * The PongViewer class is the "view", providing a panel on which to display all the elements of the game.
 * 
 * The PongInterface class is the "controller", providing the interactive user interface controls.
 * For this simple game, that consists exclusively of handling mouse clicks and movement.
 * 
 * @author Randolph M. Jones
 * @see Manager
 * @see Viewer
 * @see Interface
 */
public class Phlogenim
{
   /**
    * Height of the playable area of the phlogenim game, in pixels.
    */
   private static final int height = 500;

   /**
    * Width of the playable area of the phlogenim game, in pixels.
    */
   private static final int width = 500;

   /**
    * Simple application that creates an instance of the PongManager and let's it go to work.
    * The PongManager class does most of the work of setting up and running the game.
    * 
    * @param args	Normal system arguments.  These are ignored by this program.
    * @see Manager
    */
   public static void main(String[] args)
   {
      Manager pm;
      PhlogenimProperties p;
      
      System.out.println( "Loading Configuration...");
      p = PhlogenimProperties.getProperties();
      System.out.println("Creating Phlogenim Manager...");
      pm = new Manager(width, height);
      System.out.println("Starting game...");
      //pm.startGame();
      pm.singleThreadStart();
   }
}
