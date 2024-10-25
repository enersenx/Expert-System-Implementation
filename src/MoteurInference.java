import java.util.*;


/* "MoteurInference" est la classe principale contenant la methode de chainage avant */
public class MoteurInference {
    
    private List<Quant> baseFait;
    private List<Regle> regles;
    
    /* Le constructeur de la classe "MoteurInference". Il prend en entrée deux arguments à savoir "baseFait" qui contient les faits 
     * initiaux et "regles" qui contient l'ensemble des listes     */
    
    public MoteurInference(List<Quant> baseFait, List<Regle> regles) {
        this.baseFait = baseFait;
        this.regles = regles;
    }

    /*  La méthode principale "chainage avant"   */
    //le probleme ici est que quand on trouve le but on ne s'arrete pas, on est en train d'appliquer toutes les règles possibles 
    //jsq à se qu'on ai plus de nouveaux faits, puis dans le main on vérifie si le but appartient à la base de fait
    public boolean chainageAvant(Quant fait, List<Quant> BF, List<Regle> BR) {
        boolean res = false;
        boolean success = false;
        for(Quant bf : BF) {
    		//à modifier
    		if(bf.var.equals(fait.var) && compare(bf.value, fait.comp, fait.value)) {
    			success = true;
    			break;
    		}
    	}
    	//si le fait est déjà dans la BF
    	if (success ) {
            return true; // SUCCES
        } else {
            List<Regle> reglesNonDeclenchees = new ArrayList<>(BR);
            List<Regle> reglesAConsiderer = new ArrayList<>(BR);
           
            
            while (!reglesAConsiderer.isEmpty() && !res) {
                List<Regle> conflits = new ArrayList<>();
                //print base de fait
                System.out.println("\n\nBF: ");
                for (Quant f : baseFait) {
            		System.out.println(f.var + " " + f.comp + " " + f.value);
            	}
            	System.out.println("~~~~~~~~~~~~");
                
                while (!reglesAConsiderer.isEmpty()){
                	Regle r = choisir(reglesAConsiderer);//choisir regle avec max premisses
                	reglesAConsiderer.remove(r);
                	if (toutesLesPremissesSontVraies(r)) {//verifier si regle applicable
                		conflits.add(r);
                	}
                }
                	//print conflits
                System.out.println("\n\nConflits: ");
	                for (Regle c : conflits) {
	            		System.out.println(c.getConclusion().var + ", ");
	            	}
	                System.out.println("----------- ");
	                
                	Regle r = choisir(conflits);
                    BF.add(r.getConclusion());
                    reglesNonDeclenchees.remove(r);//enlever règle appliquée
                    reglesAConsiderer = new ArrayList<>(reglesNonDeclenchees);//les règles à considérer sont les règles non appliquées
                    //vérfier si le but est atteint
                    if (r.getConclusion().var.equals(fait.var) && compare(r.getConclusion().value, fait.comp, fait.value) ) {
                        return true;
                    
                }
            }
        }
        return res;
    }


//    private static Regle choisir(Set<Regle> regles) {
//        // implémentation de l'algorithme de choix des règles
//        // par exemple, on peut choisir la règle ayant le plus grand nombre de prémisses
//        return Collections.max(regles, Comparator.comparingInt(r -> r.getPremises().size()));
//    }

    private Regle choisir(List<Regle> regles) {
    	
        int maxPremisses = -1;
        Regle regleChoisie = null;
        for (Regle r : regles) {
            if (r.getPremises().size() > maxPremisses) {
                maxPremisses = r.getPremises().size();
                regleChoisie = r;
            } else if (r.getPremises().size() == maxPremisses && (regleChoisie == null || regles.indexOf(r) < regles.indexOf(regleChoisie))) {
                // if two rules have the same number of premisses, choose the one with the smaller index
                regleChoisie = r;
            }
        }
        return regleChoisie;
    }
    

