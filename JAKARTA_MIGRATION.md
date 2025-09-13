# Jakarta EE Migration Guide

This document outlines the migration from Java EE 7 to Jakarta EE performed in this project.

## Overview

This project has been migrated from Java EE 7 to Jakarta EE. Jakarta EE is the successor to Java EE, developed under the Eclipse Foundation.

## Changes Made

### 1. Maven Dependencies (pom.xml)

**Before (Java EE 7):**
```xml
<dependency>
    <groupId>javax</groupId>
    <artifactId>javaee-api</artifactId>
    <version>7.0</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>jstl</artifactId>
    <version>1.2</version>
</dependency>
```

**After (Jakarta EE):**
```xml
<dependency>
    <groupId>jakarta.platform</groupId>
    <artifactId>jakarta.jakartaee-api</artifactId>
    <version>10.0.0</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.glassfish.web</groupId>
    <artifactId>jakarta.servlet.jsp.jstl</artifactId>
    <version>3.0.1</version>
</dependency>
```

### 2. Java Imports

All Java classes were updated to use `jakarta.*` instead of `javax.*`:

**Before:**
```java
import javax.persistence.*;
import javax.validation.constraints.*;
import javax.ejb.Stateless;
import javax.servlet.*;
import javax.inject.Inject;
```

**After:**
```java
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.ejb.Stateless;
import jakarta.servlet.*;
import jakarta.inject.Inject;
```

### 3. Configuration Files

#### web.xml
- Updated namespace from `http://java.sun.com/xml/ns/javaee` to `https://jakarta.ee/xml/ns/jakartaee`
- Updated schema location and version to `web-app_6_0.xsd` and `version="6.0"`

#### persistence.xml
- Updated namespace from `http://java.sun.com/xml/ns/persistence` to `https://jakarta.ee/xml/ns/persistence`
- Updated schema location and version to `persistence_3_0.xsd` and `version="3.0"`

### 4. JSP Files

Updated JSTL taglib URIs:

**Before:**
```jsp
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
```

**After:**
```jsp
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
```

## Affected Files

- `pom.xml` - Updated dependencies
- `src/main/webapp/WEB-INF/web.xml` - Updated namespace and version
- `src/main/resources/META-INF/persistence.xml` - Updated namespace and version
- `src/main/java/de/iu/ipwa02/ghostnet/model/GhostNet.java` - Updated imports
- `src/main/java/de/iu/ipwa02/ghostnet/service/GhostNetService.java` - Updated imports
- `src/main/java/de/iu/ipwa02/ghostnet/web/GhostNetServlet.java` - Updated imports
- `src/main/webapp/WEB-INF/views/ghostnet-list.jsp` - Updated taglib URIs
- `src/main/webapp/index.jsp` - Updated description text

## Verification

The migration was verified by:
1. Successful compilation with `mvn compile`
2. Successful packaging with `mvn package`
3. Generated WAR file contains the correct Jakarta EE dependencies

## Application Server Requirements

This Jakarta EE application requires an application server that supports Jakarta EE, such as:
- WildFly 26+
- TomEE 9+
- Open Liberty
- Payara 6+
- GlassFish 7+

Note: This application will NOT run on traditional Java EE servers like older versions of WildFly, TomEE, or GlassFish that only support javax.* namespace.