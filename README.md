
*An extended version of the FEASIBLE is is available from https://github.com/dice-group/SPARQL-Bench. It is highly recommended to use the new repository for Benchmark generation*

# FEASIBLE: A Feature-Based SPARQL Benchmark Generation Framework

FEASIBLE is a customized SPARQL benchmark generation framework based on query logs of the RDF datasets. Complete details can be found in our [ISWC 2015 paper](http://svn.aksw.org/papers/2015/ISWC_FEASIBLE/public.pdf). 

### Running FEASIBLE from command line
Download the [feasible zip](https://drive.google.com/file/d/1AwiLMPubWfE4UB3Q0E2-UHb4awO2c0oV/view?usp=sharing) and extract to a directory. The command line input format is given below. 
```
java -jar feasible.jar  -f <queriesFile> -n <no of Queries> -cf <optional clause filter> -ff <optional feature filter> -tf <otional query type filter>

For example,
java -jar feasible.jar -f DBpedia3.5.1-CleanQueries.txt -n 5 

will generate a benchmark of 5 qeuries from DBpedia log. The queries can be of form SELECT, CONSTRUCT, ASK, or DESCRIBE. 

java -jar feasible.jar -f DBpedia3.5.1-CleanQueries.txt -n 5 -tf "SELECT"

will generate only SELECT queries benchmark of size 5 from DBpedia log. 

java -Xmx16g -jar feasible.jar -f SWDF-CleanQueries.txt -n 15 -cf "(OPTIONAL AND DISTINCT) OR (UNION)" -ff "(ResultSize >= 100 AND TriplePatternsCount >= 2 AND TriplePatternsCount <= 5)" -tf "SELECT OR CONSTRUCT"

will generate a benchmark of 15 queries from SWDF queries log. The queries will be only SELECT or CONSTRUCT and must either include both OPTIONAL and DISTINCT or contains UNION. Furthermore, the resultsize should be greater than 100 and Triple patterns should be greater or equal to 2 and less of equal to 5. 

```
The set of SPARQL clauses which can be used in the clause filter conditions (i.e., argument no. 3) are UNION, FILTER, OPTIONAL, DISTINCT, ORDERBY, GROUPBY, LIMIT, REGEX, and OFFSET. The set of SPARQL features that you can use in the feature filter condition (i.e., argument no. 4) are triplePatternCount, resultSize, meanJoinVerticesDegreee, meanTriplePatternSelectivity, BGPs, and runTime. Note the runTime is in milliseconeds. 
### Online Demo

The online demo of FEASIBLE is available [here](http://feasible.aksw.org) along with usage examples. The demo may take some time to load, so please be patient.

### Local Demo

If for any reason the online demo is not working, you can start it locally on your computer machine by following the procedure given below.

Download FEASIBLE-online from [here](https://drive.google.com/file/d/0Bw1get4GUTJrbHA3Sl9fdThpN2s/view?usp=sharing). Unzip the file and go to feasible-beckend folder. Start the start-feasible-jetty.jar from command prompt or shell. You can start the server by using the command (java -jar start-feasible-jetty.jar). Once the server is started, you can start the demo by clicking index.html file in feasible folder.

### Local Demo Docker
To deploy the Container the following steps are required:
```
clone the folder
go into the folder build the image
sudo docker build -t feasible .
run the image
sudo docker run -d -p 9898:80 --name feasible feasible
go into the image for debugging
sudo docker exec -it feasible-test /bin/bash
```
### Downloads

Here you can download all the data required for our evaluation setup and results.

| FEASIBLE Benchmarks | DBpedia 3.5.1 Dump | SWDF Dump | DBpedia 3.5.1 Logs | SWDF Logs| DBpedia 3.5.1 Clean Queries | SWDF Clean Queries|
|:------------------------|:-----------------------|:--------------|:-----------------------|:-------------|:---------------------------------|:----------------------|
| [Download](https://drive.google.com/file/d/0B1tUDhWNTjO-U1N2UldvbXpRWDQ/view?usp=sharing) | [Download](http://downloads.dbpedia.org/3.5.1/en/) | [Download](https://drive.google.com/file/d/0B1tUDhWNTjO-cjBqUG1BZF9RTnM/view?usp=sharing) | [Download](http://goo.gl/KyVusI) | [Download](http://goo.gl/3q52Ka) | [Download](https://drive.google.com/file/d/0B1tUDhWNTjO-Wmx5UzNIdWg1ckE/view?usp=sharing) | [Download](https://drive.google.com/file/d/0B1tUDhWNTjO-enhhakNTdE1pY2s/view?usp=sharing) | 

SWDF = Semantic Web Dog Food

### Source Code, Startup Information

You can checkout the source code from https://github.com/saleem-muhammad/feasible/.

### Generating FEASIBLE Benchmarks
* Package org.aksw.simba.benchmark.startup
* Class QuerySelecter

Select query log, the number of queries and your custom filters and execute. See the startup file for examples and detailed description.

##### Triple Stores Evaluation
* Package org.aksw.simba.benchmark.startup
* Class QueryEvaluation

##### FEASIBLE Clean Queries File Generation
* Package org.aksw.simba.benchmark.log.operations
* Class VirtuosoLogReader, SesameLogReader

##### Query Log RDFization
This you can use to convert query logs into RDF. The query log data can be queried from SPARQL endpoint given at http://lsq.akws.org/sparql. This is yet not part of FEASIBLE. In future, FEASIBLE will directly query this endpoint to get the list of clean queries instead of first cleaning the log and taking the resulting clean queries file as input.

* Package org.aksw.simba.benchmark.log.operations
* Class LogRDFizer

### Evaluation Results and Timeouts Queries

Our complete evaluation results can be downloaded from [here](https://drive.google.com/file/d/0BzemFAUFXpqOMm5MNXFVQzU4TDA/view?usp=sharing). The timeouts queries for each of the triple store is given [here](https://drive.google.com/file/d/0BzemFAUFXpqOdHVoY1VZcDE0VE0/view?usp=sharing).

### Benchmark Contributors

* [Muhammad Saleem](https://sites.google.com/site/saleemsweb/) (DICE, University of Leipzig)
* [Qaiser Mehmood](https://www.deri.ie/users/qaiser-mehmood/) (INSIGHT @ NUI Galway)
* [Axel-Cyrille Ngonga Ngomo](http://aksw.org/AxelNgonga.html) (DICE, Paderborn University)
* [Richard Cyganiak](http://richard.cyganiak.de/) (TopQuadrant)
