
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import game2D.*;
import game2D.entities.Enemy.Slime;
import game2D.entities.Player.Player;
import game2D.entities.Items.*;

// Game demonstrates how we can override the GameCore class
// to create our own 'game'. We usually need to implement at
// least 'draw' and 'update' (not including any local event handling)
// to begin the process. You should also add code to the 'init'
// method that will initialise event handlers etc. By default GameCore
// will handle the 'Escape' key to quit the game but you should
// override this with your own event handler.

/**
 * @author David Cairns
 *
 */
@SuppressWarnings("serial")

public class Game extends GameCore 
{
	// Useful game constants
    static int screenWidth = 512;
    static int screenHeight = 384;
    int xo = 0;
    int yo = 16;
    float gravity = 0.001f;
    int playerHealth;
    
    // Game state flags
    boolean paused;
    boolean moveLeft, moveRight, still;
    boolean slimeMoveLeft, slimeMoveRight, slimeStill;
    boolean dead;

    // Game Resources
    // Player Animations
    Animation standingAnim, runningAnim, jumpingAnim, fallingAnim, deadAnim;
    // Slime Animations
    Animation moveAnim;
    // Item Animations
    Animation chestAnim, coinAnim, flagAnim, keyAnim, runeAnim;
    // Background Animations
    Animation background1, background2, background3, background4, background5;
    // Lives Animations
    Animation heart1Anim, heart2Anim, heart3Anim;

    // Game Entities
    Player player;
    Slime slime;
    Coin coin;
    Flag flag;
    Heart heart1, heart2, heart3;
    Sprite bg1, bg2, bg3, bg4, bg5; // backgrounds

    ArrayList<Sprite> backgrounds = new ArrayList<Sprite>();
    ArrayList<Slime> slimes = new ArrayList<Slime>();
    ArrayList<Coin> coins = new ArrayList<Coin>();
    ArrayList<Heart> hearts = new ArrayList<Heart>();

    TileMap tmap = new TileMap(); // Our tile map, note that we load it in init()
    
    long score; // The score will be the total time elapsed since a crash
    long totalCoinsCollected; // The score will be the total time elapsed since a crash

    /**
	 * The obligatory main method that creates
     * an instance of our class and starts it running
     * 
     * @param args	The list of parameters this program might use (ignored)
     */
    public static void main(String[] args) {
        Game gct = new Game();
        gct.init();
        // Start in windowed mode with the given screen height and width
        gct.run(false,screenWidth,screenHeight);
    }

