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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Eden
 */
public class Loading extends javax.swing.JFrame implements 
                                        PropertyChangeListener{
    InetAddress mIp;
    int mPort, returnPort;
    Client task;
    PortClient other;
    boolean FILE_TRANSFER = false;
    boolean PORT_TRANSFER = true;
    String location;
    boolean whichOne;
    JFrame f = this;
    /**
     * Creates new form Loading
     */
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
        other = new PortClient();
        other.execute();
        try {
            System.out.println(other.get());

            if(other.get() != 0) {
                this.setVisible(true);
                mPort = other.get();
                task = new Client();
                task.addPropertyChangeListener(this);
                task.execute();
            } else {
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

    class Client extends SwingWorker <Void, Void> {
        //int bufferSize = 0;
        
        @Override
        protected Void doInBackground() {//throws Exception {
            System.out.println("ip: " + mIp + " port: " + mPort);
            Socket sock = null;
            DataInputStream sockInput = null;
            DataOutputStream sockOutput = null;
            
            try{
                sock = new Socket(mIp, mPort);
                System.out.println("socket created");
                int received = 0;
                sockInput = new DataInputStream(sock.getInputStream());
                sockOutput = new DataOutputStream(sock.getOutputStream());
                
                //how many received
                sockOutput.writeInt(received);

                //how many files want to be sent
                int whileloop = sockInput.readInt();

                //Socket sock = new Socket(ip, port);
                System.out.println("second one running");
                try{
                    System.out.println("client created");

                    System.out.println("loop " + whileloop);
                    System.out.println("or here 1");
                    int n = 0;

                    System.out.println("or here 2");
                    for(int i = 0; i < whileloop; i++) {
                        //setProgress(0);
                        //long bufferSize = sockInput.readLong();

                        long fileSize = sockInput.readLong();
                        System.out.println(fileSize);
                        byte[] mybytearray = new byte[(int)fileSize];
                        //jProgressBar1.setMaximum((int)fileSize);

                        String filename = sockInput.readUTF();
                        System.out.println(filename);

                        jProgressBar1.setString("Transferring: " + filename);
                        FileOutputStream dfos = new FileOutputStream(location + filename);
                        System.out.println("or here 4");
                        int bytesRead;
                        System.out.println("or here 5");


                        while (fileSize > 0 && (n = sockInput.read(mybytearray, 0, (int)Math.min(mybytearray.length, fileSize))) != -1)
                        {
                            //System.out.println(n);
                            dfos.write(mybytearray,0,n);
                            //System.out.println(((mybytearray.length - fileSize)/mybytearray.length)*100);
                            double percent = ((double)n/(double)mybytearray.length)*100;
                            System.out.println(percent);
                            setProgress(Math.min((int)fileSize, 100));
                            //setProgress(Math.min(mybytearray.length, 100));
                            fileSize -= n;
                        }
                        System.out.println("hereish");
                        dfos.flush();
                        System.out.println("not me");
                        dfos.close();

                        received++;
                        double percent = ((double)received/(double)whileloop) * 100;
                        System.out.println(percent);
                        //setProgress(Math.min((int)percent, 100));
                        System.out.println("me");

                        sockOutput.writeInt(received);
                        System.out.println("or me :(");
                    }
                }
                 finally{
                    System.out.println("I am here");
                }
            }catch(final Exception e) {
                System.out.println("exception caught:" + e.getMessage());
                JOptionPane.showMessageDialog(f,
                        e.getMessage(),
                        "meh error",
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
                
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            
            
            return null;
        }
        @Override
        public void done() {
            dispose();
        }
        
    }
    class PortClient extends SwingWorker <Integer, Void> {

        @Override
        protected Integer doInBackground() throws Exception {
            try{
                Socket sock = new Socket(mIp, mPort);
                System.out.println(sock.getPort());
                System.out.println("socket created");
                InputStream is = null;
                FileOutputStream fos = null;
                DataInputStream sockInput = null;
                System.out.println("first one going");
                    try {
                        System.out.println("I AM HERE");
                        System.out.println("or here");
                        sockInput = new DataInputStream(sock.getInputStream());
                        System.out.println("or here");
                        int input = 0;
                        try {
                            input = sockInput.readInt();
                        }catch(EOFException e) {
                            //server done transferring
                        }
                        returnPort = input;
                        System.out.println(returnPort);
                    } catch (ConnectException e) {
                        System.out.println("connect exception caught");
                        JOptionPane.showMessageDialog(f,
                                "Cannot connect!",
                                "erg error",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        sockInput.close();
                        sock.close();
                    }      
                    }catch(final Exception e) {
                System.out.println("exception caught:" + e.getMessage());
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
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Loading.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Loading.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Loading.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Loading.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and sockInputplay the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Loading().setVisible(true);
            }
        });
    }
    public int getPort() {
        return returnPort;
    }
    
    public javax.swing.JProgressBar getProgress() {
        return jProgressBar1;
    }
    public void propertyChange(PropertyChangeEvent evt) {
        int progress = task.getProgress();
        System.out.println("progress: " + progress);
        jProgressBar1.setValue(progress);
        /*
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();

            jProgressBar1.setValue(progress);
        } */
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fileupdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JProgressBar jProgressBar1;
    // End of variables declaration//GEN-END:variables
}
