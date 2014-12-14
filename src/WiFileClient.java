import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;


public class WiFileClient {
    public WiFileClient(InetAddress inIP, int inPort) {
        try{
            Socket sock = new Socket(inIP, inPort);
            InputStream is = null;
            FileOutputStream fos = null;
            //Socket sock = new Socket(ip, port);
            try{
                int bufferSize=sock.getReceiveBufferSize();
                byte[] mybytearray = new byte[bufferSize];
                is = sock.getInputStream();
                fos = new FileOutputStream("C:/Eden/Documents/s.png");
                int bytesRead;
                while( (bytesRead = is.read(mybytearray)) > 0) {
                    fos.write(mybytearray, 0, bytesRead);
                }
            } 
             finally{
                fos.close();
                is.close();
                sock.close();
            }
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}