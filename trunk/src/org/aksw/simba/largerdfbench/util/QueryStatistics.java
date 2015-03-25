package org.aksw.simba.largerdfbench.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.aksw.simba.hibiscus.hypergraph.HyperGraph.HyperEdge;
import org.aksw.simba.hibiscus.hypergraph.HyperGraph.Vertex;
import org.aksw.sparql.query.algebra.helpers.BGPGroupGenerator;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.repository.RepositoryException;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

/**
 * Generate various query statistics. 
 * @author Saleem
 *
 */
public class QueryStatistics {

	public static final int boundObjLiteralCount = 0;
	public static long boundSbjCount, boundPredCount, boundObjURICount, BoundObjLiteralCount,grandTotalTriplePatterns = 0 ; 

	public static void main(String[] args) throws RepositoryException, MalformedQueryException, QueryEvaluationException, IOException {

		String inputDir= "../BigRDFBench-Utilities/queries/";
		File folder = new File(inputDir);
		File[] listOfFiles = folder.listFiles();
		long count = 1; 
		for (File qryFile : listOfFiles)
		{	
			BufferedReader br = new BufferedReader(new FileReader(inputDir+qryFile.getName()));
			String line;
			String queryStr="";
			while ((line = br.readLine()) != null) {
				queryStr= queryStr+" "+line;
			}
			br.close();

			System.out.println("--------------------------------------------------------------\n"+count+ ": "+qryFile.getName()+" Query: " + queryStr);
			//	System.out.println(getQueryStats(queryStr));
			getUsedSPARQLClauses(queryStr);
			count++;
		}
		//		System.out.println("\n\n--------------% triple pattern bound tuples distribution over all queries--------------------------------");
		//		System.out.println("Bound subject:" + (100*(double)boundSbjCount/grandTotalTriplePatterns));
		//		System.out.println("Bound Predicate:" + (100*(double)boundPredCount/grandTotalTriplePatterns));
		//		System.out.println("Bound Object URI:" + (100*(double)boundObjLiteralCount/grandTotalTriplePatterns));
		//		System.out.println("Bound Object Literal:" + (100*(double)boundObjLiteralCount/grandTotalTriplePatterns));

	}

