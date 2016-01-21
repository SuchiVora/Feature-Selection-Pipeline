from sklearn.neighbors import kneighbors_graph
from sklearn.neighbors import NearestNeighbors
from sklearn.manifold import SpectralEmbedding
from sklearn.utils.graph import graph_laplacian
from scipy.spatial.distance import euclidean
from scipy.spatial.distance import cosine
from scipy.spatial.distance import jaccard
from optparse import OptionParser
import time
import numpy as np
import math
import operator

## Computes the K-nearest neighbor graph with k=5
def get_affinity_matrix_unsupervised(X,Y):
    #print X
    nn = NearestNeighbors(n_neighbors =5).fit(X,Y)
    affinity_matrix = nn.kneighbors_graph(X,5).toarray()
    print affinity_matrix
    return affinity_matrix

## computes the affinity_matrix using class labels
def get_affinity_matrix_supervised(X,Y):
    rows, cols = X.shape
    affinity = np.zeros((rows,rows),dtype=int)
    for i in range(0,rows):
        for j in range(0,rows):
            if(Y[i] == Y[j]):
                affinity[i][j]=1
    #print affinity
    return affinity
    

## computes the weight matrix using one of the following distance measures
##1. Eculidean distance
##2. Jaccard distance
##3. Symmetric Binary distance
##4. Cosine distance

def compute_weight_matrix(affinity,X,opt):
    nsamples,nfeatures = X.shape
    option = 1
    option = opt
    ## checks whether the i and j are connected
    rows ,cols = affinity.shape
    weight = np.zeros((rows,cols),dtype =float)
    for i in range(0,rows):
        for j in range(0,cols):
            if (affinity[i,j] == 1):
                x= X[i]
                y = X[j]
                if option==1:
                    ##uses eculidean distance
                    dist = euclidean_distance(X[i],X[j])
                elif option == 2:
                    ##uses jaccard_distance
                    dist = jaccard_distance(X[i],X[j])
                elif option ==3:
                    ##uses distance measure for symmetric binary variables:
                    dist= cosine_distance(X[i],X[j])
                elif option ==4:
                    dist = symmetricbinary_distance(X[i],X[j])

                #print dist
                t = 1
                weight[i,j] = math.exp(-(dist/t))
                #print weight[i][j]                                       
                #print x
                #print y
            else:
                
                weight[i,j] = 0
    
                #print weight[i][j]
    return weight

## calculates the symmetric binary distance
def symmetricbinary_distance(Xi,Xj):
    a=0
    b=0
    c=0
    d=0
##    for n in range(0,Xi.size):
##        if(Xi[n] == 1 and Xj[n]==1):
##            a= a+1
##        if(Xi[n] == 1 and Xj[n]==0):
##            b=b+1
##        if(Xi[n] == 0 and Xj[n]==1):
##            c=c+1
##        if(Xi[n] == 0 and Xj[n]==0):
##            d=d+1
##
    not_Xi = 1.0 - Xi
    not_Xj = 1.0 - Xj
    a = (Xi * Xj).sum()
    d = (not_Xi * not_Xj).sum()
    b = (Xi * not_Xi).sum()
    c = (not_Xi * Xj).sum()
##    print a
##    print b
##    print c
##    print d
    dist = (b+c)/(a+b+c+d)
    return dist
    
    
## calculates the eculidean distance
def euclidean_distance(Xi,Xj):
    dist = euclidean(Xi,Xj)
    return dist

##calculates the jaccard distance
def jaccard_distance(Xi,Xj):
    dist = jaccard(Xi,Xj)
    return dist

##calculates the cosine distance
def cosine_distance(Xi,Xj):
    dist = cosine(Xi,Xj)
    return dist



## computes the laplacian score for a feature
def get_laplacian_score(X,weight_matrix):
    lp_score={}
    
    nsamples, nfeatures = X.shape
    allones =np.ones((nsamples))
    #print nfeatures
    D= np.diag(np.dot(weight_matrix,allones.T))
    L = D-weight_matrix
    for i in range(0,nfeatures):
        #print X[:,i]
        fr = X[:,i]-(np.dot(((np.dot(np.dot(X[:,i].T , D) ,allones))/(np.dot(np.dot(allones.T,D),allones))),allones))
       # print fr
        lr = (np.dot(np.dot(fr.T,L),fr))/(np.dot(np.dot(fr.T,D),fr))
#        print lr
        if math.isnan(lr):
            lr =0.0
        lp_score[i+1]=lr
    sorted_lp_score = sorted(lp_score.items(),key=operator.itemgetter(1))
##    print len(sorted_lp_score)
##    for item in sorted_lp_score:
##        print item
    return sorted_lp_score


## writes the results to a file    
def printresults(lp_score,datasetname):
    f = open("./results/"+datasetname+"/laplacian_optimalfeatures.csv",'w')
    for item in lp_score:
        if(item[1] != 0.0):
            f.write(str(item[0]))
            f.write(",")
            f.flush()
    
    f.write("\nLaplacian Score information\n")
    for item in lp_score:
        if(item[1] != 0.0):
            f.write(str(item[0]))
            f.write(" : ")
            f.write(str(item[1]))
            f.write("\n")
            f.flush()
##def get_laplacian_matrix(affinity):
##    l, dd = graph_laplacian(affinity, return_diag=True)
##    print l
##    print dd.shape
##    print dd
##    return l, dd    
##
##nbrs = NearestNeighbors(n_neighbors=5, algorithm='auto').fit(X)
##distances, indices = nbrs.kneighbors(X)
##print X.shape
###print distances
##print  nbrs.kneighbors_graph(X).toarray()
##b = nbrs.kneighbors_graph(X).toarray()
##print b.shape
##np.savetxt("graph.csv", nbrs.kneighbors_graph(X).toarray())
##s = np.zeros(b.shape)
##t=1
##for i in range(len(b)):
##    for j in range(len(b[i])):
##        if(b[i][j] == 1):
##            s[i][j] = pow(math.e,((pow(math.fabs(b[),2)/t)))


def main(filename, classnum, distancemeasure,datasetname):
    X = np.genfromtxt(filename,dtype="float",delimiter=",",usecols=range(0,int(classnum)),skip_header=1)
    Y = np.genfromtxt(filename,dtype="str",delimiter=",",usecols=int(classnum),skip_header=1)
    #print Y
    start = time.time()
    #sp = SpectralEmbedding(affinity ="nearest_neighbors").fit_transform(X)
    #print "Calculating the affinity matrix.."
    affinity = get_affinity_matrix_supervised(X,Y)
    
    #print "computing the weight matrix.."
    weight_matrix = compute_weight_matrix(affinity, X,int(distancemeasure))
    stop = time.time()
    
    #print weight_matrix.shape
    #print "writing the laplacian score to the file.."
    printresults(get_laplacian_score(X,weight_matrix),datasetname)

    print "Time taken to select the features:" + str(stop - start)
 #   l,dd=get_laplacian_matrix(affinity)

if __name__=="__main__":
    parser = OptionParser()
    parser.add_option('-f', '--filename')
    parser.add_option('-c','--classnum', help='for example class column number')
    parser.add_option('-d','--distancemeasure', help='enter the option for distance measure to be used')
    parser.add_option('-n', '--datasetname')
    options, args = parser.parse_args()
    main(options.filename,options.classnum, options.distancemeasure,options.datasetname)
  #  main("./data/csv/train_Strength_nominal.csv",1230,4)
