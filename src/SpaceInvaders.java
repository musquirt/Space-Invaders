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
    protected InputManager inputManager;
    private Player player;
    private Image bgImage;
    private boolean paused;

    public void init() {
        super.init();
        Window window = screen.getFullScreenWindow();
        inputManager = new InputManager(window);

        createGameActions();
        loadBgImage();
        createSprites();
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

        if (isPaused() != true) {
            // check game input
            checkGameInput();

            // update sprite
            player.update(elapsedTime);
        }
    }


    /**
        Checks input from GameActions that can be pressed
        regardless of whether the game is paused or not.
    */
    public void checkSystemInput() {
        if (pause.isPressed()) {
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
        if (moveLeft.isPressed() && player.getX() > 0) {
            player.moveLeft();
        } else if (moveRight.isPressed() &&
        			 player.getX() < (screen.getWidth() - player.getWidth())) {
            player.moveRight();
        } else {
        	player.setVelocityX(0);
        }

        if (shoot.isPressed() == true)
        {
            player.shoot();
        }
    }


    public void draw(Graphics2D g) {
        // draw background
        g.drawImage(bgImage, 0, 0, null);

        // draw sprite
        player.draw(g);
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

    }
    
   	private void loadBgImage() {
   		bgImage = loadImage("../graphics/background.png");
   	}
   	
   	private void createSprites() {
    	createPlayerSprite();
    	createEnemySprites();
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
