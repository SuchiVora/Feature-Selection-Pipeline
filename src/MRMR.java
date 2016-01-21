import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.RerankingSearch;
import weka.core.Instances;
import weka.core.SelectedTag;



public class MRMR {
	Instances data, reduceddata;
	
	public void runmRmR(Instances dataset,String datasetname){
		
		data = dataset;
		ReadData rd = new ReadData();
		Map<String, Double> mrmrscore = new HashMap<String, Double>();
		String[] options = new String[6];
		options[0] = "-method";
		options[1] = "2";
		options[2] = "-blockSize";
		options[3] = "20";
		options[4] = "-rankingMeasure";
		options[5] = "0";
		//options[6] = "-search";
		//options[7] = "weka.attributeSelection.BestFirst";
		try{
			
			data.setClassIndex(data.numAttributes()-1);
		//	System.out.println(data.classAttribute());
			AttributeSelection attsel = new AttributeSelection();
			RerankingSearch rank = new RerankingSearch();
		//	InfoGainAttributeEval infogain	= new InfoGainAttributeEval();
			CfsSubsetEval cfs	= new CfsSubsetEval();
			rank.setOptions(options);
			
			
		//	attsel.setEvaluator(infogain);
			attsel.setSearch(rank);
			
			attsel.SelectAttributes(data);
			int counter=0;
			int []value1 = attsel.selectedAttributeSet();
					
			System.out.println("Feature Selection : mRmR");
			
			System.out.println("Optimal Features");
			
			for(int i=0;i<value1.length-1;i++){
				//if(value1[i>0){
				mrmrscore.put(String.valueOf(value1[i]+1), 0.0);
				counter++;
				//}
			}
						
			try {
				FileWriter writer = new FileWriter("./results/"+datasetname+"/mrmr_optimalfeatures.csv");				
				for(Map.Entry<String, Double> entry: mrmrscore.entrySet()){
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
				
		
				
				
			//	System.out.println(attsel.toResultsString());
				
				/*int []value1 = attsel.selectedAttributeSet();
				
				//System.out.println("Feature : Rank");
				for(int i=0;i<value1.length-1;i++){
					//if(value1[i>0){
						System.out.println(value1[i]+1);
						counter++;
					//}
						
					
				} */
				System.out.println("Total number of features selected :" + counter);
				/*	reduceddata = attsel.reduceDimensionality(data);
				System.out.println("Saving the Reduced Data...");
				rd.saveData(reduceddata); */
				rd.topkfeature(data, "./results/"+datasetname+"/mrmr_optimalfeatures.csv", 20);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	

}
