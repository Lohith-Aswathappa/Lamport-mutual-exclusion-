import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Server {

    public static void main(String[] args) {
        int serverId = Integer.parseInt(args[0]);
        String serverConfigFilePath = args[1];
        int capacity = 5;
        if (args.length > 2) {
            capacity = Integer.parseInt(args[2]);
        }
        ServerConfig sb = Server.GetServerDetails(serverConfigFilePath, serverId);
        ServerLam server = new ServerLam(serverId, sb.host, sb.port, sb.Dir, capacity);
        server.start();
    }

    public static ServerConfig GetServerDetails(String serverConfigFile, int serverId) {
        ServerConfig result = null;
        File f = new File(serverConfigFile);
        if (f.isFile()) {
            BufferedReader reader = null;
            FileReader freader = null;
            try {
                freader = new FileReader(f);
                reader = new BufferedReader(freader);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length == 4 && Integer.parseInt(tokens[0]) == serverId) {
                        result = new ServerConfig(Integer.parseInt(tokens[0]), tokens[1], Integer.parseInt(tokens[2]), tokens[3]);
                        break;
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