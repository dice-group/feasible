# FEASIBLE: A Featured-Based SPARQL Benchmark Generation Framework

FEASIBLE is a customized SPARQL benchmark generation framework based on query logs of the RDF datasets.

### Online Demo

The online demo of FEASIBLE is available [here](http://feasible.aksw.org) along with usage examples. The demo may take some time to load, so please be patient.

####Local Demo

If for any reason the online demo is not working, you can start it locally on your computer machine by following the procedure given below.

Download FEASIBLE-online from [here](https://drive.google.com/file/d/0Bw1get4GUTJrbHA3Sl9fdThpN2s/view?usp=sharing). Unzip the file and go to feasible-beckend folder. Start the start-feasible-jetty.jar from command prompt or shell. You can start the server by using the command (java -jar start-feasible-jetty.jar). Once the server is started, you can start the demo by clicking index.html file in feasible folder.

### Downloads

Here you can download all the data required for our evaluation setup and results.

| FEASIBLE Benchmarks | DBpedia 3.5.1 Dump | SWDF Dump | DBpedia 3.5.1 Logs | SWDF Logs| DBpedia 3.5.1 Clean Queries | SWDF Clean Queries|
|:------------------------|:-----------------------|:--------------|:-----------------------|:-------------|:---------------------------------|:----------------------|
| Download | Download | Download | Download | Download | Download | Download | 

SWDF = Semantic Web Dog Food

###Source Code, Startup Information

You can checkout the source code from. svn checkout http://feasible.googlecode.com/svn/trunk/ feasible-read-only.

###Generating FEASIBLE Benchmarks
Package org.aksw.simba.benchmark.startup

######Class QuerySelecter

Select query log, the number of queries and your custom filters and execute. See the startup file for examples and detailed description.

#####Triple Stores Evaluation
Package org.aksw.simba.benchmark.startup

#####Class QueryEvaluation

FEASIBLE Clean Queries File Generation
Package org.aksw.simba.benchmark.log.operations

Class VirtuosoLogReader, SesameLogReader

Query Log RDFization
This you can use to convert query logs into RDF. The query log data can be queried from SPARQL endpoint given at http://titan.informatik.uni-leipzig.de:3333/sparql. This is yet not part of FEASIBLE. In future, FEASIBLE will directly query this endpoint to get the list of clean queries instead of first cleaning the log and taking the resulting clean queries file as input.

Package org.aksw.simba.benchmark.log.operations

Class LogRDFizer

Evaluation Results and Timeouts Queries

Our complete evaluation results can be downloaded from here. The timeouts queries for each of the triple store is given here.

###Benchmark Contributors

Muhammad Saleem (AKSW, University of Leipzig)
Qaiser Mehmood (INSIGHT @ NUI Galway)
Axel-Cyrille Ngonga Ngomo (AKSW, University of Leipzig)
Richard Cyganiak (TopQuadrant)
