/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper.kijiji;

import java.util.Objects;

/**
*
* @author Ron
*/
public class KijijiItem {

    private String id;
    private String url;
    private String imageUrl;
    private String imageName;
    private String price;
    private String title;
    private String date;
    private String location;
    private String description;

   KijijiItem(){
       
   }

    public String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getPrice() {
        return price;
    }

    void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }
    
    public int hashCode(){
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(getId());
        return hash;
    }
    public boolean equals(Object obj){
         if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KijijiItem other = (KijijiItem) obj;
        return Objects.equals(getId(), other.getId());
        
    }
    
    public String toString(){
        return String.format("[id:%s, image_url:%s, image_name:%s, price:%s, title:%s, date:%s, location:%s, description:%s]",
                getId(), getImageUrl(), getImageName(), getPrice(), getTitle(), getDate(), getLocation(), getDescription());
    }
}
