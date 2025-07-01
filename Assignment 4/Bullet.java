import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.List;

public class Bullet {

    // Constant speed at which the bullet travels
    private static final double speed = 5.0;

    // Visual representation of the bullet
    private final ImageView view;

    // Angle (in degrees) at which the bullet moves
    private final double angle;

    // References to walls for collision detection
    private final List<ImageView> walls;

    // References to enemy tanks for collision detection
    private final List<Tank> enemyTanks;

    // Types of collision that can occur
    public enum CollisionType { NONE, WALL, TANK }

    // Last collision detected (initially NONE)
    private CollisionType lastCollision = CollisionType.NONE;

    /**
     * Constructor for Bullet
     * @param img image used to display the bullet
     * @param startX initial x-coordinate for the bullet
     * @param startY initial y-coordinate for the bullet
     * @param angle direction in which the bullet will move
     * @param walls list of wall ImageViews to check collisions
     * @param enemyTanks list of Tanks to check collisions against
     */

    public Bullet(Image img, double startX, double startY, double angle, List<ImageView> walls, List<Tank> enemyTanks) {
        // Create the ImageView and set its rotation and starting position
        this.view = new ImageView(img);
        view.setRotate(angle);
        view.setX(startX);
        view.setY(startY);
        this.angle = angle;
        this.walls = walls;
        this.enemyTanks = enemyTanks;
    }

    // Getter for the bullet's ImageView
    public ImageView getView() {return view;}

    // Getter for bullet speed
    public double getSpeed() {return speed;}

    // Getter for movement angle
    public double getAngle() {return angle;}

    // Getter for the last collision type
    public CollisionType getLastCollision() {return lastCollision;}

    /**
     * Move the bullet forward based on its speed and angle,
     * unless a collision is detected.
     */
    public void move() {
        double dx = speed * Math.cos(Math.toRadians(angle));
        double dy = speed * Math.sin(Math.toRadians(angle));

        if (canMove(dx, dy)) {
            view.setX(view.getX() + dx);
            view.setY(view.getY() + dy);
        }
    }

    /**
     * Check whether the bullet can move by (dx, dy) without hitting a wall or tank.
     * If a collision is detected, record the collision type and handle it.
     * @param dx change in x
     * @param dy change in y
     * @return true if movement is clear; false if a collision occurs
     */
    public boolean canMove(double dx, double dy) {
        Bounds target = getTargetBounds(dx, dy);

        // 1) Wall collision check
        for (ImageView w : walls) {
            if (w.getBoundsInParent().intersects(target)) {
                lastCollision = CollisionType.WALL;
                return false;
            }
        }
        // 2) Tank collision check
        for (Tank t : enemyTanks) {
            // Only register a hit if the tank has not already been hit (isShoot == false)
            if (t.getView().getBoundsInParent().intersects(target) && !t.isShoot()) {
                lastCollision = CollisionType.TANK;

                // If it's an EnemyTank, remove it from the list
                if (t instanceof EnemyTank) {
                    enemyTanks.remove(t);
                }
                // Mark the tank as hit
                t.setShoot(true);
                return false;
            }
        }
        // No collision detected
        return true;
    }

    /**
     * Compute the bounding box the bullet would occupy after moving by (dx, dy).
     * @param dx change in x
     * @param dy change in y
     * @return predicted bounds for collision checks
     */
    private Bounds getTargetBounds(double dx, double dy) {
        Bounds cur = view.getBoundsInParent();
        return new BoundingBox(cur.getMinX() + dx, cur.getMinY() + dy, cur.getWidth(), cur.getHeight());
    }
}