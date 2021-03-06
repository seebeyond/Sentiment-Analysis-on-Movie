package com.cu.bigdata.moviereview;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.mahout.classifier.sgd.L1;
import org.apache.mahout.classifier.sgd.ModelSerializer;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import org.apache.mahout.math.Vector;

import com.cu.bigdata.moviereview.data.DirSwitch;
import com.cu.bigdata.moviereview.data.ModelConfig;

public class LogisticRegressionModel {
	private OnlineLogisticRegression learningAlgo;
	
	public LogisticRegressionModel(int NumCate,int NumFeature,double lambda,int learningRate){
		learningAlgo = new OnlineLogisticRegression(NumCate, NumFeature, new L1());
        learningAlgo.lambda(lambda);
        learningAlgo.learningRate(learningRate);
	}
	
	public LogisticRegressionModel(Integer featuresNum) throws IOException, URISyntaxException{
		
        InputStream in = new FileInputStream(new DirSwitch().LogisticRegressionModel_CONS(featuresNum));
        learningAlgo = ModelSerializer.readBinary(in, OnlineLogisticRegression.class);
        in.close();
	}
	
	public boolean LRTrainModel(List<DataInstance> trainset,String model_path) throws IOException{
		for(DataInstance instance:trainset){
			learningAlgo.train(instance.lable, instance.features);
		}
		ModelSerializer.writeBinary(model_path, learningAlgo);
        learningAlgo.close();
		return true;
	}
	
	public int LRClassify(Vector feature){
		Vector r = learningAlgo.classifyFull(feature);
		return r.maxValueIndex();
	}
	
	public Vector LRClassifyInstance(Vector feature){
		Vector r = learningAlgo.classifyFull(feature);
		return r;
	}
	
	public double LRTest(List<DataInstance> testset){
		int total = 0;
		int success = 0;

		for (DataInstance vector : testset) {
			Vector prediction = learningAlgo.classifyFull(vector.features);
			
			if (prediction.maxValueIndex()==vector.lable) {
				success++;
			}
			total++;
		}

		System.out.println("Total: " + total);
		System.out.println("Success: " + success);
		System.out.println("Fail: " + (total - success));
		System.out.println("Precise: " + ((double) success / total));
		return ((double) success / total);
	}
	
	public static void main(String[] argv) throws IOException {
				
		
//		String to = "data/train_log_10000.csv";
		String to = "src/main/resources/data/train_log_5000_James.csv";
		String savemodel = "src/main/resources/data/model/LogisticRegressionModels/";
		
		// preprecess train data
//		PreProcessor processor = new PreProcessor();
//		processor.toVector(tp, lp, to);
		
		CSVHelper chCsvHelper = new CSVHelper();
		List<DataInstance> dInstance = chCsvHelper.ReadFromCSV(to);
		ModelConfig.FeatureNumber = 5000;
		LogisticRegressionModel lrmodel = new LogisticRegressionModel(4, ModelConfig.FeatureNumber, 0.1, 4);
		lrmodel.LRTrainModel(dInstance, savemodel+"f"+ModelConfig.FeatureNumber);
		
		/*
		Random rand =  new Random();

		//10 fold cross validation

		List<List<DataInstance>> datafolds = new ArrayList<List<DataInstance>>();
		
		for(int i = 0;i<10;i++){
			datafolds.add(new ArrayList<DataInstance>());
		}
		
		for(DataInstance instance:dInstance){
			int a = rand.nextInt(10);
			datafolds.get(a).add(instance);
		}
		
		double totalaccuricy = 0;
		for(int i = 0;i<10;i++){
			// train
			System.out.println("[LRModel]Fold: "+i+" training...");
			LogisticRegressionModel lrmodel = new LogisticRegressionModel(4, ModelConfig.FeatureNumber, 0.1, 4);
			List<DataInstance> trainset = new ArrayList<DataInstance>();
			List<DataInstance> testset = new ArrayList<DataInstance>();
			testset.addAll(datafolds.get(i));
			for(int j = 0;j<10;j++){
				if(j==i) continue;
				trainset.addAll(datafolds.get(j));
			}
			lrmodel.LRTrainModel(trainset, "src/main/resources/data/model/lr_model");
			System.out.println("[LRModel]Fold: "+i+" Testing...");
			
			// test
			totalaccuricy += lrmodel.LRTest(testset);
			System.out.println("[LRModel]Fold: "+i+" Complete!");
		}
		
		System.out.println("[LRModel] Average accuricy:"+totalaccuricy/10);
		*/
	}
	
}
