import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.*;

public class HandleServer_Lam extends Thread {
    ServerLam server;
    private Socket socket;

    public HandleServer_Lam(ServerLam server, Socket sock) {
        this.server = server;
        this.socket = sock;
    }
    private void closeResources(Socket socket, BufferedReader socketReader, BufferedWriter socketWriter, InputStreamReader socketInputReader, OutputStreamWriter socketOutputReader) throws IOException {
        if (socketReader != null)
            socketReader.close();
        if (socketInputReader != null)
            socketInputReader.close();
        if (socketOutputReader != null)
            socketOutputReader.close();
        if (socketWriter != null)
            socketWriter.close();
        if (socket != null)
            socket.close();
    }
    @Override
    public void run() {
        BufferedReader socketBR = null;
        InputStreamReader inputSIR = null;
        BufferedWriter socketBW = null;
        OutputStreamWriter outputSIR = null;
        try {
            inputSIR = new InputStreamReader(this.socket.getInputStream());
            socketBR = new BufferedReader(inputSIR);
            outputSIR = new OutputStreamWriter(this.socket.getOutputStream());
            socketBW = new BufferedWriter(outputSIR);
        }
        catch(IOException ex) {
            ex.printStackTrace();
            return;
        }
        while(true) {
            try {
                String request = socketBR.readLine();
                System.out.println("Received request to enter critical section: "+request);
                if ((request == null) || (request.equalsIgnoreCase(Untility_Lam.Command_Terminate))) {
                    this.closeResources(socket, socketBR, socketBW, inputSIR, outputSIR);
                    return;
                }
                else {
                    String[] splitStrings = request.split(" ");
                    if (splitStrings[0].equalsIgnoreCase(Untility_Lam.command_Enquire)) {
                        List<String> files = ServerUtil.getInstance().getAllFiles(this.server.getRootDirectory());
                        Iterator<String> fIterator = files.iterator();
                        StringBuilder allFiles = new StringBuilder();
                        while(fIterator.hasNext()) {
                            allFiles.append(fIterator.next());
                            if(fIterator.hasNext()) {
                                allFiles.append(";");
                            }
                        }
                        Untility_Lam.write(socketBW, allFiles.toString());
                    }
                    else if (splitStrings[0].equalsIgnoreCase(Untility_Lam.command_Read)) {
                        String fnames = null;
                        try {
                            fnames = splitStrings[1];
                            fnames = Paths.get(this.server.getRootDirectory(),fnames).toString();
                        }
                        catch(ArrayIndexOutOfBoundsException outOfBoundsExInstance) {
                            outOfBoundsExInstance.printStackTrace();
                        }
                        if(fnames != null) {
                            Untility_Lam.write(socketBW, ServerUtil.getInstance().readFile(fnames));
                        }
                        else {
                            Untility_Lam.write(socketBW, Untility_Lam.PrintIfFailure);
                        }
                    }
                    else if (splitStrings[0].equalsIgnoreCase(Untility_Lam.Command_Write)) {
                        String fileName = null;
                        String content = null;
                        try {
                            fileName = splitStrings[1];
                            fileName = Paths.get(this.server.getRootDirectory(),fileName).toString();
                            content = splitStrings[2];
                        }
                        catch(ArrayIndexOutOfBoundsException outOfBoundsEx) {
                            outOfBoundsEx.printStackTrace();
                        }
                        if(ServerUtil.getInstance().appendToFile(fileName, content)) {
                            Untility_Lam.write(socketBW, Untility_Lam.PrintIfSuccess);
                        }
                        else {
                            Untility_Lam.write(socketBW, Untility_Lam.PrintIfFailure);
                        }
                    }
                }
            }
            catch(IOException ex) {
                ex.printStackTrace();
                try {
                    this.closeResources(socket, socketBR, socketBW, inputSIR, outputSIR);
                }
                catch(IOException io_ex) {
                    io_ex.printStackTrace();
                }
                return;
            }
        }
    }
}
