import java.awt.*;
import java.awt.event.KeyEvent;

import com.brackeen.javagamebook.graphics.*;
import com.brackeen.javagamebook.input.*;
import com.brackeen.javagamebook.test.GameCore;

public class SpaceInvaders extends GameCore {

    public static void main(String[] args) {
        new SpaceInvaders().run();
    }

    protected GameAction shoot;
    protected GameAction exit;
    protected GameAction moveLeft;
    protected GameAction moveRight;
    protected GameAction pause;
    protected GameAction moveUp;
    protected GameAction moveDown;
    protected InputManager inputManager;
    private Player player;
    private Sprite cursor;
    private Image bgImage;
    private boolean paused;
    
    private float playPos;
    private float helpPos;
    private float backPos;
    
    // PlayMode takes three values:
    // 0=TitleScreen, 1=HowToScreen, 2=PlayGame
    private int PlayMode;

    public void init() {
        super.init();
        PlayMode = 0; // title screen by default
        Window window = screen.getFullScreenWindow();
        inputManager = new InputManager(window);
        
        helpPos = screen.getHeight()-55;
        playPos = helpPos-50;
        backPos = screen.getHeight()-65;

        createGameActions();
        createImages();
        paused = false;
    }


    /* Tests whether the game is paused or not */
    public boolean isPaused() {
        return paused;
    }


    /* Sets the paused state */
    public void setPaused(boolean p) {
        if (paused != p) {
            this.paused = p;
            inputManager.resetAllGameActions();
        }
    }


    public void update(long elapsedTime) {
        // check input that can happen whether paused or not
        checkSystemInput();
        
        if (PlayMode == 0 || PlayMode == 1) {
            cursor.update(elapsedTime);
        }

        if (PlayMode == 2) {
        	if (isPaused() != true) {
		        // check game input
		        checkGameInput();

		        // update sprite
		        player.update(elapsedTime);
		    }
        }
        else {
        	checkGameInput();
        }
    }


    /**
        Checks input from GameActions that can be pressed
        regardless of whether the game is paused or not.
    */
    public void checkSystemInput() {
        if (pause.isPressed() && PlayMode == 2) {
            setPaused(!isPaused());
        }
        if (exit.isPressed()) {
            stop();
        }
    }


    /**
        Checks input from GameActions that can be pressed
        only when the game is not paused.
    */
    public void checkGameInput() {
    	
    	if (PlayMode == 0) {
    		if (moveDown.isPressed() && cursor.getY() != helpPos) {
            	cursor.setY(helpPos);
		    } else if (moveUp.isPressed() && cursor.getY() != playPos) {
		        cursor.setY(playPos);
		    }
		    if (shoot.isPressed() == true)
		    {
		    	if (cursor.getY() == helpPos) {
		    		PlayMode = 1; // help mode
		    	} else {
		    		PlayMode = 2; // game mode
		    	}
		    	createImages();
		    }
    	} else if (PlayMode == 1) {
    		if (shoot.isPressed() == true)
		    {
		    	PlayMode = 0;
		    	createImages();
		    }
    	} else {
		    if (moveLeft.isPressed() && player.getX() > 0) {
		        player.moveLeft();
		    } else if (moveRight.isPressed() &&
		    			 player.getX() < (screen.getWidth()
		    			 					- player.getWidth())) {
		        player.moveRight();
		    } else {
		    	player.setVelocityX(0);
		    }

		    if (shoot.isPressed() == true)
		    {
		        player.shoot();
		    }
        }
    }


    public void draw(Graphics2D g) {
        // draw background
        g.drawImage(bgImage, 0, 0, null);

        // draw sprite
        if (PlayMode == 0 || PlayMode == 1) {
        	g.drawImage(cursor.getImage(),
            Math.round(cursor.getX()),
            Math.round(cursor.getY()),
            null);
        } else {
        	player.draw(g);
        }
    }


    /**
        Creates GameActions and maps them to keys.
    */
    public void createGameActions() {
        shoot = new GameAction("shoot",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        exit = new GameAction("exit",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        moveLeft = new GameAction("moveLeft");
        moveRight = new GameAction("moveRight");
        moveUp   = new GameAction("moveUp");
        moveDown = new GameAction("moveDown");
        pause = new GameAction("pause",
            GameAction.DETECT_INITAL_PRESS_ONLY);

        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
        inputManager.mapToKey(pause, KeyEvent.VK_P);

        // shoot with spacebar or mouse button
        inputManager.mapToKey(shoot, KeyEvent.VK_SPACE);

        // move with the arrow keys...
        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);

        // ... or with A and D.
        inputManager.mapToKey(moveLeft, KeyEvent.VK_A);
        inputManager.mapToKey(moveRight, KeyEvent.VK_D);
        
        // move with the arrow keys...
        inputManager.mapToKey(moveUp, KeyEvent.VK_UP);
        inputManager.mapToKey(moveDown, KeyEvent.VK_DOWN);

        // ... or with A and D.
        inputManager.mapToKey(moveUp, KeyEvent.VK_W);
        inputManager.mapToKey(moveDown, KeyEvent.VK_S);

    }
    
   	private void loadBgImage(String filename) {
   		bgImage = loadImage(filename);
   	}
   	
   	private void createImages() {
   		cursor = null;
   		player = null;
   		if (PlayMode == 0) {
   			loadBgImage("../graphics/TitleScreen.png");
   			createCursorSprite(50, playPos);
   		} else if (PlayMode == 1) {
   			loadBgImage("../graphics/HowToPlayScreen.png");
   			createCursorSprite(20, backPos);
   		} else {
   			loadBgImage("../graphics/background.png");
   			createPlayerSprite();
    		createEnemySprites();
   		}
    	return;
   	}
   	
   	private void createCursorSprite(float X, float Y) {
   		// load image
        Image curImg1 = loadImage("../graphics/small_ship_1.png");
        Image curImg2 = loadImage("../graphics/small_ship_2.png");

        // create animation
        Animation anim = new Animation();
        anim.addFrame(curImg1, 500);
        anim.addFrame(curImg2, 500);
   		cursor = new Sprite(anim);
   		cursor.setY(Y);
   		cursor.setX(X);
    	return;
   	}
   	
   	private void createPlayerSprite() {
   		// load image
        Image shipImg = loadImage("../graphics/player.png");

        // create animation
        Animation anim = new Animation();
        anim.addFrame(shipImg, 1000);
   		player = new Player(anim, 0, screen.getHeight());
   		return;
   	}
   	
   	private void createEnemySprites() {
   		
   		return;
   	}

}
