/*
 * author: Eden Akins
 * purpose: Creates a System Tray Icon that opens WiFile
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class BackgroundWiFile {
    private static final String imageName = "/wifile.jpg";
    public static WiFile bwWiFile;
    //public static void main(String[] args){
    public BackgroundWiFile(WiFile wlWiFile) {
        bwWiFile = wlWiFile;
        /* Use an appropriate Look and Feel */
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        //Schedule a job for the event-dispatching thread:
        //adding TrayIcon.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        //Check the SystemTray support
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon =
                new TrayIcon(createImage(imageName, "tray icon"));
        final SystemTray tray = SystemTray.getSystemTray();

        //MenuItem caseItem = new MenuItem("Create Support Case");
        MenuItem runItem = new MenuItem("Scan For Services");
        //MenuItem contactItem = new MenuItem("");
        MenuItem exitItem = new MenuItem("Exit WiFile");

        //popup.add(caseItem);
        popup.add(runItem);
        popup.add(exitItem);
        //popup.add(contactItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }

        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
             if(!bwWiFile.mOpen) {
                 bwWiFile = new WiFile();
             }
             else {
                 System.out.println("already open");
             }
            }
        });
        runItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!bwWiFile.mOpen) {
                    bwWiFile = new WiFile();
                }
                else {
                    System.out.println("already open");
                }
            }
        });
        /*contactItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "Akins IT: 1(949)-407-7125");
            }
        });
        caseItem.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
          URI oracle;
    try {
     oracle = new URI("http://www.akinsit.com/create-support-case");
     Desktop.getDesktop().browse(oracle);
    } catch (URISyntaxException e1) {
     // TODO Auto-generated catch block
     e1.printStackTrace();
    } catch (IOException e1) {
     // TODO Auto-generated catch block
     e1.printStackTrace();
    }
            }
        });*/
        //Automatically adjusts the size of the picture
        trayIcon.setImageAutoSize(true);
        //creates Hover Text
        trayIcon.setToolTip("WiFile");

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                System.exit(0);
            }
        });

    }
    
    //Obtain the image URL
    protected static Image createImage(String path, String description) {
        URL imageURL = BackgroundWiFile.class.getResource(imageName);
        
        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}