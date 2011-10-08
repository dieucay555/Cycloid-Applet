import java.util.ArrayList;

class Points {
    private final ArrayList<Point> points;

    public Points() {
        // default size to 1024 points to minimize resizing the list
        points = new ArrayList<Point>(1024);
    }

    // assumes that no multiple threads will access this obj
    public ArrayList<Point> getPoints() {
        return points;
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public void setPoint(int index, Point point) {
        points.set(index, point);
    }

    public Double getX(int index) {
        return points.get(index).X;
    }

    public Double getY(int index) {
        return points.get(index).Y;
    }

    // attempt to minimize resizing of the list
    public void resize(int size) {
        points.ensureCapacity(size*2);
    }

    public int length() {
        return points.size();
    }

    public void clear() {
        points.clear();
    }
}
