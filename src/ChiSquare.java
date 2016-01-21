
/**
 * @author suchivora
 * ChiSquare.java
 * Uses the Weka's ChiSquaredAttributeEval.java class which evaluates the worth of an attriute/feature 
 * by computing the value of the chi square statistic with respect to the class
 * 
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.ChiSquaredAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.core.ContingencyTables;


public class ChiSquare {
	
	Instances data,reduceddata;
	
	/**
	 * @method runchisquare()
	 * @description This method passes the dataset to the weka's chisquaredAttributeEval class 
	 * and prints the resultant important attributes/ features and their chi square statistic score to a file.
	 * 
	 * @param dataset
	 */
	
	public void runchisquare(Instances dataset,String datasetname){
		
	data = dataset;
	
	ReadData rd = new ReadData();
	
	Map<String, Double> chiscore = new HashMap<String, Double>();
	Map<String, Double> chiscorepvalue = new HashMap<String, Double>();

	Map<String, Double> sortedchiscore = new HashMap<String, Double>();
	/**
	
	**/
	try {
		int counter=0;
		
		String list="";
	data.setClassIndex(data.numAttributes()-1);
	//System.out.println(data.classAttribute());
	AttributeSelection attsel = new AttributeSelection();
	ASSearch rank = new Ranker();
	((Ranker) rank).setThreshold(0);
	
	ChiSquaredAttributeEval chisquare = new ChiSquaredAttributeEval();
	chisquare.buildEvaluator(dataset);
	System.out.println("Feature Selection : Chi Square score");
	for(int i=0;i<dataset.numAttributes();i++){
		double  pvalue = chisquare.evaluateAttributepvalue(i);
		double score = chisquare.evaluateAttribute(i);
		if(pvalue <= 0.05 && pvalue > 0.0){
			chiscorepvalue.put(String.valueOf(i+1),pvalue);
			chiscore.put(String.valueOf(i+1),score);
			list = list + String.valueOf(i+1)+",";
			//System.out.println( i+1 +" : "+pvalue);
			counter++;
			
		}
	}
	rd.printResult(rd.sortByComparatorAscen(chiscorepvalue),"./results/"+datasetname+"/chiscore_optimalfeatures.csv");
	System.out.println("Total number of features selected :" + counter);
	rd.topkfeature(data, "./results/"+datasetname+"/chiscore_optimalfeatures.csv", 20);
	//reducedDataset(list, data);
	
/*	attsel.setEvaluator(chisquare);
	attsel.setSearch(rank);
	
	//reduceddata = Filter.useFilter(data,attsel);
	attsel.SelectAttributes(data);
	System.out.println("Feature Selection : Chi Square Score");
		int j=0,counter=0;
		double [][]value = attsel.rankedAttributes();
		//System.out.println(attsel.toResultsString());
		//System.out.println("Feature : Rank");
		for(int i=0;i<value.length;i++){
			if(value[i][j+1]>0){
				
				chiscore.put(String.valueOf(value[i][j]), value[i][j+1]);
				System.out.println(value[i][j] + ":" +value[i][j+1]);
				counter++;
			}
			
			
		}
	
	
	
	
	reduceddata = attsel.reduceDimensionality(data);
	System.out.println("Saving the Reduced Data...");
	rd.saveData(reduceddata); */
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
	
	}
	
public static void reducedDataset(String featurelist,Instances dataset){
		
		
		int features[] = null;
		int counter = 0;
		Instances newdata = null;
		ReadData rd = new ReadData();
		
		
		BufferedReader br;
		try {
			//br = new BufferedReader(new FileReader(featurefilename));
			String line = "";
			
			line = featurelist+","+ String.valueOf(dataset.numAttributes()); 
			 
			Remove rm = new Remove();
			rm.setInvertSelection(true);
			rm.setAttributeIndices(line);
			rm.setInputFormat(dataset);
			
			newdata = Filter.useFilter(dataset, rm);
			rd.saveData(newdata);
			
			//System.out.println(dataset.instance(0));
			//System.out.println(newdata.instance(0));
			
			//newdata = Filter.useFilter(dataset,rm);
			//System.out.println(newdata);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	
		
	}
	

}
