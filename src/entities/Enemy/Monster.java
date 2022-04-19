package entities.Enemy;

import game2D.Animation;
import game2D.Sprite;
import game2D.TileMap;

import java.util.Random;


/**
 * @studentID 2811801
 *
 */
@SuppressWarnings("serial")

public class Monster extends Sprite {

    private final float MONSTER_SPEED = 0.2f;

    /**
     * Creates a new Sprite object with the specified Animation.
     *
     * @param anim The animation to use for the sprite.
     */
    public Monster(Animation anim) {
        super(anim);
    }

    @Override
    public void moveLeft() {
        this.setVelocityX(-MONSTER_SPEED);
        this.setScaleX(-1);
    }

    @Override
    public void moveRight() {
        this.setVelocityX(MONSTER_SPEED);
        this.setScaleX(1);
    }

    @Override
    public void stopMoving() {
        this.setVelocityX(0);
        this.setScaleX(1);
    }

    public void checkTileCollision(Monster s, TileMap tmap) {
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
                s.moveRight();
            }
            // Right
            else if ((tr != '.' && tl == '.' && Math.abs(trYDiff) >= Math.abs(trXDiff)) || (br != '.' && bl == '.' && Math.abs(brXDiff) >= Math.abs(brYDiff)) ) {
                s.moveLeft();
            }
            // Bottom
            if ((br != '.' && tr == '.' && Math.abs(brYDiff) >= Math.abs(brXDiff)) || (bl != '.' && tl == '.' && Math.abs(blYDiff) >= Math.abs(blXDiff))) {
                s.setVelocityY(0);
                s.setY(tmap.getTileYC(blXTile, blYTile) - s.getHeight());
                s.setIsOnGround(true);

                if ((br == '.' && bl == '¦') || (br == '.' && bl == 'o')) {
                    s.moveLeft();
                } else if ((bl == '.' && br == '`') || (bl == '.' && br == 'u')) {
                    s.moveRight();
                }
            }
            // Top
            else if ((tr != '.' && br == '.' && Math.abs(trYDiff) >= Math.abs(trXDiff)) || (tl != '.' && bl == '.' && Math.abs(tlYDiff) >= Math.abs(tlXDiff))) {
                s.setVelocityY(0);
                s.setY((tmap.getTileYC(trXTile, trYTile) +tileHeight) + s.getHeight());
            }
        } else {
            Random rand = new Random();
            int ranNum = rand.nextInt((1) + 1);
            if (ranNum > 0)
                s.moveLeft();
            else
                s.moveRight();
        }
    }
}
