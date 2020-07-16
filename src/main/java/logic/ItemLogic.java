/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import dal.ItemDAL;
import entity.Item;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.maven.surefire.shade.org.apache.commons.lang3.StringUtils;
import org.apache.maven.surefire.shade.org.apache.commons.lang3.math.NumberUtils;

/**
*
* @author Ron
*/
public class ItemLogic extends GenericLogic<Item, ItemDAL>{

    public static final String DESCRIPTION = "description";
    public static final String CATEGORY_ID = "categoryId";
    public static final String IMAGE_ID = "imageId";
    public static final String LOCATION = "location";
    public static final String PRICE  = "price";
    public static final String TITLE = "title";
    public static final String DATE = "date";
    public static final String URL = "url";
    public static final String ID = "id";
    
    public ItemLogic() {
        super(new ItemDAL());
    }

    @Override
    public List<Item> getAll(){
        return get(()->dao().findAll());

    }
    
    @Override
    public Item getWithId(int id){
        return get(()->dao().findById(id));
    }
    
    public List<Item> getWithPrice(BigDecimal price){
        return get(()->dao().findByPrice(price));
       
    }
    
    public List<Item> getWithTitle(String title){
        return get(()->dao().findByTitle(title));
       
    }
    
    public List<Item> getWithDate(String date){
        return get(()->dao().findByDate(date));
       
    }
   
    public List<Item> getWithLocation(String location){
        return get(()->dao().findByLocation(location));
       
    }
    
    public List<Item> getWithDescription(String description){
        return get(()->dao().findByDescription(description));
       
    }
    
    public Item getWithUrl(String url){
        return get(()->dao().findByUrl(url));
       
    }
    
    public List<Item> getWithCategory(String categoryId){
        return get(()->dao().findByCategory(categoryId));
       
    }
    
    public List<Item> search(String search){
        return get(()->dao().findContaining(search));
       
    }
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "Image", "Category", "Price", "Title", "Date", "Location", "Description", "URL");
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, IMAGE_ID, CATEGORY_ID, PRICE, TITLE, DATE, LOCATION, DESCRIPTION, URL);
    }

    @Override
    public List<?> extractDataAsList(Item e) {
        return Arrays.asList(e.getId(), e.getImage(), e.getCategory(), e.getPrice(), e.getTitle(), e.getDate(), e.getLocation(), e.getDescription(), e.getUrl());
    }

    @Override
    public Item createEntity(Map<String, String[]> parameterMap) {
        
        String price = parameterMap.get(PRICE)[0];
        String title = parameterMap.get(TITLE)[0];
        String date = parameterMap.get(DATE)[0];
        String location = parameterMap.get(LOCATION)[0];
        String description = parameterMap.get(DESCRIPTION)[0];
        String url = parameterMap.get(URL)[0];
        
        Item item = new Item();
        if(parameterMap.containsKey(ID)){
            if(parameterMap.get(ID)[0]==null || parameterMap.get(ID)[0].isEmpty()){
                throw new ValidationException("ID cannot be empty");
            }
            if(!(StringUtils.isNumeric(parameterMap.get(ID)[0]))){
                throw new ValidationException("ID can only be a Integer");
            }
            item.setId(Integer.parseInt(parameterMap.get(ID)[0]));
        }
        try{
            if (price.contains("$")) {price = price.substring(1);}
            if (price.contains(",")) {price = price.substring(0, price.indexOf(",")) + price.substring(price.indexOf(",") + 1);}
            if (price.matches("^\\d{1,13}(\\.\\d{1,2})?$") && price.length() <= 16) {
                item.setPrice(new BigDecimal(price));
            }else if(price.length()>16){
                throw new ValidationException("Price exceeds limit");
            }
        }catch(NumberFormatException ex){
            item.setPrice(null);
        }
        
        if(title.equals("") || title.isEmpty() || title==null){throw new ValidationException("Title cannot be null");}
        if (title.length() > 255) {
            throw new ValidationException("Title Too lenghty");
        }
        item.setTitle(parameterMap.get(TITLE)[0]);
        
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            if(date.matches("[a-zA-Z]+")){throw new ValidationException("Date format should be dd/MM/yyyy");}
            item.setDate(format.parse(date));
        } catch (ParseException ex) {item.setDate(new Date());}
        
        if (location.length() > 45) {throw new ValidationException("Location Too lenghty");}
        item.setLocation(location);
        
        if(description==null || description.isEmpty() || description==""){throw new ValidationException("Description cannot be null");}
        if (description.length() > 255) {description = description.substring(0, 255);}
        item.setDescription(description); 
        
        if(url==null || url.isEmpty() || url==""){ throw new ValidationException("Url cannot be null");}
        if (url.length() > 255) {throw new ValidationException("Url Too lenghty");}
        item.setUrl(url);

        return item;
        
    }

    
    
}
