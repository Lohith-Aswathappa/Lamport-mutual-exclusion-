import java.io.*;
import java.net.Socket;
import java.util.Iterator;
import java.util.Random;

public class HandleClient_Lam extends Thread {
    private Socket[] serverSoc;
    private Socket clientSoc;
    private Client_Lam client;
    private int TypeOfConnection;

    public HandleClient_Lam(Client_Lam client, int type, Socket[] sockets) {
        this.client = client;
        this.TypeOfConnection = type;
        this.serverSoc = sockets;
    }

    public HandleClient_Lam(Client_Lam client, int type, Socket socket) {
        this.client = client;
        this.TypeOfConnection = type;
        this.clientSoc = socket;
    }

    public void run() {
        if (this.TypeOfConnection == Untility_Lam.CLIENT_SERVER_CONN) {
            this.clientServerProcess();
        }
        else if (this.TypeOfConnection == Untility_Lam.PEER_TO_PEER_CONN) {
            this.clientClientProcess();
        }
    }

    private void broadcastToClients(String command, String file) throws IOException {
        Iterator<Integer> clients = this.client.SocketClient.keySet().iterator();
        while(clients.hasNext()) {
            Integer cTemp = clients.next();
            OutputStreamWriter osr = new OutputStreamWriter(this.client.SocketClient.get(cTemp).getOutputStream());
            BufferedWriter br = new BufferedWriter(osr);
            Untility_Lam.write(br, command+" "+file+" "+this.client.getId()+" "+this.client.clock);
        }
    }
    private boolean checkIfIamNext(String file) {
        if (this.client.headOfQueue(file) == null || this.client.headOfQueue(file) == this.client.getId()) {
            return true;
        }
        else {
            return false;
        }
    }

    private void clientServerProcess() {
        BufferedReader[] sBufferedReader = new BufferedReader[this.serverSoc.length];
        InputStreamReader[] socInputStreamReader = new InputStreamReader[this.serverSoc.length];
        BufferedWriter[] socBufferedWriter = new BufferedWriter[this.serverSoc.length];
        OutputStreamWriter[] socOutputStreamWriter = new OutputStreamWriter[this.serverSoc.length];
        try {
            for (int i = 0; i < this.serverSoc.length ; i++) {
                socInputStreamReader[i] = new InputStreamReader(this.serverSoc[i].getInputStream());
                sBufferedReader[i] = new BufferedReader(socInputStreamReader[i]);
                socOutputStreamWriter[i] = new OutputStreamWriter(this.serverSoc[i].getOutputStream());
                socBufferedWriter[i] = new BufferedWriter(socOutputStreamWriter[i]);
            }
        }
        catch(IOException ex) {
            ex.printStackTrace();
            return;
        }
        Random r = new Random();
        String[] files;
        try {
            Untility_Lam.write(socBufferedWriter[0], Untility_Lam.command_Enquire);
            String file = Untility_Lam.read(sBufferedReader[0]);
            System.out.println(file);
            files = file.split(";");
        }
        catch(IOException ioEx) {
            ioEx.printStackTrace();
            return;
        }

        try {
            Thread.sleep(r.nextInt(5)*1000);
            while(true) {
                int server_idx = r.nextInt(this.serverSoc.length);
                int cmd_index = r.nextInt(Untility_Lam.CMD_W_R.length);
                int file_index = r.nextInt(files.length);
                while(this.client.rcount != 1 || this.client.amIInQueue(files[file_index], this.client.getId())) {
                    Thread.sleep(500);
                }
                {
                    this.client.clock += this.client.DClock;
                    this.broadcastToClients(Untility_Lam.Command_Request, files[file_index]);
                    this.client.enqueue(files[file_index], this.client.getId(), this.client.clock);
                    while(this.client.rcount <= this.client.SocketClient.keySet().size() && !this.checkIfIamNext(files[file_index])) {
                        System.out.println("Other client is Critical Section... wait...");
                        Thread.sleep(1000);
                    }
                }
                if (Untility_Lam.CMD_W_R[cmd_index].equals(Untility_Lam.command_Read)) {
                    System.out.println("Reading file: "+files[file_index]);
                    this.client.clock += this.client.DClock;
                    Untility_Lam.write(socBufferedWriter[server_idx], Untility_Lam.command_Read +" "+files[file_index]+" "+this.client.getId());
                    String message = Untility_Lam.read(sBufferedReader[server_idx]);
                    System.out.println(message);
                }
                else if (Untility_Lam.CMD_W_R[cmd_index].equals(Untility_Lam.Command_Write)) {
                    this.client.clock += this.client.DClock;
                    System.out.println("Writing to file file: "+files[file_index]+", Client Id: "+this.client.getId()+"TimeStamp"+this.client.clock);
                    for (BufferedWriter bf: socBufferedWriter) {
                        Untility_Lam.write(bf, Untility_Lam.Command_Write +" "+files[file_index]+" "+this.client.getId()+":"+this.client.clock);
                    }
                    for (BufferedReader br: sBufferedReader) {
                        Untility_Lam.read(br);
                    }
                }
                Thread.sleep(2000);
                this.client.clock += this.client.DClock;
                this.broadcastToClients(Untility_Lam.Command_Release, files[file_index]);
                this.client.rcount = 1;
                Integer processIdAtHead = this.client.headOfQueue(files[file_index]);
                if(processIdAtHead != null && processIdAtHead == this.client.getId()) {
                    this.client.dequeue(files[file_index]);
                }
                System.out.println("Released "+files[file_index]);
//				this.client.printQueue();
            }
        }
        catch(IOException ioEx) {
            ioEx.printStackTrace();
            return;
        }
        catch(InterruptedException interruptedEx) {
            interruptedEx.printStackTrace();
        }
    }

