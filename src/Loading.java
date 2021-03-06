import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eden
 * Two background tasks, one to get port of server
 * one to transfer files
 */
public class Loading extends javax.swing.JFrame implements 
                                        PropertyChangeListener{
    //file transfer variables
    InetAddress mIp;
    int mPort, returnPort;
    Client task;
    PortClient other;
    String location;
    boolean whichOne;

    //for use with dialog boxes
    JFrame f = this;

    public Loading() {
        initComponents();
    }
    
    public Loading(InetAddress inIP, int inPort, boolean inBool) {
        mIp = inIP;
        mPort = inPort;
        whichOne = inBool;
        location = WiFile.mProperties.getProperty(WiFile.FILE_KEY, "");
        if(!location.endsWith("\\") && !location.equals("")) {
            location += "\\";
        }
        initComponents();

        //gets port from phone
        other = new PortClient();
        other.execute();


        try {
            //if the port is not zero, port transfer successful
            if(other.get() != 0) {
                //progress bar visible
                this.setVisible(true);
                mPort = other.get();
                //client transfers files
                task = new Client();
                task.addPropertyChangeListener(this);
                task.execute();
            } else {
                //if port was 0 then phone denied access
                JOptionPane.showMessageDialog(f,
                        "Connection Denied",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Loading.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(Loading.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        fileupdate = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(400, 156));

        jLabel1.setText("Transferring Files");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fileupdate)
                .addContainerGap(256, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                    .addComponent(fileupdate))
                .addGap(18, 18, 18)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //SwingWorker to transfer files
    class Client extends SwingWorker <Void, Void> {
        
        @Override
        protected Void doInBackground() {
            Socket sock = null;
            DataInputStream sockInput = null;
            DataOutputStream sockOutput = null;
            
            try{
                //make new socket
                sock = new Socket(mIp, mPort);
                int received = 0;

                //get streams
                sockInput = new DataInputStream(sock.getInputStream());
                sockOutput = new DataOutputStream(sock.getOutputStream());
                
                //how many received
                sockOutput.writeInt(received);

                //how many files want to be sent
                int loop = sockInput.readInt();

                int n = 0;

                //for however many files server wants to send,
                //do this each time
                for (int i = 0; i < loop; i++) {
                    //file size
                    long fileSize = sockInput.readLong();
                    //byte array to hold file bytes
                    byte[] mybytearray = new byte[(int) fileSize];

                    //file name
                    String filename = sockInput.readUTF();

                    //file output stream (with location and name)
                    FileOutputStream dfos = new FileOutputStream(location + filename);

                    while (fileSize > 0 && (n = sockInput.read(mybytearray, 0,
                            (int) Math.min(mybytearray.length, fileSize))) != -1) {
                        //writes bytes to array
                        dfos.write(mybytearray, 0, n);
                        fileSize -= n;
                    }
                    //flushes and closes fileoutputstream
                    dfos.flush();
                    dfos.close();

                    //we have now received a file, increment
                    received++;

                    //update progress bar
                    setProgress(Math.min((int)((double)received/(double)loop)*100, 100));

                    //and send to server
                    sockOutput.writeInt(received);
                }

            }catch(final Exception e) {
                JOptionPane.showMessageDialog(f,
                        e.getMessage(),
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            finally {
                try {
                    sock.close();
                    sockInput.close();
                    sockOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            return null;
        }
        //when swingworker finishes, get rid of progress bar
        @Override
        public void done() {
            dispose();
        }
        
    }
    class PortClient extends SwingWorker <Integer, Void> {

        @Override
        protected Integer doInBackground() throws Exception {
            try{
                //make socket
                Socket sock = new Socket(mIp, mPort);
                DataInputStream sockInput = null;
                try {
                    //get socket input
                    sockInput = new DataInputStream(sock.getInputStream());
                    //if port doesn't send over will return 0
                    //which means client will not connect
                    int input = 0;
                    try {
                        //get port
                        input = sockInput.readInt();
                    }catch(EOFException e) {
                        //server done transferring
                    }
                    returnPort = input;
                } catch (ConnectException e) {
                    JOptionPane.showMessageDialog(f,
                            "Cannot connect!",
                            "Connection Error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    sockInput.close();
                    sock.close();
                }
            }catch(final Exception e) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        JOptionPane.showMessageDialog(f,
                                e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
            return returnPort;
//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    public int getPort() {
        return returnPort;
    }
    
    public javax.swing.JProgressBar getProgress() {
        return jProgressBar1;
    }

    //progress bar updates
    public void propertyChange(PropertyChangeEvent evt) {
        int progress = task.getProgress();
        jProgressBar1.setValue(progress);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fileupdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JProgressBar jProgressBar1;
    // End of variables declaration//GEN-END:variables
}
