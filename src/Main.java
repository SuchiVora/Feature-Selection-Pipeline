

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import weka.core.Instances;
import weka.core.Utils;

import java.util.ArrayList;
import java.util.Scanner;

import weka.classifiers.meta.BaggingExt;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomTreeExt;
import weka.classifiers.trees.RandomTree;


public class Main{
	
	/*static BaggingExt bagger =  null;
	static Instances data;*/
	
	public static String datasetname;

	public static void main(String[] args) {
		
		Instances data,reduceddata;
		
		
		ReadData rd = new ReadData();
	//	datasetname = "amazon";
	//	String datasetfilename = "./data/csv/amazon.csv";		
		
		datasetname = args[0];
		String datasetfilename = args[1];
	
		Scanner read = new Scanner(System.in);
		
	//	String datasetfilename = "data/csv/polaritybinfinaldataset.csv";
	//	String datasetfilename ="data/csv/trainpolarity.csv";
		data = (Instances) rd.readdata(datasetfilename);
		if (data.classIndex() == -1)
			data.setClassIndex(data.numAttributes()-1);
		
		//System.out.println(data.numClasses());
		//System.out.println(datasetname);
		
		File d = new File("./results/"+datasetname);
		if (!d.exists()) {
			if (d.mkdir()) {
				System.out.println("Output Directory is created!");
			} else {
				System.out.println("Failed to create output directory!");
			}
		}
		System.out.println("Weka is loaded");
		
		
		/*******code for jonathan******/
		
		//runwithoutoptions(data, datasetfilename,datasetname);
		
		
		
		/*******************************/
		
		
		System.out.println("Enter the choice to select which feature selection algorithm you would like to run:");
		System.out.println("1. Chi Square Score");
		System.out.println("2. Information Gain Score");
		System.out.println("3. ReliefF");
		System.out.println("4. Variable selection using Random Forest (VSRF)");
		System.out.println("5. Fisher Score");
		System.out.println("6. Laplacian Score");
		System.out.println("7. FCBF");
		System.out.println("8. CFS");
		System.out.println("9. mRmR");
		System.out.println("10. Kruskal Wallis");
		System.out.println("11. Gini Index");
		System.out.println("12. Analytic SVM");

		int algo = read.nextInt();
		
		long starttime,stoptime;
		double elapsedtime;
		String name="";
		starttime = System.currentTimeMillis();
		
		switch(algo){
		case 1: 
			ChiSquare chisq = new ChiSquare();
			chisq.runchisquare(data,datasetname);
			name = "chisquare";
			break;
		case 2:
			InfoGain infogain = new InfoGain();
			infogain.runinfogain(data,datasetname);
			name ="infogain";
			break;
		case 3:
			ReliefF relieff = new ReliefF();
			relieff.runrelieff(data,datasetname);
			name = "relieff";
			break;
		case 4:
			RFFeatureSelection rffs = new RFFeatureSelection();
			rffs.run(data,datasetname);
			name = "rffs";
			break;
		case 5:
			FisherScore fs = new FisherScore();
			fs.runFisherScore(data,datasetname);
			name="fisher";
			break;
		case 6:
			LaplacianScore ls = new LaplacianScore();
			ls.runLaplacianScore(data,datasetfilename,datasetname);
			name = "laplacian";
			break;
		case 7:
			FCBF fcbf = new FCBF();
			fcbf.runFCBF(data,datasetname);
			name ="fcbf";
			break;
		case 8:
			CFS cfs= new CFS();
			cfs.runCFS(data,datasetname);
			name ="cfs";
			break;	
		case 9:
			MRMR mrmr= new MRMR();
			mrmr.runmRmR(data,datasetname);
			name ="mrmr";
			break;	
		case 10:
			KruskalWallis kw= new KruskalWallis();
			kw.runKruskalWallis(data,datasetname);
			name ="kruskalwallis";
			break;
		case 11:
			GiniIndex gini= new GiniIndex();
			gini.runGiniIndex(data,datasetname);
			name ="giniindex";
			break;	
		case 12:
			AnalyticSVM asvm = new AnalyticSVM();
			//asvm.runpythonscript(data,datasetname);
			asvm.readFeaturesets(data,1);
			name="analyticsvm";
		}
		
		stoptime = System.currentTimeMillis();
		elapsedtime = ((stoptime-starttime)/1000.0);
		
		System.out.println("Total Time Taken: "+elapsedtime + "sec"); 
		
		
		runclassifiers(name,datasetname);
		
	
	//	runclassifiersonly(datasetname,data);
		
	//	RFFeatureSelection.reducedDataset("./results/movie_optimalfeatures.csv", data);
	
	/*
		File f = new File("./results/performanceresults.txt");
		if(f.exists() == true){
			f.delete();
		}
		
	
		reduceddata = (Instances) rd.readdata("data/arff/reduceddata.arff");
		if (reduceddata == null){
			System.out.println("No Dataset returned");
			
		}
		
		if (reduceddata.classIndex() == -1)
			reduceddata.setClassIndex(reduceddata.numAttributes()-1);
		
		SVMClassifier svm = new SVMClassifier(reduceddata); 
		NaiveBayesClassifier nb = new NaiveBayesClassifier(reduceddata);
		RandomForestClassifier rf = new RandomForestClassifier(reduceddata);
		DecisionTreeClassifier dt = new DecisionTreeClassifier(reduceddata);
		KNNClassifier knn = new KNNClassifier(reduceddata);
		*/
		
	/*	switch(classifier){
		case 1: 
			SVM svm = new SVM(reduceddata);
			break;
		case 2:
			InfoGain infogain = new InfoGain();
			infogain.runinfogain(data);
			break;
		case 3:
			ReliefF relieff = new ReliefF();
			relieff.runrelieff(data);
			break;
		case 4:
			RFFeatureSelection rffs = new RFFeatureSelection();
			rffs.run(data,datasetname);
			break;
			
		} */
		
		// TODO Auto-generated method stub
		
	

	}
	public static void runwithoutoptions(Instances data, String datasetfilename, String datasetname){
	
		ChiSquare chisq = new ChiSquare();
		System.out.println("Running Chi-Square score");
		chisq.runchisquare(data,datasetname);
		runclassifiers("chisqaure",datasetname);
	
		InfoGain infogain = new InfoGain();
		System.out.println("Running Information Gain");
		infogain.runinfogain(data,datasetname);
		runclassifiers("infogain",datasetname);
		
		ReliefF relieff = new ReliefF();
		System.out.println("Running Relieff");
		relieff.runrelieff(data,datasetname);
		runclassifiers("relieff",datasetname);
		
		//RFFeatureSelection rffs = new RFFeatureSelection();
		//rffs.run(data,datasetname);
		//runclassifiers("rffeatureselction",datasetname);
		
		FisherScore fs = new FisherScore();
		System.out.println("Running Fisher Score");
		fs.runFisherScore(data,datasetname);
		runclassifiers("fisher",datasetname);
		
		//LaplacianScore ls = new LaplacianScore();
		//ls.runLaplacianScore(data,datasetfilename);
		//runclassifiers("laplacian",datasetname);
		
		FCBF fcbf = new FCBF();
		System.out.println("Running FCBF");
		fcbf.runFCBF(data,datasetname);
		runclassifiers("fcbf",datasetname);
		
		CFS cfs= new CFS();
		System.out.println("Running CFS");
		cfs.runCFS(data,datasetname);
		runclassifiers("cfs",datasetname);
		
		MRMR mrmr= new MRMR();
		System.out.println("Running MRMR");
		mrmr.runmRmR(data,datasetname);
		runclassifiers("mrmr",datasetname);
		
		//KruskalWallis kw= new KruskalWallis();
		//kw.runKruskalWallis(data,datasetname);
		//runclassifiers("kruskalwallis",datasetname);
		
		GiniIndex gini= new GiniIndex();
		System.out.println("Running GiniIndex");
		gini.runGiniIndex(data,datasetname);
		runclassifiers("giniindex",datasetname);
		
		
	}
	
