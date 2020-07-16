package scraper.kijiji;

import java.io.IOException;
import java.util.function.Consumer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
*
* @author Ron
*/
public class Kijiji {

    private static final String ATTRIBUTE_ID = "data-listing-id";
    private static final String URL_BASE = "https://www.kijiji.ca/b-computer-accessories/";
    private static final String URL_NEWEST_COMPUTER_ACCESSORIES = "https://www.kijiji.ca/b-computer-accessories/ottawa-gatineau-area/c128l1700184?sort=dateDesc";
    private static final String URL_NEWEST_LAPTOP_ACCESSORIES = "https://www.kijiji.ca/b-laptop-accessories/ottawa-gatineau-area/c780l1700184?sort=dateDesc";

    private Document doc;
    private Elements itemElements;

    public Kijiji downloadPage(String kijijiUrl) throws IOException {
        doc = Jsoup.connect(kijijiUrl).get();
        return this;
    }

    @Deprecated
    public Kijiji downloadDefaultPage() throws IOException {
        return downloadPage(URL_NEWEST_COMPUTER_ACCESSORIES);
    }

    public Kijiji findAllItems() {
        itemElements = doc.getElementsByAttribute(ATTRIBUTE_ID);
        return this;
    }

//    /**
//     * 
//     * @param callback
//     * @return
//     * @deprecated
//     */
//    @Deprecated
//    public Kijiji proccessItemsNoneBuilder(Consumer<BadKijijiItem> callback) {
//        for (Element element : itemElements) {
//            callback.accept( new BadKijijiItem(element));
//        }
//        return this;
//    }

    public Kijiji proccessItems(Consumer<KijijiItem> callback) {
        itemElements.forEach((Element element) -> {
            callback.accept( new ItemBuilder().setElement(element).build());
        });
        return this;
    }
    
    public static void main(String[] args) throws IOException {
        
        Consumer<KijijiItem> saveItemsNoneBuilder = (KijijiItem item) -> {
            System.out.println(item);
        };

        Kijiji kijiji = new Kijiji();
        
        kijiji.downloadDefaultPage();
        kijiji.findAllItems();
        kijiji.proccessItems(saveItemsNoneBuilder);
    }
}
