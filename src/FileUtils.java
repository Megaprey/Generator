import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


/**
 * Created by Razzhivin Igor on 28.02.2017.
 */
public class FileUtils {

    /**
     * @param directory путь к файлу
     * @return массив строк файла
     */
    public static ArrayList<String> readLines(String directory) throws IOException {
        FileInputStream inputStream = null;
        ArrayList<String> lines = new ArrayList<>();
        try {
            inputStream = new FileInputStream(directory);
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_16));
            String readLine;
            while ((readLine = in.readLine()) != null) {
                lines.add(readLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }
        return lines;


    }

    /**
     *
     * @param directory путь к файлу
     * @param result литерал для записи в файл
     * @throws IOException
     */
    public static void writeFile(String directory, String result) throws IOException {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(directory);
            outputStream.write(result.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            outputStream.close();
        }
    }

    /**
     *
     * @param directory путь к файлу
     * @return поток данных
     * @throws FileNotFoundException
     */
    public static InputStream readFileXml(String directory) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(directory);
        return  inputStream;
    }
}
