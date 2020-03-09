import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class Untility_Lam {
    public static int CLIENT_SERVER_CONN = 1;
    public static int PEER_TO_PEER_CONN = 2;

    public static String command_Read = "read";
    public static String Command_Write = "write";
    public static String command_Enquire = "enquire";
    public static String Command_Terminate = "terminate";
    public static String Command_Request = "request";
    public static String Command_Release = "release";
    public static String Command_Reply = "reply";

    public static String PrintIfSuccess = "success";
    public static String PrintIfFailure = "failure";
    public static String[] CMD_W_R = {Untility_Lam.command_Read, Untility_Lam.Command_Write};

    public static void write(BufferedWriter bw, String message) throws IOException {
        bw.write(message);
        bw.newLine();
        bw.flush();
    }
    public static String read(BufferedReader br) throws IOException {
        return br.readLine();
    }
}
