/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import common.ValidationException;
import entity.Category;
import entity.Image;
import entity.Item;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.CategoryLogic;
import logic.ImageLogic;
import logic.ItemLogic;


/**
*
* @author Ron
*/
@WebServlet(name = "CreateItem", urlPatterns = {"/CreateItem"})
public class CreateItem extends HttpServlet {

    private String errorMessage = null;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
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
            out.println("<title>Create Item</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div style=\"text-align: center;\">");
            out.println("<div style=\"display: inline-block; text-align: left;\">");
            out.println("<form method=\"post\">");
            
            out.println("Id:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>",ItemLogic.ID);
            out.println("<br>");
            out.println("Image_Id:<br>");
            out.printf("<input type=\"number\" name=\"%s\" value=\"\"><br>",ItemLogic.IMAGE_ID);
            out.println("<br>");
            out.println("Category_id:<br>");
            out.printf("<input type=\"number\" name=\"%s\" value=\"\"><br>",ItemLogic.CATEGORY_ID);
            out.println("<br>");
            out.println("Price:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>",ItemLogic.PRICE);
            out.println("<br>");
            out.println("Title:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>",ItemLogic.TITLE);
            out.println("<br>");
            out.println("Date:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>",ItemLogic.DATE);
            out.println("<br>");
            out.println("Location:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>",ItemLogic.LOCATION);
            out.println("<br>");
            out.println("Description:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>",ItemLogic.DESCRIPTION);
            out.println("<br>");
            out.println("Url:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>",ItemLogic.URL);
            out.println("<br>");
            out.println("<input type=\"submit\" name=\"view\" value=\"Add and View\">");
            out.println("<input type=\"submit\" name=\"add\" value=\"Add\">");
            
            out.println("</form>");
            if(errorMessage!=null&&!errorMessage.isEmpty()){
                out.println("<p color=red>");
                out.println("<font color=red size=4px>");
                out.println(errorMessage);
                out.println("</font>");
                out.println("</p>");
            }
            out.println("<pre>");
            out.println("Submitted keys and values:");
            out.println(toStringMap(request.getParameterMap()));
            out.println("</pre>");
            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private String toStringMap(Map<String, String[]> values) {
        StringBuilder builder = new StringBuilder();
        values.forEach((k, v) -> builder.append("Key=").append(k)
                .append(", ")
                .append("Value/s=").append(Arrays.toString(v))
                .append(System.lineSeparator()));
        return builder.toString();
    }
    
    /**
     * Handles the HTTP <code>GET</code> method.
     * 
     * get method is called first when requesting a URL. since this servlet
     * will create a host this method simple delivers the html code. 
     * creation will be done in doPost method.
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
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * 
     * this method will handle the creation of entity. as it is called by user
     * submitting data through browser.
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
        Item item = new Item();
        ItemLogic itemLogic = new ItemLogic();
        String url = request.getParameter(ItemLogic.URL);
        if(itemLogic.getWithUrl(url)!=null){
            //if duplicate print the error message
            errorMessage = "Url: \"" + url + "\" already exists";
        }else 
//            if(request.getParameter(ItemLogic.ID).isEmpty() || request.getParameter(ItemLogic.ID)==null){
//                    errorMessage = "ID cannot be null";
//        }else 
            {
            try{
                ImageLogic imageLogic = new ImageLogic();
                Image imageSavedById = imageLogic.getWithId(Integer.parseInt(request.getParameter(ItemLogic.IMAGE_ID)));
                CategoryLogic categoryLogic = new CategoryLogic();
                Category CategorySavedById = categoryLogic.getWithId(Integer.parseInt(request.getParameter(ItemLogic.CATEGORY_ID)));
                if(imageSavedById!=null && CategorySavedById!=null){
                    try{
                        errorMessage = "";
                        item = itemLogic.createEntity( request.getParameterMap()); 
                        item.setImage(imageSavedById);
                        item.setCategory(CategorySavedById);
                        itemLogic.add(item);
                    }catch(ValidationException ex){
                        errorMessage = ex.getMessage();
                    }

                }else if(imageSavedById==null){
                    errorMessage = "Image_Id not in the Database";
                }else if(CategorySavedById==null){
                    errorMessage = "Category_Id not in the Database";
                }
            }catch(NumberFormatException ex){
                errorMessage = "Image_ID & Category_Id are mandatory";
            }
        }
        if( request.getParameter("add")!=null){
            //if add button is pressed return the same page
            processRequest(request, response);
        }else if (request.getParameter("view")!=null) {
            //if view button is pressed redirect to the appropriate table
            response.sendRedirect("ItemTable");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create a Item Entity";
    }

    private static final boolean DEBUG = true;

    public void log( String msg) {
        if(DEBUG){
            String message = String.format( "[%s] %s", getClass().getSimpleName(), msg);
            getServletContext().log( message);
        }
    }

    public void log( String msg, Throwable t) {
        String message = String.format( "[%s] %s", getClass().getSimpleName(), msg);
        getServletContext().log( message, t);
    }
}
