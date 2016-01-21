import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;


public class CFS {
	
	Instances data,reduceddata;
	
	public void runCFS(Instances dataset,String datasetname){
		
		data = dataset;
		ReadData rd = new ReadData();
		Map<String, Double> cfsscore = new HashMap<String, Double>();
		try{
			
			data.setClassIndex(data.numAttributes()-1);
			//System.out.println(data.classAttribute());
			AttributeSelection attsel = new AttributeSelection();
			BestFirst bf = new BestFirst();
			
			bf.setSearchTermination(1);
		//	bf.setThreshold(0.0);
			CfsSubsetEval cfs	= new CfsSubsetEval();
			attsel.setEvaluator(cfs);
			attsel.setSearch(bf);
			attsel.SelectAttributes(data);
			System.out.println("Feature Selection : Correlation based feature subset selection");
			System.out.println();
		
			int counter=0;
			int []value1 = attsel.selectedAttributeSet();
			System.out.println("Optimal Features");
				
			for(int i=0;i<value1.length-1;i++){
				//if(value1[i>0){
				cfsscore.put(String.valueOf(value1[i]+1),0.0);
				counter++;
				//}
			}
						
			try {
				FileWriter writer = new FileWriter("./results/"+datasetname+"/cfs_optimalfeatures.csv");				
				for(Map.Entry<String, Double> entry: cfsscore.entrySet()){
					//if(counter < limit){
					writer.write(entry.getKey()+",");
					writer.flush();
					System.out.println(entry.getKey());
					//}	
				}						
				writer.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println("Total number of features selected :" + counter);				System.out.println("Features selected :" + counter); 
			/*	reduceddata = attsel.reduceDimensionality(data);
				System.out.println("Saving the Reduced Data...");
				rd.saveData(reduceddata); */
				
				rd.topkfeature(data, "./results/"+datasetname+"/cfs_optimalfeatures.csv", 20);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
