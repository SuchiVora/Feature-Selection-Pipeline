# Feature-Selection-Pipeline Project
## A. Requirements: 
In order to run FSP, the following packages are required: Java 1.7+, Python 2.7, SciPy and Scikit Learn. 
## B. Dataset Format:
The FSP jar accepts the data in the csv file format. For each dataset, the file must contain header as the first row followed by the data. The header contains the features names mapped to numbers as columns except the last column, which is named as “class”. The class column contains the target labels/class. The data is represented in binary feature vector format which indicates the presence / absence of the feature in a row in a dataset. Note that the class labels cannot be numeric values. Missing values in dataset should be left blank.

Here is a sample dataset represented in the format required by FSP.
* Sample dataset consist of 4 sentences each with a positive or negative target label: 
“Mary is very happy (positive). 
 Mother would be angry if we do this (negative). 
 It’s a very beautiful day (positive). 
 This is a boring movie (negative).” 
* If we consider following words from above sentences as features: Mary, very, happy, mother, angry, beautiful, day, boring, movie.  
* The above dataset will be represented in the file as follows:

       Sample.csv

       1,2,3,4,5,6,7,8,9,class ←(Header)
       
       1,1,1,0,0,0,0,0,0,positive
       
       0,0,0,1,1,0,0,0,0,negative ← (Data)

       0,1,0,0,0,1,1,0,0,positive

       0,0,0,0,0,0,0,1,1,negative

Where 1- Mary, 2 - very, 3 - happy and so on are the features mapped to numbers in the header. Each sentence in the dataset is represented as a comma separated binary feature vector. 

All the datasets considered in the study are represented in the above-mentioned binary feature vector format.

##C.Installation:
1. Download the FSP jar from the github repository and store it in a folder in desired location. 
2. Create two folders namely /data and /results in the same location as the jar file.
3. Store the dataset csv file in the /data folder.
4. The /results folder stores the results of each feature selection algorithms applied to the dataset in a folder named same as the dataset name.
        E.g. Suppose the name of the dataset is “Strength”, the results for each feature selection algorithm for Strength dataset will be in folder “/results/strength”

For each feature selection algorithm applied to the dataset namely two files are generated as follows:
1. “Feature-selection-algo-name_optimalfeatures.csv”: This file contains comma-separated list of features returned as optimal. It also contains the score given to each feature by the feature selection algorithm under consideration. E.g. For feature selection algorithm “ReliefF” the filename would be “relieff_optimalfeatures.csv”
2. “Feature-selection-algo-name_performanceresults.txt”: This file contains the classifier’s performance result for a given dataset and feature selection algorithm. It includes the accuracy, precision, recall and f-measure for each classifier. E.g. For feature selection algorithm ReliefF the filename would be “relieff_performanceresults.txt”

##D. Steps to run the FSP jar file:
1. Open the command line; Navigate to the location where the FSP jar is stored.
2. The FSP jar takes two runtime arguments: name of the dataset and csv dataset file. Run the following Command:

         java -jar featureselectionpipeline.jar [name of the datatset] [csv dataset file]

         e.g.: java -jar featureselectionpipeline.jar polarity trainpolarity.csv

3. Once command is run following screen:

![](https://github.com/SuchiVora/Feature-Selection-Pipeline/blob/master/images/screenshot0.png)

Enter the number of whichever feature selection algorithm one would like to run on the dataset.

E.g. if one would like to run Information Gain score algorithm, enter 2 and press the enter key.

Once the feature selection phase is completed, FSP prompts the user to select the number of optimal features to be passed to the classification phase as seen in figure 12. In the classification phase, FSP by default runs the classifiers such as SVM, NB, Random Forest, J4.8, KNN on the dataset. For the classification phase, the classifiers uses the optimal features returned by the feature selection algorithm for the given dataset. FSP provides performance measurements such as accuracy, precision, recall and F-measure for each classifier. These performance measures are shown as output on the command line interface as well as written to an output file stored in the /results folder. All the performance measures for each classifier are reported using 10-fold cross validation.

![](https://github.com/SuchiVora/Feature-Selection-Pipeline/blob/master/images/Screen%20Shot%201.png)

![](https://github.com/SuchiVora/Feature-Selection-Pipeline/blob/master/images/Screen%20Shot%202.png)

![](https://github.com/SuchiVora/Feature-Selection-Pipeline/blob/master/images/Screen%20Shot%203.png)

![](https://github.com/SuchiVora/Feature-Selection-Pipeline/blob/master/images/Screen%20Shot%204.png)
