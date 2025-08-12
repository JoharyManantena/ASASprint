package prom16.Ctrl;

import prom16.annotation.*;
import prom16.fonction.*;
// import prom16.controller.*;

@Annoter
public class UtilisateurController {
    @Get("/utilisateur")
    public ModelView afficherUtilisateur() {
        ModelView mv = new ModelView();
        mv.setUrl("/WEB-INF/views/utilisateur.jsp");
        // mv.addObject("utilisateur", new User("John", "Doe"));
        return mv;
    }
}
