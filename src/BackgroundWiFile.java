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
    public BackgroundWiFile(WiFile wlWiFile) {
        bwWiFile = wlWiFile;
        /* Use an appropriate Look and Feel */
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            if(UIManager.getLookAndFeel() == null) {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            }
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

        MenuItem runItem = new MenuItem("Scan For Services");
        MenuItem exitItem = new MenuItem("Exit WiFile");

        popup.add(runItem);
        popup.add(exitItem);

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