import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;


public class LaplacianScore {
	
	static double limit;
	
	public void runpythonscript(Instances dataset,String filename,String datasetname){
		int num = dataset.numAttributes()-1;
		
		int distancemeasure = 3;
		//ProcessBuilder pd = new ProcessBuilder("python", "/RFFEatureSelection/src/laplacianscore.py");
				//+ "-f "+ filename+" -c "+num+" -d 4");
		try {
			//System.out.println(pd);
			//Process p = pd.start();
			//Process p = Runtime.getRuntime().exec("python laplacianscore.py -f ./data/csv/train_Strength_nominal.csv -c 1231 -d 4");
			//System.out.println(filename); 
			int r = dataset.numInstances();
			
			String[] command = new String[]{};
			 command = new String[]{"python","/Users/suchivora/Workspaces/Eclipse Standard 431/RFFeatureSelection/src/laplacianscore.py" ,"-f",filename,"-c",String.valueOf(num),"-d",String.valueOf(distancemeasure),"-n",datasetname};
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader in = new BufferedReader( new InputStreamReader(p.getInputStream()));
			System.out.println(in.readLine());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void runLaplacianScore(Instances dataset,String datafilename,String datasetname )
	{
		ReadData rd = new ReadData();
		runpythonscript(dataset,datafilename,datasetname);
		reduceDimensionality(dataset,datasetname);
		rd.topkfeature(dataset, "./results/"+datasetname+"/laplacian_optimalfeatures.csv", 20);
	}
	public static void printResult(Map<String, Double> map,String datasetname){
		int counter =0;
		
		String features="./results/"+datasetname+"/laplacian_optimalfeatures.csv";
		try {
			FileWriter writer = new FileWriter(features);
		
		System.out.println("Feature"+":"+"Laplacian Score");
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
	
	private static Map<String, Double> sortByComparator(Map<String, Double> map){
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
public static void reduceDimensionality(Instances dataset,String datasetname){
		
		String features="./results/"+datasetname+"/laplacian_optimalfeatures.csv";
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
}
