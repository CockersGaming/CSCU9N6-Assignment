
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;


import game2D.*;
import game2D.entities.Enemy.Slime;
import game2D.entities.Player.Player;
import game2D.entities.items.*;

import javax.xml.stream.FactoryConfigurationError;

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
    
    // Game state flags
    boolean paused;
    boolean moveLeft, moveRight, still;
    boolean slimeMoveLeft, slimeMoveRight, slimeStill;

    // Game Resources
    // Player Animations
    Animation standingAnim, runningAnim, jumpingAnim, fallingAnim;
    // Slime Animations
    Animation moveAnim;
    // Item Animations
    Animation chestAnim, coinAnim, flagAnim, keyAnim, runeAnim;
    // Background Animations
    Animation background1, background2, background3, background4, background5;

    // Game Entities
    Player player;
    Slime slime;
    Coin coin;
    Flag flag;
    Sprite bg1, bg2, bg3, bg4, bg5; // backgrounds

    ArrayList<Sprite> backgrounds = new ArrayList<Sprite>();
    ArrayList<Slime> slimes = new ArrayList<Slime>();
    ArrayList<Coin> coins = new ArrayList<Coin>();

    TileMap tmap = new TileMap(); // Our tile map, note that we load it in init()
    
    long total; // The score will be the total time elapsed since a crash

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

        // Load the tile map and print it out so we can check it is valid
        tmap.loadMap("maps", "map.txt");
        
        setSize(tmap.getPixelWidth() / 3, tmap.getPixelHeight() + 16);
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

        // Initialise backgrounds
        {
            bg1 = new Sprite(background1);
            bg2 = new Sprite(background2);
            bg3 = new Sprite(background3);
            bg4 = new Sprite(background4);
            bg5 = new Sprite(background5);
        }

        // Add backgrounds to an arraylist
        {
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
    	total = 0;
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

        // Apply offsets to tile map and draw  it
        tmap.draw(g, xo, yo);
        
        // Show score and status information
        String msg = String.format("Score: %d", total/100);
        g.setColor(Color.black);
        g.drawString(msg, getWidth() - 80, 65);
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
            s.update(elapsed);
        }

        for (Coin c: coins) {
            c.update(elapsed);
        }

        if (player.getX() >= 500 && !(player.getX() >= (tmap.getPixelWidth() - 500))) {
            xo = (int) (500 - player.getX());
        } else {
            xo = 0;
        }

        if (moveLeft) {
            player.moveLeft();
            player.setAnimation(runningAnim);
            bg1.setVelocityX(0.0001f);
            bg2.setVelocityX(0.001f);
            bg3.setVelocityX(0.01f);
            bg4.setVelocityX(0.05f);
            bg5.setVelocityX(0.1f);
        }

        if (moveRight) {
            player.moveRight();
            player.setAnimation(runningAnim);
            bg1.setVelocityX(-0.0001f);
            bg2.setVelocityX(-0.001f);
            bg3.setVelocityX(-0.01f);
            bg4.setVelocityX(-0.05f);
            bg5.setVelocityX(-0.1f);
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

        // Then check for any collisions that may have occurred
        handleScreenEdge(player, tmap, elapsed);
        handleScreenEdge(slime, tmap, elapsed);
        checkTileCollision(player, tmap);
        for (Slime s: slimes) {
            s.checkTileCollision(s, tmap);
        }
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
    	
        if (s.getY() + s.getHeight() > tmap.getPixelHeight())
        {
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
            case KeyEvent.VK_ESCAPE: stop(); break;
            case KeyEvent.VK_LEFT: moveLeft = true; break;
            case KeyEvent.VK_RIGHT: moveRight = true; break;
            case KeyEvent.VK_SPACE: {
                if (player.isOnGround()) {
                    player.setVelocityY(-0.4f);
                    player.setIsOnGround(false);
                }
                break;
            }
        }
    }

    public boolean boundingBoxCollision(Sprite s1, Sprite s2) {
        return false;
    }
    
    /**
     * Check and handles collisions with a tile map for the
     * given sprite 's'. Initial functionality is limited...
     * 
     * @param s			The Sprite to check collisions for
     * @param tmap		The tile map to check 
     */
    public void checkTileCollision(Sprite s, TileMap tmap) {
    	// Take a note of a sprite's current position
    	float sx = s.getX();
    	float sy = s.getY();
    	
    	// Find out how wide and how tall a tile is
    	float tileWidth = tmap.getTileWidth();
    	float tileHeight = tmap.getTileHeight();

        // Top Left
    	int	tlXTile = (int)(sx / tileWidth);
    	int tlYTile = (int)(sy / tileHeight);
    	char tl = tmap.getTileChar(tlXTile, tlYTile);

        // Top Right
        int	trXTile = (int) ((sx + s.getWidth()) / tileWidth);
        int trYTile = (int) (sy / tileHeight);
        char tr = tmap.getTileChar(trXTile, trYTile);

        // Bottom Left
        int	blXTile = (int)(sx / tileWidth);
        int blYTile = (int) ((sy + s.getHeight()) / tileHeight);
        char bl = tmap.getTileChar(blXTile, blYTile);

        // Bottom Right
        int	brXTile = (int) ((sx + s.getWidth()) / tileWidth);
        int brYTile = (int) ((sy + s.getHeight()) / tileHeight);
        char br = tmap.getTileChar(brXTile, brYTile);

        // Logic to get the difference on the X and Y axis from a corner of the sprite
        // to the middle of a tile to find out what collision it is
        float tlTileMidX = tmap.getTileXC(tlXTile, tlYTile) + (tileWidth / 2);
        float tlTileMidY = tmap.getTileYC(tlXTile, tlYTile) + (tileHeight / 2);
        float tlXDiff = tlTileMidX - sx;
        float tlYDiff = tlTileMidY - sy;

        float trTileMidX = tmap.getTileXC(trXTile, trYTile) + (tileWidth / 2);
        float trTileMidY = tmap.getTileYC(trXTile, trYTile) + (tileHeight / 2);
        float trXDiff = trTileMidX - (sx + s.getWidth());
        float trYDiff = trTileMidY - sy;

        float blTileMidX = tmap.getTileXC(blXTile, blYTile) + (tileWidth / 2);
        float blTileMidY = tmap.getTileYC(blXTile, blYTile) + (tileHeight / 2);
        float blXDiff = blTileMidX - sx;
        float blYDiff = blTileMidY - (sy + s.getHeight());

        float brTileMidX = tmap.getTileXC(brXTile, brYTile) + (tileWidth / 2);
        float brTileMidY = tmap.getTileYC(brXTile, brYTile) + (tileHeight / 2);
        float brXDiff = brTileMidX - (sx + s.getWidth());
        float brYDiff = brTileMidY - (sy + s.getHeight());

        if (tl != '.' || tr != '.' || bl != '.' || br != '.') // If it's not a dot (empty space), handle it
        {
            // Left Collision
            if ((tl != '.' && tr == '.' && Math.abs(tlYDiff) >= Math.abs(tlXDiff)) || (bl != '.' && br == '.' && Math.abs(blXDiff) >= Math.abs(blYDiff))) {
                s.setVelocityX(0);
                s.setX(tmap.getTileXC(tlXTile, tlYTile) + tileWidth);
            }
            // Right
            else if ((tr != '.' && tl == '.' && Math.abs(trYDiff) >= Math.abs(trXDiff)) || (br != '.' && bl == '.' && Math.abs(brXDiff) >= Math.abs(brYDiff)) ) {
                s.setVelocityX(0);
                s.setX(tmap.getTileXC(trXTile, trYTile) - s.getWidth());
            }
            // Bottom
            if ((br != '.' && tr == '.' && Math.abs(brYDiff) >= Math.abs(brXDiff)) || (bl != '.' && tl == '.' && Math.abs(blYDiff) >= Math.abs(blXDiff))) {
                s.setVelocityY(0);
                s.setY(tmap.getTileYC(blXTile, blYTile) - s.getHeight());
                s.setIsOnGround(true);
            }
            // Top
            else if ((tr != '.' && br == '.' && Math.abs(trYDiff) >= Math.abs(trXDiff)) || (tl != '.' && bl == '.' && Math.abs(tlYDiff) >= Math.abs(tlXDiff))) {
                s.setVelocityY(0);
                s.setY((tmap.getTileYC(trXTile, trYTile) +tileHeight) + s.getHeight());
            }
        }
    }

    public void keyReleased(KeyEvent e) {

		int key = e.getKeyCode();

		// Switch statement instead of lots of ifs...
		// Need to use break to prevent fall through.
		switch (key)
		{
			case KeyEvent.VK_ESCAPE : stop(); break;
			case KeyEvent.VK_P : paused = !paused; break;
            case KeyEvent.VK_R: player.setPosition(player.getWidth(), tmap.getPixelHeight() - 192); break;
            case KeyEvent.VK_LEFT: moveLeft = false; player.stopMoving(); break;
            case KeyEvent.VK_RIGHT: moveRight = false; player.stopMoving(); break;
            default :  break;
		}
	}
}
