import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;


public class GiniIndex {
	
	Instances data;
	
	public void runGiniIndex(Instances dataset,String datasetname){
		
		data = dataset;
		checkAttributeType(data,datasetname);
		
		
	}
	/**
	 * @method CheckAttributeType(): Checks the type of the Attribute
	 * @param dataset
	 */

	public void checkAttributeType(Instances dataset,String datasetname){
		
		ReadData rd = new ReadData();
		int numAttributes = 0;
		String attrlist ="";
		Map<String, Double> output = new HashMap<String, Double>();
		Map<String, Double> sortedoutput = new HashMap<String, Double>();
			
		numAttributes = dataset.numAttributes()-1;
		for( int i=0; i<=numAttributes; i++){
				if(dataset.attribute(i).isNumeric()==true){
				//	System.out.println("Attribute is numeric");
					
					
					if(i < numAttributes-1){
						attrlist =  attrlist + String.valueOf(i+1) + ",";
					}else
					{
						attrlist =  attrlist + String.valueOf(i+1);
					}	
				}
			}
		
		if(attrlist == ""){
			
		}
		else{
			System.out.println("converting to Binary");
			convertNumericToNominal(dataset,attrlist);
			
		}
		
		output =getGiniIndex();
		sortedoutput = sortByComparator(output);
		printResult(sortedoutput,datasetname);
		rd.reduceDimensionality(dataset, "./results/"+datasetname+"/Gini_optimalfeatures.csv");
		rd.topkfeature(data, "./results/"+datasetname+"/Gini_optimalfeatures.csv", 20);
		
		
	//	System.out.println(attrlist);
	//	System.out.println(numAttributes);
		
		
		
		
	}
	/**
	 * @method convertNumericToNominal() : Converts the numeric attribute to nominal one
	 * @param dataset
	 * @param attrlist
	 */
	
	public void convertNumericToNominal(Instances dataset, String attrlist){
		
		Instances nominalizeddata= null;
		ReadData rd = new ReadData();
		try {
		NumericToNominal nn = new NumericToNominal();
		nn.setAttributeIndices(attrlist);
		nn.setInputFormat(dataset);
		
			nominalizeddata = Filter.useFilter(dataset, nn);
			rd.saveData(nominalizeddata, "./data/arff/nominalizeddata.arff");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @method getGiniIndex() 
	 */
	public Map<String, Double> getGiniIndex(){
		ReadData rd = new ReadData();
		Instances data1 = (Instances) rd.readdata("./data/arff/nominalizeddata.arff");
		data1.setClassIndex(data1.numAttributes()-1);
		int numclasses = data1.numClasses();
		int totalattrvaluewise = 0;
		int total_classwise = 0;
		Map<String, Double> output = new HashMap<String, Double>();
		
		
		for (int i=0; i<(data1.numAttributes()-1);i++)
		{
				//System.out.println(data1.attribute(i));
				double weight_gini = 0.0;
				double weight_average =0.0;
				
				int numdistinctval = data1.numDistinctValues(i);
				double [] giniindex = new double[numdistinctval];
				
				for(int j=0 ; j<numdistinctval; j++){
					
					totalattrvaluewise = 0;
					
					String attrvalue = data1.attribute(i).value(j).toString();
					
					int [] classwise_count = new int[numclasses];
					for(int k=0;k<numclasses;k++){	
						
					
					 total_classwise = 0;
						String classvalue = data1.attribute(data1.classIndex()).value(k).toString();
						
					
					for(int num=0; num<data1.numInstances(); num++){
						
						//System.out.println("class : " + classvalue);
						//System.out.println("attr : " + attrvalue);
						//System.out.println(data1.instance(num).stringValue(data1.classIndex()));
						if(data1.instance(num).stringValue(data1.classIndex()).equals(classvalue) && data1.instance(num).stringValue(i).equals(attrvalue)){
							total_classwise++;
						}
						
						
					}
					classwise_count[k]=total_classwise;
					//System.out.println(total_classwise);
					totalattrvaluewise = totalattrvaluewise + total_classwise;
					
					
					
					//System.out.println("Class : "+ classvalue + "**" + "attribute" + ": "+ i + "-"+ attrvalue + "=="+ total_classwise);
				}
					 giniindex[j] = getGini(classwise_count,totalattrvaluewise);
					 
					 weight_average += (((double)totalattrvaluewise / data1.numInstances())* giniindex[j]);
					
				
					//System.out.println(giniindex[j]);
					// System.out.println(totalattrvaluewise);
					
					//System.out.println(Double.toString(giniindex));
					
			}
				//System.out.println("weighted_gini" + weight_average);
				output.put(String.valueOf(i+1), weight_average);
			
		}
		
		return output;
	}
	public void printResult(Map<String, Double> map,String datasetname){
		int counter =0;
		String features="./results/"+datasetname+"/Gini_optimalfeatures.csv";
		try {
			FileWriter writer = new FileWriter(features);
		
		System.out.println("Feature"+":"+"Gini Index Score");
		System.out.println("Optimal features");
		for(Map.Entry<String, Double> entry: map.entrySet()){
			//if(counter < limit){
			writer.write(entry.getKey()+",");
			writer.flush();
			
			System.out.println(entry.getKey() + " : "+ entry.getValue());
			counter++;
			//}
			
		}
		writer.write("\n");
		writer.write("Score Information");
		
		for(Map.Entry<String, Double> entry: map.entrySet()){
			writer.write(entry.getKey() + " : "+ entry.getValue() +"\n");
			writer.flush();
		}
		writer.close();
		System.out.println("Total number of features selected : "+ counter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	private double getGini(int []classwise_count, int totalattrvaluewise){
		double sum=0.0;
		for(int i=0; i<classwise_count.length;i++){
		//	System.out.println(classwise_count[i]);
		//	System.out.println(totalattrvaluewise);
			double frequency = ((double)classwise_count[i] / totalattrvaluewise);
		//	System.out.println(frequency);
			sum = sum +( frequency * frequency);
		}
		
		
		
		return (1.0-sum);
		
	}
	
	public Map<String, Double> sortByComparator(Map<String, Double> map){
		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(map.entrySet());
		
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>(){
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String,Double> o2){
				return(o1.getValue()).compareTo(o2.getValue());
			}
		});
		
		Map<String, Double> sortedmap = new LinkedHashMap<String, Double>();
		for(Iterator<Map.Entry<String, Double>> it = list.iterator(); it.hasNext();){
			Map.Entry<String, Double> entry = it.next();
			sortedmap.put(entry.getKey(), entry.getValue());
		}
		return sortedmap;
		
	}
}