    /**
     * Initialise the class, e.g. set up variables, load images,
     * create animations, register event handlers
     */
    public void init() {
        paused = false;
        moveRight = false;
        moveLeft = false;
        still = true;
        slimeMoveRight = false;
        slimeMoveLeft = true;
        slimeStill = true;
        dead = false;
        playerHealth = 3;

        // Load the tile map and print it out so we can check it is valid
        tmap.loadMap("maps", "map.txt");
        
        setSize(tmap.getPixelWidth() / 6, (tmap.getPixelHeight() + 16));
        setVisible(true);

        // Player Animations
        {
            standingAnim = new Animation();
            standingAnim.loadAnimationFromSheet("images/character/stand.png", 5, 1, 90);

            runningAnim = new Animation();
            runningAnim.loadAnimationFromSheet("images/character/run.png", 8, 1, 90);

            jumpingAnim = new Animation();
            jumpingAnim.loadAnimationFromSheet("images/character/jump.png", 3, 1, 90);

            fallingAnim = new Animation();
            fallingAnim.loadAnimationFromSheet("images/character/land.png", 2, 1, 90);

            deadAnim = new Animation();
            deadAnim.loadAnimationFromSheet("images/character/dead.png", 7, 1, 90);
        }

        // Slime Animations
        {
            moveAnim = new Animation();
            moveAnim.loadAnimationFromSheet("images/enemies/slime.png", 3, 1, 180);
        }

        // Item Animations
        {
            chestAnim = new Animation();
            chestAnim.loadAnimationFromSheet("images/items/Chest.png", 4, 1, 90);
            chestAnim.pauseAt(0);

            coinAnim = new Animation();
            coinAnim.loadAnimationFromSheet("images/items/Coin.png", 4, 1, 125);

            flagAnim = new Animation();
            flagAnim.loadAnimationFromSheet("images/items/Flag.png", 4, 1, 125);

            keyAnim = new Animation();
            keyAnim.loadAnimationFromSheet("images/items/Key.png", 4, 1, 125);

            runeAnim = new Animation();
            runeAnim.loadAnimationFromSheet("images/items/Rune.png", 4, 1, 125);

            heart1Anim = new Animation();
            heart1Anim.loadAnimationFromSheet("images/items/heart.png", 4, 1, 250);

            heart2Anim = new Animation();
            heart2Anim.loadAnimationFromSheet("images/items/heart.png", 4, 1, 250);

            heart3Anim = new Animation();
            heart3Anim.loadAnimationFromSheet("images/items/heart.png", 4, 1, 250);
        }

        // Background Animations
        {
            background1 = new Animation();
            background1.loadAnimationFromSheet("images/background/1.png", 1, 1, 0);

            background2 = new Animation();
            background2.loadAnimationFromSheet("images/background/2.png", 1, 1, 0);

            background3 = new Animation();
            background3.loadAnimationFromSheet("images/background/3.png", 1, 1, 0);

            background4 = new Animation();
            background4.loadAnimationFromSheet("images/background/4.png", 1, 1, 0);

            background5 = new Animation();
            background5.loadAnimationFromSheet("images/background/5.png", 1, 1, 0);
        }

        // Initialise Game Entities
        {
            for (int i = 0; i <= tmap.getMapWidth(); i++) {
                for (int j = 0; j <= tmap.getMapHeight(); j++) {
                    int tileX, tileY;
                    char tl = tmap.getTileChar(i, j);

                    switch (tl) {
//                        case 'v': {
//                            tmap.setTileChar('.', i, j);
//                            chest = new Chest(chestAnim);
//                            tileX = tmap.getTileXC(i, j);
//                            tileY = tmap.getTileYC(i, j);
//                            chest.setPosition(tileX, tileY);
//                            entities.add(chest);
//                            break;
//                        }
                        case 'b': {
                            tmap.setTileChar('.', i, j);
                            coin = new Coin(coinAnim);
                            tileX = tmap.getTileXC(i, j);
                            tileY = tmap.getTileYC(i, j);
                            coin.setPosition(tileX, tileY);
                            coins.add(coin);
                            break;
                        }
                        case 'n': {
                            tmap.setTileChar('.', i, j);
                            flag = new Flag(flagAnim);
                            tileX = tmap.getTileXC(i, j) - (48 - tmap.getTileWidth());
                            tileY = tmap.getTileYC(i, j) - (48 - tmap.getTileHeight());
                            flag.setPosition(tileX, tileY);
                            break;
                        }
//                        case 'm': {
//                            tmap.setTileChar('.', i, j);
//                            key = new Key(keyAnim);
//                            tileX = tmap.getTileXC(i, j);
//                            tileY = tmap.getTileYC(i, j);
//                            key.setPosition(tileX, tileY);
//                            entities.add(key);
//                            break;
//                        }
//                        case ',': {
//                            tmap.setTileChar('.', i, j);
//                            rune = new Rune(runeAnim);
//                            tileX = tmap.getTileXC(i, j);
//                            tileY = tmap.getTileYC(i, j);
//                            rune.setPosition(tileX, tileY);
//                            entities.add(rune);
//                            break;
//                        }
                        case '<': {
                            tmap.setTileChar('.', i, j);
                            player = new Player(standingAnim);
                            tileX = tmap.getTileXC(i, j);
                            tileY = tmap.getTileYC(i, j);
                            player.setPosition(tileX, tileY);
                            break;
                        }
                        case '/': {
                            tmap.setTileChar('.', i, j);
                            slime = new Slime(moveAnim);
                            tileX = tmap.getTileXC(i, j);
                            tileY = tmap.getTileYC(i, j);
                            slime.setPosition(tileX, tileY);
                            slimes.add(slime);
                            break;
                        }
                        default: break;
                    }
                }
            }
        }

        // Lives Setup
        {
            // Initialise Hearts
            heart1 = new Heart(heart1Anim);
            heart2 = new Heart(heart2Anim);
            heart3 = new Heart(heart3Anim);

            // Add hearts to arraylist
            hearts.add(heart1);
            hearts.add(heart2);
            hearts.add(heart3);
        }

        // Background Setup
        {
            // Initialise backgrounds
            bg1 = new Sprite(background1);
            bg2 = new Sprite(background2);
            bg3 = new Sprite(background3);
            bg4 = new Sprite(background4);
            bg5 = new Sprite(background5);

            // Add backgrounds to an arraylist
            backgrounds.add(bg1);
            backgrounds.add(bg2);
            backgrounds.add(bg3);
            backgrounds.add(bg4);
            backgrounds.add(bg5);
        }

        initialiseGame();
    }

