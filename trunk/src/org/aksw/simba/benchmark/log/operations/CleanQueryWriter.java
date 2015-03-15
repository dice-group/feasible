package org.aksw.simba.benchmark.log.operations;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.aksw.simba.bigrdfbench.util.QueryStatistics;
import org.aksw.simba.bigrdfbench.util.Selectivity;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

public class CleanQueryWriter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public static void writeCleanQueriesWithStats ( HashMap<String, Set<String>> queries, String endpoint, String graph, String outputQueryFile) throws QueryEvaluationException, RepositoryException, MalformedQueryException, IOException
	{
		BufferedWriter	bw= new BufferedWriter(new FileWriter(outputQueryFile));	
		long count =0;
		long zeroCount =1;
		long errorCount = 1;
		Set<String> selectQueries = queries.get("select");
		long datasetSize = Selectivity.getEndpointTotalTriples(endpoint, graph);
		System.out.println("Total log queries: "+ selectQueries.size());
		for(String query:selectQueries)
		{  	long resultSize = Selectivity.getQueryResultSize(query, endpoint);
		//System.out.println(query);
		//System.out.println("resultSize: "+ resultSize);
		if (resultSize>0)
		{
			System.out.println(count+ ": writing started...");
			count++;
			try {
				String queryStats = "#-------------------------------------------------------\nQuery No: "+count+"\n";  //new query identifier
				queryStats = queryStats + "Query Type: SELECT \n";
				queryStats = queryStats + "Results Size: "+resultSize  +"\n";
				queryStats = queryStats+QueryStatistics.getQueryStats(query,endpoint,graph,datasetSize);
				queryStats = queryStats+"Query String: "+java.net.URLEncoder.encode(query, "UTF-8")+"\n";
				//System.out.println(queryStats);
				bw.write(queryStats);
				} catch (Exception e) {//	System.err.println("Syntax Error in query: " + query); 
			//System.err.println(e.getMessage());
			errorCount++; }  
		}
		else
		{
			
			zeroCount++;
			//System.err.println("\n\n -----------\n Query is not considered due to zero results " );
			//System.out.println(query);

		}
		}
		bw.write("#--end---");
		bw.close();
		System.out.println("Query with stats successfully writen to "+outputQueryFile+"\nTotal queries with error: " + errorCount);
		System.out.println(zeroCount+ " results in zero resultset and thus were not considered.");
	}
}