	/**
	 * return query statistics as string
	 * @param query SPARQL query
	 * @param endpoint endpoint url
	 * @param graph Default Graph can be null
	 * @param datasetSize Dataset size
	 * @throws MalformedQueryException
	 * @throws QueryEvaluationException 
	 * @throws RepositoryException 
	 */
	public static String getQueryStats(String query, String endpoint, String graph, long datasetSize) throws MalformedQueryException, RepositoryException, QueryEvaluationException {
		String stats =  getDirectQueryRelatedStats(query); // Query type, total triple patterns, join vertices, mean join vertices degree
		//System.out.println(stats);
		stats = stats +"Mean triple patterns selectivity: " +Selectivity.getMeanTriplePatternSelectivity(query,endpoint,graph,datasetSize)+"\n";
		//System.out.println(stats);
		stats = stats+ getUsedSPARQLClauses(query);
		//System.out.println(stats);
		return stats; 
	}
	/**
	 * Get the important set of SPARQL clauses used in the query
	 * @param query SPARQL quer
	 * @return stats SPARQL clauses as string line by line
	 * @throws MalformedQueryException
	 */
	public static String getUsedSPARQLClauses(String query) throws MalformedQueryException {
		String stats = ""; 
		SPARQLParser parser = new SPARQLParser();
		ParsedQuery parsedQuery = parser.parseQuery(query, null);
		//System.out.println(parsedQuery);
		TupleExpr tQuery = parsedQuery.getTupleExpr();
		//System.out.println(tQuery);
		String queryLines [] = tQuery.toString().split("\n");
		boolean FoundUnion = false,FoundDistinct = false ,FoundOrderBy = false, FoundRegex = false; 
		for(String line:queryLines)
		{
			if(line.trim().equals("Union") && FoundUnion == false)
				FoundUnion = true;
			if(line.trim().equals("Distinct") && FoundDistinct == false)
				FoundDistinct = true;
			if(line.trim().equals("Order") && FoundOrderBy == false)
				FoundOrderBy = true;
			if(line.trim().equals("Regex") && FoundRegex == false)
				FoundRegex = true;
		}

		if(FoundUnion == false)
			stats = stats +"UNION: No \n" ;
		else
			stats = stats +"UNION: Yes \n" ;

		if(FoundDistinct == false)
			stats = stats +"DISTINCT: No \n" ;
		else
			stats = stats +"DISTINCT: Yes \n" ;

		if(FoundOrderBy == false)
			stats = stats +"ORDER BY: No \n" ;
		else
			stats = stats +"ORDER BY: Yes \n" ;

		if(FoundRegex == false)
			stats = stats +"REGEX: No \n" ;
		else
			stats = stats +"REGEX: Yes \n" ;

		Query jenaQuery = QueryFactory.create(query);
		jenaQuery.getGraphURIs().clear();
		if(jenaQuery.hasLimit())
			stats = stats +"LIMIT: Yes \n" ;
		else
			stats = stats +"LIMIT: No \n" ;

		//		if (query.toString().contains("limit"))
		//			stats = stats +"LIMIT: Yes \n" ;
		//		else
		//			stats = stats +"LIMIT: No \n" ;
		// jenaQuery.
		if(jenaQuery.hasOffset())
			stats = stats +"OFFSET: Yes \n" ;
		else
			stats = stats +"OFFSET: No \n" ;


		//		if (tQuery.toString().contains("offset="))
		//			stats = stats +"OFFSET: Yes \n" ;
		//		else
		//			stats = stats +"OFFSET: No \n" ;

		if (query.toLowerCase().contains("optional ")|| query.toLowerCase().contains("optional\n")||query.toLowerCase().contains("optional{"))
			stats = stats +"OPTIONAL: Yes \n" ;
		else
			stats = stats +"OPTIONAL: No \n" ;

		if (query.toLowerCase().contains("filter ") || query.toLowerCase().contains("filter("))
			stats = stats +"FILTER: Yes \n" ;
		else
			stats = stats +"FILTER: No \n" ;

		if(query.toLowerCase().contains("group by"))
			stats = stats +"GROUP BY: Yes \n" ;
		else
			stats = stats +"GROUP BY: No \n" ;

		return stats;
	}
	/**
	 * Get the benchmark query features ( e.g resultsize, bgps mean join vertices etc)
	 * @param query SPARQL query
	 * @return stats Query Features as string
	 * @throws MalformedQueryException
	 */
	public static String getDirectQueryRelatedStats(String query) throws MalformedQueryException {
		String stats = "";
		HashMap<Integer, List<StatementPattern>> bgpGrps =  BGPGroupGenerator.generateBgpGroups(query);
		//System.out.println("Basic Graph Patterns (BGPs): " +bgpGrps.size());
		stats = stats +"Basic Graph Patterns (BGPs): " +bgpGrps.size()+"\n";
		long totalTriplePatterns = 0;
		HashSet<Vertex> joinVertices = new HashSet<Vertex>();
		HashSet<Vertex> vertices = new HashSet<Vertex>();
		for(int DNFkey:bgpGrps.keySet())  //DNFgrp => bgp
		{
			HashSet<Vertex> V = new HashSet<Vertex>();   //--Set of all vertices used in our hypergraph. each subject, predicate and object of a triple pattern is one node until it is repeated
			List<StatementPattern>	 stmts =  bgpGrps.get(DNFkey);
			totalTriplePatterns = totalTriplePatterns + stmts.size();
			for (StatementPattern stmt : stmts) 
			{		
				String sbjVertexLabel, objVertexLabel, predVertexLabel;
				Vertex sbjVertex, predVertex,objVertex ;
				//--------add vertices---
				sbjVertexLabel = getSubjectVertexLabel(stmt);
				predVertexLabel = getPredicateVertexLabel(stmt);
				objVertexLabel = getObjectVertexLabel(stmt);
				sbjVertex = new Vertex(sbjVertexLabel);
				predVertex = new Vertex(predVertexLabel);
				objVertex = new Vertex(objVertexLabel);
				if(!vertexExist(sbjVertex,V))
					V.add(sbjVertex);
				if(!vertexExist(predVertex,V))
					V.add(predVertex);
				if(!vertexExist(objVertex,V))
					V.add(objVertex);
				//--------add hyperedges
				HyperEdge hEdge = new HyperEdge(sbjVertex,predVertex,objVertex);
				if(!(getVertex(sbjVertexLabel,V)==null))
					sbjVertex = getVertex(sbjVertexLabel,V);
				if(!(getVertex(predVertexLabel,V)==null))
					predVertex = getVertex(predVertexLabel,V);
				if(!(getVertex(objVertexLabel,V)==null))
					objVertex = getVertex(objVertexLabel,V);
				sbjVertex.outEdges.add(hEdge); predVertex.inEdges.add(hEdge); objVertex.inEdges.add(hEdge);
			}
			vertices.addAll(V) ;
			joinVertices.addAll(getJoinVertexCount(V));
			// V.clear();
		}
		grandTotalTriplePatterns = grandTotalTriplePatterns + totalTriplePatterns;
		stats = stats +"Triple Patterns: "+ totalTriplePatterns+"\n";
		//System.out.println("Triple Patterns: " +totalTriplePatterns);
		//System.out.println("Total Vertices:"+vertices.size() + " ==> "+vertices);
		//System.out.println("Join Vertices: " +joinVertices.size()+" ==> "+joinVertices);
		stats = stats+"Join Vertices: " + joinVertices.size()+"\n";
		//System.out.println("Join Vertices to Total Vertices ratio: " +(double)joinVertices.size()/(double)vertices.size());
		double meanJoinVertexDegree = 0;
		//	String joinVertexType = "" ;   // {Star, path, hybrid, sink}
		for(Vertex jv:joinVertices)
		{
			long joinVertexDegree = (jv.inEdges.size() + jv.outEdges.size());
			meanJoinVertexDegree = meanJoinVertexDegree + joinVertexDegree;
		}
		if(joinVertices.size()==0)
			stats = stats +"Mean Join Vertices Degree: 0 \n";
		else
			stats = stats +"Mean Join Vertices Degree: "+ +(meanJoinVertexDegree/joinVertices.size())+"\n";
		return stats;	
	}

