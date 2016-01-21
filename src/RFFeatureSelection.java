
import java.io.BufferedReader;

import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

import java.util.ArrayList;

import weka.classifiers.meta.BaggingExt;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomTreeExt;
import weka.classifiers.trees.RandomTree;

public class RFFeatureSelection {
	static BaggingExt bagger =  null;
	static Instances data;
	static String datasetname;
	
	/**
	 * run method performs the different steps required for the feature selection 
	 * @param dataset
	 * @param datasetname
	 */
	public void run(Instances dataset, String datasetname){
		
		data = dataset;
		RFFeatureSelection.datasetname = datasetname;
		
		
		double [][]avgvarimp;
		double threshold;
		int []featureset;
		long starttime,stoptime,elapsedtime;
		
		ReadData rd = new ReadData();
		
		try {
			//records the start time of the program
			starttime = System.currentTimeMillis();
			
			//applies the random forest to the dataset to get the variable importance
			System.out.println("Applying Random Forest to the "+datasetname+" dataset.");
			bagger = applyrf(data);
			
			//Evaluation eval = new Evaluation(data);
			
			
			System.out.println("Getting the variable importance");
			avgvarimp = getAvgVarImp(bagger, data);
			
			
			applycart();
		
			
			threshold = getthreshold();
		//	threshold = 3.863818794497422E-5;
			System.out.println("CART Threshold:"+threshold);
			
			
			featureset = featureelimination(threshold);
			System.out.println("Total "+featureset.length+" features remaining after elminiation"); 
			sequentialaddition(featureset,data);
			
			
			
			
			stoptime = System.currentTimeMillis();
			elapsedtime = stoptime-starttime; 
			
			
			
			
			//System.out.println("\nTotal Time Taken: "+elapsedtime);
			
		//	eval.crossValidateModel(bagger, data, 10, new Random(1));
			//System.out.println(rfext.measureOutOfBagError());
		//	System.out.println(data.numAttributes());
			//System.out.println(eval.toSummaryString());
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * getOOB method returns the oobscore for the dataset
	 * @param bagger
	 * @param dataset
	 * @return
	 */
	public static double getOOB(BaggingExt bagger, Instances dataset){
		double oobscore = 0;
		try {
		/*	check this comment */
			//bagger.buildClassifier(dataset);
			oobscore=bagger.measureOutOfBagError();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return oobscore;
		
		
	}
	
	/**
	 * applyrf method sets the parameter for the classifier and build the classifier
	 * @param data
	 * @return
	 */
	public static BaggingExt applyrf(Instances data){
		
		
		try {
		//create the classifier and runs it to get the results, does 10 fold cross validation
			
		//RandomForestExt rfext =  new RandomForestExt();
		RandomTreeExt rtreeext = new RandomTreeExt();
		RandomTree rtree = new RandomTree();
		//rfext.setOptions(new String[]{"-I","5","-K","0","-S","1","-V","-T"});
		//RandomForestExt.m_bagger;
		int m_KValue = (int) Math.floor((data.numAttributes()-1)/3);
		rtreeext.setKValue(m_KValue);
		rtree.setKValue(m_KValue);
		
	
		//rfext.buildClassifier(data);

		
		///setting up bagging
		bagger = new BaggingExt();
		bagger.setClassifier(rtree);
		
		bagger.setImportDebug(false);
		bagger.setDisplayTrees(false);
	
		bagger.setSeed(1);
		bagger.setNumIterations(200);
		bagger.setCalcOutOfBag(true);
		bagger.setCalcVarImp(true);
		//bagger.buildClassifier(data);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bagger;
		
	}
	
	/**
	 * featureelimination method eliminates the features with average variable importance less than the threshold
	 * and creates a an array of featureset arranged in descending order of value of variable importance.
	 * @param threshold
	 * @return An array of features
	 */
	public static int[] featureelimination(double threshold){
		
		int featureset[]=null;
		ReadData rd = new ReadData();
		String avgvarimp = "results/"+datasetname+"/"+datasetname+"_avgvarimp.csv";
		Instances odata = (Instances) rd.readdata(avgvarimp);
		Instances newdata = new Instances(odata,0);
		
		//System.out.println(odata.numInstances());
		
		odata.setClassIndex(odata.numAttributes()-1);
		newdata.setClassIndex(odata.numAttributes()-1);
		
		for(int i =0;i<odata.numInstances();i++){
			if(odata.instance(i).classValue()>threshold){
				//System.out.println(odata.instance(i).value(0));
				//odata.delete(i);
				newdata.add(odata.instance(i));
			}
			
			
		}
		newdata.sort(newdata.numAttributes()-1);
		
		featureset = new int[newdata.numInstances()];
	
		for(int i =0;i<newdata.numInstances();i++){
			
				//System.out.println(newdata.instance(i).classValue());
				
				featureset[i]=(int) newdata.instance(i).value(0);
				//System.out.println(featureset[i]);
			
		}
		//System.out.println(newdata);
		//System.out.println(newdata);
		return featureset;
		
		
	}
	
	
	/**
	 * sequentialaddition method applies Random Forest to the dataset with one feature added to it in each run 
	 * and gets the oobscore for each model.
	 * @param featureset
	 * @param dataset
	 */
	public static void sequentialaddition(int []featureset,Instances dataset){
	
			
			
		int modelno = 1;
		ArrayList featurelist = new ArrayList();
		double oobscore[][]= new double[featureset.length][2];
		try {
		String featureoob="./results/"+datasetname+"/"+datasetname+"_feature_oob.csv";
		FileWriter writer = new FileWriter(featureoob);
		
		writer.append("ModelNo,OOBScore,Featureset\n");
		
		//System.out.println("inside sequentialaddition");
		
		ReadData rd = new ReadData();
		Instances origdata = dataset;
		Instances zeroeddata;
		Instances oobscoredata;
		
		//creates the copy of the origdata into the newdata
		Instances copydataset = new Instances(origdata);
	//	System.out.println(copydataset);
		
		//sets all the values in newdata to zero
//		zeroeddata = instanceToDoubleArrayZeros(copydataset);
		
		//System.out.print(zeroeddata);
		
		//int []featureset1={1,2};]
		
		//System.out.println("ModelNo : OOBScore");
		
		for (int i=featureset.length-1;i>=0;i--){
			/*if (modelno <=811){
				featurelist.add(featureset[i]);
				//System.out.println(featureset[i]);
				zeroeddata = createdataset(origdata,zeroeddata,featureset[i]);
				//System.out.println(newdata);
			}
			else{ */
				
			
			featurelist.add(featureset[i]);
			String feature = featurelist.toString();
			feature = feature.replace("[", "");
			feature = feature.replace("]", "");
			//System.out.println(feature); 
			zeroeddata = createdataset(origdata,feature);
			//dataset = createdataset(origdata,zeroeddata,featureset[i]);
			if (copydataset.classIndex() == -1)
				copydataset.setClassIndex(copydataset.numAttributes()-1);
			
			
			//uncomment this
		
			bagger = applyrf(zeroeddata);
			try {
				bagger.buildClassifier(zeroeddata);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			oobscore[modelno-1][0]=modelno;
			oobscore[modelno-1][1]= getOOB(bagger,zeroeddata); 
			
			
		
			//System.out.println(modelno+" : "+oobscore[modelno-1][1]);
			
			writer.append(modelno+",");
			writer.append(oobscore[modelno-1][1]+",");
			writer.append(featurelist+"\n");
			
			
			
			
		//	}
			
		//	System.out.println(modelno+" : "+oobscore[modelno-1][1]);
			writer.flush();
			modelno++;
		} 
		
		
		writer.close();
		optimalfeatureset(oobscore,featureoob,origdata);
		
	//	System.out.println(newdata);
		//System.out.println(newdata);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public static Instances createdataset(Instances dataset, String feature){
		
		Instances newdataset= null;
		try {
			String name ="./results/"+datasetname+"/"+datasetname+"_newdatatset.csv";
			ReadData rd = new ReadData();
		String line = feature+","+ String.valueOf(dataset.numAttributes());
		Remove rm = new Remove();
		rm.setInvertSelection(true);
		rm.setAttributeIndices(line);
		rm.setInputFormat(dataset);
		
		
			newdataset = Filter.useFilter(dataset, rm);
			rd.saveData(newdataset, name);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return newdataset;
		
	}
	
	public static void optimalfeatureset(double[][] oobscore,String filename, Instances origdata){
		 try{
			 
			 ReadData rd = new ReadData();
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line="";
			String nameoptimal="./results/"+datasetname+"/"+datasetname+"_optimalfeatures.csv";
			FileWriter writer = new FileWriter(nameoptimal);
			
			double oob[] = new double[oobscore.length];
			
			for(int i=0;i<oobscore.length;i++){
				oob[i] = oobscore[i][1];
			}
			  double min=oob[0];
			  int minindex = 0;
		      for (int i = 0; i < oob.length; i++) {
		    	 
		    		  if (oob[i] < min) {
		        	
		              min = oob[i];
		              minindex = i;
		              
		              break;
		          }
		    	}
		//System.out.println(min);
		      System.out.println("Optimal features : ");
		      br.readLine();
		      while((line = br.readLine()) != null){
					String info[] = line.split(",");
					if(Double.parseDouble(info[1]) == oobscore[minindex][1]){
						int len = info.length;
						for(int i=2; i<len;i++){
							writer.append(info[i].replace("[", "").replace("]", "").replace(" ", ""));
							if(i != len-1){
								writer.append(",");
							}
							
							System.out.print(info[i].replace("[", "").replace("]", ""));
						}
						writer.flush();
					
					}
				}
		      writer.close();
		      System.out.println();
		      reducedDataset(nameoptimal,origdata);
		      rd.topkfeature(origdata, nameoptimal, 20);
             // System.out.println(oobscore[minindex][0]+" : "+oobscore[minindex][1]);
		 }catch(Exception e){
			 
		 }
		 
	}
	
	public static void reducedDataset(String featurefilename,Instances dataset){
		
		
		int features[] = null;
		int counter = 0;
		Instances newdata = null;
		ReadData rd = new ReadData();
		
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(featurefilename));
			String line = "";
			
			line = br.readLine()+","+ String.valueOf(dataset.numAttributes());
			 
			Remove rm = new Remove();
			rm.setInvertSelection(true);
			rm.setAttributeIndices(line);
			rm.setInputFormat(dataset);
			
			newdata = Filter.useFilter(dataset, rm);
			rd.saveData(newdata);
			
			//System.out.println(dataset.instance(0));
			//System.out.println(newdata.instance(0));
			
			//newdata = Filter.useFilter(dataset,rm);
			//System.out.println(newdata);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	/*	ReadData rd = new ReadData();
		Instances origdata = dataset;
		Instances zeroeddata;
		Instances oobscoredata;
		int features[] = null;
		int counter = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(featurefilename));
			String line = "";
			 while((line = br.readLine()) != null){
				 System.out.println(line);
				 features[counter]= Integer.parseInt(line);
				 counter++;	
			 }
				 
				 
			 
			 //creates the copy of the origdata into the newdata
			 Instances copydataset = new Instances(origdata);
	//	System.out.println(copydataset);
		
		//sets all the values in newdata to zero
		zeroeddata = instanceToDoubleArrayZeros(copydataset);
		
		//System.out.print(zeroeddata);
		
		//int []featureset1={1,2};]
		
		//System.out.println("ModelNo : OOBScore");
		
		for (int i=features.length-1;i>=0;i--){
			/*if (modelno <=811){
				featurelist.add(featureset[i]);
				//System.out.println(featureset[i]);
				zeroeddata = createdataset(origdata,zeroeddata,featureset[i]);
				//System.out.println(newdata);
			}
			else{ */
				
/*			//System.out.println(modelno); 
			
			
			dataset = createdataset(origdata,zeroeddata,features[i]);
			if (copydataset.classIndex() == -1)
				copydataset.setClassIndex(copydataset.numAttributes()-1);
			rd.saveData(zeroeddata);
		
		
		
		}	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
		
		
	}
	
	private static Instances instanceToDoubleArrayZeros(Instances dataset) {

		for(int i = 0;i<dataset.numInstances();i++){
			for (int j=0;j<dataset.numAttributes()-1;j++){
				//System.out.println(j);
				//dataset.instance(i).classAttribute();
				dataset.instance(i).setValue(j, 0);
				
			}
			//System.out.println("instnace:"+dataset.instance(i));
		}
		
		return dataset;
	}

	
	public static Instances createdataset(Instances originaldataset, Instances newdataset,int feature){
		
		try {
			String name ="./results/"+datasetname+"/"+datasetname+"_newdatatset.csv";
			FileWriter writer = new FileWriter(name);
			
		for(int i = 0;i<originaldataset.numInstances();i++){
			for (int j=0;j<originaldataset.numAttributes()-1;j++){
				
				String att = originaldataset.attribute(j).name().toString();
				
			/*	if((att.equals( Integer.toString(feature))  && (originaldataset.instance(i).value(j)==1))){
						newdataset.instance(i).setValue(j,1);
					}*/
				if((att.equals( Integer.toString(feature))  && (originaldataset.instance(i).value(j)!=0))){
					newdataset.instance(i).setValue(j, originaldataset.instance(i).value(j));
					
				//	newdataset.instance(i).setValue(j,1);
					}
				}
				
			}
		for(int i = 0;i<originaldataset.numInstances();i++){
			for (int j=0;j<originaldataset.numAttributes()-1;j++){
				
				writer.append(Double.toString(newdataset.instance(i).value(j)));
				writer.append(",");
				
			}
			writer.append("\n");
			writer.flush();
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		//System.out.println(newdataset);
		return newdataset;
		
	}
	
	
	public static double getthreshold(){
		double threshold;
		double sortedarray[] = null; 
		ReadData rd = new ReadData();
		String name="results/"+datasetname+"/"+datasetname+"_cartprediction.csv";
		Instances predictdata = (Instances) rd.readdata(name);
		predictdata.setClassIndex(predictdata.numAttributes()-1);
		sortedarray = new double[predictdata.numInstances()];
		for(int i = 0;i<predictdata.numInstances();i++)
		{
			predictdata.sort(predictdata.numAttributes()-1);
			sortedarray[i]= predictdata.instance(i).classValue();

			
			//writer.append(i+1+","+clslabel+"\n");
			//System.out.println(predictdata.instance(i));
			//System.out.println(sortedarray[i]);
			
		}
		threshold = min(sortedarray);
		//System.out.println(threshold);
		return threshold;
		
	}
	public static double[] applycart(){
		double predict[] = null;
		try {
			String namepredict="./results/"+datasetname+"/"+datasetname+"_cartprediction.csv";
		FileWriter writer = new FileWriter(namepredict);
		ReadData rd = new ReadData();
		REPTree carttree = new REPTree();
		String cartdataname ="results/"+datasetname+"/"+datasetname+"_cartdata.csv";
		Instances cartdata = (Instances) rd.readdata(cartdataname);
		cartdata.setClassIndex(cartdata.numAttributes()-1);
		predict = new double[cartdata.numInstances()];
		
			carttree.buildClassifier(cartdata);
			for(int i = 0;i<cartdata.numInstances();i++)
			{
				double clslabel = carttree.classifyInstance(cartdata.instance(i));
				predict[i]=clslabel;
				writer.append(i+1+","+clslabel+"\n");
				//System.out.println(cartdata.numInstances());
				
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return predict;
		
		
		
	}
	public static double[][] getAvgVarImp(BaggingExt bagger, Instances dataset){
		int n= 50;
		double [][]avg = new double[data.numAttributes()-1][n+1];
		try {
			String name = "./results/"+datasetname+"/"+datasetname+"_avgvarimp.csv";
			String cartdataname ="./results/"+datasetname+"/"+datasetname+"_cartdata.csv";
			FileWriter writer = new FileWriter(name);
			FileWriter cartwriter = new FileWriter(cartdataname);
			for(int i=0; i < data.numAttributes()-1; i++){
				//System.out.println(data.attribute(i).name());
				avg[i][0]=  Double.parseDouble(data.attribute(i).name());
				//avg[i][0]=i;
				
			}
		
		
		
			for (int j=0; j <n;j++){
				
				System.out.println("Run:"+j);
			
				bagger.buildClassifier(data);
			
			
				for(int i=0; i< bagger.getVarImp().length; i++){
					avg[i][j+1]= bagger.getVarImp()[i];
				}
				
				}
				
			
				for(int j=0;j<=n;j++){
					if(j==0){
						writer.append("Features,");
					}
					else{
						writer.append(j+",");
						cartwriter.append(j+",");
					}
					
				
			}
			writer.append("stddev");
			cartwriter.append("stddev");
			writer.append("\n");
			cartwriter.append("\n");
			writer.flush();
			cartwriter.flush();
			
			double a[] = new double[n];	
			double std;
			for(int i=0;i< data.numAttributes()-1; i++){
				for(int j=0;j<=n;j++){
					if(j==0){
						writer.append((int)avg[i][j]+",");
					}
					else{
						writer.append(avg[i][j]+",");
						cartwriter.append(avg[i][j]+",");
					}
					
					if (j>0)
					{
						a[j-1] = avg[i][j];
					}
				
				
				//System.out.print(avg[i][j]);
				
			}
			std= stddev(a);
			writer.append(Double.toString(std));
			cartwriter.append(Double.toString(std));
			writer.append("\n");
			cartwriter.append("\n");
			//System.out.print("\n");
			}
			writer.flush();
			cartwriter.flush();
			
			
			writer.close();
			cartwriter.close();
			
			
			
		
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return avg;
		
		}
	
	public static double mean(double[] a) {
        if (a.length == 0) return Double.NaN;
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum = sum + a[i];
        }
        return sum / a.length;
    }
	
	public static double var(double[] a) {
        if (a.length == 0) return Double.NaN;
        double avg = mean(a);
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += (a[i] - avg) * (a[i] - avg);
        }
        return sum / (a.length - 1);
    }

	  public static double stddev(double[] a) {
	        return Math.sqrt(var(a));
	    }
	  
	  public static double min(double[] array) {
	      // Validates input
	      if (array == null) {
	          throw new IllegalArgumentException("The Array must not be null");
	      } else if (array.length == 0) {
	          throw new IllegalArgumentException("Array cannot be empty.");
	      }
	  
	      // Finds and returns min
	      double min=0;
	      for (int i = 1; i < array.length; i++) {
	    	  if(array[i]>0){
	    		  min=array[i];
	    		  break;
	    		/*  if (array[i] < min) {
	        	  System.out.println(array[i]);
	              min = array[i];
	              break;
	          }*/
	    	  }
	      }
	  
	      return min;
	  }

}
