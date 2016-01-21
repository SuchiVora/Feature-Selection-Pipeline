import java.io.File;
import java.io.IOException;
import java.util.Random;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.CSVLoader;
import weka.core.converters.ArffSaver;

public class CreateArff {
	public CreateArff(Instances dataset,String filename){
		//saves the dataset in arff file formats
		String fname = "./data/arff/"+filename+".arff";
		ArffSaver arffsaver = new ArffSaver();
			arffsaver.setInstances(dataset);
			try {
				arffsaver.setFile(new File(fname));
				arffsaver.writeBatch(); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
	

}
