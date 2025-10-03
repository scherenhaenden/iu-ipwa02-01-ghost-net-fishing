package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class AppErrorController implements ErrorController {

    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        ServletWebRequest webRequest = new ServletWebRequest(request);
        ErrorAttributeOptions options = ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.MESSAGE,
                ErrorAttributeOptions.Include.EXCEPTION,
                ErrorAttributeOptions.Include.STACK_TRACE,
                ErrorAttributeOptions.Include.BINDING_ERRORS
        );
        Map<String, Object> attrs = this.errorAttributes.getErrorAttributes(webRequest, options);

        // Lege die Attribute für das Template fest
        model.addAttribute("timestamp", attrs.get("timestamp"));
        model.addAttribute("status", attrs.get("status"));
        model.addAttribute("error", attrs.get("error"));
        model.addAttribute("message", attrs.get("message"));
        model.addAttribute("path", attrs.get("path"));
        model.addAttribute("trace", attrs.get("trace"));
        model.addAttribute("errors", attrs.get("errors"));

        // Optional: weiterführende Details
        model.addAttribute("attributes", attrs);

        return "error";
    }

    // @ControllerAdvice or inside your error controller
    @ModelAttribute("attributesFlat")
    public Map<String, String> attributesFlat(HttpServletRequest req) {
        Map<String, String> out = new LinkedHashMap<>();
        req.getAttributeNames().asIterator().forEachRemaining(n -> {
            Object v = req.getAttribute(n);
            out.put(n, String.valueOf(v)); // or serialize with Jackson if you prefer JSON
        });
        return out;
    }
}