    /* Cette méthode permet de vérifier si toutes les prémisses d'une règle sont vraies en parcourant la liste des prémisses, 
     * en les comparant avec les faits dans baseFait et en renvoyant false 
     * si une prémisses est fausse et true si toutes les prémisses sont vraies.  */
    private boolean toutesLesPremissesSontVraies(Regle regle) {
    	
    	boolean exists = true;
    	for (Quant premisses : regle.getPremises()) {
    	    // check if the variable is in the base de fait
    		System.out.println("\n\nregle a verifiee: ");
    		System.out.println(regle.getConclusion().var);
    		System.out.println("premisse a verifiee: ");
    		System.out.println(premisses.var);
    	    boolean found = false;
    	    System.out.println("BF comparaison: ");
    	    for (Quant bf : baseFait) {
    	    	
    	    	System.out.print(bf.var+ " == ");
//    	    	if(bf.var.equals("taille")) {
//    	    		System.out.println("\nTAILLE ");
//    	    		System.out.println(bf.var.equals(premisses.var)+ " /"+ compare(bf.value, premisses.comp, premisses.value)+ "\n");
//    	    		
//    	    	}
    	    	
    	        if (bf.var.equals(premisses.var) && compare(bf.value, premisses.comp, premisses.value)) {
    	        	System.out.println("c true!! ");
    	            found = true;
    	            break;
    	        }
    	    }
    	    
    	    if (!found && !premisses.value.equals("false") ) {
    	        exists = false;
    	        break;
    	        
    	    }else if(!found && premisses.value.equals("false")){
    	    	System.out.println("c true pas dans bf!! ");
	            
    	    }
    	}
    	return exists;
    }

    
    
    
    // Fonction chaînageArriere
public boolean chainageArriere(List<Regle> BR, List<Quant> BF, List<Quant> listeButs) {
    if (listeButs.isEmpty()) {
    	
        return true; // SUCCES
    } else {
    	Quant premierBut = listeButs.get(0);
        if (demBut(BR, BF, premierBut)) {
        	System.out.print("BF AR: {");
        	for (Quant f : BF) {
        		System.out.print(f.var + ", ");
        	}
        	System.out.print("}\n");
            return chainageArriere(BR, BF, listeButs.subList(1, listeButs.size())); // suite(listeButs)
        } else {
            return false; // ECHEC
        }
    }
}

// Fonction demBut
public boolean demBut(List<Regle> BR, List<Quant> BF, Quant but) {
	boolean success = false;
	for(Quant bf : BF) {
		//à modifier
		if(bf.var.equals(but.var) && compare(bf.value, but.comp, but.value)) {
			success = true;
			break;
		}
	}
	
	if (success ) {
        return true; // SUCCES
    } 
	else {
        Set<Regle> regles = new HashSet<Regle>(BR);
        boolean res = false; // ECHEC
        while (!regles.isEmpty() && !res) {
            Regle r = choix(regles);
            regles.remove(r);
            if ((conclusion(r).var).equals(but.var) && compare(but.value,conclusion(r).comp,conclusion(r).value)) {
            	res = chainageArriere(BR, BF, premisse(r));
            }
        }
        if (res) {
            BF.add(but);
        }
        return res;
    }
}
//Fonction verify


// Fonction choix
public Regle choix(Set<Regle> BR) {
    return BR.iterator().next();
}

// Fonctions auxiliaires
public List<Quant> premisse(Regle r) {
    return r.getPremises();
}

public Quant conclusion(Regle r) {
    return r.getConclusion();
}


//resultat de la comparaison dans le quant
//on n'a pas de premisse qui prend un bool une fois 
public Boolean compare(String bfval,String comp, String pval){
	
	if(bfval.equals("true") || bfval.equals("false")) {
		boolean bfvalc = Boolean.parseBoolean(bfval);
        boolean pvalc = Boolean.parseBoolean(pval);
        // comparer les booleans
        //System.out.println(bfvalc == pvalc);
        return (bfvalc == pvalc);
	}
	else {
		float bfvalf = Float.parseFloat(bfval);
        float pvalf = Float.parseFloat(pval);
		//comparer les float
		switch(comp) {
			case "<":
				return bfvalf<=pvalf;
			case">":
				return bfvalf>=pvalf;
			case"=":
				return bfvalf==pvalf;
			}
	}
	return null;
 
	}
    
