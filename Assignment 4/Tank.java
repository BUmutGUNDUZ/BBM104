import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;

public class Tank {

    // Current position
    private double x;
    private double y;

    // The visual representation of the tank
    private final ImageView view;

    // Movement speed per update
    private final double speed = 6.5;

    // List of wall images to check collisions against
    private final List<ImageView> walls;

    // List of EnemyTanks to check collisions against
    private List<Tank> enemies;

    // Animation frames for the tank sprite
    private final Image[] frames;
    private int currentFrame = 0;

    // Track whether the tank has fired a bullet
    private boolean isShoot;
    private int currentAngle;  // Direction the tank is facing (in degrees)

    /**
     * Constructor
     * @param spritePaths paths to each frame image
     * @param startX initial x-position
     * @param startY initial y-position
     * @param walls list of wall ImageViews for collision checks
     */
    public Tank(String[] spritePaths, double startX, double startY, List<ImageView> walls) {
        // Load all animation frames
        frames = new Image[spritePaths.length];
        for (int i = 0; i < spritePaths.length; i++) {
            frames[i] = new Image(spritePaths[i]);
        }
        // Initialize the ImageView with the first frame
        view = new ImageView(frames[0]);
        this.walls = walls;

        // Set initial facing direction (up)
        view.setRotate(-90);
        currentAngle = 270;

        // Set starting position
        this.x = startX;
        this.y = startY;
        view.setX(x);
        view.setY(y);

        this.enemies = new ArrayList<>();
    }

    // Getter for the tank's ImageView
    public ImageView getView() {
        return view;
    }

    // Update x-position and move the view
    public void setX(double x) {
        this.x = x;
        this.view.setX(x);
    }

    // Update y-position and move the view
    public void setY(double y) {
        this.y = y;
        this.view.setY(y);
    }

    public boolean isShoot() {
        return isShoot;
    }
    public void setShoot(boolean shoot) {
        isShoot = shoot;
    }
    public void setCurrentAngle(int currentAngle) {
        this.currentAngle = currentAngle;
    }
    public void setEnemies(List<Tank> enemies) {
        this.enemies = enemies;
    }

    /**
     * Fire a bullet if not already shot.
     * @param bulletImage image for the bullet
     * @param root pane to add the bullet to
     * @param bullets list to track active bullets
     * @param walls walls for bullet collision
     * @param enemyTanks tanks for bullet to hit
     */
    public void fire(Image bulletImage, Pane root, List<Bullet> bullets, List<ImageView> walls, List<Tank> enemyTanks) {
        if (isShoot) {
            return;  // Already shot, it can't shoot
        }
        // Calculate center of tank
        double tankW = view.getBoundsInParent().getWidth();
        double tankH = view.getBoundsInParent().getHeight();
        double Xcenter = view.getX() + tankW / 2;
        double Ycenter = view.getY() + tankH / 2;

        // Offset so bullet appears at tank barrel end
        double barrelLength = tankW / 2.0;
        double bulletRadius = bulletImage.getWidth() / 2.0;
        double offset = barrelLength + bulletRadius;
        double angle = currentAngle;

        double startX = Xcenter + Math.cos(Math.toRadians(angle)) * offset - bulletRadius;
        double startY = Ycenter + Math.sin(Math.toRadians(angle)) * offset - bulletRadius;

        // Create and register the bullet
        Bullet b = new Bullet(bulletImage, startX, startY, angle, walls, enemyTanks);
        bullets.add(b);
        root.getChildren().add(b.getView());
    }

    /** Move up (negative y) */
    public void moveForward() {
        setCurrentAngle(270);
        view.setRotate(270);
        if (canMove(0, -speed)) {
            animateAndUpdate(0, -speed);
        }
    }

    /** Move down (positive y) */
    public void moveBackward() {
        setCurrentAngle(90);
        view.setRotate(90);
        if (canMove(0, speed)) {
            animateAndUpdate(0, speed);
        }
    }

    /** Move left (negative x) */
    public void moveLeft() {
        setCurrentAngle(180);
        view.setRotate(180);
        if (canMove(-speed, 0)) {
            animateAndUpdate(-speed, 0);
        }
    }

    /** Move right (positive x) */
    public void moveRight() {
        setCurrentAngle(0);
        view.setRotate(0);
        if (canMove(speed, 0)) {
            animateAndUpdate(speed, 0);
        }
    }

    /**
     * Compute the bounding box after moving by (dx,dy)
     * @param dx change in x
     * @param dy change in y
     * @return predicted bounds
     */
    private Bounds getTargetBounds(double dx, double dy) {
        Bounds cur = view.getBoundsInParent();
        return new BoundingBox(cur.getMinX() + dx, cur.getMinY() + dy, cur.getWidth(), cur.getHeight());
    }

    /**
     * Check collision with walls before moving
     * @param dx change in x
     * @param dy change in y
     * @return true if movement is clear
     */
    private boolean canMove(double dx, double dy) {
        Bounds target = getTargetBounds(dx, dy);
        for (ImageView w : this.walls) {
            if (w.getBoundsInParent().intersects(target)) {
                return false;  // Hit a wall
            }
        }
        for (Tank t : this.enemies) {
            if (t.getView().getBoundsInParent().intersects(target) && !t.isShoot()) return false;
        }
        return true;  // No collision
    }

    /**
     * Cycle sprite frame and update position
     * @param dx change in x
     * @param dy change in y
     */
    private void animateAndUpdate(double dx, double dy) {
        // Advance animation
        currentFrame = (currentFrame + 1) % frames.length;
        view.setImage(frames[currentFrame]);

        // Move tank
        x += dx;
        y += dy;
        view.setX(x);
        view.setY(y);
    }
}