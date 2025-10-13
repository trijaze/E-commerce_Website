package vn.bachhoa.controller;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Servlet để serve static images từ webapp/images/
 */
@WebServlet("/images/*")
public class ImageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // Ví dụ: /bachibo.jpg
        
        if (pathInfo == null || pathInfo.length() <= 1) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Remove leading slash
        String fileName = pathInfo.substring(1);
        
        // Get real path to images folder
        String imagePath = getServletContext().getRealPath("/images/" + fileName);
        File imageFile = new File(imagePath);
        
        if (!imageFile.exists() || !imageFile.isFile()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Set content type based on file extension
        String contentType = getContentType(fileName);
        resp.setContentType(contentType);
        resp.setContentLength((int) imageFile.length());
        
        // Set caching headers
        resp.setHeader("Cache-Control", "public, max-age=3600"); // Cache 1 hour
        
        // Stream file to response
        try (FileInputStream fis = new FileInputStream(imageFile);
             OutputStream os = resp.getOutputStream()) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }
    
    private String getContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "svg":
                return "image/svg+xml";
            default:
                return "application/octet-stream";
        }
    }
}