/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import dal.CategoryDAL;
import entity.Category;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
*
* @author Ron
*/
public class CategoryLogic extends GenericLogic<Category, CategoryDAL>{

    public static final String TITLE = "title";
    public static final String URL = "url";
    public static final String ID = "id";
    
    public CategoryLogic() {
        super(new CategoryDAL());
    }
    
    @Override
    public List<Category> getAll() {
        return get(()->dao().findAll());
    }

    @Override
    public Category getWithId(int id) {
        return get(()->dao().findById(id));
    }

    public Category getWithUrl(String url){
        return get(()->dao().findByUrl(url));
    }
    
    public Category getWithTitle(String title){
        return get(()->dao().findByTitle(title));
    }
    
    public List<Category> search(String search){
        return get(()->dao().findContaining(search));
    }
    
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "URL", "Title");
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, URL, TITLE);
    }

    @Override
    public List<?> extractDataAsList(Category e) {
        return Arrays.asList(e.getId(), e.getUrl(), e.getTitle());
    }

    @Override
    public Category createEntity(Map<String, String[]> parameterMap) {
        String url = parameterMap.get(URL)[0];
        String title = parameterMap.get(TITLE)[0];
        Category category = new Category();
        if(parameterMap.containsKey(ID)){
            if(parameterMap.get(ID)[0]==null || parameterMap.get(ID)[0].isEmpty()){
                throw new ValidationException("ID cannot be empty");
            }
            if(!(StringUtils.isNumeric(parameterMap.get(ID)[0]))){
                throw new ValidationException("ID can only be a Integer");
            }
            category.setId(Integer.parseInt(parameterMap.get(ID)[0]));
        }
        if(url==null || url.isEmpty()){throw new ValidationException("Url cannot be empty");}
        if(url.length() > 255){throw new ValidationException("Url too lengthy");    }
        category.setUrl(parameterMap.get(URL)[0]);
        
        if(title==null || title.isEmpty() || title==""){throw new ValidationException("Title cannot be empty");}
        if(title.length() > 255){throw new ValidationException("Title too lengthy");    }
        category.setTitle(parameterMap.get(TITLE)[0]);

        return category;
    }

   
    
}
