import java.util.ArrayList;
import java.util.HashMap;
%%

%class Flexer
%unicode
/*%debug*/
%int
%line
%column

%{
	static ArrayList<String> listV = new ArrayList<String>();
	static ArrayList<String> listE = new ArrayList<String>();
	//valoarea din HashMap va fi o lista de liste
	static HashMap<String, ArrayList<ArrayList<String>>> mapRules = new HashMap<String, ArrayList<ArrayList<String>>>();
	//mai intai trebuie adaugate in listForMap, iar apoi listForMap este adaugata in mapRules
	static ArrayList<ArrayList<String>> listForMap = new ArrayList<ArrayList<String>>();
	static ArrayList<String> interiorList;
	static char initialState;
	static String key;
	static String value;
	static String startSymbol;
	//pentru a verifica daca avem erori de sintaxa
	static boolean checkSyntaxErrors = false;
%}


LineTerminator = \r|\n|\r\n
WS = {LineTerminator} | [ \t\f]
special = "-"|"="|"["|"]"|";"|"\\"|"."|"/"|"~"|"!"|"@"|"#"|"$"|"%"|"^"|"&"|"*"|"_"|"+"|"\""|":"|"|"|"<"|">"|"?"|"'"|"`"
terminals = [a-d]|[f-z]
nonterminals = [A-Z]
//terminalsWithoutE = [a-d]|[f-z]

Terminals = {terminals} | [:digit:] | {special}
TermOrNonterm = {Terminals} | {nonterminals}
NonTerminals = {nonterminals}
TermNonTermOrE = {Terminals} | {NonTerminals} | "e"
//WithoutE = {terminalsWithoutE} | {nonterminals}
%state S1 V S2 S3 S4 E S5 S6 S7 S8 R S9 S10 S11 S12 S13 S14 S15 S16 STATE S17 FINAL
/*
*/

%%

{WS}    {/*Ignora spatiile albe din orice stare*/}

<YYINITIAL>"(" {
	
    yybegin(S1);
}

<S1>"{" {
	yybegin(V);
}

<V> {TermOrNonterm} {
	//este terminal sau neterminal si se adauga in lista respectiva
	yybegin(S2);
	String symbol = yytext();
	listV.add(symbol);
}

//momentul in care se intalneste un separator "," sau "}"
<S2> {
	","	{
		yybegin(V); 
	}
	"}"	{
		yybegin(S3);
	}
}

<S3>"," {
	yybegin(S4);
}

<S4>"{" {
	yybegin(E);
}

<E> {
	"}" {
		yybegin(S7);
	}
	{Terminals} {

		yybegin(S5);
		String symbol = yytext();
		listE.add(symbol);
	}
}

<S5> {
	"," {
		yybegin(S6);
	}
	"}" {
		yybegin(S7);
	}
}

<S6>{Terminals} {
	yybegin(S5);
	String symbol = yytext();
	listE.add(symbol);
}

<S7>"," {
	yybegin(S8);
}

<S8>"{" {

	yybegin(R);
}

<R> {
	"}" {
		yybegin(S16);
	}
	"(" {
		yybegin(S9);
	}
}

<S9> {NonTerminals} {
	yybegin(S10);
	key = yytext();
	//in S11 se adauga in listForMap si dupa in mapRules
}

<S10>"," {
	yybegin(S11);
}

<S11> {
	{TermNonTermOrE}	{
		
		
		value = yytext();
		//adaug in lista interioara
		interiorList = new ArrayList<String>();
		interiorList.add(value);
		yybegin(S12);
	}
}

<S12> {
	{TermOrNonterm} {

		value = yytext();
		interiorList.add(value);
		yybegin(S12);
	}

	")" {
		yybegin(S13);
		if(mapRules.containsKey(key)) {

			mapRules.get(key).add(interiorList);
		}
		else {
			listForMap = new ArrayList<ArrayList<String>>();
			listForMap.add(interiorList);
			mapRules.put(key, listForMap);
		}	
	}
}

<S13> {

	"," {
		yybegin(S14);
	}

	"}" {
		yybegin(S16);
	}
}

<S14>"(" {

	yybegin(S15);
}

<S15>{NonTerminals} {

	yybegin(S10);
	key = yytext();
	
}

<S16>"," {

	yybegin(STATE);
}

<STATE> {NonTerminals} {
	
	yybegin(S17);
	startSymbol = yytext();
}

<S17>")" {

	yybegin(FINAL);
}

<FINAL> "," {
}

. {
	checkSyntaxErrors = true;
}
