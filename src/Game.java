import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import entities.Enemy.Monster;
import game2D.*;
import entities.Enemy.Slime;
import entities.Player.Player;
import entities.Items.*;

import javax.sound.sampled.*;
import javax.swing.*;

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
    int yo = 0;
    float gravity = 0.001f;
    int playerHealth;
    int level = 1;
    final int MAX_LEVELS = 2;
    int monsterHealth;
    int keysCollected;
    
    // Game state flags
    boolean gameStart;
    boolean paused;
    boolean moveLeft, moveRight, still, attack;
    boolean slimeMoveLeft, slimeMoveRight, slimeStill;
    boolean dead;
    boolean help;
    boolean levelComplete;

    // Game Resources
    // Player Animations
    Animation standingAnim, runningAnim, jumpingAnim, fallingAnim, deadAnim, attackAnim;
    // Slime Animations
    Animation slimeMoveAnim;
    // Monster Animations
    Animation monsterMoveAnim;
    // Item Animations
    Animation chestAnim, coinAnim, flagAnim, keyAnim, runeAnim;
    // Background Animations
    Animation background1, background2, background3, background4, background5;
    // Lives Animations
    Animation heart1Anim, heart2Anim, heart3Anim;
    // Sound Clip
    Clip startClip, mainClip, overClip;
    AudioInputStream start, main, over;

    // Game Entities
    Player player;
    Slime slime;
    Monster monster;
    Chest chest;
    Key key;
    Coin coin;