    /**
     * You will probably want to put code to restart a game in
     * a separate method so that you can call it to restart
     * the game.
     */
    public void initialiseGame() {
        totalCoinsCollected = 0;
        player.setVelocityX(0);
        player.setVelocityY(0);
        player.show();
    }
    
    /**
     * Draw the current state of the game
     */
    public void draw(Graphics2D g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());

        for (int i = 0; i < 5; i++) {
            for (Sprite bg : backgrounds) {
                bg.setOffsets((bg.getWidth() * i), 0);
                bg.setScale(1, 1.18f);
                bg.drawTransformed(g);
            }
        }

        for(Slime s: slimes) {
            s.setOffsets(xo, yo);
            s.drawTransformed(g);
        }

        flag.setOffsets(xo, yo);
        flag.drawTransformed(g);

        for(Coin c: coins) {
            c.setOffsets(xo, yo);
            c.drawTransformed(g);
        }

        player.setOffsets(xo, yo);
        player.drawTransformed(g);

        int i = 1;
        for (Heart h: hearts) {
            h.setPosition((37 * i), 52);
            h.draw(g);
            i++;
        }

        // Apply offsets to tile map and draw  it
        tmap.draw(g, xo, yo);

        // Show score and status information
        String msg = String.format("Score: %d", (score/100) + (totalCoinsCollected * 10));
        g.setColor(Color.white);
        g.drawString(msg, getWidth() - 80, 65);

