public class ClientConfig {
    public int id;
    public int port;
    public String hostname;

    public ClientConfig(int id, String hostname, int port) {
        this.id = id;
        this.hostname = hostname;
        this.port = port;
    }
}
