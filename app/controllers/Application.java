package controllers;

import java.util.Map;

import genBot2.*;
import play.*;
import play.cache.Cache;
import play.mvc.*;
import views.html.*;
import models.*;

import java.rmi.*;


public class Application extends Controller {
	private static RMIInterface rmiConnect() {
		RMIInterface genBotRMI = null;
		try {
			genBotRMI = (RMIInterface) Naming.lookup("//127.0.0.1/rmiImpl");
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
		return genBotRMI;		
	}

    public static Result index() {
    	return ok("a");
    	// Load GenBotWrapper
        //GenBotWrapper genBot = (GenBotWrapper) Cache.get("genBot");
        
        //genBot = new GenBotWrapper();
        // If not exist create new one
        /*if(genBot == null) {
        	genBot = new GenBotWrapper();
	    	genBot.init();
        }
        
        try {
        	genBot.manager.evolve();
        } catch (FitnessNotSetException e) {
        	e.printStackTrace();
        } 
        //genBot.manager.getGenerationNumber()()
        //Cocktail[] cList = genBot.manager.getCocktailGeneration().getPopulation();
        
        Cache.set("genBot", genBot); 
        //return ok(gen.render("afe", genBot.manager, cList));
        return ok(genNew.render("afe", genBot));
        //return ok("a");   */
    }
    
    public static Result indexRMI() {
    	// Load GenBotWrapper
    	RMIInterface genBotRMI = rmiConnect();
    	if(genBotRMI == null) {
    		return ok("ERROR");
    	} else {
    		
    		try {
    			Cocktail[] population = genBotRMI.getPopulation();
    			return ok("length:" + population.length);
    		} catch (Exception e) {
    			return ok("ERROR2");
    		}
    	}
    }

    public static Result generation(Long id) {
        //return ok(index.render("Your new application is ready."));
        //return ok("talking about my generation");
    	return ok(index.render("blup"));
    }

    public static Result setFitness() {
    	Map<String, String[]> parameters = request().body().asFormUrlEncoded();
    	
    	return ok(parameters.get("fitness")[0]);
    }
}
