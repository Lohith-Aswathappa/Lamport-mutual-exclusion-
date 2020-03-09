import java.io.*;
import java.util.*;

public class Client {

    public static void main(String[] args) {
        int clientId = Integer.parseInt(args[0]);
        String clientConfigFilePath = args[1];
        String serverConfigFilePath = args[2];
        ClientConfig cb = Client.GetClientDetails(clientConfigFilePath, clientId);
        Client_Lam client = new Client_Lam(cb);
        List<ClientConfig> cbs = Client.getAllClientdata(clientConfigFilePath);
        List<ServerConfig> sbs = Client.getAllServerData(serverConfigFilePath);
        client.startClient(sbs, cbs);
    }

    public static ClientConfig GetClientDetails(String clientConfigFile, int clientId) {
        ClientConfig result = null;
        File f = new File(clientConfigFile);
        if (f.isFile()) {
            BufferedReader read = null;
            FileReader fileread = null;
            try {
                fileread = new FileReader(f);
                read = new BufferedReader(fileread);
                String line = null;
                while ((line = read.readLine()) != null) {
                    String[] splitString = line.split(",");
                    if (splitString.length == 3 && Integer.parseInt(splitString[0]) == clientId) {
                        result = new ClientConfig(Integer.parseInt(splitString[0]), splitString[1], Integer.parseInt(splitString[2]));
                        break;
                    }
                }
            }
            catch(IOException ioEx) {
                ioEx.printStackTrace();
            }
            finally {
                try {
                    if (fileread != null)
                        fileread.close();
                    if(read != null)
                        read.close();
                }
                catch(IOException ioEx) {
                    ioEx.printStackTrace();
                }
            }
        }
        return result;
    }

    public static List<ServerConfig> getAllServerData(String ConfigFileServer) {
        List<ServerConfig> servers = new ArrayList<ServerConfig>();
        File f = new File(ConfigFileServer);
        if (f.isFile()) {
            BufferedReader read = null;
            FileReader fileread = null;
            try {
                fileread = new FileReader(f);
                read = new BufferedReader(fileread);
                String line = null;
                while ((line = read.readLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length == 4) {
                        servers.add(new ServerConfig(Integer.parseInt(tokens[0]), tokens[1], Integer.parseInt(tokens[2]), tokens[3]));
                    }
                }
            }
            catch(IOException ioEx) {
                ioEx.printStackTrace();
            }
            finally {
                try {
                    if (fileread != null)
                        fileread.close();
                    if(read != null)
                        read.close();
                }
                catch(IOException ioEx) {
                    ioEx.printStackTrace();
                }
            }
        }
        return servers;
    }

    public static List<ClientConfig> getAllClientdata(String clientConfigFile) {
        List<ClientConfig> result = new ArrayList<ClientConfig>();
        File f = new File(clientConfigFile);
        if (f.isFile()) {
            BufferedReader reader = null;
            FileReader freader = null;
            try {
                freader = new FileReader(f);
                reader = new BufferedReader(freader);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length == 3) {
                        result.add(new ClientConfig(Integer.parseInt(tokens[0]), tokens[1], Integer.parseInt(tokens[2])));
                    }
                }
            }
            catch(IOException ioEx) {
                ioEx.printStackTrace();
            }
            finally {
                try {
                    if (freader != null)
                        freader.close();
                    if(reader != null)
                        reader.close();
                }
                catch(IOException ioEx) {
                    ioEx.printStackTrace();
                }
            }
        }
        return result;
    }

}
