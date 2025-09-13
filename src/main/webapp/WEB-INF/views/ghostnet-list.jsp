<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Ghost Net List</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Ghost Net Management</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/">Home</a>
                <a href="${pageContext.request.contextPath}/ghostnets/new">Report New Net</a>
            </nav>
        </header>
        
        <main>
            <h2>All Ghost Nets</h2>
            
            <c:choose>
                <c:when test="${empty ghostNets}">
                    <div class="no-data">
                        <p>No ghost nets reported yet.</p>
                        <a href="${pageContext.request.contextPath}/ghostnets/new" class="btn btn-primary">Report the first one</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="ghost-net-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Location</th>
                                <th>Coordinates</th>
                                <th>Size</th>
                                <th>Status</th>
                                <th>Reported Date</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="net" items="${ghostNets}">
                                <tr>
                                    <td>${net.id}</td>
                                    <td>${net.location}</td>
                                    <td>${net.latitude}, ${net.longitude}</td>
                                    <td>${net.sizeEstimate}</td>
                                    <td class="status-${net.status.name().toLowerCase()}">${net.status.displayName}</td>
                                    <td>
                                        <fmt:formatDate value="${net.reportedDate}" pattern="yyyy-MM-dd HH:mm"/>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/ghostnets/${net.id}" class="btn btn-small">View</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </main>
    </div>
</body>
</html>