    public static void main(String[] args) {
    	
    	
    	
        // initialiser la base de faits avec des faits initiaux
    	List<Quant> baseFait = new ArrayList<>();
    	//exemple titanosauria
//        baseFait.add(new Quant("pubis_arriere","=", "false"));
//        baseFait.add(new Quant("pubis_avant","=", "true"));
//        baseFait.add(new Quant("marche_sur","=", "4"));
//        baseFait.add(new Quant("taille",">", "20"));
//        baseFait.add(new Quant("cou_epais","=", "true"));
//        baseFait.add(new Quant("mange_herbe","=", "true"));
    	//exemple stegosausore
    	  baseFait.add(new Quant("pubis_arriere","=", "true"));
	      baseFait.add(new Quant("pubis_avant","=", "false"));
	      baseFait.add(new Quant("blinde","=", "true"));
	      baseFait.add(new Quant("visage_cornu","=", "true"));
	      baseFait.add(new Quant("assiete_osseuse_dos","=", "true"));
	      baseFait.add(new Quant("marche_sur","=", "4"));
	      baseFait.add(new Quant("taille",">", "6"));
      
        
        // initialiser la base de faits avec des faits initiaux
        List<Quant> baseFait2 = new ArrayList<>();
      //exemple titanosauria
//        baseFait2.add(new Quant("pubis_arriere","=", "false"));
//        baseFait2.add(new Quant("pubis_avant","=", "true"));
//        baseFait2.add(new Quant("marche_sur","=", "4"));
//        baseFait2.add(new Quant("taille",">", "20"));
//        baseFait2.add(new Quant("cou_epais","=", "true"));
//        baseFait2.add(new Quant("mange_herbe","=", "true"));
        //exemple stegosausore
          baseFait2.add(new Quant("pubis_arriere","=", "true"));
	      baseFait2.add(new Quant("pubis_avant","=", "false"));
	      baseFait2.add(new Quant("blinde","=", "true"));
	      baseFait2.add(new Quant("visage_cornu","=", "true"));
	      baseFait2.add(new Quant("assiete_osseuse_dos","=", "true"));
	      baseFait2.add(new Quant("marche_sur","=", "4"));
	      baseFait2.add(new Quant("taille",">", "6"));
        
        // définir quelques règles
        List<Regle> regles = new ArrayList<>();
        
        regles.add(new Regle(Arrays.asList(new Quant("mange_viande","=", "true")), new Quant("carnivore","=", "true")));
        regles.add(new Regle(Arrays.asList(new Quant("mange_herbe","=", "true")), new Quant("herbivore","=", "true")));
        regles.add(new Regle(Arrays.asList(new Quant("marche_sur","=", "2")), new Quant("bipede","=", "true")));
        regles.add(new Regle(Arrays.asList(new Quant("marche_sur","=", "4")), new Quant("quadrupede","=", "true")));
        regles.add(new Regle(Arrays.asList(new Quant("mange_viande","=", "true"), new Quant("mange_herbe","=", "true")), new Quant("omnivore","=", "true")));

        regles.add(new Regle(Arrays.asList(new Quant("pubis_arriere","=", "false"),new Quant("pubis_avant","=", "true")), new Quant("saurischia","=", "true")));

        regles.add(new Regle(Arrays.asList(new Quant("saurischia","=", "true"),new Quant("carnivore","=", "true"),new Quant("bipede","=", "true")), new Quant("therapode","=", "true")));
        regles.add(new Regle(Arrays.asList(new Quant("saurischia","=", "true"),new Quant("herbivore","=", "true"),new Quant("quadrupede","=", "true")), new Quant("sauropode","=", "true")));
        
        regles.add(new Regle(Arrays.asList(new Quant("therapode","=", "true"),new Quant("doigts","=", "3"),new Quant("membres_posterieurs_alloges","=", "true")), new Quant("coelurosaure","=", "true")));
        regles.add(new Regle(Arrays.asList(new Quant("therapode","=", "true"),new Quant("doigts","=", "3"),new Quant("membres_anterieurs_alloges","=", "true"),new Quant("plumes","=", "true"),new Quant("griffe_faucille","=", "true")), new Quant("dromaeosauridae","=", "true")));
        regles.add(new Regle(Arrays.asList(new Quant("therapode","=", "true"),new Quant("bec","=", "true")), new Quant("oviraptorosauria","=", "true")));
        regles.add(new Regle(Arrays.asList(new Quant("therapode","=", "true"),new Quant("taille",">", "12"),new Quant("tete_massive","=", "true"),new Quant("machoire_inferieure_allongee","=", "true")), new Quant("allosauridae","=", "true")));
        regles.add(new Regle(Arrays.asList(new Quant("therapode","=", "true"),new Quant("taille","<", "6"),new Quant("tete_massive","=", "true"),new Quant("machoire_inferieure_allongee","=", "true")), new Quant("ceratosauria","=", "true")));
        regles.add(new Regle(Arrays.asList(new Quant("therapode","=", "true"),new Quant("plumes","=", "true"),new Quant("ailes","=", "true")), new Quant("avilae","=", "true")));
        regles.add(new Regle(Arrays.asList(new Quant("therapode","=", "true"),new Quant("crete_osseuse_dos","=", "true")), new Quant("spinosauridae","=", "true")));
        regles.add(new Regle(Arrays.asList(new Quant("therapode","=", "true"),new Quant("crete_double_crane","=", "true")), new Quant("dilophosauridae","=", "true")));
        regles.add(new Regle(Arrays.asList(new Quant("therapode","=", "true"),new Quant("tete_grande","=", "true"),new Quant("machoire_puissante","=", "true"),new Quant("bras_tres_petits","=", "true")), new Quant("tyrannosauridae","=", "true")));


        regles.add(new Regle(Arrays.asList(new Quant("sauropode","=", "true"),new Quant("taille",">", "20"),new Quant("cou_epais","=", "true")), new Quant("titanosauria","=", "true")));
        regles.add(new Regle(Arrays.asList(new Quant("sauropode","=", "true"),new Quant("taille",">", "20"),new Quant("cou_fin","=", "true")), new Quant("diplodocidae","=", "true")));
        
        regles.add(new Regle(Arrays.asList(new Quant("pubis_arriere","=", "true"),new Quant("pubis_avant","=", "false")), new Quant("ornithischia","=", "true")));
        regles.add(new Regle(Arrays.asList(new Quant("blinde","=", "true"),new Quant("ornithischia","=", "true")), new Quant("thyreophora","=", "true")));
        
        regles.add(new Regle(Arrays.asList(new Quant("visage_cornu","=", "true"),new Quant("assiete_osseuse_dos","=", "true"),new Quant("quadrupede","=", "true"),new Quant("taille",">", "5"),new Quant("thyreophora","=", "true")), new Quant("stegosausore","=", "true")));
        regles.add(new Regle(Arrays.asList(new Quant("visage_cornu","=", "false"),new Quant("batons_de_queue","=", "true"),new Quant("quadrupede","=", "true"),new Quant("taille",">", "5"),new Quant("thyreophora","=", "true")), new Quant("ankylosaur","=", "true")));

        regles.add(new Regle(Arrays.asList(new Quant("crane_epais","=", "true"),new Quant("bipede","=", "true"),new Quant("taille",">", "6"),new Quant("chyreophora","=", "true")), new Quant("cachycephalosauridae","=", "true")));
        regles.add(new Regle(Arrays.asList(new Quant("blinde","=", "false"),new Quant("ornithischia","=", "true")), new Quant("cerapode","=", "true")));
        regles.add(new Regle(Arrays.asList(new Quant("crane_epais","=", "true"),new Quant("bipede","=", "true"),new Quant("taille","<", "6"),new Quant("cerapode","=", "true")), new Quant("cachycephalausoria","=", "true")));


        regles.add(new Regle(Arrays.asList(new Quant("visage_cornu","=", "true"),new Quant("taille",">", "0"),new Quant("cerapode","=", "true")), new Quant("ceratopsian","=", "true")));
        
        
        /* Dans notre cas, notre but est de prouver (ou démenti) "I" */  
        //Quant fait = new Quant("titanosauria","=", "true");
        Quant fait = new Quant("stegosausore","=", "true");
        
        //créer le moteur d'inférence et l'exécuter sur la base de faits
        MoteurInference moteur = new MoteurInference(baseFait, regles);
        

        
        //verifier si la bf contient le but
        boolean estVrai = moteur.chainageAvant(fait, baseFait, regles);
        
        if(estVrai==true)System.out.println(fait.var + " est vrai " +"");
        else System.out.println(fait.var + " est faux " +"");
        
      //afficher base de fait
        for (Quant f : moteur.baseFait) {
    		System.out.println(f.var + ", ");
    	}
        System.out.println("---------------");
        // créer le moteur d'inférence et l'exécuter sur la base de faits
        MoteurInference moteur2 = new MoteurInference(baseFait2, regles);
        //List<Quant> listbuts = Arrays.asList(new Quant("titanosauria","=", "true"));
        List<Quant> listbuts = Arrays.asList(new Quant("stegosausore","=", "true"));
        
        moteur2.chainageArriere(regles, baseFait2, listbuts);
        /* Dans notre cas, notre but est de prouver (ou démenti) "I" */  
        
        boolean estVrai2 = false;
        for(Quant bf : moteur2.baseFait) {
    		if((bf.var.equals(listbuts.get(0).var))) {
    			
    			estVrai2 = true;
    			break;
    		}
        }
        if(estVrai2==true)System.out.println(listbuts.get(0).var + " est vrai " +"");
        else System.out.println(listbuts.get(0).var + " est faux " +"");
        //afficher base de fait
        for (Quant f : moteur2.baseFait) {
    		System.out.println(f.var + ", ");
    	}
        
    }
}

    /* C'est la classe qui représente la base de régles. Elle comporte deux variables représentent respectivement la 
     * liste des prémisses de la règle et sa conclusion. Les prémisses sont une liste de chaînes de caractères 
     * et la conclusion est une chaîne de caractères.  */
    class Regle {
        
        private List<Quant> premisses;
        private Quant conclusion;
        
        public Regle(List<Quant> premises, Quant conclusion) {
            this.premisses = premises;
            this.conclusion = conclusion;
        }
        
        public List<Quant> getPremises() {
            return premisses;
        }
        
        public Quant getConclusion() {
            return conclusion;
        }
        
        
    }

    class Quant 
    {
    	String var ;
    	String comp;
    	String value;
    	 public Quant(String var , String comp, String value) {
             this.var = var;
             this.comp = comp;
             this.value = value;
         }
    	 public String getvar() {
             return var;
         }
    	 public String getcomp() {
             return comp;
         }
    	 public String getvalue() {
             return value;
         }
         
    	
    }
