import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServerLam {
    private int id = -1;
    private String host = null;
    private String root = null;
    private int port = -1, capacity = 5;

    public ServerLam(int id, String hostname, int port, String rootDir, int capacity) {
        this.id = id;
        this.host = hostname;
        this.port = port;
        this.root = rootDir;
        this.capacity = capacity;
    }

    public int getPort() {
        return this.port;
    }
    public String getRootDirectory() {
        return this.root;
    }

    public void start() {
        if (this.port != -1) {
            Socket socket = null;
            ServerSocket LamSocket = null;
            try {
                LamSocket = new ServerSocket(this.port);
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
            int count = this.capacity;
            List<HandleServer_Lam> handlers = new ArrayList<HandleServer_Lam>(count);
            while(count > 0) {
                try {
                    System.out.println("Started Server "+this.host+" Listening at port : "+this.port);
                    socket = LamSocket.accept();
                }
                catch(IOException ex) {
                    ex.printStackTrace();
                }
                HandleServer_Lam Lam_Handler = new HandleServer_Lam(this, socket);
                Lam_Handler.start();
                handlers.add(Lam_Handler);
                count -= 1;
            }
            System.out.println("All 3 servers started!!");
            Iterator<HandleServer_Lam> handlerIterator = handlers.iterator();
            while(handlerIterator.hasNext()) {
                HandleServer_Lam h = handlerIterator.next();
                try {
                    h.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                LamSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String getHostName() {
        return this.host;
    }

}
