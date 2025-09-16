package de.iu.project.iuipwa0201ghostnetfishing.web;

/* indo docs: GhostNetBean class
   Plain JavaBean (POJO) for web layer data binding or utility purposes.
   No Spring annotations; can be used in JSF, Thymeleaf forms, or as a data holder.
   Currently holds a reference to GhostNetService but is not a managed Spring bean.
*/
public class GhostNetBean {

    /* indo docs: Service reference
       Optional reference to GhostNetService; not injected, must be set manually if needed.
    */
    private final GhostNetService service;

    /* indo docs: Constructor
       Initializes the bean with a GhostNetService instance (manual wiring).
    */
    public GhostNetBean(GhostNetService service) {
        this.service = service;
    }

    /* indo docs: Note on usage
       This class is a simple POJO wrapper without Spring-specific annotations to avoid compilation issues in non-Spring environments.
       Extend as needed for form backing objects or view models.
    */
}
