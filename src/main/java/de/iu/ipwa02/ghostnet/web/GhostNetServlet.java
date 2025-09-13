package de.iu.ipwa02.ghostnet.web;

import de.iu.ipwa02.ghostnet.model.GhostNet;
import de.iu.ipwa02.ghostnet.model.NetStatus;
import de.iu.ipwa02.ghostnet.service.GhostNetService;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet for handling Ghost Net operations
 */
@WebServlet(name = "GhostNetServlet", urlPatterns = {"/ghostnets", "/ghostnets/*"})
public class GhostNetServlet extends HttpServlet {

    @Inject
    private GhostNetService ghostNetService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // List all ghost nets
            List<GhostNet> ghostNets = ghostNetService.findAllGhostNets();
            request.setAttribute("ghostNets", ghostNets);
            request.getRequestDispatcher("/WEB-INF/views/ghostnet-list.jsp").forward(request, response);
        } else if (pathInfo.equals("/new")) {
            // Show form for new ghost net
            request.setAttribute("statuses", NetStatus.values());
            request.getRequestDispatcher("/WEB-INF/views/ghostnet-form.jsp").forward(request, response);
        } else {
            // Show specific ghost net
            try {
                Long id = Long.parseLong(pathInfo.substring(1));
                GhostNet ghostNet = ghostNetService.findGhostNetById(id);
                if (ghostNet != null) {
                    request.setAttribute("ghostNet", ghostNet);
                    request.getRequestDispatcher("/WEB-INF/views/ghostnet-detail.jsp").forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("create".equals(action)) {
            // Create new ghost net
            String location = request.getParameter("location");
            String latitudeStr = request.getParameter("latitude");
            String longitudeStr = request.getParameter("longitude");
            String sizeEstimate = request.getParameter("sizeEstimate");
            String notes = request.getParameter("notes");
            
            try {
                Double latitude = Double.parseDouble(latitudeStr);
                Double longitude = Double.parseDouble(longitudeStr);
                
                GhostNet ghostNet = new GhostNet(location, latitude, longitude, sizeEstimate);
                ghostNet.setNotes(notes);
                
                ghostNetService.createGhostNet(ghostNet);
                response.sendRedirect(request.getContextPath() + "/ghostnets");
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid latitude or longitude format");
                request.setAttribute("statuses", NetStatus.values());
                request.getRequestDispatcher("/WEB-INF/views/ghostnet-form.jsp").forward(request, response);
            }
        } else if ("recover".equals(action)) {
            // Mark as recovered
            String idStr = request.getParameter("id");
            try {
                Long id = Long.parseLong(idStr);
                ghostNetService.markAsRecovered(id);
                response.sendRedirect(request.getContextPath() + "/ghostnets/" + id);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
}