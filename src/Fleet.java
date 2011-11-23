import com.brackeen.javagamebook.graphics.*;
import java.awt.*;
import javax.swing.ImageIcon;
import java.util.List;
import java.util.ArrayList;

public class Fleet {
	
	private List<Enemy> ships = new ArrayList<Enemy>();
    private int    		screenMaxY;
    private int     	screenMinY;
	private int 		numships;		
	private float		speed;
	private int 		columns;
	private int 		rows;
	private int 		vertspace;
	private int 		horizspace;
	private int			yoffset;
	private int			xoffset;
	
	public Fleet(int screenMin, int screenMax) {
		columns = 11;
		rows = 5;
		vertspace = 30;
		horizspace = 40;
		yoffset = 100;
		xoffset = 100;
		float startingspeed = .05f;
		
		numships = columns*rows;
		speed = startingspeed;
		
		Image small1 = new ImageIcon("../graphics/small_ship_1.png").getImage();
		Image small2 = new ImageIcon("../graphics/small_ship_2.png").getImage();
		Image med1 = new ImageIcon("../graphics/med_ship_1.png").getImage();
		Image med2 = new ImageIcon("../graphics/med_ship_2.png").getImage();
		Image big1 = new ImageIcon("../graphics/big_ship_1.png").getImage();
		Image big2 = new ImageIcon("../graphics/big_ship_2.png").getImage();
			
		screenMinY = screenMin;
        screenMaxY = screenMax;
		
		int smalloffset = 0; // Small ships need to be offset to stay centered with rest of fleet
		
		Enemy ship;
		for(int r = 0; r < rows; r++) {
			
			for(int c = 0; c < columns; c++) {
				
				// 1 row of small ships
				if (r == 0) {
					Animation small = new Animation();
					small.addFrame(small1, 500);
					small.addFrame(small2, 500);
					ship = new Enemy(small, screenMin, screenMax);
					smalloffset = 3;
				}
				// 2 rows of medium ships
				else if ((r == 1) || (r == 2)) {
					Animation med = new Animation();
					med.addFrame(med1, 500);
					med.addFrame(med2, 500);
					ship = new Enemy(med, screenMin, screenMax);
					smalloffset = 0;
				}
				// The rest are big ships
				else {
					Animation big = new Animation();
					big.addFrame(big1, 500);
					big.addFrame(big2, 500);
					ship = new Enemy(big, screenMin, screenMax);
					smalloffset = 0;
				}
				
				ship.setX(c*horizspace+xoffset+smalloffset);
				ship.setY(r*vertspace+yoffset);
				ships.add(ship);
			}
		}
	}

	public void draw(Graphics2D g) {
    	for (int i = 0; i < numships; i++) {
			if (ships.get(i) != null) {
				g.drawImage(ships.get(i).getImage(),
				Math.round(ships.get(i).getX()),
				Math.round(ships.get(i).getY()),
				null);
			}
		}
    	//if (theBullet != null) {
    	//	theBullet.draw(g);
        //}
    }
	
	public void update(long elapsedTime) {
		for (int i = 0; i < numships; i++) {
			if (ships.get(i) != null) {
				ships.get(i).setVelocityX(speed);
				ships.get(i).update(elapsedTime);
			}
		}
    }
	
}