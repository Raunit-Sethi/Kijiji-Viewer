/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper.kijiji;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author priyank
 */
public class ItemBuilder {
    private static final String URL_BASE = "https://www.kijiji.ca";
    private static final String ATTRIBUTE_ID = "data-listing-id";
    private static final String ATTRIBUTE_IMAGE = "image";
    private static final String ATTRIBUTE_IMAGE_SRC = "data-src";
    private static final String ATTRIBUTE_IMAGE_NAME = "alt";
    private static final String ATTRIBUTE_PRICE = "price";
    private static final String ATTRIBUTE_TITLE = "title";
    private static final String ATTRIBUTE_LOCATION = "location";
    private static final String ATTRIBUTE_DATE = "date-posted";
    private static final String ATTRIBUTE_DESCRIPTION = "description";
    
    private Element element;
    private KijijiItem item;
    
    ItemBuilder(){
        item = new KijijiItem();
    }
    
    public ItemBuilder setElement(Element element){
        this.element = element;
        return this;
        
    }
    
    public KijijiItem build(){
        item.setId(element.attr(ATTRIBUTE_ID).trim());
        
        item.setUrl(URL_BASE+element.getElementsByClass(ATTRIBUTE_TITLE).get(0).child(0).attr("href").trim());
        
        Elements imgUrl = element.getElementsByClass(ATTRIBUTE_IMAGE);
        if(imgUrl.isEmpty()){ item.setImageUrl("");} 
        String imageUrl = imgUrl.get(0).child(0).attr(ATTRIBUTE_IMAGE_SRC).trim();
        if (imageUrl.isEmpty()) {
            imageUrl = imgUrl.get(0).child(0).attr("src").trim();
            if (imageUrl.isEmpty()) {imageUrl = imgUrl.get(0).child(0).child(1).attr(ATTRIBUTE_IMAGE_SRC).trim();}
        }
        item.setImageUrl(imageUrl);
        
        Elements imgName = element.getElementsByClass(ATTRIBUTE_IMAGE);
        if(imgName.isEmpty()){
            item.setImageName("");
        }else{
        String imageName = imgName.get(0).child(0).attr(ATTRIBUTE_IMAGE_NAME).trim();
        if (imageName.isEmpty()) {
            imageName = imgName.get(0).child(0).child(1).attr(ATTRIBUTE_IMAGE_NAME).trim();
        }
        item.setImageName(imageName);
        }
        
        
        Elements price = element.getElementsByClass(ATTRIBUTE_PRICE);
        if(price.isEmpty()){item.setPrice("");}
        else  {item.setPrice(price.get(0).text().trim());}
        
        Elements title = element.getElementsByClass(ATTRIBUTE_TITLE);
        if(title.isEmpty()){item.setTitle("");}
        else  {item.setTitle(title.get(0).child(0).text().trim());}
        
        Elements date = element.getElementsByClass(ATTRIBUTE_DATE);
        if(date.isEmpty()){item.setDate("");}
        else  {item.setDate(date.get(0).text().trim());}

        Elements location = element.getElementsByClass(ATTRIBUTE_LOCATION);
        if(location.isEmpty()){item.setLocation("");}
        else  {item.setLocation(location.get(0).childNode(0).outerHtml().trim());}
        
        Elements description = element.getElementsByClass(ATTRIBUTE_DESCRIPTION);
        if(description.isEmpty()){item.setDescription("");}
        else  {item.setDescription(description.get(0).text().trim());}

        return item;
    }
}
