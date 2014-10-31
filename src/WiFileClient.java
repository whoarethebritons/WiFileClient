import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;


public class WiFileClient {
    public WiFileClient(InetAddress inIP, int inPort) {
        try{
            System.out.println("client created");
            Socket sock = new Socket(inIP, inPort);
            int bufferSize=sock.getReceiveBufferSize();
            byte[] mybytearray = new byte[bufferSize];
            InputStream is = sock.getInputStream();
            FileOutputStream fos = new FileOutputStream("s.jpg");
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int bytesRead = is.read(mybytearray, 0, mybytearray.length);
            bos.write(mybytearray, 0, bytesRead);
            bos.close();
            sock.close();
        }
        catch(IOException e) {
            System.out.println("Connection refused.");
        }
    }
}