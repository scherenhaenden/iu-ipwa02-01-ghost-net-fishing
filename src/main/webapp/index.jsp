<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Ghost Net Fishing - Welcome</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Ghost Net Fishing Management System</h1>
            <p>Helping to remove abandoned fishing nets from our oceans</p>
        </header>
        
        <main>
            <div class="welcome-section">
                <h2>Welcome to the Ghost Net Management System</h2>
                <p>This application helps track and manage the recovery of ghost nets - abandoned, lost, or otherwise discarded fishing nets that continue to capture marine life.</p>
                
                <div class="action-buttons">
                    <a href="${pageContext.request.contextPath}/ghostnets" class="btn btn-primary">View All Ghost Nets</a>
                    <a href="${pageContext.request.contextPath}/ghostnets/new" class="btn btn-secondary">Report New Ghost Net</a>
                </div>
            </div>
            
            <div class="info-section">
                <h3>About Ghost Nets</h3>
                <p>Ghost nets are fishing nets that have been abandoned, lost, or discarded in the ocean. These nets continue to fish autonomously, trapping and killing fish, sea turtles, marine mammals, and seabirds.</p>
                <p>This system helps coordinate the reporting and recovery of these dangerous nets to protect marine ecosystems.</p>
            </div>
        </main>
        
        <footer>
            <p>&copy; 2024 Ghost Net Fishing Management System - Java EE 7 Application</p>
        </footer>
    </div>
</body>
</html>