import javax.swing.JApplet;
import javax.swing.SwingUtilities;

public class CycloidApplet extends JApplet {

    // Runs applet in the event dispatch thread
    public void init() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI();
                }
            });
        } catch (Exception e) {
            System.err.println("createGUI didn't complete successfully");
            e.printStackTrace();
        }
    }

    private void createGUI() {
        CycloidPanel newContentPane = new CycloidPanel();
        newContentPane.setOpaque(true);
        setContentPane(newContentPane);
    }
}
