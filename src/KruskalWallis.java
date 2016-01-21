import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import weka.core.Instances;


public class KruskalWallis {
	
	public void runKruskalWallis(Instances dataset,String datasetname){
		
		ReadData rd = new ReadData();
		
		Map<String, Double> h = new HashMap<String, Double>();
		Map<String, Double> sortedh = new HashMap<String, Double>();
		System.out.println("Feature Selection : Kruskal Wallis ");
		h=getScores(dataset);
		sortedh = sortByComparatordescen(h);
		printResult1(sortedh,datasetname);
		rd.reduceDimensionality(dataset, "./results/"+datasetname+"/kruskalwallis_optimalfeatures.csv");
		rd.topkfeature(dataset, "./results/"+datasetname+"/kruskalwallis_optimalfeatures.csv", 20);
		//System.out.println(h);
		
		
		
	}
	
	public Map<String, Double> getScores(Instances dataset){
		
		
		Instances rankdataset = new Instances(dataset);
		Map<String, Double> h_statistic = new HashMap<String, Double>();
		
		
		for(int i=0;i<dataset.numAttributes()-1;i++){
			//System.out.println(dataset.attribute(i));
			
			Map<String, Double> attrvalues = new HashMap<String,Double>();
			Map<Double, Double> rankedattrval = new HashMap<Double, Double>();
			
			
			
			for(int num=0;num<dataset.numInstances();num++){
				//System.out.println(dataset.instance(num).value(i));
				attrvalues.put(String.valueOf(num+1),(double)dataset.instance(num).value(i));
			}
			Map<String, Double> sortedmap =  sortByComparatorAscen(attrvalues);
			//printResult1(sortedmap);
			rankedattrval = Rank(sortedmap);
			for(Map.Entry<Double, Double> entry: rankedattrval.entrySet()){
				for(int num=0;num<dataset.numInstances();num++){
					if(dataset.instance(num).value(i) == entry.getKey()){
						 rankdataset.instance(num).setValue(i, entry.getValue());
						 
					 }
					 //System.out.println(rankdataset.instance(num));
				}
				//System.out.println(dataset.instance(num).value(i));	
			}
			//System.out.println("rank");
			
			Map<String, Double> classwise_summap = new HashMap<String, Double>();
			Map<String, Double> classwise_countmap = new HashMap<String, Double>();
			Map<String, Double> classwise = new HashMap<String, Double>();
			//double [] classwise_sumrank = new double[dataset.numClasses()];
		//	int [] classwise_count = new int[dataset.numClasses()];
		//	printResult(rankedattrval);
			for(int k=0;k<dataset.numClasses();k++){
				int count = 0;
				double sum = 0.0;
				String classvalue = rankdataset.attribute(rankdataset.classIndex()).value(k).toString();
				for(int num=0;num<dataset.numInstances();num++){
					if(rankdataset.instance(num).stringValue(rankdataset.classIndex()).equals(classvalue)){
						count++;
						sum = sum + rankdataset.instance(num).value(i);
						
					}
					

				}
			
				classwise_summap.put(rankdataset.attribute(rankdataset.classIndex()).value(k).toString(), sum*sum);
				classwise_countmap.put(rankdataset.attribute(rankdataset.classIndex()).value(k).toString(), (double) count);
				classwise.put(rankdataset.attribute(rankdataset.classIndex()).value(k).toString(), ((sum*sum)/count));
				
				//classwise_sumrank[k] = sum;
				//classwise_count[k]= count;
			}
			//System.out.println(classwise_summap);
			//System.out.println(classwise_countmap);
			
			double total_count =0.0;
			double total_sum = 0.0;
			for(Map.Entry<String, Double> entry: classwise.entrySet()){
				total_sum = total_sum + entry.getValue();
			}
			for(Map.Entry<String, Double> entry: classwise_countmap.entrySet()){
				total_count = total_count + entry.getValue();
			}
			
			//System.out.println(total_sum);
			//System.out.println(total_count);
			double h_stats = ((12/(total_count*(total_count +1))*total_sum)-(3*(total_count+1)));
			//System.out.println(h_stats);
			if(h_stats >0){
				
			
			h_statistic.put(String.valueOf(i+1), h_stats);
			}
		}
		//System.out.println(h_statistic);
		return h_statistic;
		
		
	}
	
	/*public void createRankDataset(Instances rankdataset, Map<Double, Double> rankedattr){
		
		for(int i=0;i<rankdataset.numAttributes()-1;i++){
			for(Map.Entry<Double, Double> entry: rankedattr.entrySet()){
				if(rankdataset.instance(index))
			}
		}
	}*/
		
	
	public Map<String, Double> sortByComparatorAscen(Map<String, Double> map){
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
	public Map<String, Double> sortByComparatordescen(Map<String, Double> map){
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
	
	public Map<Double,Double> Rank(Map<String, Double> inputmap){
		
		Map<Double, List<Double>> rankmap = new HashMap<Double,List<Double>>();
		Map<Double, Double> avgrankmap = new HashMap<Double,Double>();
		
		
		double average;
		double count = 1.0;
		
		//Iterator iterator = inputmap.entrySet().iterator();
		//while(iterator.hasNext()){
		//System.out.println("Feature"+":"+"Fisher Score");
		for(Map.Entry<String, Double> entry: inputmap.entrySet()){
			
			List<Double> entries = rankmap.get(entry.getValue());
			if(entries == null){
				entries = new ArrayList<Double>();
			}
			entries.add(count);
			//Map.Entry<String, Double> inputmapentry = (Map.Entry) iterator.next();
			//System.out.println(entry.getValue());
			rankmap.put(entry.getValue(),entries);
			count++;
		}
		
		for(Map.Entry<Double, List<Double>> entry: rankmap.entrySet()){
			
			double sum=0.0;
			int counter=0;
			List<Double> value = entry.getValue();
			Double key = entry.getKey();
			
			for(double ent: value){
				sum +=ent;
				counter++;
			}
			
			average = sum /counter;
			avgrankmap.put(key, average);
					
		}
		//System.out.println(avgrankmap);
		return avgrankmap;
		
	}
	
	public void printResult1(Map<String, Double> map,String datasetname){
		int counter =0;
		String features="./results/"+datasetname+"/kruskalwallis_optimalfeatures.csv";
		try {
			FileWriter writer = new FileWriter(features);
		
		for(Map.Entry<String, Double> entry: map.entrySet()){
			//if(counter < limit){
			writer.write(entry.getKey()+",");
			writer.flush();
			//System.out.println(entry.getKey() + " : "+ entry.getValue());
			counter++;
			//}
			
		}
		writer.write("\n");
		System.out.println("Optimal Features");
		for(Map.Entry<String, Double> entry: map.entrySet()){
			//if(counter < limit){
			writer.write(entry.getKey() + " : "+ entry.getValue()+"\n");
			writer.flush();
			System.out.println(entry.getKey() + " : "+ entry.getValue());
			//}
			
		}
		writer.close();
		System.out.println("Total number of features selected : "+ counter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
