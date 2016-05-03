package org.aksw.simba.dataset.lsq;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

import org.aksw.simba.benchmark.encryption.EncryptUtils;
import org.aksw.simba.benchmark.log.operations.DateConverter.DateParseException;
import org.aksw.simba.benchmark.log.operations.LinkedGeoDataLogReader;
import org.aksw.simba.benchmark.log.operations.SesameLogReader;
import org.aksw.simba.largerdfbench.util.QueryStatistics;
import org.aksw.simba.largerdfbench.util.Selectivity;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sparql.SPARQLRepository;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
/**
 * Generat Linked Log RDF dataset 
 * @author Saleem
 *
 */
public class LogRDFizer {
	public static BufferedWriter 	bw ;
	public  RepositoryConnection con = null;
	public static  BufferedWriter tobw= null;
	public static long queryNo = 1;
	public  static int  maxRunTime ;  //max query execution time in seconds
	public int runtimeErrorCount;
	public long endpointSize = 0;
	public static void main(String[] args) throws IOException, RepositoryException, MalformedQueryException, QueryEvaluationException, ParseException, DateParseException {
		//String queryLogDir = "D:/QueryLogs/SWDF-Test/";  //dont forget last /
		 // String queryLogDir = "/home/MuhammadSaleem/dbpedia351logs/";
		  String queryLogDir = "D:/QueryLogs/USEWOD2014/data/LinkedGeoData/";
		// String queryLogDir = "D:/QueryLogs/RKBExplorer/";
				 
		 String acronym = "LGD" ; //  a short acronym of the dataset
		
		 String localEndpoint = "http://linkedgeodata.org/sparql";
		// String localEndpoint = "http://linkedgeodata.org/sparql";
		
		//String graph = "http://aksw.org/benchmark"; //can be null
		String graph = "http://linkedgeodata.org"; //can be null
		//String graph = null;
		
		String outputFile = "Linked-SQ-LGD-Aidan.ttl";
		//String outputFile = "LinkedDBpedia351SQL.ttl";
		//String outputFile = "Linked-SQ-DBpedia-Fixed.ttl";
		
		// String publicEndpoint = "http://data.semanticweb.org/sparql";
		//String publicEndpoint = "http://dbpedia.org/sparql";
		String publicEndpoint = "http://linkedgeodata.org/sparql";
		
		maxRunTime = 900;
		tobw = new BufferedWriter(new FileWriter("timeOutQueries.txt"));
		String separator = "- -";   // this is separator which separates  the agent ip and corresponding exe time. can be null if there is no user I.P provided in log
		//String separator = null;  //null is when IP is missing. like in BM
		
		//SesameLogReader slr = new SesameLogReader();
		// DBpediaLogReader dblr = new DBpediaLogReader();
		//RKBExplorerLogReader rkblr = new RKBExplorerLogReader();
		LinkedGeoDataLogReader lglr = new LinkedGeoDataLogReader();
		
		LogRDFizer rdfizer = new LogRDFizer();
		
		//Map<String, Set<String>> queryToSubmissions = slr.getSesameQueryExecutions(queryLogDir);  // this map contains a query as key and their all submissions
	//	Map<String, Set<String>> queryToSubmissions = dblr.getVirtuosoQueryExecutions(queryLogDir);  // this map contains a query as key and their all submissions
	//	Map<String, Set<String>> queryToSubmissions = rkblr.getBritishMuseumQueryExecutions(queryLogDir); 
		Map<String, Set<String>> queryToSubmissions = lglr.getVirtuosoQueryExecutions(queryLogDir);
		
		System.out.println(queryToSubmissions.keySet().size());
		
		System.out.println("Number of Distinct queries: " +  queryToSubmissions.keySet().size());
		rdfizer.rdfizeLog(queryToSubmissions,localEndpoint,publicEndpoint,graph,outputFile,separator,acronym);
		System.out.println("Dataset stored at " + outputFile);
	}
	/**
	 * RDFize Log	
	 * @param queryToSubmissions A map which store a query string (single line) as key and all the corresponding submissions as List. Where a submission is a combination
	 * of User encrypted ip and the data,time of the query request. The I.P and the time is separated by a separator
	 * @param localEndpoint Endpoint which will be used for feature generation
	 * @param publicEndpoint Public endpoint of the log
	 * @param graph named Graph, can be null
	 * @param baseURI  Base URI of the log dataset
	 * @param outputFile The output RDF file
	 * @param separator Submission separator. Explained above
	 * @param acronym A Short acronym of the dataset log, e.g., DBpedia or SWDF
	 * @throws IOException
	 * @throws RepositoryException
	 * @throws MalformedQueryException
	 * @throws QueryEvaluationException
	 * @throws ParseException 
	 */
	public void rdfizeLog(Map<String, Set<String>> queryToSubmissions, String localEndpoint, String publicEndpoint, String graph, String outputFile, String separator, String acronym) throws IOException, RepositoryException, MalformedQueryException, QueryEvaluationException, ParseException {
	
	}

	
	/**
	 * Get all executions (IP,Time) of the given query
	 * @param query Query
	 * @param submissions  Query submissions in form of IP:Time
	 * @param separator String separator between IP:Time 
	 * @return Stats
	 * @throws ParseException 
	 */
	public String getRDFUserExecutions(Set<String> submissions, String separator) throws ParseException {
		String queryStats = "\nlsqrd:q"+(LogRDFizer.queryNo-1);
		queryStats = queryStats+ " lsqv:execution ";
		int subCount = 1;
		for(int i=0; i<submissions.size();i++)
		{
			if(i<submissions.size()-1)
			{
				queryStats = queryStats + "lsqrd:q"+(queryNo-1)+"-e"+subCount+ " , ";
			}
			else
			{
				queryStats = queryStats + "lsqrd:q"+(queryNo-1)+"-e"+subCount+ " . \n ";
			}
			subCount++;
		}
		int j = 1;
		if(!(separator==null))  //i.e both I.P and Time is provided in log
		{
		for(String submission:submissions)
		{
			String prts [] = submission.split(separator);
			String txt=prts[0].replace(".", "34x49") ;
			String key="key phrase used for XOR-ing";
			txt=EncryptUtils.xorMessage( txt, key );
			String encoded=Base64.getEncoder().encodeToString( txt.getBytes() ); 
			encoded = encoded.replace("=", "-");
			encoded = encoded.replace("+", "-");
		   	queryStats = queryStats + "lsqrd:q"+(queryNo-1)+"-e"+j+ " lsqv:agent lsqr:A-"+encoded+"  ; dct:issued \""+prts[1]+"\"^^xsd:dateTimeStamp . \n";
			j++;
		}
		}
		else  //only exe time is stored
		{
			for(String submission:submissions)
			{
				queryStats = queryStats + "lsqrd:q"+(queryNo-1)+"-e"+j+ " dct:issued \""+submission+"\"^^xsd:dateTimeStamp . \n";
				j++;
			}
			
		}
		return queryStats;

	}