    private void clientClientProcess() {
        InputStreamReader clientISR = null;
        BufferedReader clientBR = null;
        OutputStreamWriter clientOSW = null;
        BufferedWriter	clientBW = null;
        try {
            clientISR = new InputStreamReader(this.clientSoc.getInputStream());
            clientBR  = new BufferedReader(clientISR);
            clientOSW = new OutputStreamWriter(this.clientSoc.getOutputStream());
            clientBW = new BufferedWriter(clientOSW);

            while(true) {
                String line = clientBR.readLine();
                if (line != null) {
                    String[] SplitStrings = line.split(" ");

                    if (Untility_Lam.Command_Request.equalsIgnoreCase(SplitStrings[0])) {
                        String file = SplitStrings[1];
                        String process = SplitStrings[2];
                        String timestamp = SplitStrings[3];
                        this.client.clock = Math.max(Long.parseLong(timestamp), this.client.clock) + this.client.DClock;
                        this.client.enqueue(file, Integer.parseInt(process), Long.parseLong(timestamp));
                        this.client.clock += this.client.DClock;
                        Untility_Lam.write(clientBW, Untility_Lam.Command_Reply +" "+this.client.clock);
                    }
                    else if (Untility_Lam.Command_Release.equalsIgnoreCase(SplitStrings[0])) {
//						System.out.println("Received RELEASE "+line);
                        String file = SplitStrings[1];
                        String process = SplitStrings[2];
                        String timestamp = SplitStrings[3];
                        this.client.clock = Math.max(Long.parseLong(timestamp), this.client.clock) + this.client.DClock;
                        Integer processIdAtHead = this.client.headOfQueue(file);
                        if(processIdAtHead != null && processIdAtHead == Integer.parseInt(process) ) {
                            this.client.dequeue(file);
                        }
//						this.client.printQueue();
                    }
                    else if (Untility_Lam.Command_Reply.equalsIgnoreCase(SplitStrings[0])) {
                        String timestamp = SplitStrings[1];
                        this.client.clock = Math.max(Long.parseLong(timestamp), this.client.clock) + this.client.DClock;
                        this.client.rcount += 1;
                    }
                }
            }
        } catch (IOException e) {
            try {
                if(clientISR != null) {
                    clientISR.close();
                }
                if (clientBR != null) {
                    clientBR.close();
                }
                e.printStackTrace();
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
