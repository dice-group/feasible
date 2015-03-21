package org.aksw.simba.largerdfbench.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.aksw.sparql.query.algebra.helpers.BGPGroupGenerator;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sparql.SPARQLRepository;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;


/**
 * Dataset-restricted Filtered/Simple Triple pattern selectivities. 
 * @author Saleem
 *
 */
public class Selectivity {

	public static RepositoryConnection con = null;

	public static void main(String[] args) throws RepositoryException, MalformedQueryException, QueryEvaluationException, IOException {
		String inputDir= "../BigRDFBench-Utilities/queries/";
		String endpoint = "http://localhost:8890/sparql";
		String graph = null; //can be null
		long endpointSize = getEndpointTotalTriples(endpoint,graph);
		System.out.println("Dataset size: " + endpointSize);
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
			long queryResultSize = getQueryResultSize(queryStr,endpoint,"tuple");
			System.out.println(queryResultSize);
			double meanSel=	getMeanTriplePatternSelectivity(queryStr,endpoint,graph, endpointSize);
			
			System.out.println("Mean triple patterns selectivity: " + meanSel);
			count++;
		}

	}
	public static long getQueryResultSize(String queryStr, String endpoint, String sesameQueryType) throws RepositoryException, MalformedQueryException {
		long totalSize = -1;
		initializeRepoConnection(endpoint);
		try{
		Query jenaQuery = QueryFactory.create(queryStr);
		jenaQuery.getGraphURIs().clear();
		queryStr= jenaQuery.toString();
		//System.out.println(queryStr);
		}
		catch (QueryParseException e) {	//System.out.println(e.getMessage());	
		}
		
		if(sesameQueryType.equals("tuple"))
		{
	  	TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,queryStr );
		TupleQueryResult res;
		try {
			res = tupleQuery.evaluate();
			totalSize = 0;
			while(res.hasNext())
			{
				res.next();
				totalSize++;
			}
		} catch (QueryEvaluationException e) {	//System.out.println(e.getMessage());	
		}
		}
		else
		{
			try {
				GraphQueryResult graphResult = con.prepareGraphQuery(QueryLanguage.SPARQL, queryStr).evaluate();
				totalSize = 0;
				while (graphResult.hasNext()) 
				{
					graphResult.next();
					totalSize++;
				}
			} catch (QueryEvaluationException e) {//System.out.println(e.getMessage());
			}
			
		}
       con.close();
		return totalSize;

	}
	//	/**
	//	 * Load endpoint to results map from a file containing endpoint urls and corresponding results. This is good for big datasets where on-the-fly total results calculation need a lot of time. 
	//	 * @param file File containing resultset sizes of endpoints
	//	 * @throws IOException 
	//	 */
	//	public static void loadEPtoRSfromFile(String file) throws IOException {
	//		BufferedReader br = new BufferedReader(new FileReader(file));
	//		String line;
	//		while ((line = br.readLine()) != null) {
	//			String prts [] = line.split("\t");
	//			//System.out.println(prts[0]);
	//			// System.out.println(prts[1]);
	//			epToResults.put(prts[0],prts[1]);
	//		}
	//		br.close();	
	//	}
	//	/**
	//	 * Load those triple patterns whose selectivity is depended upon Filter clause into a set
	//	 * @param file File containing filter information. note: strict formating required
	//	 * @throws IOException
	//	 */
	//	public static void loadStatementFilters(String file) throws IOException {
	//		BufferedReader br = new BufferedReader(new FileReader(file));
	//		String line;
	//		while ((line = br.readLine()) != null) {
	//			String prts [] = line.split("\t");
	//			//System.out.println(prts[0]);
	//			// System.out.println(prts[1]);
	//			stmtFilters.put(prts[0],prts[1]);
	//		}
	//		br.close();
	//
	//	}
	/**
	 * get Mean Triple pattern selectivity
	 * @param query SPARQL query
	 * @param endpointSize Total number of triples patterns in the endpoint
	 * @param endpoint Endpoint Url
	 * @param graph Default graph can be null
	 * @return meanQrySel Mean triple pattern selectivity across complete query
	 * @throws MalformedQueryException
	 * @throws QueryEvaluationException 
	 * @throws RepositoryException 
	 */
	public static double getMeanTriplePatternSelectivity(String query, String endpoint,String graph, Long endpointSize) throws MalformedQueryException, RepositoryException, QueryEvaluationException {
		HashMap<Integer, List<StatementPattern>> bgpGrps =  BGPGroupGenerator.generateBgpGroups(query);
		//List<Double> meanTPSelectivities = new ArrayList<Double>()  ;
		//System.out.println("Basic Graph Patterns (BGPs): " +bgpGrps.size());
		long totalTriplePatterns = 0;
		double meanQrySel =0 ; //mean of the avg triple pattern selectivity of all the triple patterns in a query
		for(int DNFkey:bgpGrps.keySet())  //DNFgrp => bgp
		{
			List<StatementPattern>	 stmts =  bgpGrps.get(DNFkey);
			totalTriplePatterns = totalTriplePatterns + stmts.size();
			for (StatementPattern stmt : stmts) 
			{		
				String sbjVertexLabel, objVertexLabel, predVertexLabel;
				sbjVertexLabel = getSubjectVertexLabel(stmt);
				predVertexLabel = getPredicateVertexLabel(stmt);
				objVertexLabel = getObjectVertexLabel(stmt);
				String tp = sbjVertexLabel+"_"+predVertexLabel+"_"+objVertexLabel;
				double tpSel = 	getTriplePatternSelectivity(stmt,tp,endpoint,graph,endpointSize);
				meanQrySel  = meanQrySel+ tpSel;
				//meanTPSelectivities.add(tpSel);
				//System.out.println("Average (across all datasets) Triple pattern selectivity: "+ meanTripleSel);
			}
		}
		if(totalTriplePatterns==0)
			meanQrySel =0;
		else
		meanQrySel = meanQrySel/totalTriplePatterns;
		con.close();
		return meanQrySel;
		//System.out.println("\nMean query selectivity (average of the mean triple pattern selectivities): " + meanQrySel);
		// System.out.println(meanTPSelectivities);
		//	double stdv = getStandardDeviation(meanTPSelectivities,meanQrySel);
		//	System.out.println("Query Selectivities standard deviation: " + stdv );
		//	System.out.println("Triple Patterns: " +totalTriplePatterns);
	}
	public static long getEndpointTotalTriples(String endpoint, String graph) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		long totalSize = 0;
		initializeRepoConnection(endpoint);
		String queryStr ="";
		if(graph==null)
			queryStr = "SELECT count(?s) AS ?totalSize WHERE {?s ?p ?o}";
		else
			queryStr = "SELECT count(?s) AS ?totalSize FROM <"+graph+"> WHERE {?s ?p ?o}";
		TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,queryStr );
		TupleQueryResult res = tupleQuery.evaluate();
		while(res.hasNext())
			totalSize = Long.parseLong(res.next().getValue("totalSize").stringValue().toString());
		con.close();
		return totalSize;
	}
	//	/**
	//	 * Calculate standard deviation of given set of selectivities
	//	 * @param meanTPSelectivities Set of mean triple patterns selectivities
	//	 * @param mean Average of selectivities
	//	 * @return Standard Deviation
	//	 */
	//	public static double getStandardDeviation(List<Double> meanTPSelectivities, double mean) {
	//
	//		// sd is sqrt of sum of (values-mean) squared divided by n - 1
	//		// Calculate the mean
	//		//  double mean = 0;
	//		final int n = meanTPSelectivities.size();
	//		if ( n < 2 )
	//		{
	//			return Double.NaN;
	//		}
	//		// calculate the sum of squares
	//		double sum = 0;
	//		for ( int i=0; i<n; i++ )
	//		{
	//			final double v = meanTPSelectivities.get(i) - mean;
	//			sum += v * v;
	//		}
	//		// Change to ( n - 1 ) to n if you have complete data instead of a sample.
	//		return Math.sqrt( sum / ( n - 1 ) );
	//	}
	/**
	 * Get Triple pattern selectivity
	 * @param stmt Statement pattern
	 * @param tp  triples representation of statement pattern
	 * @param endpoint Endpoint Url
	 * @param graph Default graph can be null
	 * @param endpointSize Total number of triples in the endpoint
	 * @return Selectivity of the filtered triple pattern
	 * @throws RepositoryException
	 * @throws MalformedQueryException
	 * @throws QueryEvaluationException
	 */
	public static double getTriplePatternSelectivity(StatementPattern stmt,
			String tp, String endpoint, String graph, long endpointSize) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		double selectivity = 0 ;
		long resultSize = 0;
		if (stmt.getSubjectVar().getValue()==null && stmt.getPredicateVar().getValue()==null && stmt.getObjectVar().getValue()==null )
			selectivity = 1; 
		else
		{
			String queryString = getQueryString(stmt,tp,graph);
			//System.out.println("\nTriple pattern: " + triplePattern );
			//System.out.println(queryString);
			initializeRepoConnection(endpoint);
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult res = tupleQuery.evaluate();
			while(res.hasNext())
				resultSize = Long.parseLong(res.next().getValue("size").stringValue().toString());
			if(resultSize>0)
				selectivity = (double) resultSize/ endpointSize;
		}
		return selectivity;
	}
	/**
	 * Get triple pattern from statement pattern
	 * @param stmt Statement pattern
	 * @return triplePattern Triple pattern
	 */
	public static String getTriplePattern(StatementPattern stmt) {
		String subject = getSubject(stmt);
		String object = getObject(stmt);
		String predicate = getPredicate(stmt);
		String triplePattern = subject + predicate + object ;
		return triplePattern;
	}
	/**
	 * Get query to the count of results against triple pattern
	 * @param stmt Triple pattern
	 * @param tp Triple pattern as key for stmtFilters hash map. For checking whether stmt should contain Filter clause as well
	 * @param graph Default graph can be null
	 * @return query SPARQL query
	 */
	public static String getQueryString(StatementPattern stmt, String tp, String graph) {
		String query = "";
		String triplePattern = getTriplePattern(stmt);
		if(graph==null)
			query ="SELECT COUNT(*) AS ?size WHERE { "+triplePattern +"} ";
		else
			query = "SELECT COUNT(*) AS ?size FROM <"+graph+"> WHERE { "+triplePattern +"} ";

		return query;

	}

	/**
	 * Get Predicate from triple pattern
	 * @param stmt Triple pattern
	 * @return tuple Subject tuple
	 */
	public static String getPredicate(StatementPattern stmt) {
		String tuple;
		if (stmt.getPredicateVar().getValue()!=null)
			tuple = " <"+stmt.getPredicateVar().getValue().stringValue()+"> ";
		else
			tuple =" ?"+stmt.getPredicateVar().getName(); 
		return tuple;
	}
	/**
	 * Get object from triple pattern
	 * @param stmt Triple pattern
	 * @return tuple Subject tuple
	 */
	public static String getObject(StatementPattern stmt) {
		String tuple;
		if (stmt.getObjectVar().getValue()!=null && (stmt.getObjectVar().getValue().toString().startsWith("http://") || stmt.getObjectVar().getValue().toString().startsWith("ftp://")))
			tuple = " <"+stmt.getObjectVar().getValue().stringValue()+"> ";
		else if (stmt.getObjectVar().getValue()!=null)
			tuple = " '"+stmt.getObjectVar().getValue().stringValue()+"' ";
		else
			tuple =" ?"+stmt.getObjectVar().getName(); 
		return tuple;
	}
	/**
	 * Get subject from triple pattern
	 * @param stmt Triple pattern
	 * @return tuple Subject tuple
	 */
	public static String getSubject(StatementPattern stmt) {
		String tuple;
		if (stmt.getSubjectVar().getValue()!=null )
			tuple = "<"+stmt.getSubjectVar().getValue().stringValue() + "> ";
		else if (stmt.getSubjectVar().getValue()!=null )
			tuple = "'"+stmt.getSubjectVar().getValue().stringValue() + "' ";
		else
			tuple ="?"+stmt.getSubjectVar().getName(); 
		return tuple;
	}

	/**
	 * Get label for the object vertex of a triple pattern
	 * @param stmt triple pattern 
	 * @return label Vertex label
	 */
	public static String getObjectVertexLabel(StatementPattern stmt) {
		String label ; 
		if (stmt.getObjectVar().getValue()!=null)
			label = stmt.getObjectVar().getValue().stringValue();
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
			label = stmt.getPredicateVar().getValue().stringValue();
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
			label = stmt.getSubjectVar().getValue().stringValue();
		else
			label =stmt.getSubjectVar().getName(); 
		return label;

	}



	/**
	 * Initialize repository for a SPARQL endpoint
	 * @param endpointUrl Endpoint Url
	 * @throws RepositoryException
	 */
	public static void initializeRepoConnection(String endpointUrl) throws RepositoryException {
		Repository repo = new SPARQLRepository(endpointUrl);
		repo.initialize();
		con = repo.getConnection();

	}
}