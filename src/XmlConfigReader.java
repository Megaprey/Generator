import org.jdom2.Element;
import org.jdom2.input.DOMBuilder;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Razzhivin Igor on 27.02.2017.
 */
public class XmlConfigReader{
    /**
     *
     * @return коллекцию HashMap<Sizes ,Integer> заполненную размерами страницы и таблицы, полученные из settings.xml
     */
    public static HashMap<Sizes ,Integer> getMetaData(InputStream inputStream)
    {
        org.jdom2.Document jdomDocument = null;
        try {
            jdomDocument = createJDOMusingDOMParser(inputStream);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element root = jdomDocument.getRootElement();
        Element root1 = root.getChild(PropertyHelper.getProperty("gen.xml.COLUMNS"));
        List<Element> elPage = root.getChildren(PropertyHelper.getProperty("gen.xml.PAGE"));
        List<Element> elColumn = root1.getChildren(PropertyHelper.getProperty("gen.xml.COLUMN"));
        Element page = elPage.get(0);
        int pageWidth = Integer.parseInt(page.getChildText(PropertyHelper.getProperty("gen.xml.WIDTH")));
         int pageHeigth = Integer.parseInt(page.getChildText(PropertyHelper.getProperty("gen.xml.HEIGHT")));

        int numberWidth = 0;
         int dateWidth = 0;
         int nameWidth = 0;

        for (Element elementColumn : elColumn ) {
            String title = elementColumn.getChildText(PropertyHelper.getProperty("gen.xml.TITLE"));
            int width = Integer.parseInt(elementColumn.getChildText(PropertyHelper.getProperty("gen.xml.WIDTH")));


            if(title.equals(PropertyHelper.getProperty("gen.NUMBER")))
                numberWidth = width;
            if(title.equals(PropertyHelper.getProperty("gen.DATE")))
                dateWidth = width;
            if(title.equals(PropertyHelper.getProperty("gen.FIO")))
                nameWidth = width;

        }
        HashMap<Sizes, Integer> pageSize = new HashMap();
        pageSize.put(Sizes.pageWidth, pageWidth);
        pageSize.put(Sizes.pageHeigth, pageHeigth);
        pageSize.put(Sizes.numberWidth, numberWidth);
        pageSize.put(Sizes.dateWidth, dateWidth);
        pageSize.put(Sizes.nameWidth, nameWidth);


        return pageSize;


    }

    /**
     *
     * @param inputStream поток данных
     * @return объект Document для необходимого XML-файла
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private static org.jdom2.Document createJDOMusingDOMParser(InputStream inputStream)
            throws ParserConfigurationException, SAXException, IOException {
        //создаем DOM Document
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        documentBuilder = dbFactory.newDocumentBuilder();
        Document doc = documentBuilder.parse(inputStream);
        DOMBuilder domBuilder = new DOMBuilder();

        return domBuilder.build(doc);

    }
}
