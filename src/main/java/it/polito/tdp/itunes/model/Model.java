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

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	private Graph<Track,DefaultWeightedEdge> grafo;
	private ItunesDAO dao;
	private Map<Integer,Track> mapId;
	
	public Model() {
		this.dao = new ItunesDAO();
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
}
