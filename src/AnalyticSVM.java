import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 * Code to run Carly's program
 * 
 * write the code to run analytic svm python code from java
 * @author suchivora
 *
 */
public class AnalyticSVM {
	
	public void runpythonscript(Instances dataset, String datasetname){
		int num = dataset.numAttributes()-1;
		String filename,feature_grps, class_labels;
		int label_count=0;
		Scanner read = new Scanner(System.in);
		try {
			System.out.println("Analytic SVM algorithm requires LIBSVM dataset format. Enter the following information");
			System.out.println("Enter the dataset file");
			filename = read.nextLine();
			System.out.println("Enter the feature groups(for example 1:28,29:40 will make two feature groups 1-28 and 29-40)");
			feature_grps = read.nextLine();
			System.out.println("Enter the class labels seperated by comma");
			class_labels = read.nextLine();
			
			//System.out.println(filename); 
			String[] command = new String[]{};
			command = new String[]{"python","/Users/suchivora/Workspaces/Eclipse Standard 431/RFFeatureSelection/src/general_feature_selection.py" ,"-f",filename,"-t",feature_grps,"-l",class_labels,"-d","./results"};
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader in = new BufferedReader( new InputStreamReader(p.getInputStream()));
			//System.out.println(in.readLine());
			label_count = Integer.parseInt(in.readLine());
			System.out.println(label_count);
			
			p.waitFor();
			
			readFeaturesets(dataset, label_count);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void readFeaturesets(Instances Dataset,int label_count){
		
		
		
		int attributes=0;
		String separator = ",";
		String strcurrentline,line = null;
		String classes[]=null;
		Object[] featurenum = null;
		int num = 0;
		int l=0;
		BufferedReader br = null;
		
		
		attributes = (Dataset.numAttributes()-1);
		
		try {
			for(l=0;l<label_count;l++){
				
			//String filename="./results/classifier_"+String.valueOf(l+1)+".csv";
				String filename="./results/classifier_1.csv";
			br = new BufferedReader(new FileReader(filename));
			int counter = 1;
			while((strcurrentline = br.readLine()) != null)
			{
				//boolean featurevector[] = {false,false,false,false,false,false};
				List featurevector = new ArrayList();
				System.out.println(counter+":");
				if(separator != "\t")
				{
					strcurrentline = strcurrentline.replace(separator, "\t");

				}
				classes = strcurrentline.split("\t");
			
				List features = new ArrayList();
				for(int i=0;i< classes.length;i++)
				{
					String str = new String(classes[i]);
					//System.out.println(str);
					str = str.replace(" ", "");
					
					featurevector = setfeaturevector(str,features);
				//	featurevector = setfeaturevectormovies(str,features);
					line =featurevector.toString();
					line = line.replace("[", "");
					line= line.replace("]", "");
					
				}
				
				if(featurevector != null){
					System.out.println(line);
					reducedDataset(line,Dataset);
				}
				else
				{
					System.out.println("null");
				}
				ReadData rd = new ReadData();
				Instances reduceddata = (Instances) rd.readdata("data/arff/reduceddata.arff");
				if (reduceddata.classIndex() == -1)
					reduceddata.setClassIndex(reduceddata.numAttributes()-1);
				SVMClassifier svm = new SVMClassifier(reduceddata,"./results/analtyicsvm_performanceresults.txt"); 
				counter++;
				
			}
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
public static void reducedDataset(String features,Instances dataset){
		
		
		
		int counter = 0;
		Instances newdata = null;
		ReadData rd = new ReadData();

		try {
			
			String strfeatures = features+","+ String.valueOf(dataset.numAttributes());
			Remove rm = new Remove();
			rm.setInvertSelection(true);
			rm.setAttributeIndices(strfeatures);
			rm.setInputFormat(dataset);
			
			newdata = Filter.useFilter(dataset, rm);
			//System.out.println(newdata);
			rd.saveData(newdata);
			
			//System.out.println(dataset.instance(0));
			//System.out.println(newdata.instance(0));
			
		
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}

public static List setfeaturevectormovies(String featurerange,List features)
{

	if (featurerange.equals("'1:845'"))
	{
		//System.out.println("entered");
		for(int i=1;i<=845;i++){
			features.add(i);
		}
	
	}
	if (featurerange.equals("'846:1384'"))
	{
		for(int i=846;i<=1384;i++){
			features.add(i);			}
		
	}
	if (featurerange.equals("'1385:1447'"))
	{
		for(int i=1385;i<=1447;i++){
			features.add(i);				}
		
	}
	
	return features;
	
	 
	
}

	
	public static List setfeaturevector(String featurerange,List features)
	{

		
		
		if (featurerange.equals("'696:826'"))
		{
			//System.out.println("entered");
			for(int i=696;i<=826;i++){
				features.add(i);
			}
		
		}
		if (featurerange.equals("'1219:1230'"))
		{
			for(int i=1219;i<=1230;i++){
				features.add(i);			}
			
		}
		if (featurerange.equals("'827:876'"))
		{
			for(int i=827;i<=876;i++){
				features.add(i);				}
			
		}
		if (featurerange.equals("'877:1218'"))
		{
			for(int i=877;i<=1218;i++){
				features.add(i);			}
			
		}
		if (featurerange.equals("'1:695'"))
		{
			
			for(int i=1;i<=695;i++){
				features.add(i);			}
		}
		
		return features;
		
		 
		
	}

}
