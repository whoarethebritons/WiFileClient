
import javax.swing.*;
import java.io.File;

/**
 *
 * @author Eden
 * Creates new form FileBrowser that allows the user
 * to select a folder to store their transfers in
 */
public class FileBrowser extends javax.swing.JFrame {
    //variable for returning the folder
    String returnPath;

    public FileBrowser() {
        //initializes jFileChooser
        initComponents();
        //return value for okay/cancel buttons
        int returnVal = chooser.showOpenDialog(getParent());
        //0 is okay
        if(returnVal == 0) {
            //then get the file that was selected
            File file = chooser.getSelectedFile();
            //and set our returnPath variable to it
            returnPath = file.getAbsolutePath();
        }
    }
    //so that Settings can access it
    public String getReturnPath() {
        return returnPath;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        chooser = new javax.swing.JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        chooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooserActionPerformed(evt);
            }
        });
        }// </editor-fold>//GEN-END:initComponents

        private void chooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooserActionPerformed
            // TODO add your handling code here:
            //
        }//GEN-LAST:event_chooserActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser chooser;
    // End of variables declaration//GEN-END:variables
}
