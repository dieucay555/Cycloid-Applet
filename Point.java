/**
 * A class to hold X,Y coordinates for drawing figures.
 */
class Point {
    // don't need a lot of setter/getters but be careful
    public double X;
    public double Y;

    Point() {
        X = 0.0;
        Y = 0.0;
    }

    Point(double X, double Y) {
        this.X = X;
        this.Y = Y;
    }
}
