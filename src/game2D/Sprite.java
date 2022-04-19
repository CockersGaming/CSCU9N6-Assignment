package game2D;

import java.awt.Image;
import java.awt.*;
import java.awt.geom.*;

/**
 * This class provides the functionality for a moving animated image or Sprite.
 * 
 * @studentID 2811801
 *
 */
public class Sprite {

	// The current Animation to use for this sprite
    private Animation anim;		

    // Position (pixels)
    private float x;
    private float y;

    // Velocity (pixels per millisecond)
    private float dx;
    private float dy;

    // Dimensions of the sprite
    private float height;
    private float width;
    private float radius;

    // The scale to draw the sprite at where 1 equals normal size
    private double xscale;
    private double yscale;
    
    // The rotation to apply to the sprite image
    private double rotation;

    // If render is 'true', the sprite will be drawn when requested
    private boolean render;
    
    // The draw offset associated with this sprite. Used to draw it
    // relative to specific on screen position (usually the player)
    private int xoff=0;
    private int yoff=0;

    private boolean isOnGround;

    private final float MOVE_SPEED = 0.2f;

    /**
     *  Creates a new Sprite object with the specified Animation.
     *  
     * @param anim The animation to use for the sprite.
     * 
     */
    public Sprite(Animation anim) 
    {
        this.anim = anim;
        render = true;
        xscale = 1.0f;
        yscale = 1.0f;
        rotation = 0.0f;
    }

    /**
     * Change the animation for the sprite to 'a'.
     *
     * @param a The animation to use for the sprite.
     */
    public void setAnimation(Animation a)
    {
    		anim = a;
    }
    
    /**
     * Set the current animation to the given 'frame'
     * 
     * @param frame The frame to set the animation to
     */
    public void setAnimationFrame(int frame)
    {
    	anim.setAnimationFrame(frame);
    }
    
    /**
     * Pauses the animation at its current frame. Note that the 
     * sprite will continue to move, it just won't animate
     */
    public void pauseAnimation()
    {
    	anim.pause();
    }
    
    /**
     * Pause the animation when it reaches frame 'f'. 
     * 
     * @param f The frame to stop the animation at
     */
    public void pauseAnimationAtFrame(int f)
    {
    	anim.pauseAt(f);
    }
    
    /**
     * Change the speed at which the current animation runs. A
     * speed of 1 will result in a normal animation,
     * 0.5 will be half the normal rate and 2 will double it.
     * 
     * Note that if you change animation, it will run at whatever
     * speed it was previously set to.
     * 
     * @param speed	The speed to set the current animation to.
     */
    public void setAnimationSpeed(float speed)
    {
    	anim.setAnimationSpeed(speed);
    }
    
    /**
     * Starts an animation playing if it has been paused.
     */
    public void playAnimation()
    {
    	anim.play();
    }
    
    /**
     * Returns a reference to the current animation
     * assigned to this sprite.
     * 
     * @return A reference to the current animation
     */
    public Animation getAnimation()
    {
    	return anim;
    }

    /**
        Updates this Sprite's Animation and its position based
        on the elapsedTime.
        
        @param elapsedTime time that has elapsed since the last call to update
    */
    public void update(long elapsedTime) 
    {
    	if (!render) return;
        x += dx * elapsedTime;
        y += dy * elapsedTime;
        anim.update(elapsedTime);
        width = getWidth();
        height = getHeight();
        if (width > height)
        	radius = width / 2.0f;
        else
        	radius = height / 2.0f;
    }

    /**
        Gets this Sprite's current x position.
    */
    public float getX() 
    {
    	return x;
    }

    /**
        Gets this Sprite's current y position.
    */
    public float getY() 
    {
    	return y;
    }

    /**
        Sets this Sprite's current x position.
    */
    public void setX(float x) 
    {
    	this.x = x;
    }

    /**
        Sets this Sprite's current y position.
    */
    public void setY(float y) 
    {
    	this.y = y;
    }

    /**
	    Sets this Sprite's new x and y position.
	*/
	public void setPosition(float x, float y) 
	{
	    setX(x);
	    setY(y);
	}

    public void shiftX(float shift)
    {
    	this.x += shift;
    }
    
    public void shiftY(float shift)
    {
    	this.y += shift;
    }
    
    /**
        Gets this Sprite's width, based on the size of the
        current image.
    */
    public int getWidth() 
    {
        return (int)(anim.getImage().getWidth(null)*Math.abs(xscale));
    }

    /**
        Gets this Sprite's height, based on the size of the
        current image.
    */
    public int getHeight() 
    {
        return (int)(anim.getImage().getHeight(null)*Math.abs(yscale));
    }

    /**
    	Gets the sprites radius in pixels
    */
    public float getRadius()
    {
    	return radius;
    }

    /**
        Gets the horizontal velocity of this Sprite in pixels
        per millisecond.
    */
    public float getVelocityX() 
    {
        return dx;
    }

    /**
        Gets the vertical velocity of this Sprite in pixels
        per millisecond.
    */
    public float getVelocityY() 
    {
        return dy;
    }
    

    /**
        Sets the horizontal velocity of this Sprite in pixels
        per millisecond.
    */
    public void setVelocityX(float dx) 
    {
        this.dx = dx;
    }

    /**
        Sets the vertical velocity of this Sprite in pixels
        per millisecond.
    */
    public void setVelocityY(float dy) 
    {
        this.dy = dy;
    }

