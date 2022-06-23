package it.polito.tdp.itunes.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	private Graph<Track,DefaultWeightedEdge> grafo;
	private ItunesDAO dao;
	private Map<Integer,Track> mapId;
	private List<Track> best;
	
	public Model() {
		this.dao = new ItunesDAO();
		this.best = new LinkedList<Track>();
	}
	
	public String creaGrafo(Genre g) {
		this.grafo = new SimpleWeightedGraph<Track,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.mapId = new HashMap<Integer,Track>();
		List<Track> vertici = dao.getTracksWithGenre(g,this.mapId);
		Graphs.addAllVertices(this.grafo, vertici);
		
		List<Coppia> listaC = dao.getCoppie(g);
		
		for(Coppia c : listaC) {
			if(c.getPeso()>0) {
				Graphs.addEdgeWithVertices(this.grafo, this.mapId.get(c.getT1()), this.mapId.get(c.getT2()), c.getPeso());
			}
		}
		
		return "#VERTICI: "+this.grafo.vertexSet().size()+"\n#ARCHI: "+this.grafo.edgeSet().size();		
		
	
	}
	
	public List<CoppiaM> getMigliori(){
		double max = 0.0;
		List<CoppiaM> listaM = new LinkedList<CoppiaM>();
		
		for(DefaultWeightedEdge d : this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(d)>max) {
				max = this.grafo.getEdgeWeight(d);
			}
		}
		
		for(DefaultWeightedEdge d : this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(d) == max) {
				Track t1 = this.grafo.getEdgeSource(d);
			    Track t2 = this.grafo.getEdgeTarget(d);
			    double peso = this.grafo.getEdgeWeight(d);
			    CoppiaM c = new CoppiaM(t1, t2, peso);
			    listaM.add(c);
			}
		}
		return listaM;
	}
	
	public List<Genre> getGenre(){
		return dao.getAllGenres();
	}
	
	
	public List<Track> calcolaInsieme(Track t, int mem){
		List<Track> parziale = new LinkedList<Track>();
		List<Track> connesse = getConnesse(t);
		parziale.add(t);
		best.add(t);
		ricorsione(parziale,connesse,mem,1);
		return best;
	}
	
private void ricorsione(List<Track> parziale, List<Track> connesse, int mem, int l) {
		//nonPossoAggiungerePiuNullaACausaDellaMem
	   
	if(sommaMem(parziale)>mem) {
	    	parziale.remove(parziale.size()-1);
	    	if(parziale.size()> best.size()) {
	    		best = new LinkedList<>(parziale);
	    	}
	    	return;
	    }
	  
	  //nonCiSonoPiuCanzoniDaAggiungere
	  if(l==connesse.size()+1) {
		  if(parziale.size()> best.size()) {
	    		best = new LinkedList<>(parziale);
	    	}
	    	return;
	  }
	  
	    parziale.add(connesse.get(l-1));
		ricorsione(parziale, connesse, mem, l+1);
		
		parziale.remove(connesse.get(l-1));
		ricorsione(parziale, connesse, mem, l+1);
	  
	    
		
	}

public List<Track> getConnesse (Track c)  {
		
		GraphIterator<Track,DefaultWeightedEdge> visita = new BreadthFirstIterator<>(this.grafo,c);
		List<Track> listaConnesse = new LinkedList<Track>();
		 
		 while(visita.hasNext()) {
		 Track t = visita.next();
		 if(t.equals(c)==false) {
			 listaConnesse.add(t);
		 }
	    }
		 
		 return listaConnesse;
	}
	
 public double sommaMem (List<Track> parziale) {

	 int somma = 0;
	 
	 for(Track t : parziale) {
		 somma += t.getBytes();
	 }
	 return somma;
 }
	
 public List<Track> getV(){
	 List<Track> v = new LinkedList<>(this.grafo.vertexSet());
	 return v;
 }
	
}
