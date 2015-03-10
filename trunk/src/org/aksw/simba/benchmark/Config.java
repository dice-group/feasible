package org.aksw.simba.benchmark;

public class Config {
	//--------Basic Query Types to to be considered into the benchmark--------
	public static boolean CONSTRUCT  = false ;    // should the benchmark consider CONSTRUCT queries ?
	public static boolean ASK  = false ; 
	public static boolean DESCRIBE  = false ; 
	public static boolean SELECT  = true ; 
	//-----------Features --------
	public static boolean triplePatternsCount  = true ;  // no of triple patterns in a query
	public static boolean resultSize  = true ;      //query resultset size
	public static boolean joinVertices  = true ;   // no. of join vertices
	public static boolean meanJoinVerticesDegree  = true ;
	public static boolean meanTriplePatternSelectivity  = true ;
	public static boolean BGPs  = true ;
	//-------SPARQL constructs ------
	public static boolean UNION  = true ;
	public static boolean FILTER  = true ;
	public static boolean OPTIONAL  = true ;
	public static boolean DISTINCT  = true ;
	public static boolean ORDERBY  = true ;
	public static boolean GROUPBY  = true ;
	public static boolean LIMIT  = true ;
	public static boolean REGEX  = true ;
	public static boolean OFFSET  = true ;
//--------Single Triple pattern ------
	public static boolean STP  = true ;
	public static void main(String[] args) {
		System.out.println(CONSTRUCT);
		considerConstruct(true);
		System.out.println(CONSTRUCT);
	}
	
	public static void considerSTP(boolean value)
	{
		STP = value; 
	}

	public static boolean isSTPConsidered()
	{
		return STP;
	}
	public static void considerBGPs(boolean value)
	{
		BGPs= value; 
	}

	public static boolean isBGPsConsidered()
	{
		return STP;
	}

	public static void considerConstruct(boolean value)
	{
		CONSTRUCT = value; 
	}

	public static boolean isConstructConsidered()
	{
		return CONSTRUCT;
	}

	public static void considerAsk(boolean value)
	{
		ASK = value; 
	}

	public static boolean isAskConsidered()
	{
		return ASK;
	}

	public static void considerDescribe(boolean value)
	{
		DESCRIBE = value; 
	}

	public static boolean isDescribeConsidered()
	{
		return DESCRIBE;
	}

	public static void considerSelect(boolean value)
	{
		SELECT = value; 
	}

	public static boolean isSelectConsidered()
	{
		return SELECT;
	}
	//--------Features----------
	public static void considerTriplePatternsCount(boolean value)
	{
		triplePatternsCount = value; 
	}

	public static boolean isTriplePatternsCountConsidered()
	{
		return triplePatternsCount ;
	}

	public static void considerResultSize(boolean value)
	{
		resultSize = value; 
	}

	public static boolean isResultSizeConsidered()
	{
		return triplePatternsCount ;
	}

	public static void considerJoinVertices(boolean value)
	{
		joinVertices = value; 
	}

	public static boolean isJoinVerticesConsidered()
	{
		return joinVertices ;
	}

	public static void considerMeanJoinVerticesDegree(boolean value)
	{
		meanJoinVerticesDegree = value; 
	}

	public static boolean isJoinMeanJoinVerticesDegreeConsidered()
	{
		return meanJoinVerticesDegree ;
	}

	public static void considerMeanTriplePatternSelectivity(boolean value)
	{
		meanTriplePatternSelectivity = value; 
	}

	public static boolean isMeanTriplePatternSelectivityConsidered()
	{
		return meanTriplePatternSelectivity ;
	}

	//----------Constructs--------------
	public static void considerUnion(boolean value)
	{
		UNION = value; 
	}

	public static boolean isUnionConsidered()
	{
		return UNION ;
	}

	public static void considerFilter(boolean value)
	{
		FILTER = value; 
	}

	public static boolean isFilterConsidered()
	{
		return FILTER ;
	}
	public static void considerOffset(boolean value)
	{
		OFFSET = value; 
	}

	public static boolean isOfsetConsidered()
	{
		return OFFSET ;
	}
}
