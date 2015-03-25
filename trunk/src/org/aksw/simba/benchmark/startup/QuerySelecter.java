package org.aksw.simba.benchmark.startup;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.aksw.simba.benchmark.Config;
import org.aksw.simba.benchmark.clustring.QueryClustering;
import org.aksw.simba.benchmark.log.operations.CleanQueryReader;
/**
 * From  here you start your benchmark generation
 * @author Saleem
 *
 */
public class QuerySelecter {

	public static void main(String[] args) throws IOException {
		String queryFileWithStats = "CleanQueries.txt";
		// You can set various Filters on benchmark query features and SPARQL clauses . e.g  Resultsize should be between 5 to 10 and BGPs must be greater than 2
		// and Triple patterns should be less or equal to 10 or Mean triple pattern selectivity >= 0.0001
		//See the config file for futher deatils
		//Config.featureFilter = "(ResultSize >= 5 AND ResultSize <= 100 AND BGPs >= 2 AND TriplePatterns <=10) OR (MeanTriplePatternsSelectivity >= 0.0001)";
		//Config.clauseFilter = "(OPTIONAL AND DISTINCT) OR (UNION)";
		// I am not interested in DESCRIBE Quereis
		Config.DESCRIBE = false; 
		long curTime = System.currentTimeMillis();
		Set<String> queries = getBenchmarkQueries(queryFileWithStats,10);
		System.out.println("Total Queries: "+ queries.size());
		int count = 1;
		for(String query:queries)
		{
			String [] qryParts = query.split("Query String:");
			System.out.println(count+":----------------------\nQuery Features: \n\n"+qryParts[0]+ "\nQuery: \n"+qryParts[1].trim());
			count++;
		}
		System.out.println("\n-----\nBenchmark generation time (sec): "+(System.currentTimeMillis()-curTime)/1000);

	}
	/**
	 * Get benchmark queries 
	 * @param queryFileWithStats Clean queries file with stats
	 * @param numberOfQueries The number of queries to be included in benchmark
	 * @return queries The required benchmark queries 
	 * @throws IOException
	 */
	public static Set<String> getBenchmarkQueries(String queryFileWithStats,int numberOfQueries) throws IOException {
		Map<String, Double[]> normalizedVectors = 	CleanQueryReader.getNormalizedFeaturesVectors(queryFileWithStats);
		QueryClustering qc = new QueryClustering();
		Set<String> queriesIds = qc.getPrototypicalQueries(normalizedVectors,numberOfQueries);
		Set<String> queries = CleanQueryReader.getQueriesWithStats(queryFileWithStats,queriesIds);
		return queries;
	}

}
