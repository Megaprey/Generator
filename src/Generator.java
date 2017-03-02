import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Razzhivin Igor on 14.02.2017.
 */
public class Generator {

    public static final String USER_DIR = "user.dir";

    public static void main(String[] args) throws IOException {
        ArrayList<String> lines;

        String source = System.getProperty(USER_DIR) + PropertyHelper.getProperty("gen.file.SOURCE_DATA_TSV");
        lines = FileUtils.readLines(source);

        // читаем метаиформацию
        String directory = System.getProperty(USER_DIR) + PropertyHelper.getProperty("gen.xml.SETTINGS_XML");
        InputStream isWithMetaData = FileUtils.readFileXml(directory);
        HashMap<Sizes, Integer> metaDataForFormating = XmlConfigReader.getMetaData(isWithMetaData);
        // получаем конечный результат в виде символьной таблицы
        String result = resultReturn(metaDataForFormating, lines);
        FileUtils.writeFile(System.getProperty(USER_DIR) +  PropertyHelper.getProperty("gen.file.EXAMPLE_REPORT_TXT"), result);
    }


    /**
     * @param metaDataForFormating коллекция, которая содержит размеры страницы и размеры таблицы
     * @param lines                массив строк полученных из source-data.tsv
     * @return строку отредактированных данных в таблицу
     */
    private static String resultReturn(HashMap<Sizes, Integer> metaDataForFormating, ArrayList<String> lines) {

        int pageWidth = metaDataForFormating.get(Sizes.pageWidth);
        int pageHeigth = metaDataForFormating.get(Sizes.pageHeigth);
        int numberWidth = metaDataForFormating.get(Sizes.numberWidth);
        int dateWidth = metaDataForFormating.get(Sizes.dateWidth);
        int nameWidth = metaDataForFormating.get(Sizes.nameWidth);

        String result = "";
        String number = PropertyHelper.getProperty("gen.NUMBER");
        String date = PropertyHelper.getProperty("gen.DATE");
        String name = PropertyHelper.getProperty("gen.FIO");
        result += resultLine(number, date, name, numberWidth, dateWidth, nameWidth);
        result += lineDash(pageWidth);
        int lineCount = 3;


        for (int i = 0; i < lines.size(); i++) {
            String[] strTables = lines.get(i).split("\t");

            String firstField, secondField, thirdField;
            firstField = "";
            secondField = "";
            thirdField = "";
            String firstFieldCorrect = strTables[0];
            String secondFieldCorrect = strTables[1];
            String thirdFieldCorrect = strTables[2];

            while (firstFieldCorrect.length() != 0 || secondFieldCorrect.length() != 0 || thirdFieldCorrect.length() != 0) {

                firstField = transferDash(firstFieldCorrect, numberWidth, numberWidth, " ");
                secondField = transferDash(secondFieldCorrect, dateWidth, dateWidth, "/");
                thirdField = transferDash(thirdFieldCorrect, nameWidth, nameWidth, " ");

                if (lineCount == pageHeigth) {
                    result += "~\n" + resultLine(number, date, name, numberWidth, dateWidth, nameWidth);
                    result += lineDash(pageWidth);
                    lineCount += 3;
                }
                result += resultLine(firstField, secondField, thirdField, numberWidth, dateWidth, nameWidth);
                lineCount++;

                firstFieldCorrect = fieldCorrect(firstFieldCorrect, firstField, numberWidth);
                secondFieldCorrect = fieldCorrect(secondFieldCorrect, secondField, dateWidth);
                thirdFieldCorrect = fieldCorrect(thirdFieldCorrect, thirdField, nameWidth);
            }
            if (lineCount == pageHeigth) {
                result += "~\n" + resultLine(number, date, name, numberWidth, dateWidth, nameWidth);
                result += lineDash(pageWidth);
                lineCount += 3;
            } else {
                result += lineDash(pageWidth);
                lineCount++;
            }

        }
        return result;
    }

