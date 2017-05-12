/**
 * Main driver class to test concepts. 
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.MissingResourceException;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

/**
 * @author Rick Huff
 *
 */
public class JVEditor {

	private final static Logger LOG = Logger.getLogger(JVEditor.class);

	// Menus
    private JMenuBar menuBar = null;

    // About Box
    private JDialog aboutBox = null;

    private JFrame frame = null;

    JVEditor() {
//    	GraphicsConfiguration gc = GraphicsEnvironment.
//    			getLocalGraphicsEnvironment().
//    			getDefaultScreenDevice().
//    			getDefaultConfiguration();
//    	frame = createFrame(gc);
		JFrame frame = new JFrame("JVEditor");
        menuBar = createMenus();
        frame.setJMenuBar(menuBar);

        MidiDevice.Info midiInfo[] = MidiSystem.getMidiDeviceInfo();
		/*
		 * Create an Edit/Preferences menu where a user can select the desired MIDI device.
		 */
		JTextArea textArea = new JTextArea(getMidiDevices(midiInfo));
		frame.add(textArea);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 300);
		frame.setVisible(true);
    }
    
    /**
     * Create a frame for JVEDItor to reside in.
     */
    public static JFrame createFrame(GraphicsConfiguration gc) {
        JFrame frame = new JFrame(gc);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
    }

    private JMenuBar createMenus() {
        JMenuItem mi;
        // ***** create the menubar ****
        JMenuBar menuBar = new JMenuBar();
        menuBar.getAccessibleContext().setAccessibleName(
            getString("MenuBar.accessible_description"));

        // ***** create File menu
        JMenu fileMenu = (JMenu) menuBar.add(new JMenu(getString("FileMenu.file_label")));
        fileMenu.setMnemonic(getMnemonic("FileMenu.file_mnemonic"));
        fileMenu.getAccessibleContext().setAccessibleDescription(getString("FileMenu.accessible_description"));

        createMenuItem(fileMenu, "FileMenu.about_label", "FileMenu.about_mnemonic",
                       "FileMenu.about_accessible_description", new AboutAction(this));

        fileMenu.addSeparator();

        createMenuItem(fileMenu, "FileMenu.open_label", "FileMenu.open_mnemonic",
                       "FileMenu.open_accessible_description", null);

        createMenuItem(fileMenu, "FileMenu.save_label", "FileMenu.save_mnemonic",
                       "FileMenu.save_accessible_description", null);

        createMenuItem(fileMenu, "FileMenu.save_as_label", "FileMenu.save_as_mnemonic",
                       "FileMenu.save_as_accessible_description", null);
        return menuBar;
	}

    /**
     * Creates a generic menu item
     */
    public JMenuItem createMenuItem(JMenu menu, String label, String mnemonic,
                               String accessibleDescription, Action action) {
        JMenuItem mi = (JMenuItem) menu.add(new JMenuItem(getString(label)));
        mi.setMnemonic(getMnemonic(mnemonic));
        mi.getAccessibleContext().setAccessibleDescription(getString(accessibleDescription));
        mi.addActionListener(action);
        if(action == null) {
            mi.setEnabled(false);
        }
        return mi;
    }

    /**
     * Returns a mnemonic from the resource bundle. Typically used as
     * keyboard shortcuts in menu items.
     */
    public char getMnemonic(String key) {
        return (getString(key)).charAt(0);
    }

    /**
     * This method returns a string from the demo's resource bundle.
     */
    public String getString(String key) {
        String value = null;
        try {
            value = TextAndMnemonicUtils.getTextAndMnemonicString(key);
        } catch (MissingResourceException e) {
            System.out.println("java.util.MissingResourceException: Couldn't find value for: " + key);
        }
        if(value == null) {
            value = "Could not find resource: " + key + "  ";
        }
        return value;
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JVEditor jve = new JVEditor();
	}
	
	private static String getMidiDevices(Info[] midiInfo) {
		StringBuilder midiDevices = new StringBuilder();
		
		for(MidiDevice.Info info : midiInfo) {
			midiDevices.append(info.getName() + ":" + info.getDescription() + "\n");
			LOG.debug(info);		
		}
		
		return midiDevices.toString();
	}
	
    /**
     * Returns the frame instance
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * Creates an icon from an image contained in the "images" directory.
     */
    public ImageIcon createImageIcon(String filename, String description) {
        String path = "/images/" + filename;
        return new ImageIcon(getClass().getResource(path));
    }

    class AboutAction extends AbstractAction {
        JVEditor jve;
        protected AboutAction(JVEditor jve) {
            super("AboutAction");
            this.jve = jve;
        }

        public void actionPerformed(ActionEvent e) {
            if(aboutBox == null) {
                // JPanel panel = new JPanel(new BorderLayout());
                JPanel panel = new AboutPanel(jve);
                panel.setLayout(new BorderLayout());

                aboutBox = new JDialog(jve.getFrame(), getString("AboutBox.title"), false);
                aboutBox.setResizable(false);
                aboutBox.getContentPane().add(panel, BorderLayout.CENTER);

                // JButton button = new JButton(getString("AboutBox.ok_button_text"));
                JPanel buttonpanel = new JPanel();
                buttonpanel.setBorder(new javax.swing.border.EmptyBorder(0, 0, 3, 0));
                buttonpanel.setOpaque(false);
                JButton button = (JButton) buttonpanel.add(
                    new JButton(getString("AboutBox.ok_button_text"))
                );
                panel.add(buttonpanel, BorderLayout.SOUTH);

                button.addActionListener(new OkAction(aboutBox));
            }
            aboutBox.pack();
            aboutBox.setLocationRelativeTo(getFrame());
            aboutBox.show();
        }
    }

    class OkAction extends AbstractAction {
        JDialog aboutBox;

        protected OkAction(JDialog aboutBox) {
            super("OkAction");
            this.aboutBox = aboutBox;
        }

        public void actionPerformed(ActionEvent e) {
            aboutBox.setVisible(false);
        }
    }

    class AboutPanel extends JPanel {
        ImageIcon aboutimage = null;
        JVEditor jve = null;

        public AboutPanel(JVEditor jve) {
            this.jve = jve;
            aboutimage = jve.createImageIcon("About.jpg", "AboutBox.accessible_description");
            setOpaque(false);
        }

        public void paint(Graphics g) {
            aboutimage.paintIcon(this, g, 0, 0);
            super.paint(g);
        }

        public Dimension getPreferredSize() {
            return new Dimension(aboutimage.getIconWidth(),
                                 aboutimage.getIconHeight());
        }
    }
}