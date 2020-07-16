/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import entity.Item;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
*
* @author Ron
*/
@WebServlet(name = "image", urlPatterns = {"/image/*"})
public class ImageDelivery extends HttpServlet {

    

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException {
      // Get the absolute path of the image
        String filename =  request.getPathInfo().substring(1);
      response.setContentType("image/jpg");
      File file = new File(filename);
      response.setContentLength((int)file.length());
      
    try(
        FileInputStream in = new FileInputStream(file);
        OutputStream out = response.getOutputStream();){

        // Copy the contents of the file to the output stream
        byte[] buf = new byte[1024];
        int count = 0;
        while ((count = in.read(buf)) >= 0) {
            out.write(buf, 0, count);
        }
    }
}

    @Override
    public String getServletInfo() {
        return "Image Delivery";
    }// </editor-fold>

}
