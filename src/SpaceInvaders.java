import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.Calendar;

import com.brackeen.javagamebook.graphics.*;
import com.brackeen.javagamebook.input.*;
import com.brackeen.javagamebook.test.GameCore;

public class SpaceInvaders extends GameCore implements MouseMotionListener, MouseListener {

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
    private Sprite pauseSprite;
    private Image bgImage;
    private boolean paused;
    
    private float playPos;
    private float helpPos;
    private float backPos;
    private float resetPos;
    private float clearPos;
    private float exitPos;
    
    private boolean redOn;
    private Sprite redEnemy;
    private Random randNum;
    private Sprite explosion;
    private long time;
    private long d_time;
    
    // integer value of 0=None, 1=Reset, 2=Clear, 3=Exit
    private int pauseSelect = 0;
    
    private int mouseX;
    private int mouseY;
    
    // PlayMode takes three values:
    // 0=TitleScreen, 1=HowToScreen, 2=PlayGame, 3=GamePaused
    private int PlayMode;
    private int setNewPlayMode = -1;

    public void init() {
        super.init();
        PlayMode = 0; // title screen by default
        Window window = screen.getFullScreenWindow();
        inputManager = new InputManager(window);
        
        helpPos = 73*screen.getHeight()/80;
        playPos = 33*screen.getHeight()/40;
        backPos = 223*screen.getHeight()/250;
        
        resetPos = 9 * screen.getHeight() / 20;
        clearPos = 10 * screen.getHeight() / 20;
        exitPos  = 11 * screen.getHeight() / 20;

        createGameActions();
        createImages();
        paused = false;
        redOn  = false;
        randNum = new Random();
        
        window.addMouseMotionListener(this);
        window.addMouseListener(this);
    }
    
