package komiwojazer.rmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import komiwojazer.MapUtil;
import komiwojazer.GeneticAlgorithm.Costs;
import komiwojazer.GeneticAlgorithm.Path;
import komiwojazer.Map.City;
import komiwojazer.Map.CityMap;

public class Server extends UnicastRemoteObject implements ServerInterface{

	private static final long serialVersionUID = -2065432534190043743L;
	
	private float crossProb, mutateProb;
	private int generationsCount, populationCount;
	
	private Costs costArray;
	private int[] nodeNumbers;
	
	private CityMap map;
	
	private List<Integer> clientRegistry;

	public Server() throws RemoteException {
		super();
		init();
	}

	public Server(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
		super(port, csf, ssf);
		init();
	}

	public Server(int port) throws RemoteException {
		super(port);
		init();
	}
	
	private void init(){
		clientRegistry = new ArrayList<Integer>();
	}
	

	@Override
	public float getMutationProbability() throws RemoteException {
		return mutateProb;
	}
	
	public void setMutationProbability(float prob){
		this.mutateProb = prob;
	}

	@Override
	public float getCrossProbability() throws RemoteException {
		return crossProb;
	}
	public void setCrossProbability(float prob){
		crossProb = prob;
	}

	@Override
	public int getPopulationCount() throws RemoteException {
		return populationCount;
	}
	public void setPopulationCount(int count){
		populationCount = count;
	}

	@Override
	public int getGenerationsCount() throws RemoteException {
		return generationsCount;
	}
	
	public void setGenerationsCount(int count){
		generationsCount = count;
	}

	@Override
	public Costs getCostArray() throws RemoteException {
		return costArray;
	}
	
	public void setCostArray(Costs c){
		costArray = c;
	}

	@Override
	public int[] getNodeNumbers() throws RemoteException {
		return nodeNumbers;
	}
	
	public void setNodeNumbers(int[] num){
		nodeNumbers = num;
	}
	
	public void setMap(CityMap m){
		map = m;
	}

	@Override
	public List<Path> register(int clientID) throws RemoteException {
		return null;
	}

	@Override
	public List<Path> newGeneration(int clientID, List<Path> currentGeneration) throws RemoteException {
		return null;
	}
	
	public static void main(String[] args){
		if (System.getSecurityManager() == null) {
		    System.setSecurityManager(new RMISecurityManager());
		}
		try {
			Server s = new Server(7878);
			s.setCrossProbability(0.6f);
			s.setMutationProbability(0.03f);
			s.setGenerationsCount(1000);
			s.setPopulationCount(10000);
			CityMap m = readMap("graph.txt");
			MapUtil mu = new MapUtil(m);
			s.setNodeNumbers(mu.getNodeNumbers());
			s.setCostArray(mu.getCostArray());
			s.setMap(m);
			Naming.rebind("//localhost:7878/ParalellGeneticServer", s);
			System.out.println("Server started on port 7878");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
