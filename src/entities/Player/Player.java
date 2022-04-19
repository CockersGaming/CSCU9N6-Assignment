package entities.Player;

import game2D.Animation;
import game2D.Sprite;
import game2D.TileMap;


/**
 * @studentID 2811801
 *
 */
@SuppressWarnings("serial")

public class Player extends Sprite {

    private final float PLAYER_SPEED = 0.2f;

    /**
     * Creates a new Sprite object with the specified Animation.
     *
     * @param anim
     */
    public Player(Animation anim) {
        super(anim);
    }

    public void moveLeft() {
        this.setVelocityX(-PLAYER_SPEED);
        this.setScaleX(-1);
    }

    public void moveRight() {
        this.setVelocityX(PLAYER_SPEED);
        this.setScaleX(1);
    }

    public void stopMoving() {
        this.setVelocityX(0);
        this.setScaleX(1);
    }

    /**
     * Check and handles collisions with a tile map for the
     * given sprite 's'. Initial functionality is limited...
     *
     * @param p			The Sprite to check collisions for
     * @param tmap		The tile map to check
     */
    public void checkTileCollision(Player p, TileMap tmap) {
        // Take a note of a sprite's current position
        float sx = p.getX();
        float sy = p.getY();

        // Find out how wide and how tall a tile is
        float tileWidth = tmap.getTileWidth();
        float tileHeight = tmap.getTileHeight();

        // Top Left
        int	tlXTile = (int)(sx / tileWidth);
        int tlYTile = (int)(sy / tileHeight);
        char tl = tmap.getTileChar(tlXTile, tlYTile);

        // Top Right
        int	trXTile = (int) ((sx + p.getWidth()) / tileWidth);
        int trYTile = (int) (sy / tileHeight);
        char tr = tmap.getTileChar(trXTile, trYTile);

        // Bottom Left
        int	blXTile = (int)(sx / tileWidth);
        int blYTile = (int) ((sy + p.getHeight()) / tileHeight);
        char bl = tmap.getTileChar(blXTile, blYTile);

        // Bottom Right
        int	brXTile = (int) ((sx + p.getWidth()) / tileWidth);
        int brYTile = (int) ((sy + p.getHeight()) / tileHeight);
        char br = tmap.getTileChar(brXTile, brYTile);

        // Logic to get the difference on the X and Y axis from a corner of the sprite
        // to the middle of a tile to find out what collision it is
        float tlTileMidX = tmap.getTileXC(tlXTile, tlYTile) + (tileWidth / 2);
        float tlTileMidY = tmap.getTileYC(tlXTile, tlYTile) + (tileHeight / 2);
        float tlXDiff = tlTileMidX - sx;
        float tlYDiff = tlTileMidY - sy;

        float trTileMidX = tmap.getTileXC(trXTile, trYTile) + (tileWidth / 2);
        float trTileMidY = tmap.getTileYC(trXTile, trYTile) + (tileHeight / 2);
        float trXDiff = trTileMidX - (sx + p.getWidth());
        float trYDiff = trTileMidY - sy;

        float blTileMidX = tmap.getTileXC(blXTile, blYTile) + (tileWidth / 2);
        float blTileMidY = tmap.getTileYC(blXTile, blYTile) + (tileHeight / 2);
        float blXDiff = blTileMidX - sx;
        float blYDiff = blTileMidY - (sy + p.getHeight());

        float brTileMidX = tmap.getTileXC(brXTile, brYTile) + (tileWidth / 2);
        float brTileMidY = tmap.getTileYC(brXTile, brYTile) + (tileHeight / 2);
        float brXDiff = brTileMidX - (sx + p.getWidth());
        float brYDiff = brTileMidY - (sy + p.getHeight());

        if (tl != '.' || tr != '.' || bl != '.' || br != '.') // If it's not a dot (empty space), handle it
        {
            // Left Collision
            if ((tl != '.' && tr == '.' && Math.abs(tlYDiff) >= Math.abs(tlXDiff)) || (bl != '.' && br == '.' && Math.abs(blXDiff) >= Math.abs(blYDiff))) {
                p.setVelocityX(0);
                p.setX(tmap.getTileXC(tlXTile, tlYTile) + tileWidth);
            }
            // Right
            else if ((tr != '.' && tl == '.' && Math.abs(trYDiff) >= Math.abs(trXDiff)) || (br != '.' && bl == '.' && Math.abs(brXDiff) >= Math.abs(brYDiff)) ) {
                p.setVelocityX(0);
                p.setX(tmap.getTileXC(trXTile, trYTile) - p.getWidth());
            }
            // Bottom
            if ((br != '.' && tr == '.' && Math.abs(brYDiff) >= Math.abs(brXDiff)) || (bl != '.' && tl == '.' && Math.abs(blYDiff) >= Math.abs(blXDiff))) {
                p.setVelocityY(0);
                p.setY(tmap.getTileYC(blXTile, blYTile) - p.getHeight());
                p.setIsOnGround(true);
            }
            // Top
            else if ((tr != '.' && br == '.' && Math.abs(trYDiff) >= Math.abs(trXDiff)) || (tl != '.' && bl == '.' && Math.abs(tlYDiff) >= Math.abs(tlXDiff))) {
                p.setVelocityY(0);
                p.setY((tmap.getTileYC(trXTile, trYTile) + tileHeight) + p.getHeight());
            }
        }
    }
}
