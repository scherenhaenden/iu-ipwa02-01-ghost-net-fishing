<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Ghost Net Details</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Ghost Net Details</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/">Home</a>
                <a href="${pageContext.request.contextPath}/ghostnets">View All Nets</a>
                <a href="${pageContext.request.contextPath}/ghostnets/new">Report New Net</a>
            </nav>
        </header>
        
        <main>
            <c:if test="${not empty ghostNet}">
                <div class="ghost-net-detail">
                    <h2>Ghost Net #${ghostNet.id}</h2>
                    
                    <div class="detail-section">
                        <h3>Location Information</h3>
                        <p><strong>Location:</strong> ${ghostNet.location}</p>
                        <p><strong>Coordinates:</strong> ${ghostNet.latitude}, ${ghostNet.longitude}</p>
                        <p><strong>Size Estimate:</strong> ${ghostNet.sizeEstimate}</p>
                    </div>
                    
                    <div class="detail-section">
                        <h3>Status Information</h3>
                        <p><strong>Current Status:</strong> 
                           <span class="status-${ghostNet.status.name().toLowerCase()}">${ghostNet.status.displayName}</span>
                        </p>
                        <p><strong>Reported Date:</strong> 
                           <fmt:formatDate value="${ghostNet.reportedDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
                        </p>
                        <c:if test="${not empty ghostNet.recoveryDate}">
                            <p><strong>Recovery Date:</strong> 
                               <fmt:formatDate value="${ghostNet.recoveryDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
                            </p>
                        </c:if>
                    </div>
                    
                    <c:if test="${not empty ghostNet.notes}">
                        <div class="detail-section">
                            <h3>Additional Notes</h3>
                            <p>${ghostNet.notes}</p>
                        </div>
                    </c:if>
                    
                    <div class="actions">
                        <c:if test="${ghostNet.status != 'RECOVERED'}">
                            <form action="${pageContext.request.contextPath}/ghostnets" method="post" style="display: inline;">
                                <input type="hidden" name="action" value="recover">
                                <input type="hidden" name="id" value="${ghostNet.id}">
                                <button type="submit" class="btn btn-success" 
                                        onclick="return confirm('Mark this ghost net as recovered?')">
                                    Mark as Recovered
                                </button>
                            </form>
                        </c:if>
                    </div>
                </div>
            </c:if>
        </main>
    </div>
</body>
</html>