    /**
    	Sets the horizontal and vertical velocity of this Sprite in pixels
    	per millisecond.
	*/
	public void setVelocity(float dx, float dy) 
	{
		this.dx = dx;
		this.dy = dy;
	}

	/**
		Set the x and y scale of the sprite to 'scx' and 'scy' respectively. 
		If scx and scy are 1, the sprite will be drawn at normal size. If they
		are 0.5 it will be drawn at half size. If scx is -1, the sprite will be
		flipped along its vertical axis (it will face left instead of right). 
		Negative values of scy will flip along the horizontal axis. The flipping
		and scaling of the sprite are now accounted for when setting a sprite 
		position and getting its width and height (you will always reference 
		the top left of the sprite irrespective of the scaling). 
		Note that scaling and rotation are only applied when
		using the drawTransformed method.
	*/
    public void setScale(float scx, float scy)
    {
    	xscale = scx;
    	yscale = scy;
    }

	/**
	Set the scale of the sprite to 's'. If s is 1
	the sprite will be drawn at normal size. If 's'
	is 0.5 it will be drawn at half size. Note that
	scaling and rotation are only applied when
	using the drawTransformed method.
	*/
	public void setScale(float s)
	{
		xscale = s;
		yscale = s;
	}

    public void setScaleX(float s) {
        xscale = s;
    }

    public void setScaleY(float s) {
        yscale = s;
    }

    
	/**
		Get the current value of the x scaling attribute.
		See 'setScale' for more information.
	*/
    public double getScaleX()
    {
    	return xscale;
    }
    
	/**
	Get the current value of the y scaling attribute.
	See 'setScale' for more information.
	 */
	public double getScaleY()
	{
		return yscale;
	}

	/**
		Set the rotation angle for the sprite in degrees.
		Note that scaling and rotation are only applied when
		using the drawTransformed method.
	*/
    public void setRotation(double r)
    {
    	rotation = Math.toRadians(r);
    }

	/**
		Get the current value of the rotation attribute.
		in degrees. See 'setRotation' for more information.
	*/
    public double getRotation() 
    {
    	return Math.toDegrees(rotation);
    }

    /**
     	Stops the sprites movement at the current position
    */
    public void stop()
    {
    	dx = 0;
    	dy = 0;
    }

    /**
        Gets this Sprite's current image.
    */
    public Image getImage() 
    {
        return anim.getImage();
    }

	/**
		Draws the sprite with the graphics object 'g' at
		the current x and y co-ordinates. Scaling and rotation
		transforms are NOT applied.
	*/
    public void draw(Graphics2D g)
    {
    	if (!render) return;

    	g.drawImage(getImage(),(int)x+xoff,(int)y+yoff,null);
    }

    /**
		Draws the bounding box of this sprite using the graphics object 'g' and
		the currently selected foreground colour.
	*/
    public void drawBoundingBox(Graphics2D g)
    {
    	if (!render) return;

		Image img = getImage();
    	g.drawRect((int)x,(int)y,img.getWidth(null),img.getHeight(null));
    }
    
    /**
		Draws the bounding circle of this sprite using the graphics object 'g' and
		the currently selected foreground colour.
	*/
    public void drawBoundingCircle(Graphics2D g)
    {
    	if (!render) return;

		Image img = getImage();
		
    	g.drawArc((int)x,(int)y,img.getWidth(null),img.getHeight(null),0, 360);
    }
    
	/**
		Draws the sprite with the graphics object 'g' at
		the current x and y co-ordinates with the current scaling
		and rotation transforms applied.
		
		@param g The graphics object to draw to,
	*/
    public void drawTransformed(Graphics2D g)
    {
    	if (!render) return;

		AffineTransform transform = new AffineTransform();
		
		// Apply scaling to current x and y positions to 
		// ensure shifted left and up when flipped due to scaling.
		float shiftx = 0;
		float shifty = 0;
		if (xscale < 0) shiftx = getWidth();
		if (yscale < 0) shifty = getHeight();
		
		transform.translate(Math.round(x)+shiftx+xoff,Math.round(y)+shifty+yoff);
		transform.scale(xscale,yscale);
		transform.rotate(rotation,getImage().getWidth(null)/2,getImage().getHeight(null)/2);
		// Apply transform to the image and draw it
		g.drawImage(getImage(),transform,null);
    }


	/**
		Hide the sprite.
	*/
    public void hide()  {	render = false;  }

	/**
		Show the sprite
	*/
    public void show()  {  	render = true;   }

	/**
		Check the visibility status of the sprite.
	*/
    public boolean isVisible() { return render; }

	/**
		Set an x & y offset to use when drawing the sprite.
		Note this does not affect its actual position, just
		moves the drawn position.
	*/
    public void setOffsets(int x, int y)
    {
    	xoff = x;
    	yoff = y;
    }

    public boolean isOnGround() {
        return isOnGround;
    }

    public void setIsOnGround(boolean isOnGround) {
        this.isOnGround = isOnGround;
    }

    public void moveLeft() {
        this.setVelocityX(-MOVE_SPEED);
        this.setScaleX(-1);
    }

    public void moveRight() {
        this.setVelocityX(MOVE_SPEED);
        this.setScaleX(1);
    }

    public void stopMoving() {
        this.setVelocityX(0);
        this.setScaleX(1);
    }
}
