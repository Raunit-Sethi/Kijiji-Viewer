/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import common.FileUtility;
import entity.Category;
import entity.Image;
import entity.Item;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.CategoryLogic;
import logic.ImageLogic;
import logic.ItemLogic;
import scraper.kijiji.Kijiji;
import scraper.kijiji.KijijiItem;

/**
*
* @author Ron
*/
@WebServlet(name = "Kijiji", urlPatterns = {"/Kijiji"})
public class KijijiView extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet KijijiView</title>");  
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style/KijijiStyle.css\">");
            out.println("</head>");
            out.println("<body>");
            
            ItemLogic IL = new ItemLogic();
            List<Item> itemList = IL.getAll();
            for (Item item : itemList) {
                out.println("<div class=\"center-column\">");
                out.println("<div class=\"item\">");
                out.println("<div class=\"image\">");
                out.println("<img src=\"image/"+item.getImage().getPath()+"\" style=\"max-width: 250px;");
                out.println("max-height: 200px;\" />");
                out.println("</div>");
                out.println("<div class=\"details\">");
                out.println("<div class=\"title\">");
                out.println("<a href=");
                out.println(item.getUrl());
                out.println("target=\"_blank\">"+item.getTitle()+"</a>");
                out.println("</div>");
                out.println("<div class=\"price\">");
                out.println("price: ");
                if(item.getPrice()==null){
                    out.println("Please Contact");
                }else{
                    out.println("$"+item.getPrice());
                }
                out.println("</div>");
                out.println("<div class=\"date\">");
                out.println("date: ");
                out.println(item.getDate());
                out.println("</div>");
                out.println("<div class=\"location\">");
                out.println("location: ");
                out.println(item.getLocation());
                out.println("</div>");
                out.println("<div class=\"description\">");
                out.println("description: ");
                out.println(item.getDescription());
                out.println("</div>");
                out.println("</div>");
                out.println("</div>");
                out.println("</div>");
            }
            
            out.println("</body>");
            out.println("</html>");
        }
    }

    private String toStringMap(Map<String, String[]> m) {
        StringBuilder builder = new StringBuilder();
        for (String k : m.keySet()) {
            builder.append("Key=").append(k)
                    .append(", ")
                    .append("Value/s=").append(Arrays.toString(m.get(k)))
                    .append(System.lineSeparator());
        }
        return builder.toString();
    }
    

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("GET");
        Kijiji kijiji = new Kijiji();
        CategoryLogic c = new CategoryLogic();
        Category category = c.getAll().get(0);
        String url = category.getUrl();
        kijiji.downloadPage(url);
        kijiji.findAllItems();
        ItemLogic iteml = new ItemLogic();
        
        Consumer<KijijiItem> lamda = ( KijijiItem item) -> {
            if(iteml.getWithId(Integer.parseInt(item.getId()))==null){
                Item dataItem;
                Image image;
                ImageLogic imagel = new ImageLogic();
                Map<String, String[]> dbimage = new HashMap<>();
                dbimage.put( ImageLogic.NAME, new String[]{item.getImageName()});
                dbimage.put( ImageLogic.URL, new String[]{item.getImageUrl()});
                dbimage.put( ImageLogic.PATH, new String[]{System.getProperty("user.home")+"/KijijiImages/"+item.getId()+".jpg"});
                image = imagel.createEntity(dbimage);
                
                FileUtility.downloadAndSaveFile(item.getImageUrl(),System.getProperty("user.home")+"/KijijiImages/",item.getId()+".jpg");
                if(imagel.getWithPath(image.getPath())==null){
                    imagel.add(image);
                }

                Map<String, String[]> dbitem = new HashMap<>();
                dbitem.put(ItemLogic.ID, new String[]{item.getId()});
                dbitem.put(ItemLogic.PRICE, new String[]{item.getPrice()});
                dbitem.put(ItemLogic.TITLE, new String[]{item.getTitle()});
                dbitem.put(ItemLogic.DATE, new String[]{item.getDate()});
                dbitem.put(ItemLogic.LOCATION, new String[]{item.getLocation()});
                dbitem.put(ItemLogic.DESCRIPTION, new String[]{item.getDescription()});
                dbitem.put(ItemLogic.URL, new String[]{item.getUrl()});
                dataItem = iteml.createEntity(dbitem);
                dataItem.setCategory(category);
                dataItem.setImage(imagel.getWithPath(image.getPath()));
                iteml.add(dataItem);
            }
            };
            kijiji.proccessItems(lamda);
            processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("POST");
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Sample of Kijiji View";
    }

    private static final boolean DEBUG = true;

    public void log(String msg) {
        if (DEBUG) {
            String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
            getServletContext().log(message);
        }
    }

    public void log(String msg, Throwable t) {
        String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
        getServletContext().log(message, t);
    }

}