//    Rune rune;
    Flag flag;
    Heart heart1, heart2, heart3;
    Sprite bg1, bg2, bg3, bg4, bg5; // backgrounds

    ArrayList<Sprite> backgrounds = new ArrayList<Sprite>();
    ArrayList<Slime> slimes = new ArrayList<Slime>();
    ArrayList<Monster> monsters = new ArrayList<Monster>();
    ArrayList<Key> keys = new ArrayList<Key>();
    ArrayList<Key> collectedKeys = new ArrayList<Key>();
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
        gameStart = true;
        paused = false;
        moveRight = false;
        moveLeft = false;
        still = true;
        attack = false;
        slimeMoveRight = false;
        slimeMoveLeft = true;
        slimeStill = true;
        dead = false;
        playerHealth = 3;
        help = false;
        levelComplete = false;
        monsterHealth = 2;
        keysCollected = 0;

        // JFrame Apperance Changes
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        this.setSize(width/2, height/2);

        this.setLocationRelativeTo(null);
        this.setUndecorated(true);
        this.setBackground(Color.BLUE);

        // Load the tile map and print it out so we can check it is valid
        tmap.loadMap("maps", "level_" + level + ".txt");
        
        setSize(tmap.getPixelWidth() / 6, (tmap.getPixelHeight()));
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

            attackAnim = new Animation();
            attackAnim.loadAnimationFromSheet("images/character/attack.png", 6, 1, 90);
        }

        // Enemy Animations
        {
            slimeMoveAnim = new Animation();
            slimeMoveAnim.loadAnimationFromSheet("images/enemies/slime.png", 3, 1, 200);

            monsterMoveAnim = new Animation();
            monsterMoveAnim.loadAnimationFromSheet("images/enemies/monster.png", 4, 1, 300);
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
            keyAnim.loadAnimationFromSheet("images/items/Key.png", 4, 1, 225);

            runeAnim = new Animation();
            runeAnim.loadAnimationFromSheet("images/items/Rune.png", 4, 1, 125);

            heart1Anim = new Animation();
            heart1Anim.loadAnimationFromSheet("images/items/Heart.png", 4, 1, 250);

            heart2Anim = new Animation();
            heart2Anim.loadAnimationFromSheet("images/items/Heart.png", 4, 1, 250);

            heart3Anim = new Animation();
            heart3Anim.loadAnimationFromSheet("images/items/Heart.png", 4, 1, 250);
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
                        case 'v': {
                            tmap.setTileChar('.', i, j);
                            chest = new Chest(chestAnim);
                            tileX = tmap.getTileXC(i, j);
                            tileY = tmap.getTileYC(i, j);
                            chest.setPosition(tileX, tileY);
                            break;
                        }
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
                        case 'm': {
                            tmap.setTileChar('.', i, j);
                            key = new Key(keyAnim);
                            tileX = tmap.getTileXC(i, j);
                            tileY = tmap.getTileYC(i, j);
                            key.setPosition(tileX, tileY);
                            keys.add(key);
                            break;
                        }
//                        case ',': {
//                            tmap.setTileChar('.', i, j);
//                            rune = new Rune(runeAnim);
//                            tileX = tmap.getTileXC(i, j);
//                            tileY = tmap.getTileYC(i, j);
//                            rune.setPosition(tileX, tileY);
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
                            slime = new Slime(slimeMoveAnim);
                            tileX = tmap.getTileXC(i, j);
                            tileY = tmap.getTileYC(i, j);
                            slime.setPosition(tileX, tileY);
                            slimes.add(slime);
                            break;
                        }
                        case '?': {
                            tmap.setTileChar('.', i, j);
                            monster = new Monster(monsterMoveAnim);
                            tileX = tmap.getTileXC(i, j);
                            tileY = tmap.getTileYC(i, j);
                            monster.setPosition(tileX, tileY);
                            monsters.add(monster);
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

        // Audio setup
        {
            try {
                start = AudioSystem.getAudioInputStream(new File("sounds/Game Start.mid"));
                main = AudioSystem.getAudioInputStream(new File("sounds/Load Game.mid"));
                over = AudioSystem.getAudioInputStream(new File("sounds/Game Over.mid"));

                startClip = AudioSystem.getClip();
                startClip.open(start);

                mainClip = AudioSystem.getClip();
                mainClip.open(main);

                overClip = AudioSystem.getClip();
                overClip.open(over);

                startClip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
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

        if (!startClip.isRunning()) {
            startClip.close();
            gameStart = false;
            mainClip.loop(Clip.LOOP_CONTINUOUSLY);
        }

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

        for(Monster m: monsters) {
            m.setOffsets(xo, yo);
            m.drawTransformed(g);
        }

        flag.setOffsets(xo, yo);
        flag.drawTransformed(g);

        for(Coin c: coins) {
            c.setOffsets(xo, yo);
            c.drawTransformed(g);
        }

        for(Key k: keys) {
            k.setOffsets(xo, yo);
            k.drawTransformed(g);
        }

//        rune.setOffsets(xo, yo);
//        rune.draw(g);

        chest.setOffsets(xo, yo);
        chest.draw(g);

        player.setOffsets(xo, yo);
        player.drawTransformed(g);

        int i = 1;
        for (Heart h: hearts) {
            h.setPosition((36 * i), 36);
            h.draw(g);
            i++;
        }

        int j = 1;
        int collKeysX = 150;
        for (Key k: collectedKeys) {
            k.setScale(1.5f);
            k.setPosition(collKeysX, 42);
            collKeysX += k.getWidth() * j;
            k.drawTransformed(g);
            j++;
        }

        // Apply offsets to tile map and draw it
        tmap.draw(g, xo, yo);

        FontMetrics fm = g.getFontMetrics();

        if (gameStart || help) {
            String msg = "Press H to see these help messages,\n" +
                    "Press space to Jump,\n" +
                    "Press the left and right arrow to move left and right,\n" +
                    "To attack press A while running,\n" +
                    "Press R to reset back to the start,\n" +
                    "Press P to pause,\n" +
                    "Press ESC to quit,\n" +
                    "Collect coins on your way,\n" +
                    "Find the key to the chest,\n" +
                    "Solve the puzzle to get through the door!\n" +
                    "Ready? GO!";

            g.setColor(Color.WHITE);

            int y = (getHeight()/4);
            for (String line : msg.split("\n")) {
                int x = ((getWidth() - fm.stringWidth(line)) / 2);
                g.drawString(line, x, y);
                y += (fm.getHeight() + 2);
            }
        }

        // Show score and status information
        String scoreMsg = String.format("Score: %d", score + (totalCoinsCollected * 10));
        g.setColor(Color.WHITE);
        g.drawString(scoreMsg, (getWidth() - fm.stringWidth(scoreMsg)) - 36, 48);

        if (dead) {
            mainClip.stop();
            overClip.loop(Clip.LOOP_CONTINUOUSLY);
            String msgDead1 = String.format("You died with a Score of: %d", score + (totalCoinsCollected * 10));
            String msgDead2 = "Press ESC to quit";
            g.setColor(Color.WHITE);
            g.drawString(msgDead1, ((getWidth()/2) - (fm.stringWidth(msgDead1) / 2)), getHeight() / 2);
            g.drawString(msgDead2, ((getWidth()/2) - (fm.stringWidth(msgDead2) / 2)), ((getHeight() / 2) + fm.getHeight() + 5));
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
//        rune.update(elapsed);
        chest.update(elapsed);

        for (Slime s: slimes) {
            s.setVelocityY(s.getVelocityY() + (gravity * elapsed));
            s.setAnimationSpeed(.5f);
            s.update(elapsed);
        }

        for (Monster m: monsters) {
            m.setVelocityY(m.getVelocityY() + (gravity * elapsed));
            m.setAnimationSpeed(.5f);
            m.update(elapsed);
        }

        for (Coin c: coins) {
            c.update(elapsed);
        }

        for (Key k: keys) {
            k.update(elapsed);
        }

        for (Key k: collectedKeys) {
            k.update(elapsed);
        }

        for (Heart h: hearts) {
            h.update(elapsed);
        }

        if (player.getX() <= bg1.getWidth()) {
            xo = 0;
        }

        // Got 342 by tracking the player X until the edge of the map is shown to stop the map movement
        if (player.getX() >= ((float) (tmap.getPixelWidth() / 6) / 2) && player.getX() <= tmap.getPixelWidth() - 342) {
            xo = (int) (((tmap.getPixelWidth() / 6) / 2) - player.getX());
        }

        if (player.getX() <= ((float) (tmap.getPixelWidth() / 6) / 2) || player.getX() >= tmap.getPixelWidth() - 342) {
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

        coins.removeIf(c -> {
            if (boundingBoxCollision(player, c)) {
                totalCoinsCollected++;
                return true;
            } else {
                return false;
            }
        });

        keys.removeIf(k -> {
            if (boundingBoxCollision(player, k)) {
                collectedKeys.add(k);
                score += 25;
                return true;
            } else {
                return false;
            }
        });

        if (boundingBoxCollision(player, chest) && collectedKeys.size() == 3) {
            chest.playAnimation();
            chest.pauseAnimationAtFrame(3);
            levelComplete = true;
        }

        slimes.removeIf(s -> {
            if (boundingBoxCollision(player, s)) {
                return attackEnemy();
            } else {
                return false;
            }
        });

        monsters.removeIf(m -> {
            if (boundingBoxCollision(player, m)) {
                return attackEnemy();
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
        player.checkTileCollision(player, tmap);

        for (Slime s: slimes) {
            s.checkTileCollision(s, tmap);
            handleScreenEdge(s, tmap, elapsed);
        }

        for (Monster m: monsters) {
            m.checkTileCollision(m, tmap);
            handleScreenEdge(m, tmap, elapsed);
        }

        if (levelComplete) {
            player.stopMoving();
            player.setAnimation(standingAnim);
            mainClip.close();
            overClip.start();

            nextLevel();
        }
    }

    private boolean attackEnemy() {
        if (attack) {
            attack = false;
            if (monsterHealth == 1){
                score += 75;
            }
            else {
                monsterHealth--;
            }
        } else {
            playerHealth--;
        }
        return true;
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
                if (!dead && !gameStart && !levelComplete) {
                    moveLeft = true;
                }
                break;
            }
            case KeyEvent.VK_RIGHT: {
                if (!dead && !gameStart && !levelComplete) {
                    moveRight = true;
                }
                break;
            }
            case KeyEvent.VK_SPACE: {
                if (player.isOnGround() && !dead && !gameStart && !levelComplete) {
                    player.setVelocityY(-0.4f);
                    player.setIsOnGround(false);
                    Sound jump = new Sound("sounds/jump.wav");
                    jump.start();
                }
                break;
            }
            case KeyEvent.VK_A: {
                if (!dead && !gameStart && !levelComplete) {
                    player.setAnimation(attackAnim);
                    attack = true;
                }
                break;
            }
            case KeyEvent.VK_H: {
                if (!dead && !gameStart && !levelComplete) {
                    help = true;
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
        if (!levelComplete && !dead && !gameStart) {
            switch (key) {
                case KeyEvent.VK_P: {
                    paused = !paused;
                    break;
                }
                case KeyEvent.VK_R: {
                    player.setPosition(tmap.getTileWidth() + player.getWidth(), tmap.getPixelHeight() - 128);
                    xo = 0;
                    break;
                }
                case KeyEvent.VK_LEFT: {
                    moveLeft = false;
                    player.stopMoving();
                    break;
                }
                case KeyEvent.VK_RIGHT: {
                    moveRight = false;
                    player.stopMoving();
                    break;
                }
                case KeyEvent.VK_H: {
                    help = false;
                }
                default:
                    break;
            }
        }
	}

	private void nextLevel() {

        if (level < MAX_LEVELS) {
            level++;
        }

        int result = JOptionPane.showConfirmDialog(this, "You have completed the level!\n\nDo you want to continue to the next level?", "Next Level?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            overClip.close();
            this.dispose();
            Game nextLevel = new Game();
            nextLevel.init();
            nextLevel.run(false, screenWidth, screenHeight);
        } else if (result == JOptionPane.NO_OPTION){
            overClip.close();
            stop();
        }
    }
}
