import java.io.*;
import java.util.Properties;

/**
 * Created by Razzhivin Igor on 01.03.2017.
 */
public class PropertyHelper {
    /**
     *
     * @param str ключ для необходимого параметра
     * @return параметр
     */
    public static String getProperty(String str) {
        InputStreamReader isr = null;
        String strRes = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(FileUtils.readFileXml(System.getProperty(Generator.USER_DIR) + "/gener.properties")));
            Properties property = new Properties();
            property.load(in);
            strRes = property.getProperty(str);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return strRes;
    }
}
