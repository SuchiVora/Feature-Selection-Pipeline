
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.classifiers.Evaluation;

public class SVMClassifier {
	SVMClassifier(Instances dataset, String filename){
		
		SMO svm = new SMO();
	
		
		try {
			Evaluation eval = new Evaluation(dataset);
			ReadData rd = new ReadData();
			
			svm.buildClassifier(dataset);
			eval.crossValidateModel(svm, dataset, 10, new Random(1));
			
			int classes = dataset.numClasses();
			int i;
			
			rd.writeclassifierresults("SVM", rd.classifierResults(eval, classes,"SVM"),filename);
		/*	double acc =0,pre=0, rec=0;
			for (i=0;i<classes;i++){
				acc = acc + ((eval.numTruePositives(i)+eval.numTrueNegatives(i))/(eval.numFalseNegatives(i)+eval.numFalsePositives(i)+eval.numTrueNegatives(i)+eval.numTruePositives(i)));
				pre = pre +((eval.numTruePositives(i))/(eval.numTruePositives(i)+eval.numFalsePositives(i)));
				rec = rec + ((eval.numTruePositives(i))/(eval.numTruePositives(i)+eval.numFalseNegatives(i)));
			}
			Map<String, Double> scores = new HashMap<String, Double>();
			scores.put("Accuracy", (acc/classes)*100);
			scores.put("Precision",(eval.weightedPrecision()*100));
			scores.put("Recall:", (eval.weightedRecall()*100));
			scores.put("F- Measure:", (eval.weightedFMeasure()*100));
			
			
			
			System.out.println("\nClassifier: SVM \n");
			System.out.println((acc/classes)*100);
			System.out.println((pre/classes)*100);
			System.out.println((rec/classes)*100);
			System.out.println((2*(pre*rec)/(pre+rec))*100);
			System.out.println(eval.pctCorrect());
			System.out.println((eval.weightedPrecision()*100));
			System.out.println( (eval.weightedRecall()*100));
			System.out.println( (eval.weightedFMeasure()*100));
			
		
			System.out.println("\nClassifier: SVM \n");
			System.out.println("Accuracy:"+(acc/classes)*100);
			System.out.println("Precision:"+(eval.weightedPrecision()*100));
			System.out.println("Recall:"+ (eval.weightedRecall()*100));
			System.out.println("F- Measure:"+ (eval.weightedFMeasure()*100));
			//System.out.println(eval.toMatrixString().toString()); */
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
