

public class ServerConfig {
    public int id;
    public int port;
    public String host, Dir;

    public ServerConfig() {}
    public ServerConfig(int id, String hostname, int port, String rootDir ) {
        this.id = id;
        this.host = hostname;
        this.port = port;
        this.Dir = rootDir;
    }
}

