package de.iu.project.iuipwa0201ghostnetfishing.web;

import de.iu.project.iuipwa0201ghostnetfishing.model.GhostNet;
import de.iu.project.iuipwa0201ghostnetfishing.service.GhostNetService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
@RequestScoped
public class GhostNetBean {

    @Inject
    private GhostNetService service;

    private String name;

    /**
     * Creates a new entry with the provided name if it is not null or empty.
     */
    public String create() {
        if (name != null && !name.trim().isEmpty()) {
            service.create(name.trim());
            name = ""; // Reset
        }
        // Bleibe auf derselben Seite
        return null;
    }

    /**
     * Retrieves a list of all GhostNet entities.
     */
    public List<GhostNet> getAll() {
        return service.findAll();
    }

    /**
     * Returns the name.
     */
    public String getName() { return name; }
    /**
     * Sets the name of the object.
     */
    public void setName(String name) { this.name = name; }
}

