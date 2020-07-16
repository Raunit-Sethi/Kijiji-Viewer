/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import dal.ImageDAL;
import entity.Image;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
*
* @author Ron
*/
public class ImageLogic extends GenericLogic<Image, ImageDAL>{

    public static final String NAME = "name";
    public static final String PATH = "path";
    public static final String URL = "url";
    public static final String ID = "id";
    
    public ImageLogic() {
        super(new ImageDAL());
    }
    
     @Override
    public List<Image> getAll() {
        return get(()->dao().findAll());
    }

    @Override
    public Image getWithId(int id) {
        return get(()->dao().findById(id));
    }

    public List<Image> getWithUrl(String url){
        return get(()->dao().findByUrl(url));       
    }
    
    public Image getWithPath(String path){
        return get(()->dao().findByPath(path));
    }
    
    public List<Image> getWithName(String name){
        return get(()->dao().findByName(name));
    }
    
    public List<Image> search(String search){
        return get(()->dao().findContaining(search));    
    }
    
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "URL", "Path", "Name");
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, URL, PATH, NAME);
    }

    @Override
    public List<?> extractDataAsList(Image e) {
        return Arrays.asList(e.getId(), e.getUrl(), e.getPath(), e.getName());
    }


    @Override
    public Image createEntity(Map<String, String[]> parameterMap) {
        
        String url = parameterMap.get(URL)[0];
        String path = parameterMap.get(PATH)[0];
        String name = parameterMap.get(NAME)[0];
        Image image = new Image();
        if(parameterMap.containsKey(ID)){
            if(parameterMap.get(ID)[0]==null || parameterMap.get(ID)[0].isEmpty()){
                throw new ValidationException("ID cannot be empty");
            }
            if(!(StringUtils.isNumeric(parameterMap.get(ID)[0]))){
                throw new ValidationException("ID can only be a Integer");
            }
            image.setId(Integer.parseInt(parameterMap.get(ID)[0]));
        }
        if(url==null || url.isEmpty()){throw new ValidationException("Url cannot be empty");}
        if(url.length() > 255){throw new ValidationException("Url too lengthy");}
        image.setUrl(url);
        
        if(path==null || path.isEmpty()){throw new ValidationException("Path cannot be empty");}
        if(path.length() > 255){throw new ValidationException("Path too lengthy");    }
        image.setPath(path);
        
        if(name==null || name.isEmpty()){throw new ValidationException("Name cannot be empty");}
        if(name.length() > 255){throw new ValidationException("Name too lengthy");}
        image.setName(name);
        return image;
    }
}