        if (dead) {
            FontMetrics metrics = g.getFontMetrics(getFont()); // Allows you to get the x and y length of a string to allow the string to be in the middle of the screen
            String msgDead1 = String.format("You died with a Score of: %d", (score/100) + (totalCoinsCollected * 10));
            String msgDead2 = "Press ESC to quit";
            g.setColor(Color.white);
            g.drawString(msgDead1, ((getWidth()/2) - (metrics.stringWidth(msgDead1)/2)), getHeight()/2);
            g.drawString(msgDead2, ((getWidth()/2) - (metrics.stringWidth(msgDead2)/2)), ((getHeight()/2) + metrics.getHeight() + 5));
            paused = true;
        }
    }

    /**
     * Update any sprites and check for collisions
     * 
     * @param elapsed The elapsed time between this call and the previous call of elapsed
     */    
    public void update(long elapsed) {
    	if (paused) return;

        // Make adjustments to the speed of the sprite due to gravity
        player.setVelocityY(player.getVelocityY() + (gravity * elapsed));
       	player.setAnimationSpeed(1.0f);
       	
        // Now update the sprites animation and position
        for (Sprite bg: backgrounds) {
            bg.update(elapsed);
        }

        player.update(elapsed);
        flag.update(elapsed);

        for (Slime s: slimes) {
            s.setVelocityY(s.getVelocityY() + (gravity * elapsed));
            s.setAnimationSpeed(.5f);
            s.update(elapsed);
        }

        for (Coin c: coins) {
            c.update(elapsed);
        }

        for (Heart h: hearts) {
            h.update(elapsed);
        }

        if (player.getX() <= bg1.getWidth()) {
            xo = 0;
        }

        //  && player.getX() <= (tmap.getPixelWidth() - bg1.getWidth())

        if (player.getX() >= ((float) (tmap.getPixelWidth() / 8) / 2) && player.getX() <= 3304) {
            xo = (int) (((tmap.getPixelWidth() / 8) / 2) - player.getX());
        }

        if (player.getX() <= ((float) (tmap.getPixelWidth() / 8) / 2) || player.getX() >= 3304) {
            for (Sprite bg: backgrounds) {
                bg.setVelocityX(0);
            }
        } else {
            if (moveLeft) {
                bg1.setVelocityX(0.0001f);
                bg2.setVelocityX(0.001f);
                bg3.setVelocityX(0.01f);
                bg4.setVelocityX(0.05f);
                bg5.setVelocityX(0.1f);
            }

            if (moveRight) {
                bg1.setVelocityX(-0.0001f);
                bg2.setVelocityX(-0.001f);
                bg3.setVelocityX(-0.01f);
                bg4.setVelocityX(-0.05f);
                bg5.setVelocityX(-0.1f);
            }
        }

        if (moveLeft) {
            player.moveLeft();
            player.setAnimation(runningAnim);
        }

        if (moveRight) {
            player.moveRight();
            player.setAnimation(runningAnim);
        }


        if (player.getVelocityX() == 0) {
            for (Sprite bg: backgrounds) {
                bg.setVelocityX(0);
            }
            player.setAnimation(standingAnim);
        }

        if (player.getVelocityY() == 0) {
            for (Sprite bg: backgrounds) {
                bg.setVelocityY(0);
            }
        }

        if (!player.isOnGround() && player.getVelocityY() < 0)
            player.setAnimation(jumpingAnim);
        else if (!player.isOnGround() && player.getVelocityY() > 0)
            player.setAnimation(fallingAnim);

        for (Slime s: slimes) {
            s.checkTileCollision(s, tmap);
        }

        coins.removeIf(c -> {
            if (boundingBoxCollision(player, c)) {
                totalCoinsCollected++;
                return true;
            } else {
                return false;
            }
        });

        slimes.removeIf(s -> {
            if (boundingBoxCollision(player, s)) {
                playerHealth--;
                return true;
            } else {
                return false;
            }
        });

        switch (playerHealth) {
            case 0: {
                hearts.get(0).getAnimation().pauseAt(3);
                player.setAnimation(deadAnim);
                if ((hearts.get(0).getImage() == hearts.get(0).getAnimation().getFrameImage(3)) && (player.getImage() == player.getAnimation().getFrameImage(6))) {
                    dead = true;
                }
                break;
            }
            case 1: {
                hearts.get(1).getAnimation().pauseAt(3);
                break;
            }
            case 2: {
                hearts.get(2).getAnimation().pauseAt(3);
                break;
            }
        }

        // Then check for any collisions that may have occurred
        handleScreenEdge(player, tmap, elapsed);
        handleScreenEdge(slime, tmap, elapsed);
        player.checkTileCollision(player, tmap);
    }

    /**
     * Checks and handles collisions with the edge of the screen
     * 
     * @param s			The Sprite to check collisions for
     * @param tmap		The tile map to check 
     * @param elapsed	How much time has gone by since the last call
     */
    public void handleScreenEdge(Sprite s, TileMap tmap, long elapsed) {
    	// This method just checks if the sprite has gone off the bottom screen.
    	// Ideally you should use tile collision instead of this approach
    	
        if (s.getY() + s.getHeight() > tmap.getPixelHeight()) {
        	// Put the player back on the map 1 pixel above the bottom
        	s.setY(tmap.getPixelHeight() - s.getHeight());
        }
    }

    /**
     * Override of the keyPressed event defined in GameCore to catch our
     * own events
     * 
     *  @param e The event that has been generated
     */
    public void keyPressed(KeyEvent e) {
    	int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_ESCAPE: {
                stop();
                break;
            }
            case KeyEvent.VK_LEFT: {
                if (!dead) {
                    moveLeft = true;
                }
                break;
            }
            case KeyEvent.VK_RIGHT: {
                if (!dead) {
                    moveRight = true;
                }
                break;
            }
            case KeyEvent.VK_SPACE: {
                if (player.isOnGround() && !dead) {
                    player.setVelocityY(-0.4f);
                    player.setIsOnGround(false);
                }
                break;
            }
        }
    }

    public boolean boundingBoxCollision(Sprite s1, Sprite s2) {

        Rectangle sprite1 = new Rectangle((int) s1.getX(), (int) s1.getY(), s1.getWidth(), s1.getHeight());
        Rectangle sprite2 = new Rectangle((int) s2.getX(), (int) s2.getY(), s2.getWidth(), s2.getHeight());

        return sprite1.intersects(sprite2);
    }

    public void keyReleased(KeyEvent e) {

		int key = e.getKeyCode();

		// Switch statement instead of lots of ifs...
		// Need to use break to prevent fall through.
		switch (key)
		{
			case KeyEvent.VK_P : {
                if (!dead) {
                    paused = !paused;
                }
                break;
            }
            case KeyEvent.VK_R: {
                if (!dead) {
                    player.setPosition(tmap.getTileWidth() + player.getWidth(), tmap.getPixelHeight() - 128);
                    xo = 0;
                }
                break;
            }
            case KeyEvent.VK_LEFT: {
                if (!dead) {
                    moveLeft = false;
                    player.stopMoving();
                }
                break;
            }
            case KeyEvent.VK_RIGHT: {
                if (!dead) {
                    moveRight = false;
                    player.stopMoving();
                }
                break;
            }
            default :  break;
		}
	}
}