	public synchronized void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        
        if (PlayMode == 0 && e.getX() < 3*screen.getWidth()/7 &&
        			e.getY() > 4*screen.getHeight()/5) {
		    if (cursor.getY() != playPos &&
		    			e.getY() < (helpPos+playPos)/2) {
		    	cursor.setY(playPos);
		    } else if (cursor.getY() != helpPos &&
		    				e.getY() > (helpPos + playPos)/2) {
		    	cursor.setY(helpPos);
		    }
		} else if (PlayMode == 2) {
			player.setX(e.getX()-player.getWidth()/2);
		} else if (PlayMode == 3) {
			if (e.getY() > (pauseSprite.getY()+30) &&
				e.getY() < (pauseSprite.getY()+pauseSprite.getHeight()) &&
				e.getX() < (pauseSprite.getX()+pauseSprite.getWidth()) &&
				e.getX() > (pauseSprite.getX())) {
				// change pauseSprite image
			
				if (e.getY() < exitPos) {
					if (e.getY() < clearPos) {
						Image   i =
						loadImage("../graphics/pauseScreenResetHighlight.png");
						Animation anim = new Animation();
						anim.addFrame(i,1000);
						Sprite ts = new Sprite(anim);
						ts.setX(pauseSprite.getX());
						ts.setY(pauseSprite.getY());
						pauseSprite = ts;
						pauseSelect = 1;
					} else {
						Image   i =
						loadImage("../graphics/pauseScreenClearHighlight.png");
						Animation anim = new Animation();
						anim.addFrame(i,1000);
						Sprite ts = new Sprite(anim);
						ts.setX(pauseSprite.getX());
						ts.setY(pauseSprite.getY());
						pauseSprite = ts;
						pauseSelect = 2;
					}
				} else {
					Image   i =
						loadImage("../graphics/pauseScreenExitHighlight.png");
					Animation anim = new Animation();
					anim.addFrame(i,1000);
					Sprite ts = new Sprite(anim);
					ts.setX(pauseSprite.getX());
					ts.setY(pauseSprite.getY());
					pauseSprite = ts;
					pauseSelect = 3;
				}
			} else {
				Image   i =
					loadImage("../graphics/pauseScreen.png");
				Animation anim = new Animation();
				anim.addFrame(i,1000);
				Sprite ts = new Sprite(anim);
				ts.setX(pauseSprite.getX());
				ts.setY(pauseSprite.getY());
				pauseSprite = ts;
			}
		}
    }

    /* Tests whether the game is paused or not */
    public boolean isPaused() {
        return paused;
    }


    /* Sets the paused state */
    public void setPaused(boolean p) {
        if (paused != p) {
        	if (p == true) {
        		// load sprite for pause menu
        		Image pauseImage = loadImage("../graphics/pauseScreen.png");
        		Animation anim = new Animation();
        		anim.addFrame(pauseImage, 1000);
        		pauseSprite = new Sprite(anim);
				pauseSprite.setX(screen.getWidth()/2
									-pauseSprite.getWidth()/2);
				pauseSprite.setY(screen.getHeight()/2
									-pauseSprite.getHeight()/2);
				PlayMode = 3;
        	} else {
        		pauseSprite = null;
        		PlayMode = 2;
        	}
            this.paused = p;
            inputManager.resetAllGameActions();
        }
    }

    public void update(long elapsedTime) {
    	if (setNewPlayMode != -1) {
    		PlayMode = setNewPlayMode;
    		createImages();
    		setNewPlayMode = -1;
    	}
    	
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
		        
		        if (redOn == true) {
		        	redEnemy.update(elapsedTime);
		        	if (redEnemy.getX()+redEnemy.getWidth() <= 0) {
		        		redOn = false;
		        		redEnemy = null;
		        	}
		        } else {
		        	if (randNum.nextInt(10000) % 20 == 0) {
		        		Image redImage = loadImage("../graphics/xl_ship.png");
		        		Animation anim = new Animation();
		        		anim.addFrame(redImage,1000);
		        		redEnemy = new Sprite(anim);
		        		redEnemy.setY(screen.getHeight()/15);
		        		redEnemy.setX(screen.getWidth()+redEnemy.getWidth());
		        		redEnemy.setVelocityX(-.20f);
		        		redOn = true;
		        	}
		        }
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
        if (pause.isPressed() && (PlayMode == 2 || PlayMode == 3)) {
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
    	} else if (PlayMode == 2) {
		    if (moveLeft.isPressed() &&
		    				player.getX() > 0) {
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
		    
		    checkCollisions();
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
        	if (pauseSprite != null) {
        		g.drawImage(pauseSprite.getImage(),
				    Math.round(pauseSprite.getX()),
				    Math.round(pauseSprite.getY()),
				    null);
        	}
        	if (redEnemy != null) {
        		g.drawImage(redEnemy.getImage(),
				    Math.round(redEnemy.getX()),
				    Math.round(redEnemy.getY()),
				    null);
        	}
        	if (explosion != null) {
        		g.drawImage(explosion.getImage(),
        			Math.round(explosion.getX()),
				    Math.round(explosion.getY()),
				    null);
				d_time = Calendar.getInstance().getTimeInMillis();
				if (d_time - time >= 250) {
					explosion = null;
				}
        	}
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
   			setPaused(false); // if coming out of reset, unpause
   			loadBgImage("../graphics/bgImproved.png");
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
   		player = new Player(anim, screen.getHeight()/24, 14*screen.getHeight()/15);
   		return;
   	}
   	
   	private void createEnemySprites() {
   		
   		return;
   	}
   	
   	// from the MouseMotionListener interface
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }
    
    // from the MouseListener interface
    public void mousePressed(MouseEvent e) {
    }


    // from the MouseListener interface
    public void mouseReleased(MouseEvent e) {
        // do nothing
    }


    // from the MouseListener interface
    public void mouseClicked(MouseEvent e) {
        if (PlayMode == 2) {
        	player.shoot();
        } else if (PlayMode == 1) {
        	setNewPlayMode = 0;
        } else if (PlayMode == 0) {
        	if (cursor.getY() == helpPos) {
        		// go to help
        		setNewPlayMode = 1;
        	} else {
        		// go to game
        		setNewPlayMode = 2;
        	}
        } else if (PlayMode == 3) {
        	// select option
        	if (pauseSelect == 1) {
        		setNewPlayMode = 2;
        	} else if (pauseSelect == 2) {
        		// call some subroutine to clear the high score
        	} else if (pauseSelect == 3) {
        		stop();
        	}
        	pauseSelect = 0;
        }
    }


    // from the MouseListener interface
    public void mouseEntered(MouseEvent e) {
    }


    // from the MouseListener interface
    public void mouseExited(MouseEvent e) {
    }
    
    public void checkCollisions() {
    	Point p = player.getBulletLocation();
    	if (p != null) {
			if (redEnemy != null) {
				if (p.x <= redEnemy.getX()+redEnemy.getWidth() && 
						p.x >= redEnemy.getX() &&
						p.y <= redEnemy.getY() &&
						p.y >= redEnemy.getY()-redEnemy.getHeight()) {
					System.out.println("red hit!");
					destroyShipAnimation(redEnemy);
					redOn = false;
					redEnemy = null;
					player.BulletCollided();
					// increase score
				}
			}
    	}
    }
    
    public void destroyShipAnimation(Sprite s) {
    	Animation a = new Animation();
    	Image i = loadImage("../graphics/explosion.png");
    	a.addFrame(i,400);
    	explosion = new Sprite(a);
    	explosion.setX(s.getX()+Math.abs(s.getVelocityX())*40);
    	explosion.setY(s.getY());
    	time = Calendar.getInstance().getTimeInMillis();
    }

}
