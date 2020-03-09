import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServerUtil {
    private static ServerUtil serverUtil;

    private ServerUtil() {}

    synchronized public static ServerUtil getInstance() {
        if (serverUtil == null) {
            serverUtil = new ServerUtil();
        }
        return serverUtil;
    }

    public List<String> getAllFiles(String rootDirectory) {
        final File DirRoot = new File(rootDirectory);
        List<String> allFiles = new ArrayList<String>();
        if (DirRoot.isDirectory()) {
            for (final File file: DirRoot.listFiles()) {
                allFiles.add(file.getName());
            }
        }
        return allFiles;
    }

    public boolean appendToFile(String file, String content) {
        final File f = new File(file);
        boolean writeFile = true;
        if (!f.isDirectory()) {
            BufferedWriter writer = null;
            FileWriter fwriter = null;
            try {
                fwriter = new FileWriter(f, true);
                writer = new BufferedWriter(fwriter);
                Untility_Lam.write(writer, content);
            }
            catch(IOException io_ex) {
                writeFile = false;
                io_ex.printStackTrace();
            }
            finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                    if(fwriter != null) {
                        fwriter.close();
                    }
                }
                catch(IOException io_ex) {
                    io_ex.printStackTrace();
                }
            }
        }
        return writeFile;
    }

    public String readFile(String file) {
        final File f = new File(file);
        String LineLast = "";
        if (!f.isDirectory()) {
            BufferedReader readerBW = null;
            FileReader readFile = null;
            try {
                readFile = new FileReader(f);
                readerBW = new BufferedReader(readFile);
                String line;
                while((line = readerBW.readLine()) != null) {
                    LineLast = line;
                }
            }
            catch(IOException io_ex) {
                io_ex.printStackTrace();
            }
            finally {
                try {
                    if (readerBW != null) {
                        readerBW.close();
                    }
                    if (readFile != null) {
                        readFile.close();
                    }
                }
                catch(IOException io_ex) {
                    io_ex.printStackTrace();
                }
            }
        }
        return LineLast;
    }


}
