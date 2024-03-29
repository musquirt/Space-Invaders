import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.Calendar;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;

import com.brackeen.javagamebook.graphics.*;
import com.brackeen.javagamebook.input.*;
import com.brackeen.javagamebook.test.GameCore;

public class SpaceInvaders extends GameCore implements MouseMotionListener, MouseListener {

    public static void main(String[] args) {
        new SpaceInvaders().run();
    }
    
    static final int NUM_DIGITS = 5;
    static final int NUM_STARTING_LIVES = 3;

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
    
    private int theScore;
    private int hiScore;
    private Set<Sprite> scoreDigits;
    private Set<Sprite> hiScoreDigits;
    private Sprite GameOverSprite;
    
    private int numLives = 0;
    private Set<Sprite> liveSprites;
    private int levelNum = 0;
    private Sprite levelDisp;
    
    // integer value of 0=None, 1=Reset, 2=Clear, 3=Exit
    private int pauseSelect = 0;
    
    private int mouseX;
    private int mouseY;
    
    // PlayMode takes three values:
    // 0=TitleScreen, 1=HowToScreen, 2=PlayGame,
    // 3=GamePaused, 4=Attract, 5=GameOver
    private int PlayMode;
    private int setNewPlayMode = -1;
    
    public final String hiScoreFile = "../hiScores";
	
	// Enemy declarations
	private Fleet invaders;
	private List<Bullet> missiles;
	
	// Blockades
	private Blockades blocks;
	private Block b;
	
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
        
        scoreDigits = new HashSet<Sprite>();
        hiScoreDigits = new HashSet<Sprite>();
        liveSprites = new HashSet<Sprite>();
        GameOverSprite = null;
        
        theScore = 0;
        hiScore = getHiScoreFromFile();
        
        window.addMouseMotionListener(this);
        window.addMouseListener(this);
        
        // start timing for demo mode
        time = Calendar.getInstance().getTimeInMillis();
		