    /**
     * @param fieldCorr значение колонки которое необходимо скорректировать
     * @param field     скорректированное значение колонки таблицы
     * @param Width     ширина колонки таблицы
     * @return ту часть значения колонки в таблице, которую необходимо перенести на следующую строку, либо пустую строку
     * если данное значение помещается
     */
    private static String fieldCorrect(String fieldCorr, String field, int Width) {
        if (fieldCorr.length() > 0 && fieldCorr.charAt(0) == ' ')
            fieldCorr = fieldCorr.substring(1);

        if (fieldCorr.length() > Width) {
            fieldCorr = fieldCorr.substring(field.length());
        } else {
            if (field.equals(fieldCorr))
                fieldCorr = "";
        }
        return fieldCorr;
    }

    /**
     * @param pageWidth ширина страницы
     * @return строку заполненную "-"
     */
    private static String lineDash(int pageWidth) {
        String result = "";
        for (int j = 0; j < pageWidth; j++)
            result += "-";
        result += "\n";
        return result;
    }

    /**
     * @param column      значение колонки или название столбца таблицы
     * @param columnWidth ширина столбца
     * @return строку "| " + column + необходимое количество пробелов
     */
    private static String fillSpace(String column, int columnWidth) {
        String result = "| " + column;

        int countSpace = columnWidth - column.length();
        for (int i = 0; i <= countSpace; i++) {
            result += " ";
        }
        return result;
    }

    /**
     * @param firstColumn  строка для заполнения первой колонки
     * @param secondColumn строка для заполнения второй колонки
     * @param thirdColumn  строка для заполнения третьей колонки
     * @param numberWidth  ширина столбца Номер
     * @param dateWidth    ширина столбца Дата
     * @param nameWidth    ширина столбца ФИО
     * @return строку в таблице с необходимым количеством отступов
     */
    private static String resultLine(String firstColumn, String secondColumn, String thirdColumn, int numberWidth, int dateWidth, int nameWidth) {
        String result = fillSpace(firstColumn, numberWidth);
        result += fillSpace(secondColumn, dateWidth);
        result += fillSpace(thirdColumn, nameWidth);
        result += "|\n";
        return result;
    }


    /**
     * @param transfer
     * @param currentLength текущая ширина столбца
     * @param columnLength  ширина столбца
     * @param splitSibol    символ по которому происходит перенос слова на следующую строку
     * @return строку длиной данной колонки
     */
    private static String transferDash(String transfer, int currentLength, int columnLength, String splitSibol) {
        if (transfer.length() > 0 && transfer.charAt(0) == ' ')
            transfer = transfer.substring(1);
        String[] t = transfer.split(splitSibol);
        if (t.length == 1) {
            if (splitSibol.equals(" "))
                return transferDash(transfer, columnLength, columnLength, "-");
            if (transfer.length() <= currentLength)
                return transfer;
            else
                return transfer.substring(0, currentLength);
        } else {
            if (t[0].length() > currentLength) {
                if (splitSibol.equals(" "))
                    return transferDash(transfer, columnLength, columnLength, "-");
                return t[0].substring(0, currentLength);
            } else {

                String firstAndNext = t[0];
                String previousStep = "";
                for (int i = 1; i < t.length; i++) {
                    previousStep = firstAndNext;
                    firstAndNext += splitSibol;
                    if (firstAndNext.length() > currentLength) {
                        return previousStep;

                    }
                    previousStep = firstAndNext;
                    firstAndNext += t[i];
                    if (t[i].length() > columnLength) {
                        if (splitSibol.equals(" "))
                            return check(t[i], previousStep, columnLength);
                        return firstAndNext.substring(0, currentLength);

                    } else {
                        if (firstAndNext.length() > currentLength) {
                            if (splitSibol.equals(" "))
                                return check(t[i], previousStep, columnLength);
                            return previousStep;
                        }
                    }
                }


            }
        }
        return transfer;

    }

    /**
     * @param t            строка из массива t полученного методом split() по разбивающему элементу splitSimbol
     * @param previousStep строка до последнего прибавления к ней следующего разбивающего символа, либо значения из массива t ,
     *                     которые состовляют значение данной колонки
     * @param columnLength ширина колонки
     * @return если t содержит "-" то данный метод возвращает строку ,равную previousStep + метод transferDash с разбивающим элементом "-",
     * иначе возвращает previousStep
     */
    private static String check(String t, String previousStep, int columnLength) {
        if (t.contains("-"))
            return previousStep + transferDash(t, columnLength - previousStep.length(), columnLength, "-");
        else
            return previousStep;
    }

}


