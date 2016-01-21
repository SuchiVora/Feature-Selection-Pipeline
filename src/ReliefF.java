import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.ReliefFAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;

public class ReliefF {
	Instances data, reduceddata;
	public void runrelieff(Instances dataset,String datasetname){
		
		data = dataset;
		ReadData rd = new ReadData();
		Map<String, Double> relieffscore = new HashMap<String, Double>();
		Map<String, Double> dummy = new HashMap<String, Double>();

		try {
			data.setClassIndex(data.numAttributes()-1);
			//System.out.println(data.classAttribute());
			
			AttributeSelection attsel = new AttributeSelection();
			Ranker rank = new Ranker();
			rank.setThreshold(0.0);
			ReliefFAttributeEval relieff = new ReliefFAttributeEval();
			attsel.setEvaluator(relieff);
			attsel.setSearch(rank);
			attsel.SelectAttributes(data);
			
			System.out.println("Feature Selection : ReliefF");
				int j=0,counter=0;
				double [][]value = attsel.rankedAttributes();
				//System.out.println(attsel.toResultsString());
				//System.out.println("Feature : Rank");
				for(int i=0;i<value.length;i++){
					if(value[i][j+1]>0){
						relieffscore.put(String.valueOf((int) (value[i][j]+1)), value[i][j+1]);
						//System.out.println(value[i][j+1]);
						counter++;
					}
						
					
				}
				dummy = rd.sortByComparatorDesc(relieffscore);
				rd.printResult(dummy,"./results/"+datasetname+"/relieff_optimalfeatures.csv");
				/*try {
					FileWriter writer = new FileWriter("./results/"+datasetname+"/relieff_optimalfeatures.csv");				
					for(Map.Entry<String, Double> entry: dummy.entrySet()){
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
					}*/
				System.out.println("Total number of features selected :" + counter);
				//reduceddata = attsel.reduceDimensionality(data);
				//System.out.println(reduceddata);
				//rd.saveData(reduceddata); 
				rd.topkfeature(data, "./results/"+datasetname+"/relieff_optimalfeatures.csv", 20);
				System.out.println("Saving the Reduced Data...");
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
	}

}
