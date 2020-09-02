import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileWorker {

    public static void write(String fileName, String text) {
        try{
            File recordsFile = new File(fileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(recordsFile, true));
            StringBuilder sb =read(fileName);
            if (sb == null){
                writer.newLine();
                writer.append(text);
                writer.flush();

            } else {

                update(fileName, text);
            }
        } catch (Exception e){
            e.printStackTrace();
        }


    }

    public static StringBuilder read (String fileName) throws Exception {
        StringBuilder sb = new StringBuilder();
        File recordsFile = new File(fileName);
        if (recordsFile.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(recordsFile));
            String val;
            while ((val = reader.readLine()) != null) {
                if (val == "\n") {
                    sb.append("\n");

                } else {
                    sb.append(val).append(" ");
                }
            }
            return sb;
        }
        return null;
    }

    private static void update(String fileName, String text) throws Exception {
        Path path = Paths.get(fileName);
        Files.write(path, text.getBytes(), StandardOpenOption.APPEND);
    }
}