		Fleet.startingspeed = .05f;
		missiles = new ArrayList<Bullet>();
    }
    
	public synchronized void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        
        if (PlayMode == 0) {
        	if (e.getX() < 3*screen.getWidth()/7 &&
        			e.getY() > 4*screen.getHeight()/5) {
				if (cursor.getY() != playPos &&
							e.getY() < (helpPos+playPos)/2) {
					cursor.setY(playPos);
				} else if (cursor.getY() != helpPos &&
								e.getY() > (helpPos + playPos)/2) {
					cursor.setY(helpPos);
				}
			}
			time = Calendar.getInstance().getTimeInMillis();
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
		} else if (PlayMode == 4) {
			setNewPlayMode = 0;
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
            if (PlayMode == 0) {
            	if (Calendar.getInstance().getTimeInMillis() - time >= 5000) {
            		// go to demo mode
            		setNewPlayMode = 4;
            		time = Calendar.getInstance().getTimeInMillis();
            	}
            }
        }

        if (PlayMode == 2) {
        	if (isPaused() != true) {
		        // check game input
		        checkGameInput();

		        // update sprite
		        player.update(elapsedTime);
		        invaders.update(elapsedTime);
				invaders.shoot(missiles);
				blocks.update(elapsedTime);
				
				int i = 0;
				while (i < missiles.size()) {
					missiles.get(i).update(elapsedTime);
					if (missiles.get(i).getLive() == false) {
						missiles.remove(i);
					}
					else {
						i++;
					}
				}
				
				if (invaders.checkGameOver() == true) {
					gameOver();
				}
				if (invaders.checkNextLevel() == true) {
					changeLevel();
					Fleet.scaleProbability(1.5);
					Fleet.startingspeed = Fleet.startingspeed * -1.1f;
					if (Fleet.startingspeed > 1f) {
						Fleet.startingspeed = 1;
					}
					else if (Fleet.startingspeed < -1f) {
						Fleet.startingspeed = -1;
					}
					createEnemySprites();
				}
				
				if (explosion != null) {
					explosion.update(elapsedTime);
				}
				
		        if (redOn == true) {
		        	redEnemy.update(elapsedTime);
		        	if ((redEnemy.getVelocityX() < 0 && 
		        			redEnemy.getX()+redEnemy.getWidth() <= 0) ||
		        			redEnemy.getVelocityX() > 0 &&
		        			redEnemy.getX()-redEnemy.getWidth() 
		        			>= screen.getWidth()) {
		        		redOn = false;
		        		redEnemy = null;
		        	} 
		        } else {
		        	int n = randNum.nextInt(10000);
		        	if (n % 2000 == 0) {
		        		Image redImage = loadImage("../graphics/xl_ship.png");
		        		Animation anim = new Animation();
		        		anim.addFrame(redImage,1000);
		        		redEnemy = new Sprite(anim);
		        		redEnemy.setY(screen.getHeight()/15);
		        		switch(n) {
		        			case 0: redEnemy.setVelocityX(-.16f); break;
		        			case 2000: redEnemy.setVelocityX(.16f); break;
		        			case 4000: redEnemy.setVelocityX(-.20f); break;
		        			case 6000: redEnemy.setVelocityX(.20f); break;
		        			case 8000: redEnemy.setVelocityX(-.13f); break;
		        			case 10000: redEnemy.setVelocityX(.13f); break;
		        			default: redEnemy.setVelocityX(-.18f); break;
		        		}
		        		if (redEnemy.getVelocityX() < 0) {
		        			redEnemy.setX(screen.getWidth()+
		        							redEnemy.getWidth());
		        		} else {
		        			redEnemy.setX(0-redEnemy.getWidth());
		        		}
		        		redOn = true;
		        	}
		        }
		    }
        } else if (PlayMode == 4) {
        	checkGameInput();
        	moveDummyPlayer();
        	player.update(elapsedTime);
        	invaders.update(elapsedTime);
			invaders.shoot(missiles);
			
			int i = 0;
			while (i < missiles.size()) {
				missiles.get(i).update(elapsedTime);
				if (missiles.get(i).getLive() == false) {
					missiles.remove(i);
				}
				else {
					i++;
				}
			}
			
			if (invaders.checkGameOver() == true) {
				setNewPlayMode = 0;
			}
			if (invaders.checkNextLevel() == true) {
				changeLevel();
				Fleet.startingspeed = Fleet.startingspeed * -1.1f;
				if (Fleet.startingspeed > 1f) {
					Fleet.startingspeed = 1;
				}
				else if (Fleet.startingspeed < -1f) {
					Fleet.startingspeed = -1;
				}
				createEnemySprites();
			}
        	
        	if (redOn == true) {
		        	redEnemy.update(elapsedTime);
		        	if ((redEnemy.getVelocityX() < 0 && 
		        			redEnemy.getX()+redEnemy.getWidth() <= 0) ||
		        			redEnemy.getVelocityX() > 0 &&
		        			redEnemy.getX()-redEnemy.getWidth() 
		        			>= screen.getWidth()) {
		        		redOn = false;
		        		redEnemy = null;
		        	} 
		        } else {
		        	int n = randNum.nextInt(10000);
		        	if (n % 2000 == 0) {
		        		Image redImage = loadImage("../graphics/xl_ship.png");
		        		Animation anim = new Animation();
		        		anim.addFrame(redImage,1000);
		        		redEnemy = new Sprite(anim);
		        		redEnemy.setY(screen.getHeight()/15);
		        		switch(n) {
		        			case 0: redEnemy.setVelocityX(-.16f); break;
		        			case 2000: redEnemy.setVelocityX(.16f); break;
		        			case 4000: redEnemy.setVelocityX(-.20f); break;
		        			case 6000: redEnemy.setVelocityX(.20f); break;
		        			case 8000: redEnemy.setVelocityX(-.13f); break;
		        			case 10000: redEnemy.setVelocityX(.13f); break;
		        			default: redEnemy.setVelocityX(-.18f); break;
		        		}
		        		if (redEnemy.getVelocityX() < 0) {
		        			redEnemy.setX(screen.getWidth()+
		        							redEnemy.getWidth());
		        		} else {
		        			redEnemy.setX(0-redEnemy.getWidth());
		        		}
		        		redOn = true;
		        	}
		        }
        }
        else {
        	checkGameInput();
        }
    }
    
    void moveDummyPlayer() {
    	if (Calendar.getInstance().getTimeInMillis() - time >= 2000) {
			int controlInt = randNum.nextInt(1000);
			if (controlInt < 400) {
				if (player.getX() > 0) {
					player.moveLeft(.15f);
				} else {
					player.moveRight(.15f);
				}
			} else if (controlInt < 800) {
				if (player.getX() < screen.getWidth() - player.getWidth()) {
					player.moveRight(.15f);
				} else {
					player.moveLeft(.15f);
				}
			} else if (controlInt < 900) {
				player.setVelocityX(0);
			}
			
			player.shoot();
			
			time = Calendar.getInstance().getTimeInMillis();
		}
		
		if (player.getX() > screen.getWidth() - player.getWidth()) {
			player.moveLeft(.15f);
		} else if (player.getX() < 1) {
			player.moveRight(.15f);
		}
		return;
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
		    		setNewPlayMode = 1; // help mode
		    	} else {
		    		setNewPlayMode = 2; // game mode
		    	}
		    }
    	} else if (PlayMode == 1) {
    		if (shoot.isPressed() == true)
		    {
		    	setNewPlayMode = 0;
		    	time = Calendar.getInstance().getTimeInMillis();
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
        } else if (PlayMode == 4) {
        	if (moveLeft.isPressed() || moveRight.isPressed() ||
        				 shoot.isPressed()) {
        		setNewPlayMode = 0;
        		time = Calendar.getInstance().getTimeInMillis();
        	}
        	checkCollisions();
        } else if (PlayMode == 5) {
        	if (shoot.isPressed()) {
        		setNewPlayMode = 0;
        		time = Calendar.getInstance().getTimeInMillis();
        		theScore = 0;
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
			blocks.draw(g);
			invaders.draw(g);
			for (int i = 0; i < missiles.size(); i++) {
				missiles.get(i).draw(g);
			}
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
        	
        	Iterator<Sprite> it = scoreDigits.iterator();
        	while (it.hasNext()) {
        		Sprite s = it.next();
        		g.drawImage(s.getImage(),
        			Math.round(s.getX()),
        			Math.round(s.getY()),
        			null);
        	}
        	it = hiScoreDigits.iterator();
        	while (it.hasNext()) {
        		Sprite s = it.next();
        		g.drawImage(s.getImage(),
        			Math.round(s.getX()),
        			Math.round(s.getY()),
        			null);
        	}
        	
        	it = liveSprites.iterator();
        	while (it.hasNext()) {
        		Sprite s = it.next();
        		g.drawImage(s.getImage(),
        			Math.round(s.getX()),
        			Math.round(s.getY()),
        			null);
        	}
        	
        	g.drawImage(levelDisp.getImage(),
        			Math.round(levelDisp.getX()),
        			Math.round(levelDisp.getY()),
        			null);
        	
        	if (PlayMode == 5) {
		    	g.drawImage(GameOverSprite.getImage(),
		    		Math.round(GameOverSprite.getX()),
				    Math.round(GameOverSprite.getY()),
				    null);
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

        // ... or with W and S.
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
   			levelNum = 1;
   			setPaused(false); // if coming out of reset, unpause
   			if (PlayMode == 4) {
   				loadBgImage("../graphics/bgAttract.png");
   			} else {
   				loadBgImage("../graphics/bgImproved.png");
   			}
   			numLives = NUM_STARTING_LIVES;
   			createPlayerSprite();
    		createEnemySprites();
    		createScoreSprites();
    		createHiScoreSprites();
    		createLiveSprites();
			createBlockadeSprites();
    		changeLevel();
    		if (PlayMode != 5) {
    			theScore = 0;
    		}
   		}
    	return;
   	}
   	
	private void createBlockadeSprites() {
		blocks = new Blockades(3, screen.getHeight(), screen.getWidth(), player);
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
		invaders = new Fleet(screen.getHeight()/24, 14*screen.getHeight()/15, screen.getWidth(), player);
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
        		hiScore = 0;
        		storeHiScoreToFile();
        		createHiScoreSprites();
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
					// increase score
					if (PlayMode != 4) theScore += 200;
					destroyShipAnimation(redEnemy);
					redOn = false;
					redEnemy = null;
					player.BulletCollided();
				}
			}
    	}
		
		for (int i = 0; i < missiles.size(); i++) {
			if (player.checkCollisions(missiles.get(i).getBulletLocation()) == true) {
				playerDied();
				destroyShipAnimation(player);
				missiles.clear();
				break;
			}
		}
		
		Enemy hit = invaders.checkCollisions(player.getBulletLocation());
		if (hit != null) {
			if (PlayMode != 4) theScore += hit.getPoints();
			destroyShipAnimation(hit);
			player.BulletCollided();
		}
		
		blocks.checkCollisions(player, missiles, invaders.getEnemies());
    }
    
    public void playerDied() {
    	numLives--;
    	destroyShipAnimation(player);
    	if (numLives == 0) {
    		liveSprites.clear();
    		gameOver();
    	} else {
    		
    		createLiveSprites();
    	}
    }
    
	public void gameOver() {
		// if we're in Attract, just go back to Title Screen
		if (PlayMode == 4) {
			setNewPlayMode = 0;
			return;
		}
		// check   HI-SCORE, display appropriate message
		Image i = null;
		if (checkHiScore() == true) {
			i = loadImage("../graphics/GameOverNewScore.png");
			hiScore = theScore;
			storeHiScoreToFile();
		} else {
			i = loadImage("../graphics/GameOver.png");
		}
		Animation a = new Animation();
		a.addFrame(i,1000);
		GameOverSprite = new Sprite(a);
		GameOverSprite.setX(screen.getWidth()/2
								-GameOverSprite.getWidth()/2);
		GameOverSprite.setY(screen.getHeight()/2
								-GameOverSprite.getHeight()/2);
		PlayMode = 5; // (GAME OVER MODE)
	}
	
	public void changeLevel() {
		// change level display at bottom right of screen
		Image i = loadNumberImage(1, levelNum);
		Animation a = new Animation();
		a.addFrame(i, 1000);
		levelDisp = new Sprite(a);
		levelDisp.setX(screen.getWidth()-levelDisp.getWidth()-5);
		levelDisp.setY(screen.getHeight()-levelDisp.getHeight()-5);
		levelNum++;
	}
	
    public void createLiveSprites() {
    	liveSprites.clear();
    	int offset = 0;
    	for (int i=0; i<numLives; i++) {
    		Image img = loadImage("../graphics/player.png");
    		Animation a = new Animation();
    		a.addFrame(img, 1000);
    		Sprite s = new Sprite(a);
    		s.setX(offset);
    		s.setY(29*screen.getHeight()/30);
    		liveSprites.add(s);
    		offset += 3*s.getWidth()/2;
    	}
    }
    
    public void destroyShipAnimation(Sprite s) {
    	Animation a = new Animation();
    	Image i = loadImage("../graphics/explosion.png");
    	a.addFrame(i,400);
    	explosion = new Sprite(a);
    	explosion.setX(s.getX()+s.getVelocityX()*40);
    	explosion.setY(s.getY()+s.getVelocityY()*40);
		explosion.setVelocityX(s.getVelocityX());
		explosion.setVelocityY(s.getVelocityY());
		
    	time = Calendar.getInstance().getTimeInMillis();
    	
    	// reevaluate score if necessary
    	if (s != player) {
    		createScoreSprites();
    		if (checkHiScore() == true) {
    			createHiScoreSprites();
    			bgImage = loadImage("../graphics/bgImprovedHiScore.png");
    		}
    	}
    }
    
    public int getHiScoreFromFile() {
    	try {
    		FileReader fr = new FileReader(hiScoreFile);
    		char[] buf = new char[NUM_DIGITS];
    		fr.read(buf);
    		fr.close();
    		int n = 1;
    		for (int i=0; i<NUM_DIGITS-1; i++) {
    			n = n * 10;
    		}
    		int high = 0;
    		for (int i=0; i<NUM_DIGITS; i++) {
    			if (Character.isDigit(buf[i])) {
    				high += Character.digit(buf[i],10)*n;
    			}
    			n = n/10;
    		}
    		return high;
    	} catch(Exception e) {
    		// no hi-score file found
    		return 0;
    	}
    }
    
    public void storeHiScoreToFile() {
    	try {
    		FileWriter fw = new FileWriter(hiScoreFile);
    		String ts = Integer.toString(hiScore);
    		char[] buf = ts.toCharArray();
    		for (int i=0; i<NUM_DIGITS-ts.length(); i++) {
    			fw.write("0");
    		}
    		fw.write(buf);
    		fw.close();
    		return;
    	} catch(Exception e) {
    		// file could not be opened
    		return;
    	}
    }
    
    public void createScoreSprites() {
    	scoreDigits.clear();
    	int offset = screen.getWidth()/8;
    	for (int j=NUM_DIGITS; j>0; j--) {
    		Image i = loadNumberImage(j, theScore);
    		Animation a = new Animation();
    		a.addFrame(i,1000);
    		Sprite s = new Sprite(a);
    		s.setX(offset);
    		s.setY(screen.getHeight()/100);
    		offset += 3*s.getWidth()/2;
    		scoreDigits.add(s);
    	}
    }
    
    public void createHiScoreSprites() {
    	int writeScore = 0;
    	if (checkHiScore() == false) {
    		writeScore = hiScore;
    	} else {
    		writeScore = theScore;
    	}
    	
    	hiScoreDigits.clear();
    	int offset = 70*screen.getWidth()/82;
    	for (int j=NUM_DIGITS; j>0; j--) {
    		Image i = loadNumberImage(j, writeScore);
    		Animation a = new Animation();
    		a.addFrame(i,1000);
    		Sprite s = new Sprite(a);
    		s.setX(offset);
    		s.setY(screen.getHeight()/100);
    		offset += 3*s.getWidth()/2;
    		hiScoreDigits.add(s);
    	}

    }
    
    public Image loadNumberImage(int pos, int theNum) {
    	Image i = null;
    	for (int j=pos-1; j>0; j--) {
    		theNum = theNum / 10;
    	}
    	theNum = theNum % 10;
    	switch (theNum) {
    		case 0: i = loadImage("../graphics/num_0.png"); break;
    		case 1: i = loadImage("../graphics/num_1.png"); break;
    		case 2: i = loadImage("../graphics/num_2.png"); break;
    		case 3: i = loadImage("../graphics/num_3.png"); break;
    		case 4: i = loadImage("../graphics/num_4.png"); break;
    		case 5: i = loadImage("../graphics/num_5.png"); break;
    		case 6: i = loadImage("../graphics/num_6.png"); break;
    		case 7: i = loadImage("../graphics/num_7.png"); break;
    		case 8: i = loadImage("../graphics/num_8.png"); break;
    		case 9: i = loadImage("../graphics/num_9.png"); break;
    		default: break;
    	}
    	return i;
    }
    
    public boolean checkHiScore() {
    	if (theScore > hiScore) {
    		return true;
    	} else {
    		return false;
    	}
    }

}
