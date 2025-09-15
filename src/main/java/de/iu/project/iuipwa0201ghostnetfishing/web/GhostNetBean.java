package de.iu.project.iuipwa0201ghostnetfishing.web;

import de.iu.project.iuipwa0201ghostnetfishing.service.GhostNetService;

public class GhostNetBean {

    private final GhostNetService service;

    public GhostNetBean(GhostNetService service) {
        this.service = service;
    }

    // Diese Klasse ist jetzt ein einfacher POJO-Wrapper ohne Spring-spezifische Annotations,
    // um Kompilationsprobleme in Umgebungen zu vermeiden, in denen Spring-Annotationen
    // zur Kompilierzeit nicht verfügbar sind.
}
