/**
 * Created by Eden on 10/15/2014.
 */

import javax.jmdns.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WiFileService {
      JmDNS mJmdns;
      static ArrayList<ServerInfo> servicesfound = new ArrayList<ServerInfo>();
      public static class ServerInfo{
            String mType;
            String mName;
            String mIP;
            int mPort;
            public ServerInfo(String inType, String inName, String inIP, int inPort) {
                mType = inType;
                mName = inName;
                mIP = inIP;
                mPort = inPort;
            }
            public boolean has(String s) {
                if (mName.contains(s)) {
                    return true;
                } else {
                    return false;
                }
            }
            public String toString() {
                return mIP + "|" + mPort;
            }
        };

      static class ServListener implements ServiceListener, ServiceTypeListener {
          JmDNS jmdns;

          public ServListener(JmDNS j) {
              jmdns = j;
          }

          public ArrayList<ServerInfo> getinf() {
              ArrayList<ServerInfo> found = new ArrayList<ServerInfo>();
              for (ServerInfo s : servicesfound) {
                  if (s.has("NsdWiFile")) {
                      found.add(s);
                  }
              }
              return found;
          }


          @Override
          public void serviceAdded(ServiceEvent event) {
              System.out.println("Service added   : " + event.getName() + "." + event.getType());
              jmdns.requestServiceInfo(event.getType(), event.getName());
          }

          @Override
          public void serviceRemoved(ServiceEvent event) {
              System.out.println("Service removed : " + event.getName() + "." + event.getType());
          }

          @Override
          public void serviceResolved(ServiceEvent event) {
              try {
                  System.out.println("Service resolved: " + event.getInfo());
                  String name = event.getName();
                  String type = event.getType();
                  ServiceInfo info = event.getInfo();
                  String ret;
                  if (info == null) {
                      ret = "service not found";
                      System.out.println(ret);
                  } else {
                      //ServerInfo s = new ServerInfo(name, type, info.getInetAddress().toString(), info.getPort());
                      /*
                      StringBuilder buf = new StringBuilder();
                      buf.append(name);
                      buf.append('.');
                      buf.append(type);
                      buf.append('\n');
                      buf.append(info.getServer());
                      buf.append(':');
                      buf.append(info.getPort());
                      buf.append('\n');
                      buf.append(info.getInetAddress());
                      buf.append(':');
                      buf.append(info.getPort());
                      buf.append('\n');

                      for (Enumeration names = info.getPropertyNames() ; names.hasMoreElements() ; ) {
                          String prop = (String)names.nextElement();
                          buf.append(prop);
                          buf.append('=');
                          buf.append(info.getPropertyString(prop));
                          buf.append('\n');
                      }

                      ret = buf.toString();
                      */
                      ServerInfo s = new ServerInfo(name, type, info.getInetAddress().toString(), info.getPort());
                      servicesfound.add(s);
                      //System.out.println(s);
                      System.out.println(name + '.' + type + '\n' + info.getServer() + ':' + info.getPort() + '\n' +
                              info.getInetAddress().toString() + ':' + info.getPort());

                      //ServersInfo(info.getAddress(), info.getPort());
                      try {
                          Socket sock = new Socket(info.getAddress(), info.getPort());

                          DataInputStream is = new DataInputStream(sock.getInputStream());
                          int input = is.readInt();
                          System.out.println(input);
                          //WiFileClient wfc = new WiFileClient(inIP, input);
                          System.out.println("success");
                          sock.close();
                      } catch (IOException e) {
                          e.printStackTrace();
                      }
                  }
              } catch (StringIndexOutOfBoundsException e) {
                  e.printStackTrace();
              }

          }

          public void serviceTypeAdded(ServiceEvent event) {
              String type = event.getType();

              System.out.println("TYPE: " + type);
              //String type = event.getType();
          }

          public void subTypeForServiceTypeAdded(ServiceEvent event) {

          }

          public void ServersInfo(InetAddress inIP, int inPort) {

          }
      }
    public ServiceInfo registerWiFile() {
        try {
            ServerSocket s = new ServerSocket(0);
            int wfPort = s.getLocalPort();
            return ServiceInfo.create("_ftp._tcp.", "DNS-WiFile", wfPort,
                    "computer WiFile service");
        } catch (IOException e) {
            return null;
        }
    }
    public JmDNS getjm() {
        return mJmdns;
    }
    public static void main(String[] args) {
        try {

            // Activate these lines to see log messages of JmDNS
            boolean log = true;
            if (log) {
                Logger logger = Logger.getLogger(JmDNS.class.getName());
                ConsoleHandler handler = new ConsoleHandler();
                logger.addHandler(handler);
                logger.setLevel(Level.FINER);
                handler.setLevel(Level.FINER);
            }

            final WiFileService wf = new WiFileService();
            InetAddress mIP = InetAddress.getLocalHost();
            String mHostname = InetAddress.getByName(mIP.getHostName()).toString();
            System.out.println(mIP.toString());

            JmDNS jmdns = JmDNS.create(mIP, mHostname);
            wf.mJmdns = jmdns;

            //jmdns.registerServiceType("_ftp._tcp.");

            ServListener sl = new ServListener(jmdns);
            //jmdns.registerServiceType("_ftp._tcp.");
            wf.mJmdns.addServiceListener("_ftp._tcp", sl);

            //jmdns.addServiceTypeListener(sl);
            //jmdns.registerService(wf.registerWiFile());
            /*
            String list[] = new String[] {
                    "_http._tcp.local.",
                    "_ftp._tcp.local.",
                    "_tftp._tcp.local.",
                    "_ssh._tcp.local.",
                    "_smb._tcp.local.",
                    "_printer._tcp.local.",
                    "_airport._tcp.local.",
                    "_afpovertcp._tcp.local.",
                    "_ichat._tcp.local.",
                    "_eppc._tcp.local.",
                    "_presence._tcp.local."
            };

            for (int i = 0 ; i < list.length ; i++) {
                wf.mJmdns.addServiceListener(list[i],sl);
            }
            */
            Runtime.getRuntime().addShutdownHook(new Thread()
            {
                @Override
                public void run()
                {
                    System.out.println("Shutdown hook ran!");
                    try {
                        wf.getjm().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            System.out.println("Press q and Enter, to quit");
            int b;
            while ((b = System.in.read()) != -1 || (char) b != 'q') {
                /* Stub */
                //jmdns.addServiceListener("_ftp._tcp.", sl);
            }
            jmdns.close();
            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
