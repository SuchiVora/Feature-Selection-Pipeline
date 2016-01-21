
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import weka.core.Instances;
import weka.classifiers.lazy.IBk;
import weka.classifiers.Evaluation;

public class KNNClassifier  {
	
	KNNClassifier(Instances dataset, String filename){
		
	
	IBk knn = new IBk();
	try{
		
		Evaluation eval = new Evaluation(dataset);
		
		knn.setKNN(5);
		knn.buildClassifier(dataset);
		eval.crossValidateModel(knn, dataset, 10, new Random(1));
		
		ReadData rd = new ReadData();
		int classes = dataset.numClasses();
		
		rd.writeclassifierresults("KNN", rd.classifierResults(eval, classes,"KNN"),filename);
/*
		int i;
		double acc =0;
		for (i=0;i<classes;i++){
			acc = acc + ((eval.numTruePositives(i)+eval.numTrueNegatives(i))/(eval.numFalseNegatives(i)+eval.numFalsePositives(i)+eval.numTrueNegatives(i)+eval.numTruePositives(i)));
			
		}
		
		Map<String, Double> scores = new HashMap<String, Double>();
		scores.put("Accuracy", (acc/classes)*100);
		scores.put("Precision",(eval.weightedPrecision()*100));
		scores.put("Recall", (eval.weightedRecall()*100));
		scores.put("F- Measure", (eval.weightedFMeasure()*100));
		
		rd.writeclassifierresults("KNN", scores);
		System.out.println("\nClassifier: KNN \n");
		System.out.println((acc/classes)*100);
		System.out.println((eval.weightedPrecision()*100));
		System.out.println( (eval.weightedRecall()*100));
		System.out.println( (eval.weightedFMeasure()*100));
		
		System.out.println("\nClassifier: KNN(N=5) (IBk) \n");
		System.out.println("Accuracy:"+(acc/classes)*100);
		System.out.println("Precision:"+(eval.weightedPrecision()*100));
		System.out.println("Recall:"+ (eval.weightedRecall()*100));
		System.out.println("F- Measure:"+ (eval.weightedFMeasure()*100));
	//	System.out.println(eval.toMatrixString().toString()); */
		
	}catch(Exception e){
		e.printStackTrace();
	}
	
	}

}