	/**
	 * Get result size of the given query
	 * @param queryStr Query
	 * @param localEndpoint Endpoint url where this query has to be executed
	 * @param sesameQueryType Query type {SELECT, ASK, CONSTRUCT, DESCRIBE}
	 * @return ResultSize
	 * @throws RepositoryException
	 * @throws MalformedQueryException
	 * @throws IOException
	 */
	public long getQueryResultSize(String queryStr, String localEndpoint,String sesameQueryType) throws RepositoryException, MalformedQueryException, IOException
	{
		long totalSize = -1;
		this.initializeRepoConnection(localEndpoint);
		if(sesameQueryType.equals("select") || sesameQueryType.equals("ask") )
		{
			try {
				if (sesameQueryType.equals("select"))
				{
					TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,queryStr );
					//System.out.println(queryStr);
					tupleQuery.setMaxQueryTime(maxRunTime);
					TupleQueryResult res;
					res = tupleQuery.evaluate();
					//System.out.println(res);
					totalSize = 0;
					while(res.hasNext())
					{
						res.next();
						totalSize++;
					}
				}
				else
				{
					BooleanQuery booleanQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL,queryStr );
					//System.out.println(queryStr);
					booleanQuery.setMaxQueryTime(maxRunTime);
					booleanQuery.evaluate();
					//System.out.println(res);
					totalSize = 1;

				}

			} catch (QueryEvaluationException ex) { 
				String runtimeError = ex.getMessage().toString().replace("\"", "'").replaceAll("\n", " ").replace("\r", "");
				if(runtimeError.length()>1000)  //this is to avoid sometime too big errors
					runtimeError = "Unknown runtime error";
				bw.write(" lsqv:runtimeError \""+runtimeError+ "\" ; ");
				runtimeErrorCount++;
			}
		}
		else
		{
			try {
				GraphQuery gq = con.prepareGraphQuery(QueryLanguage.SPARQL, queryStr);
				gq.setMaxQueryTime(maxRunTime);
				GraphQueryResult graphResult = gq.evaluate();
				totalSize = 0;
				while (graphResult.hasNext()) 
				{
					graphResult.next();
					totalSize++;
				}
			} catch (QueryEvaluationException ex) {
				String runtimeError = ex.getMessage().toString().replace("\"", "'").replaceAll("\n", " ").replace("\r", "");
				if(runtimeError.length()>1000)  //this is to avoid sometime too big errors
					runtimeError = "Unknown runtime error";
				bw.write(" lsqv:runtimeError \""+runtimeError+ "\" ; ");
				runtimeErrorCount++;
			}

		}
		con.close();
		return totalSize;
	}
	/**
	 * Write RDF Prefixes
	 * @param acronym Acronym of the dataset e.g. DBpedia or SWDF
	 * @throws IOException
	 */
	public void writePrefixes(String acronym) throws IOException {
		bw.write("@prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> . \n");
		bw.write("@prefix lsqr:<http://lsq.aksw.org/res/> . \n");
		bw.write("@prefix lsqrd:<http://lsq.aksw.org/res/"+acronym+"-> . \n");
		bw.write("@prefix lsqv:<http://lsq.aksw.org/vocab#> . \n");
		bw.write("@prefix sp:<http://spinrdf.org/sp#> . \n");
		bw.write("@prefix void:<http://rdfs.org/ns/void#> . \n");
	    bw.write("@prefix dct:<http://purl.org/dc/terms/> . \n");
	    bw.write("@prefix xsd:<http://www.w3.org/2001/XMLSchema#> . \n");
	    bw.write("@prefix sd:<http://www.w3.org/ns/sparql-service-description#> . \n\n");
	    	    

	}
		
	/**
	 * Initialize repository for a SPARQL endpoint
	 * @param endpointUrl Endpoint Url
	 * @throws RepositoryException
	 */
	public void initializeRepoConnection(String endpointUrl) throws RepositoryException {
		Repository repo = new SPARQLRepository(endpointUrl);
		repo.initialize();
		con = repo.getConnection();

	}

}