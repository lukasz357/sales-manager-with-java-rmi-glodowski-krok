package komiwojazer.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import komiwojazer.GeneticAlgorithm.Costs;
import komiwojazer.GeneticAlgorithm.Path;

public interface ServerInterface extends Remote{
	
	/*
	 * wspolne dla wszystkich klientow
	 */
	public float getMutationProbability() throws RemoteException;
	public float getCrossProbability() throws RemoteException;
	public int getPopulationCount() throws RemoteException;
	public int getGenerationsCount() throws RemoteException;
	public Costs getCostArray() throws RemoteException;
	public int[] getNodeNumbers() throws RemoteException;
	public int getTourCount() throws RemoteException;
	
	
	/**
	 * wywolujac te metode, klient podaje swoj id, czeka az serwer pozwoli
	 * mu sie dolaczyc i w rezultacie otrzymuje populacje na ktorej ma pracowac
	 */
	public List<Path> register(int clientID) throws RemoteException;
	
	/**
	 * To jest wywolywane na koniec danego pokolenia. klient zglasza najlepszych osobnikow
	 * (na razie cala populacja), ktorzy zostana polaczeni z innymi najlepszymi od pozostalych klientow.
	 * Wynikowa populacja najlepszych zostanie zwrocona klientowi do dalszych obliczen.
	 */
	public List<Path> newGeneration(int clientID, List<Path> currentGeneration) throws RemoteException;
}