	/**
	 * Get the the list of join vertices
	 * @param Vertices List of vertices in BGP
	 * @return V Collection of join vertices
	 */
	public static HashSet<Vertex> getJoinVertexCount(HashSet<Vertex> Vertices) {
		HashSet<Vertex> V = new HashSet<Vertex>();
		for (Vertex vertex:Vertices)
		{
			long inDeg = vertex.inEdges.size();
			long outDeg = vertex.outEdges.size();
			long degSum = inDeg + outDeg;
			if(degSum>1)
				V.add(vertex);
		}
		return V;
	}
	/**
	 * Get label for the object vertex of a triple pattern
	 * @param stmt triple pattern 
	 * @return label Vertex label
	 */
	public static String getObjectVertexLabel(StatementPattern stmt) {
		String label ; 
		if (stmt.getObjectVar().getValue()!=null)
		{
			label = stmt.getObjectVar().getValue().stringValue();
			if(label.startsWith("http://") || label.startsWith("ftp://"))
				boundObjURICount++;
			else
				BoundObjLiteralCount++;
		}
		else
			label =stmt.getObjectVar().getName(); 
		return label;

	}
	/**
	 * Get label for the predicate vertex of a triple pattern
	 * @param stmt triple pattern 
	 * @return label Vertex label
	 */
	public static String getPredicateVertexLabel(StatementPattern stmt) {
		String label ; 
		if (stmt.getPredicateVar().getValue()!=null)
		{
			label = stmt.getPredicateVar().getValue().stringValue();
			boundPredCount++;
		}
		else
			label =stmt.getPredicateVar().getName(); 
		return label;

	}
	/**
	 * Get label for the subject vertex of a triple pattern
	 * @param stmt triple pattern 
	 * @return label Vertex label
	 */
	public static String getSubjectVertexLabel(StatementPattern stmt) {
		String label ; 
		if (stmt.getSubjectVar().getValue()!=null)
		{
			label = stmt.getSubjectVar().getValue().stringValue();
			boundSbjCount++;
		}
		else
			label =stmt.getSubjectVar().getName(); 
		return label;

	}

	/**
	 * Check if a  vertex already exists in set of all vertices
	 * @param sbjVertex
	 * @param V Set of all vertices
	 * @return 
	 * @return value Boolean value
	 */
	public static  boolean vertexExist(Vertex sbjVertex, HashSet<Vertex> V) {
		for(Vertex v:V)
		{
			if(sbjVertex.label.equals(v.label))
				return true;
		}
		return false;
	}
	/**
	 * Retrieve a vertex having a specific label from a set of Vertrices
	 * @param label Label of vertex to be retrieved
	 * @param V Set of vertices
	 * @return Vertex if exist otherwise null
	 */
	public static Vertex getVertex(String label, HashSet<Vertex> V) {
		for(Vertex v:V)
		{
			if(v.label.equals(label))
				return v;
		}
		return null;
	}


}