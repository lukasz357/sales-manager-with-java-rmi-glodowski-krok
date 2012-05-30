/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package komiwojazer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

import komiwojazer.FileParsers.ArgsParser.BadArgsException;
import komiwojazer.GeneticAlgorithm.Costs;
import komiwojazer.Map.City;
import komiwojazer.Map.CityMap;


/**
 *
 * @author Łukasz
 */
public class Main {

    public static void main(String[] args) throws BadArgsException, ClassNotFoundException, SQLException, IOException, FileFormatException {

    	CityMap m = readMap("graph.txt");
        Worker or = new Worker(m);
        // go(liczba_pokolen, liczebnosc_pokolenia
        ArrayList<City> result = or.go(1000, 100);
        StringBuffer sb = new StringBuffer();
        for(City c : result){
        	if(sb.length()>0) sb.append(" -> ");
        	sb.append(c.getVal());
        }
        System.out.println(sb); 
        System.out.println("Laczny koszt: "+or.getTotalLength());
    }
    
    
    
    private static CityMap readMap(String path) throws IOException {
    	TreeMap<String, City> nodes = new TreeMap<String, City>();
        String tmp; String [] line; int fileLine=1;

        BufferedReader in = new BufferedReader(new FileReader(new File(path)));
        //sprawdzam czy jest nagłówek Nodes:
        if(!(tmp = in.readLine()).equals("Nodes:")){
            throw new FileFormatException(1, "Brak naglowka \"Nodes:\"");
        }

        //wczytuję wierzchołki grafu
        while((tmp = in.readLine())!=null && !tmp.equals("Edges:") ){
            nodes.put(tmp, new City(tmp));
            fileLine++;
        }

        //sprawdzam czy jest nagłówek Edges:
        if(tmp==null){
            throw new FileFormatException(fileLine, "Brak naglowka \"Edges:\"");
        }else{
            //wczytuję połaczenia między wierzchołkami
            while((tmp = in.readLine()) != null){
                ++fileLine;
                line=tmp.split(" ");
                if(line.length!=4)
                    throw new FileFormatException(fileLine, "Bledna ilosc danych. Wymagane 4.");
                nodes.get(line[0]).AddOutgoingRoute(nodes.get(line[1]), Integer.parseInt(line[2]),Integer.parseInt(line[3]));
            }
        }
        in.close();

        return new CityMap(nodes);
    }
    
    static class FileFormatException extends IOException{
        public FileFormatException(int line, String message){
            super("Blad struktury pliku w linii " + line + ": " + message);
        }
    }
    
}
