import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;


public class InfoGain {
	Instances data, reduceddata;
	
	public void runinfogain(Instances dataset,String datasetname){

		data = dataset;
		ReadData rd = new ReadData();
		Map<String, Double> gain = new HashMap<String, Double>();
		/*// TODO Auto-generated method stub
		String datasetfilename = "data/arff/train_Strength_nominal.arff";
		try {
			DataSource source = new DataSource(datasetfilename);
			data = source.getDataSet();
		
		//data = (Instances) rd.readdata(datasetfilename);
		
		*/
		try{
			
		data.setClassIndex(data.numAttributes()-1);
		//System.out.println(data.classAttribute());
		AttributeSelection attsel = new AttributeSelection();
		Ranker rank = new Ranker();
		rank.setThreshold(0.0);
		InfoGainAttributeEval infogain	= new InfoGainAttributeEval();
		attsel.setEvaluator(infogain);
		attsel.setSearch(rank);
		attsel.SelectAttributes(data);
		System.out.println("Feature Selection : Information Gain");
	
	
			int j=0,counter=0;
			double [][]value = attsel.rankedAttributes();
			//System.out.println(attsel.toResultsString());
			//System.out.println("Feature : Rank");
			for(int i=0;i<value.length;i++){
				if(value[i][j+1]>0){
					gain.put(String.valueOf((int)(value[i][j]+1)), value[i][j+1]);
					//System.out.println(value[i][j+1]);
					counter++;
				}
					
				
			}
			
			rd.printResult(rd.sortByComparatorDesc(gain),"./results/"+datasetname+"/infogain_optimalfeatures.csv");
			System.out.println("Total number of features selected :" + counter);
			/*	reduceddata = attsel.reduceDimensionality(data);
			System.out.println("Saving the Reduced Data...");
			rd.saveData(reduceddata); */
			rd.topkfeature(data, "./results/"+datasetname+"/infogain_optimalfeatures.csv", 20);
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	}
	
	
}
