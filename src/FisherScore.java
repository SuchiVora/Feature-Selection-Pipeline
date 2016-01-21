import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sun.misc.Sort;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;


public class FisherScore {
	
	static double limit;
	
	public void runFisherScore(Instances dataset,String datasetname){
		
		ReadData rd = new ReadData();
		
		Map<String, Double> output = new HashMap<String, Double>();
		output =  getScores(dataset);
		
		Map<String, Double> sortedoutput = sortByComparator(output);
		
		printResult(sortedoutput,datasetname);
		reduceDimensionality(dataset);
		rd.topkfeature(dataset, "./results/"+datasetname+"/fisher_optimalfeatures.csv", 20);
		}
	//}
	
	public Map<String, Double> getScores(Instances dataset){
		
		limit = Math.floor(dataset.numAttributes()/2); // returns features selected 50% of the total number of features in the dataset
		//limit = 53; // limit same as the features selected by chi square
		int numclasses = dataset.numClasses();
		int numattr = dataset.numAttributes()-1; 
		
		//System.out.println("number of attribute:"+numattr);
		double mean_ik, stddev_ik,mean_i,stddev_i;
		Map<String, Double> output = new HashMap<String, Double>();
		//double output[] = new double[numattr];
		
		
		
		for(int i=0; i<numattr;i++){
			
		if(dataset.attribute(i).isNumeric() ==false)
		{
			try {
				throw new CustomException("Algorithm works for numeric or binary(0/1) attributes");
			} catch (CustomException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			
			double temp1=0.0;
			double temp2=0.0;
			
			
			
			mean_i = dataset.meanOrMode(i);
			stddev_i = Math.sqrt(dataset.variance(i));
			//System.out.println(dataset.attribute(i));
			//System.out.println("Mean_"+ i+":"+mean_i);
			//System.out.println("stddev_"+ i+":"+stddev_i);
			double instance_i[] = dataset.attributeToDoubleArray(i);
			
			for(int k=0;k<numclasses;k++){
				double n_k=0;  //number of instances belonging to class k
				List<Double> instances_ik = new ArrayList();
				for(int j=0;j<dataset.numInstances();j++){
					
					if(dataset.instance(j).classValue() == k){
						
						instances_ik.add(instance_i[j]);
					//	System.out.println(instances_ik[j]);
						n_k++;
					}
				}
				
			//	System.out.println("n_k : "+n_k);
				
				mean_ik = mean(instances_ik);
				stddev_ik = stddev(instances_ik);
				
				
				temp1 = temp1 + (n_k * Math.pow((mean_ik - mean_i),2));
				temp2 = temp2 + (n_k * Math.pow(stddev_ik, 2));
				
				
		}
		//	System.out.println("temp1 : "+ temp1);
		//	System.out.println("temp2 : "+temp2);
			if(temp1 == 0){
				output.put(Integer.toString(i+1),0.0);
				//output[i]=0;
			}
			else{
				if(temp2 == 0){
					output.put(Integer.toString(i+1).toString(),100.0);
					//output[i]=100;
				}
				else{
					output.put(Integer.toString(i+1).toString(),(temp1/temp2));
					//output[i] = temp1/temp2;
					
				}
			}
		
		}

		}
		return output;
	}
	public Map<String, Double> sortByComparator(Map<String, Double> map){
		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(map.entrySet());
		
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>(){
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String,Double> o2){
				return(o2.getValue()).compareTo(o1.getValue());
			}
		});
		
		Map<String, Double> sortedmap = new LinkedHashMap<String, Double>();
		for(Iterator<Map.Entry<String, Double>> it = list.iterator(); it.hasNext();){
			Map.Entry<String, Double> entry = it.next();
			sortedmap.put(entry.getKey(), entry.getValue());
		}
		return sortedmap;
		
	}
	public void printResult(Map<String, Double> map,String datasetname){
		int counter =0;
		String features="./results/"+datasetname+"/fisher_optimalfeatures.csv";
		try {
			FileWriter writer = new FileWriter(features);
		
		System.out.println("Feature"+":"+"Fisher Score");
		for(Map.Entry<String, Double> entry: map.entrySet()){
			if(counter < limit){
			writer.write(entry.getKey()+",");
			writer.flush();
			System.out.println(entry.getKey() + " : "+ entry.getValue());
			counter++;
			}
			
		}
		writer.close();
		System.out.println("Total number of features selected : "+ counter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void reduceDimensionality(Instances dataset){
		
		String features="./results/fisher_optimalfeatures.csv";
		Instances newdata = null;
		ReadData rd = new ReadData();
		
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(features));
			String line = "";
			
			line = br.readLine()+ String.valueOf(dataset.numAttributes());
			 
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
	
	public static double mean(List<Double> a) {
        if (a.size() == 0) return Double.NaN;
        double sum = 0.0;
        for (int i = 0; i < a.size(); i++) {
            sum = sum + a.get(i);
        }
        return sum / a.size();
    }
	public static double var(List<Double> a) {
        if (a.size() == 0) return Double.NaN;
        double avg = mean(a);
        double sum = 0.0;
        for (int i = 0; i < a.size(); i++) {
            sum += (a.get(i) - avg) * (a.get(i) - avg);
        }
        return sum / (a.size() - 1);
    }

	  public static double stddev(List<Double> a) {
	        return Math.sqrt(var(a));
	    }
}
