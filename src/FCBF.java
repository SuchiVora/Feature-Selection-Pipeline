import java.util.HashMap;
import java.util.Map;

import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.FCBFSearch;
import weka.attributeSelection.SymmetricalUncertAttributeSetEval;
import weka.core.Instances;


public class FCBF {
	
	Instances data,reduceddata;
	public void runFCBF(Instances dataset,String datasetname){
	
		data = dataset;
		ReadData rd = new ReadData();
		Map<String, Double> fcbfresult = new HashMap<String, Double>();
		
		data.setClassIndex(data.numAttributes()-1);
		AttributeSelection attsel = new AttributeSelection();
		ASSearch fcbf = new FCBFSearch();
		attsel.setSearch(fcbf);
		
		SymmetricalUncertAttributeSetEval su = new SymmetricalUncertAttributeSetEval();
		
		
		
		try {
			attsel.setEvaluator(su);
			attsel.SelectAttributes(data);
			System.out.println("Feature Selection : FCBF");
			int j=0,counter=0;
			int []att = attsel.selectedAttributes();
			double [][]value = attsel.rankedAttributes();
			//System.out.println(attsel.toResultsString());
			//System.out.println("Feature : Rank");
			for(int i=0;i<value.length;i++){
				if(value[i][j+1]>0){
					fcbfresult.put(String.valueOf((int)(value[i][j]+1)), value[i][j+1]);
					//System.out.println(value[i][j+1]);
					counter++;
				}
				
				
			}
			rd.printResult(rd.sortByComparatorDesc(fcbfresult),"./results/"+datasetname+"/fcbf_optimalfeatures.csv");
			System.out.println("Total number of features selected :" + counter);
			/*	reduceddata = attsel.reduceDimensionality(data);
			System.out.println("Saving the Reduced Data...");
			rd.saveData(reduceddata); */
			
			rd.topkfeature(data, "./results/"+datasetname+"/fcbf_optimalfeatures.csv", 20);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
