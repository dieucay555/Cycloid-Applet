import java.util.ArrayList;

public class Points {
    private ArrayList<Double> x;
    private ArrayList<Double> y;

    public Points() {
        // default size to 1024 points to minimize resizing the list
        x = new ArrayList<Double>(1024);
        y = new ArrayList<Double>(1024);
    }

    // assumes that no multiple threads will access this obj
    public ArrayList<Double> getX() {
        return x;
    }

    public ArrayList<Double> getY() {
        return y;
    }

    // attempt to minimize resizing of the list
    public void resize(int size) {
        x.ensureCapacity(size*2);
        y.ensureCapacity(size*2);
    }

    public int length() {
        return x.size();
    }

    public void clear() {
        x.clear();
        y.clear();
    }
}
