import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Stores an array of Point objects which represents either cycloid or catenary figure.
 */
class Points implements Iterable<Point>, Iterator<Point> {
    private final ArrayList<Point> points;

    public Points() {
        // default size to 1024 points to minimize resizing the list
        points = new ArrayList<Point>(1024);
    }

    private int count = 0;

    /**
     * Implements iterator
     */
    public boolean hasNext() {
        if (count < points.size()) {
            return true;
        }
        return false;
    }

    public Point next() {
        if (count == points.size()) {
            throw new NoSuchElementException();
        }
        count++;
        return points.get(count-1);
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Implements iterable
     */
    public Iterator<Point> iterator() {
        return this;
    }

    /**
     * Appends Point to the internal array of Points.
     *
     * @param point point to append
     */
    public void addPoint(Point point) {
        points.add(point);
    }

    /**
     * Inserts Point to the internal array of Points.
     *
     * @param index index of the internal array where point will be inserted
     * @param point point to insert
     */
    public void setPoint(int index, Point point) {
        points.set(index, point);
    }

    /**
     * Returns X value of the index'th point in the internal array.
     *
     * Autoboxing to double will happend on its user's side
     * @param index index of the point obj X will be extracted
     * @return the X value of the requested point
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Double getX(int index) {
        return points.get(index).X;
    }

    /**
     * Returns Y value of the index'th point in the internal array.
     *
     * Autoboxing to double will happend on its user's side
     * @param index index of the point obj Y will be extracted
     * @return the Y value of the requested point
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Double getY(int index) {
        return points.get(index).Y;
    }

    /**
     * Increases the capcity of points array at least the twice of the requested.
     *
     * This is mainly to reduce chance of the array being increased dynamically
     * @param size the desired minimum capacity
     */
    public void resize(int size) {
        points.ensureCapacity(size*2);
    }

    /**
     * Returns the length of the points array.
     *
     * @return the number of points residing in the array
     */
    public int length() {
        return points.size();
    }

    /**
     * Returns the subset of the points array.
     *
     * @param fromIndex low endpoint (inclusive) of the array
     * @param toIndex high endpoint (exclusive) of the array
     * @throws IndexOutOfBoundsException endpoint index value out of range
     * @throws IllegalArgumentException if the endpoint indices are out of order
     */
    public List<Point> subList(int fromIndex, int toIndex) throws IndexOutOfBoundsException,
                                                                  IllegalArgumentException
    {
        return points.subList(fromIndex, toIndex);
    }

    public void clear() {
        points.clear();
    }
}
