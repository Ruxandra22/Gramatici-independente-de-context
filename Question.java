import java.util.LinkedHashSet;
/**
 * 
 * @author Osman Maria - Ruxandra
 * Grupa 334CA
 * 
 * Clasa in care verificam limbajele pentru a raspunde la cele 3 intrebari:
 * 		- daca limbajul generat de o gramatica este vid
 * 		- daca limbajul generat contine sirul vid
 * 		- care sunt simbolurile neterminale inutile 
 *
 */

public class Question {

	//lista de marcaje pentru neterminalii utili
	public static LinkedHashSet<String> marksList = new LinkedHashSet<String>();
	//lista pentru neterminalii nefolositori
	public LinkedHashSet<String> useless = new LinkedHashSet<String>();
	//lista pentru neterminalii care contin si pot genera e
	public LinkedHashSet<String> haveEList = new LinkedHashSet<String>();
	
	
	/**
	 * Metoda care verifica daca limbajul generat poate fi vid
	 * 		=> doar daca simbolul de start este inutil
	 */
	public boolean isVoid() {
		
		uselessNonTerminals();
		if(useless.contains(Flexer.startSymbol)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Metoda care adauga in prima etapa in lista cu neterminali care pot genera e
	 */
	public void addInHasEList() {
		
		int lowerCaseCount = 0;
		//se apeleaza aceasta functie pentru a determina neterminalii nefolositori
		uselessNonTerminals();
		
		//parcurgem toata lista de neterminali folositori
		for(String key: marksList) {
			
			//luam fiecare litera in parte din dreapa fiecarei reguli
			for(int i = 0; i < Flexer.mapRules.get(key).size(); i ++) {
				for(int j = 0; j < Flexer.mapRules.get(key).get(i).size(); j ++) {
					
					int size = Flexer.mapRules.get(key).get(i).size();
					String letter = Flexer.mapRules.get(key).get(i).get(j);
					char c = letter.charAt(0);
					
					//daca este litera mica si este "e", crestem numarul de litere mici 
					if(Character.isLowerCase(c) && letter.equals("e")) {
						lowerCaseCount ++;
					}
					
					//daca toate literele mici sunt "e"
					if(lowerCaseCount == size) {
						haveEList.add(key);
					}
				}
				lowerCaseCount = 0;
			}
		}
	}
	
	/**
	 * Metoda care verifica daca limbajul generat contine e
	 * 		=> doar daca simbolurile care sunt utile contin numai e
	 * 			si daca mai sunt simboluri utile care sunt deja in haveEList
	 */
	public boolean hasE() {
		
		//adaugam doar simbolurile care contin doar e
		addInHasEList();
		int count = 0;
		int upperCaseCount = 0;
		
		//adaugam si simbolurile care contin in regula neterminali care au fost deja
		//adaugati in haveEList
		while(count != haveEList.size()) {
			count = haveEList.size();
			
			for(String key: marksList) {
				for(int i = 0; i < Flexer.mapRules.get(key).size(); i ++) {
					for(int j = 0; j < Flexer.mapRules.get(key).get(i).size(); j ++) {
						
						int size = Flexer.mapRules.get(key).get(i).size();
						String letter = Flexer.mapRules.get(key).get(i).get(j);
						char c = letter.charAt(0);
						//vedem cate litere mari care sunt deja in haveEList avem
						if(Character.isUpperCase(c) && haveEList.contains(letter)){
							upperCaseCount ++;
						}
						
						//daca toate literele sunt mari si sunt deja in haveEList
						//atunci adaugam si cheia respectiva in haveEList
						if(upperCaseCount == size) {
							haveEList.add(key);
						}
					}
					upperCaseCount = 0;
				}
			}
		}
		//daca haveEList contine simbolul de start => true, altfel => false
		if(haveEList.contains(Flexer.startSymbol)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Metoda care determina neterminalii nefolositori
	 * Se adauga initial in marksList neterminalii care contin doar terminali in regula
	 * adaugam ulterior si neterminalii care au in regula neterminali care sunt deja in marksList
	 */
	public void uselessNonTerminals() {
		
		boolean isUpperCase;
		int upperCaseCount = 0;
		int lowerCaseCount = 0;
		
		for(String key: Flexer.mapRules.keySet()) {
		    for(int i = 0; i < Flexer.mapRules.get(key).size(); i ++) {
		    	isUpperCase = false;
		    	for(int j = 0; j < Flexer.mapRules.get(key).get(i).size(); j ++) {
		    		String letter = Flexer.mapRules.get(key).get(i).get(j);
		    		char c = letter.charAt(0);
		    		//daca gasesc o litera care nu este mica, atunci nu marchez cheia
		    		if(Character.isUpperCase(c)) {
		    			isUpperCase = true;
		    		}
		    	}
		    	//daca nicio litera nu este mare =? avem numai litere mici
		    	// => marcam cheia
		    	if(!isUpperCase) {
		    		marksList.add(key);
				    break;
		    	}
		    }
		}
		
		int count = 0;
		//parcurg din nou regulile si daca gasesc litere mari care sunt deja in marksList
	    //atunci adaug si cheia corespunzatoare lor in marksList
		while(count != marksList.size() && !marksList.isEmpty()) { 
			count = marksList.size();
			for(String key: Flexer.mapRules.keySet()) {
				for(int i = 0; i < Flexer.mapRules.get(key).size(); i ++) {
			    	int lettersCount = 0;
			    	for(int j = 0; j < Flexer.mapRules.get(key).get(i).size(); j ++) {
			    		String letter = Flexer.mapRules.get(key).get(i).get(j);
			    		char c = letter.charAt(0);
			    		//vedem cate litere mari contine tuplul
			    		if(Character.isUpperCase(c)) {
			    			upperCaseCount ++;
			    		}
			    		//vedem cate litere mici contine tuplul
			    		if(Character.isLowerCase(c)) {
			    			lowerCaseCount ++;
			    		}
			    		//daca gasesc o litera mare care este deja marcata, cresc numarul de litere mari gasite si deja marcate
			    		if(Character.isUpperCase(c) && marksList.contains(Character.toString(c))) {
			    			lettersCount ++;
			    		}
			    	}
			    	//daca avem numai litere mari si nicio litera mica, atunci adaug cheia in marksList	
			    	if(lettersCount == upperCaseCount && lowerCaseCount != Flexer.mapRules.get(key).get(i).size()) {
			    		marksList.add(key);
			    	}
			    	upperCaseCount = 0;
			    	lowerCaseCount = 0;
			    }
			}
		}
		
		//il lista useless adaugam toate elementele care sunt in V, dar nu sunt in marksList
		for(String element : Flexer.listV) {
			char c = element.charAt(0);
			if(Character.isUpperCase(c) && !marksList.contains(element)) {
				useless.add(element);
			}
		}
	}
}