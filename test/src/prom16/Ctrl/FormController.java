package prom16.Ctrl;

import prom16.annotation.Annoter;
import prom16.annotation.Get;
import prom16.annotation.Param;
import prom16.fonction.ModelView;
import prom16.Entite.Employer;

@Annoter()
public class FormController {

    @Get("/listeEmp")
    public String listeEmp() {
        return " Miretourn Liste";
    }

    @Get("/hello")
    public String helloWorld() {
        return "Hello, World!";
    }

    @Get("/greet")
    public String greetUser(@Param("name") String name) {
        return "Hello, " + name + "!";
    }

    @Get("/testeView")
    public ModelView transferView(@Param("nomView") String nom, @Param("mailView") String mailView, @Param("ageView") double ageView) {
        System.out.println("nomView: " + nom);
        System.out.println("mailView: " + mailView);
        System.out.println("ageView: " + ageView);

        ModelView mv = new ModelView("/view/liste.jsp");
        mv.addObject("mail", mailView);
        mv.addObject("anarana", nom);
        mv.addObject("age", ageView);
        return mv;
    }

    @Get("/")
    public ModelView home() {
        ModelView mv = new ModelView("/view/home.jsp");
        return mv;
    }


    @Get("/insertion")
    public String insertion(@Param("nom") String nom, @Param("mail") String mail, @Param("age") double taona) {
        System.out.println("nom: " + nom);
        System.out.println("mail: " + mail);
        System.out.println("taona: " + taona);
        return "Vous avez tape dans le paramètre nom = " + nom + " et dans le mail = " + mail + " et dans le mois " + String.valueOf(taona) + " chiffres de type double";
    }

    
    @Get("/insertionObjet")
    public String insertionObject(@Param("poste") String poste, @Param("num") String num, @Param("emp") Employer emp) {
        // Verifications de nullite et instructions de debogage
        if (poste == null) {
            return "Le paramètre poste est null";
        }
        if (num == null) {
            return "Le paramètre num est null";
        }
        if (emp == null) {
            return "L'objet Employer est null";
        }
        if (emp.getNom() == null) {
            return "L'attribut nom de Employer est null";
        }
        if (emp.getEmail() == null) {
            return "L'attribut email de Employer est null";
        }
        // if (emp.getVola() == 0) {
        //     return "L'attribut vola de Employer est null";
        // }

        return "Vous avez tape dans le paramètre object avec attribut nom = " + emp.getNom() +
            " et l'attribut mail = " + emp.getEmail() +
            " avec de l'argent " + emp.getVola() +
            " qui a le poste " + poste +
            " et le numero " + num;
    }
}
