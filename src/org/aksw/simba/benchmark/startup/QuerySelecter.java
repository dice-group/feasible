package org.aksw.simba.benchmark.startup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.simba.benchmark.Config;
import org.aksw.simba.benchmark.Queries;
import org.aksw.simba.benchmark.Similarity;
import org.aksw.simba.benchmark.clustring.QueryClustering;
import org.aksw.simba.benchmark.clustring.VoronoiPanel;
import org.aksw.simba.benchmark.comparisons.AvgStats;
import org.aksw.simba.benchmark.log.operations.CleanQueryReader;
/**
 * From  here you start your benchmark generation
 * @author Saleem
 *
 */
public class QuerySelecter {

	public static void main(String[] args) throws IOException {
		//--Configuration and input files specifications ------------
		if(args[0].equals("-help"))
			help();
		List<String> arguments = getList(args);
		if(!(args.length%2==0) || args.length==0){
			help();
		}
		if (!arguments.contains("-f")||!arguments.contains("-n")){
			System.out.println("The arguments -f <queries file> and -n <no of queries> are mendatory");
			help();
		}
		else
		{
						
				String queryFileWithStats = arguments.get(arguments.indexOf("-f")+1) ; 
			//	String queryFileWithStats = "DBpedia3.5.1-CleanQueries.txt";
				//String queryFileWithStats = "service_logs_bio2rdf_cleanqueries.txt";
				int numberOfQueries = Integer.parseInt(arguments.get(arguments.indexOf("-n")+1) );  // number of queries to be generated for a benchmark
				if(arguments.contains("-cf"))
					Config.clauseFilter = arguments.get(arguments.indexOf("-cf")+1) ;
				if(arguments.contains("-ff"))
					Config.featureFilter = arguments.get(arguments.indexOf("-ff")+1) ;
				if(arguments.contains("-tf")){
				Config.formFilter = arguments.get(arguments.indexOf("-tf")+1) ;
				Config.ASK=false; Config.SELECT=false ; Config.DESCRIBE=false; Config.CONSTRUCT=false;
				}
				//selectCustomFilters();
				long curTime = System.currentTimeMillis();
				//Set<String> queries = Queries.getBenchmarkQueries(queryFileWithStats,10);
				QueryClustering qc = new QueryClustering();
				Map<String, Double[]> normalizedVectors  = 	CleanQueryReader.getNormalizedFeaturesVectors(queryFileWithStats);
				Set<String> queriesIds = qc.getPrototypicalQueries(normalizedVectors,numberOfQueries);
				Set<String> benchmarkQueriesIds = new HashSet<String>(queriesIds); //we need to copy the query ids here/ later being used for similarity calculation. The next function will clear the queriesIds. So we have to copy before
				Set<String> queries = CleanQueryReader.getQueriesWithStats(queryFileWithStats,queriesIds);
//				int queryNo = 1;
//				for(String qry:queries){
//             	System.out.println(queryNo+"------------------\n"+qry);
//             	queryNo++;
//             	}
				String outputDir = "";
				Queries.printBenchmarkQueries(queries,outputDir);
				System.out.println("\n-----\nBenchmark details saved to "+outputDir+"\nBenchmark generation time (sec): "+(System.currentTimeMillis()-curTime)/1000);
				Double similarityScore = Similarity.getSimilarityScore(normalizedVectors,benchmarkQueriesIds);
				System.out.println("Similarity Error : " + similarityScore);
			//	VoronoiPanel.drawVoronoiDiagram(normalizedVectors,outputDir+"voronoi.png");
				System.out.println("------------Detailed Analysis of the Generated Benchmark--------------");
				 AvgStats.getPercentUsedLogConstructs(outputDir+"queries-stats.txt");
				 AvgStats.getAvgLogFeatures(outputDir+"queries-stats.txt");	
	}
	}
		private static List<String> getList(String[] args) {
			List<String> arguments = new ArrayList<String>();
			for (int i = 0 ; i<args.length;i++)
				arguments.add(args[i]);
			return arguments;
		}

	private static void help() {
		System.out.println("--- Requried input format--- \n\n java -jar feasible.jar  -f <queriesFile> -n <no of Queries> -cf <optional clause filter> -ff <optional feature filter> -tf <otional query type filter>");
		System.out.println("For example, java -Xmx16g -jar feasible.jar -f SWDF-CleanQueries.txt -n 5 -cf \"(OPTIONAL AND DISTINCT) OR (UNION)\" -ff \"(ResultSize >= 100 AND TriplePatternsCount >= 2 AND TriplePatternsCount <= 5)\" -tf \"SELECT OR CONSTRUCT\"");
	System.exit(0);

	}
	/**
	 * Customize your benchmark by activating various filters. See examples below
	 */
	private static void selectCustomFilters() {
		Config.drawVoronoiDiagram = true ;
		// You can set various Filters on benchmark query features and SPARQL clauses . e.g  Resultsize should be between 5 to 10 and BGPs must be greater than 2
		// and Triple patterns should be less or equal to 10 or Mean triple pattern selectivity >= 0.0001
		//See the config file for further deatils
		//Config.featureFilter = "(BGPs == 1)";
	//	Config.featureFilter = "(BGPs <= 1)";
		//Config.clauseFilter = "(OPTIONAL AND DISTINCT) OR (UNION)";
		//Config.featureFilter = "(ResultSize >= 100 AND TriplePatternsCount >= 2 AND TriplePatternsCount <= 5)";
		//Config.clauseFilter = "(DISTINCT AND FILTER) OR (GROUPBY)";
		//------ You can turn on/of basic query types -----
		//Config.ASK =false;
		//Config.DESCRIBE = false; 
		//Config.SELECT=false;
		//Config.CONSTRUCT = false;

	}



}
