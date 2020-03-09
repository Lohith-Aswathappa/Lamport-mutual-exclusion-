import java.io.*;
import java.net.*;
import java.util.*;

public class Client_Lam {
    private ClientConfig myClient;
    private HashMap<String, Queue<Request>> queues;
    public HashMap<Integer, Socket> SocketClient;
    public long clock;
    public int rcount, DClock;
    public boolean inCS;	// for checking is the process is in Critical Section

    private class Request implements Comparable<Request>{
        public long timeStamp;
        public int id;
        public Request(int id, long timestamp) {
            this.id = id;
            this.timeStamp = timestamp;
        }
        @Override
        public int compareTo(Request x) {
            if (x.timeStamp < this.timeStamp) {
                return 1;
            }
            else if (x.timeStamp == this.timeStamp) {
                if (x.id < this.id) {
                    return 1;
                }
                else {
                    return -1;
                }
            }
            else {
                return -1;
            }
        }

        @Override
        public boolean equals(Object req) {
            return (this.id == ((Request)req).id);
        }
    }

    public void printQueue() {
        Iterator<String> it = this.queues.keySet().iterator();
        while(it.hasNext()) {
            String f = it.next();
            System.out.println(f);
            Queue<Request> q = this.queues.get(f);
            Iterator<Request> itTmp = q.iterator();
            while(itTmp.hasNext()) {
                Request r = itTmp.next();
                System.out.print("File Id is : " + r.id+ ":" + "time stamp is : " + r.timeStamp +" ");
            }
            System.out.println("======");
        }
    }

    public int getPort() {
        return this.myClient.port;
    }

    public void startClient(List<ServerConfig> servers, List<ClientConfig> allClients) {
        Client_Lam that = this;
        Thread clientHandlerThread = new Thread () {
            @Override
            public synchronized void run() {
                Socket s;
                ServerSocket ss;
                List<HandleClient_Lam> clients = new ArrayList<HandleClient_Lam>();
                try {
                    ss = new ServerSocket(that.myClient.port);
                    int TotalClientsExceptCurrent = allClients.size() - 1;
                    while(TotalClientsExceptCurrent > 0) {
                        s = ss.accept();
                        HandleClient_Lam handlerTmp = new HandleClient_Lam(that, Untility_Lam.PEER_TO_PEER_CONN, s);
                        handlerTmp.start();
                        clients.add(handlerTmp);
                        TotalClientsExceptCurrent--;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Iterator<HandleClient_Lam> it = clients.iterator();
                while(it.hasNext()) {
                    HandleClient_Lam client = it.next();
                    try {
                        client.join();
                    }
                    catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        clientHandlerThread.start();
        try {
            Thread.sleep(10000);
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
        // connect to other clients
        Iterator<ClientConfig> clientIt = allClients.iterator();
        while(clientIt.hasNext()) {
            ClientConfig client = clientIt.next();
            if (client.id != this.myClient.id) {
                try {
                    this.SocketClient.put(client.id, new Socket(client.hostname, client.port));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Socket[] sockets = new Socket[servers.size()];
        try {
            Iterator<ServerConfig> it = servers.iterator();
            int i = 0;
            while (it.hasNext()) {
                ServerConfig sb = it.next();
                sockets[i++] = new Socket(sb.host, sb.port);
            }
        }
        catch(IOException ioEx) {
            ioEx.printStackTrace();
        }
        HandleClient_Lam serverHandlerThread = new HandleClient_Lam(this, Untility_Lam.CLIENT_SERVER_CONN, sockets);
        serverHandlerThread.start();

        try {
            clientHandlerThread.join();
            serverHandlerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Integer headOfQueue(String file) {
        if(!this.queues.containsKey(file)) {
            return null;
        }
        Queue<Request> queue = this.queues.get(file);
        if (queue == null) {
            return null;
        }
        Request nextRequest = queue.peek();
        if(nextRequest == null) {
            return null;
        }
        return nextRequest.id;
    }

    public int getId() {
        return this.myClient.id;
    }

    public void enqueue(String file, int id, long timestamp) {
        if(!this.queues.containsKey(file)) {
            this.queues.put(file, new PriorityQueue<Request>());
        }
        Request rTmp = new Request(id, timestamp);
        Queue<Request> queue = this.queues.get(file);
        queue.add(rTmp);
    }
    public boolean amIInQueue(String file, int id) {
        boolean isThere = false;
        if(this.queues.containsKey(file)) {
            Queue<Request> q = this.queues.get(file);
            return q.contains(id);
        }
        return isThere;
    }

    public void dequeue(String file) {
        if(this.queues.containsKey(file)) {
            Queue<Request> queue = this.queues.get(file);
            if (queue != null) {
                queue.poll();
            }
        }
    }

    public Client_Lam(ClientConfig cb) {
        this.myClient = cb;
        this.queues = new HashMap<String, Queue<Request>>();
        this.SocketClient = new HashMap<Integer, Socket>();
        this.clock = 0;
        this.rcount = 1;
        this.DClock = 1;
        this.inCS = false;
    }
}
