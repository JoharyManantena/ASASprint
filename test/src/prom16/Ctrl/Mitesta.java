package prom16.Ctrl;

import java.util.Vector;

import prom16.annotation.Annoter;
import prom16.annotation.Get;
import prom16.fonction.ModelView;

@Annoter()
public class Mitesta {

    @Get("/listeFamily")
    public String listeEmp(){
        return "Nom du methode est listeEmp et tu est dans ce methode";
    }

    @Get("/testeView")
    public ModelView transferView(){
        ModelView mv = new ModelView("/view/liste.jsp");
        Vector<String> donnees = new Vector<String>();
        donnees.add("Jean");
        donnees.add("Jeanette");
        donnees.add("Jeanne");
        donnees.add("Jaune");
        mv.addObject("FamilleJean", donnees);
        return mv;
    }

    
}