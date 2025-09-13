<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Report Ghost Net</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Report New Ghost Net</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/">Home</a>
                <a href="${pageContext.request.contextPath}/ghostnets">View All Nets</a>
            </nav>
        </header>
        
        <main>
            <c:if test="${not empty error}">
                <div class="error-message">
                    <p>${error}</p>
                </div>
            </c:if>
            
            <form action="${pageContext.request.contextPath}/ghostnets" method="post" class="ghost-net-form">
                <input type="hidden" name="action" value="create">
                
                <div class="form-group">
                    <label for="location">Location:</label>
                    <input type="text" id="location" name="location" required maxlength="100" 
                           placeholder="e.g., North Sea, 50km from Helgoland">
                </div>
                
                <div class="form-group">
                    <label for="latitude">Latitude:</label>
                    <input type="number" id="latitude" name="latitude" required step="any" 
                           placeholder="e.g., 54.1825" min="-90" max="90">
                </div>
                
                <div class="form-group">
                    <label for="longitude">Longitude:</label>
                    <input type="number" id="longitude" name="longitude" required step="any" 
                           placeholder="e.g., 7.8946" min="-180" max="180">
                </div>
                
                <div class="form-group">
                    <label for="sizeEstimate">Size Estimate:</label>
                    <select id="sizeEstimate" name="sizeEstimate" required>
                        <option value="">Select size</option>
                        <option value="Small (< 10m)">Small (< 10m)</option>
                        <option value="Medium (10-50m)">Medium (10-50m)</option>
                        <option value="Large (50-100m)">Large (50-100m)</option>
                        <option value="Very Large (> 100m)">Very Large (> 100m)</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="notes">Additional Notes:</label>
                    <textarea id="notes" name="notes" rows="4" maxlength="500" 
                              placeholder="Any additional information about the ghost net..."></textarea>
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Report Ghost Net</button>
                    <a href="${pageContext.request.contextPath}/ghostnets" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </main>
    </div>
</body>
</html>