	public static void runclassifiers(String fsname,String datasetname){
		
		
		Instances reduceddata;
		ReadData rd = new ReadData();
		String resultfilename = "./results/"+datasetname+"/"+fsname+"_performanceresults.txt";
		File f = new File(resultfilename);
		if(f.exists() == true){
			f.delete();
		}
		
	
		reduceddata = (Instances) rd.readdata("./results/"+Main.datasetname+"/reduceddata.arff");
		if (reduceddata == null){
			System.out.println("No Dataset returned");
			
		}
		
		if (reduceddata.classIndex() == -1)
			reduceddata.setClassIndex(reduceddata.numAttributes()-1);
		
		SVMClassifier svm = new SVMClassifier(reduceddata,resultfilename); 
		NaiveBayesClassifier nb = new NaiveBayesClassifier(reduceddata,resultfilename);
		RandomForestClassifier rf = new RandomForestClassifier(reduceddata,resultfilename);
		DecisionTreeClassifier dt = new DecisionTreeClassifier(reduceddata,resultfilename);
		KNNClassifier knn = new KNNClassifier(reduceddata,resultfilename);
		
	}
	
	public static void runclassifiersonly(String datasetname, Instances data){
		Instances reduceddata;
		
		String resultfilename = "./results/"+datasetname +"_performanceresults.txt";
		File f = new File(resultfilename);
		if(f.exists() == true){
			f.delete();
		}
		
	
		reduceddata = data;
		if (reduceddata == null){
			System.out.println("No Dataset returned");
			
		}
		
		if (reduceddata.classIndex() == -1)
			reduceddata.setClassIndex(reduceddata.numAttributes()-1);
		
		SVMClassifier svm = new SVMClassifier(reduceddata,resultfilename); 
		NaiveBayesClassifier nb = new NaiveBayesClassifier(reduceddata,resultfilename);
		RandomForestClassifier rf = new RandomForestClassifier(reduceddata,resultfilename);
		DecisionTreeClassifier dt = new DecisionTreeClassifier(reduceddata,resultfilename);
		KNNClassifier knn = new KNNClassifier(reduceddata,resultfilename);
		
	}

}



