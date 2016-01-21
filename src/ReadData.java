import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import java.util.Random;
import java.util.Scanner;

import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.SVMLightLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 * Class ReadData reads the dataset from file and stores it into weka dataset object.
 * @author suchivora
 *
 */

public class ReadData {
	/**
	 * Method readdata() reads the data from file, checks for the type of the extension of the file
	 * and covnerts is into the weka required format. The program excepts file with csv or dat(svm light format) extension
	 * It takes String filename as input and returns Dataset object data 
	 * @param filename
	 * @return data
	 */
	public Object readdata(String filename)
	{
		File inputfile = new File(filename);
		Instances data = null;
		
		String fileextension = getFileExtension(filename);
		
		if(fileextension != null){
			if(fileextension.contentEquals("csv")){
				CSVLoader csvloader = new CSVLoader();
				
				try {
					csvloader.setSource(new File(filename));
					data = csvloader.getDataSet();
				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(fileextension.contentEquals("dat")){
				SVMLightLoader svmlightloader = new SVMLightLoader();
				
				try {
					svmlightloader.setSource(new File(filename));
					data = svmlightloader.getDataSet();
				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else if(fileextension.contentEquals("arff")){
			ArffLoader arffloader = new ArffLoader();
			
			try {
				arffloader.setSource(new File(filename));
				data = arffloader.getDataSet();
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
		
		return data;
		
	}
	
	
	/**
	 * The method getFileExtension reads the string filename and return extension of the file if present else returns null
	 * @param filename
	 * @return file extension or null
	 */

	private static String getFileExtension(String filename){
		if(filename.lastIndexOf(".") != -1  && filename.lastIndexOf(".") !=0)
		return filename.substring(filename.lastIndexOf(".")+1);
		else
		return null;
	}
	
	/**
	 * 
	 */
	public void saveData(Instances reduceddata){
		ArffSaver arffsaver = new ArffSaver();
		arffsaver.setInstances(reduceddata);
		try {
			arffsaver.setFile(new File("./results/"+Main.datasetname+"/reduceddata.arff"));
			arffsaver.writeBatch();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public void saveData(Instances reduceddata,String filename){
		ArffSaver arffsaver = new ArffSaver();
		arffsaver.setInstances(reduceddata);
		try {
			arffsaver.setFile(new File(filename));
			arffsaver.writeBatch();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public Map<String, Double> classifierResults(Evaluation eval, int classes, String name){
		double acc=0,pre=0, rec=0, f=0;
		int i=0;
		
		for (i=0;i<classes;i++){
			pre = pre + eval.precision(i);
			rec = rec+ eval.recall(i);
			f = f+ eval.fMeasure(i);
		}
		
		Map<String, Double> scores = new HashMap<String, Double>();
		scores.put("Accuracy", (eval.pctCorrect()));
		scores.put("Precision",((pre/classes)*100));
		scores.put("Recall:", ((rec/classes)*100));
		scores.put("F- Measure:", ((f/classes)*100));
		
	/*	System.out.println("\nClassifier:"+name+" \n");
		System.out.println(eval.pctCorrect());
		System.out.println((pre/classes)*100);
		System.out.println((rec/classes)*100);
		System.out.println((f/classes)*100); */
		
		System.out.println("\nClassifier: "+name+" \n");
		System.out.println("Accuracy:"+eval.pctCorrect());
		System.out.println("Precision:"+((pre/classes)*100));
		System.out.println("Recall:"+ ((rec/classes)*100));
		System.out.println("F- Measure:"+ ((f/classes)*100)); 
		
		
		
		try {
			System.out.println(eval.toMatrixString().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return scores;
		
	}
	public void topkfeature(Instances dataset, String filename, int l){
		//int k=Math.round((dataset.numAttributes()-1)/4);
		//int k = dataset.numAttributes()-1;
		int k=0;
		Scanner read = new Scanner(System.in);
		System.out.println();
		System.out.println("Enter the choice to select the number of optimal features to be the classification phase:");
		System.out.println("1. All the features returned by the feature selection algorithm.");
		System.out.println("2. Top 25% of the size of the dataset.");
		System.out.println("3. Top 50% of the size of the dataset.");
		System.out.println("Note: If the number of features returned by the feature selection algorithm is less than the selected criterion then FSP will use the smaller number of features returned by default by the algorithm.");
		int criteria = read.nextInt();
		
		
		switch(criteria){
		case 1: 
			k=dataset.numAttributes()-1;
			break;
		case 2:
			k=Math.round((dataset.numAttributes()-1)/4);
			break;
		case 3:
			k=Math.round((dataset.numAttributes()-1)/2);
			break;
		}
		int i=0;
		Instances newdata = null;
		BufferedReader br;
		String ar[]= null;
		String line="";
		try {
			br = new BufferedReader(new FileReader(filename));
			String features = "";
			
			features = br.readLine();
			ar = features.split(",");
			if (ar.length < k){
				k=ar.length;
			}
			line = ar[0];
			for(i=1;i<k;i++){
				line = line+","+ String.valueOf(ar[i]);
			}
			System.out.println("Top "+i+" Feature Selected");
			System.out.println(line);
			line = line +","+ String.valueOf(dataset.numAttributes());
			
			Remove rm = new Remove();
			rm.setInvertSelection(true);
			rm.setAttributeIndices(line);
			rm.setInputFormat(dataset);
		
			newdata = Filter.useFilter(dataset, rm);
			//System.out.println(newdata);
			saveData(newdata,"./results/"+Main.datasetname+"/reduceddata.arff");
			br.close();
		//String features = filename;
		}catch(Exception e){
			
		}
	}
public void reduceDimensionality(Instances dataset, String filename){
		
		String features=filename;
		Instances newdata = null;
		ReadData rd = new ReadData();
		
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(features));
			String line = "";
			
			line = br.readLine()+ String.valueOf(dataset.numAttributes());
			//System.out.println(line);
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
	
	
	public void printResult(Map<String, Double> map, String filename){
		int counter =0;
		//String features=filename;
		try {
			FileWriter writer = new FileWriter(filename);
		
		//System.out.println("Feature"+":"+"Fisher Score");
		for(Map.Entry<String, Double> entry: map.entrySet()){
			//if(counter < limit){
			writer.write(entry.getKey()+",");
			writer.flush();
			//System.out.println(entry.getKey() + " : "+ entry.getValue());
			counter++;
			//}
			
		}
		writer.write("\nScore Information \n");
		System.out.println("Optimal Features");
		for(Map.Entry<String, Double> entry: map.entrySet()){
			//if(counter < limit){
			writer.write(entry.getKey() + " : "+ entry.getValue()+"\n");
			writer.flush();
			System.out.println(entry.getKey() + " : "+ entry.getValue());
			counter++;
			//}
			
		}
		writer.close();
		//System.out.println("Total number of features selected : "+ counter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeclassifierresults(String classifier, Map<String, Double> values, String filename){
		
		try{
			FileWriter writer = new FileWriter(filename,true);
			writer.write("\n" + classifier +":\n");
			for(Map.Entry<String, Double> entry : values.entrySet()){
				writer.write(entry.getKey()+ ":"+ entry.getValue()+"\n");
				writer.flush();
				
			}
			
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
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
	
	public Map<String, Double> sortByComparatorDesc(Map<String, Double> map){
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

}
