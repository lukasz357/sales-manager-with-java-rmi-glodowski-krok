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

    	
        MapUtil or = new MapUtil(m);
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
    
    
    
    
    
}
