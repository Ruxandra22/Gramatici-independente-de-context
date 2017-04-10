import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 * 
 * @author Osman Maria - Ruxandra
 * Grupa 334CA
 * Clasa principala
 *
 */
public class Main {

	public static void main(String[] args) {
		BufferedReader br;
		boolean argumentError = false;
		boolean syntaxError = false;
		boolean semanticError = false;
		boolean argumentIsVoid = false;
		boolean argumentHasE = false;
		boolean argumentUseless = false;
		
		//daca nu avem argument in linia de comanda -> "Argument error"
		if(args.length != 1) {
			
			System.err.println("Argument error");
			argumentError = true;
		}
		else if(args[0].equals("--is-void")) {
			argumentIsVoid = true;
		}
		else if(args[0].equals("--has-e")) {
			argumentHasE = true;
		}
		else if(args[0].equals("--useless-nonterminals")) {
			argumentUseless = true;
		}
		else {
			System.err.println("Argument error");
			argumentError = true;
		}
		
		//daca nu avem "Argument error", verificam si celelalte erori
		if(!argumentError) {
			try {
				br = new BufferedReader(new FileReader("grammar"));
				Flexer scanner = new Flexer(br);
				//deschiderea fisierului flex
				scanner.yylex();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//afisarea erorilor de sintaxa
			if(Flexer.checkSyntaxErrors) {
				syntaxError = true;
				System.err.println("Syntax error");
			}
			
			//daca nu sunt erori sintactice, verificam daca exista erori semantice
			if(!syntaxError) {
				// 1) verificam daca E este inclus in V
				for(String e : Flexer.listE) {
					if(!Flexer.listV.contains(e)) {
						semanticError = true;
					}
				}
				
				// 2) verificam daca exista terminali in V care nu sunt in E
				for(String e : Flexer.listV) {
					char c = e.charAt(0);
					if(Character.isLowerCase(c)) {
						if(!Flexer.listE.contains(Character.toString(c))) {
							semanticError = true;
						}
					}
				}
				
				// 3) verificam daca simbolul de start face parte din V/E
				if(!Flexer.listV.contains(Flexer.startSymbol)) {
					semanticError = true;
				}
				
				// 4) verificam daca partea din stanga a unei reguli apartine V/E
				for (String key : Flexer.mapRules.keySet()) {
					if(!Flexer.listV.contains(key)) {
						semanticError = true;
					}
				}
				
				// 5) verificam daca partea dreapta din fiecare regula apartine lui V*
				for(String key: Flexer.mapRules.keySet()) {
				    for(int i = 0; i < Flexer.mapRules.get(key).size(); i ++) {
				    	for(int j = 0; j < Flexer.mapRules.get(key).get(i).size(); j ++) {
				    		String letter = Flexer.mapRules.get(key).get(i).get(j);
				    		if(!letter.equals("e")) {
					    		if(!Flexer.listV.contains(letter)) {
					    			semanticError = true;
					    		}
				    		}
				    	}
				    }
				}
				
				if(semanticError) {
					System.err.println("Semantic error");
				}
				
				//daca nu avem nici erori de semantica, atunci putem raspunde la intrebari
				if(!semanticError) {
					Question q = new Question();
					
					if(argumentIsVoid) {
						//daca este adevarat -> "Yes"
						//altfel	-> "No"
						if(q.isVoid()) {
							System.out.println("Yes");
						}
						else {
							System.out.println("No");
						}
					}
					
					else if(argumentHasE) {
						//daca este adevarat -> "Yes"
						//altfel	-> "No"
						if(q.hasE()) {
							System.out.println("Yes");
						}
						else {
							System.out.println("No");
						}
					}
					else if(argumentUseless) {
						q.uselessNonTerminals();
						
						//afisam toti neterminalii inutili
						for(String e : q.useless) {
							System.out.println(e);
						}
					}
				}
			}
		}
	}
}
