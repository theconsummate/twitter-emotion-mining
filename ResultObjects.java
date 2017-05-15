package test;


import java.util.Map;

/**
 * 
 * Class Description : DataObject definition for aggregate measures and
 * 					   getInstance() method initialize the aggregate TP,FP,TN,FN,FScore as 0
 * 					   It also has the evaluation metric for each class in a map , with key as 
 * 					   String element for class label and value as object of EvalautionDO.
 * 						
 * 	
 *
 */
public class ResultObjects {

	private double allTP;
	private double allFP;
	private double allTN;
	private double allFN;
	private double allFScore;

	private double microfscore;
	private double macrofscore;
	private Map<String, EvalObjects> evaluationMatrix;

	public Map<String, EvalObjects> getEvaluationMatrix() {
		return evaluationMatrix;
	}

	public void setEvaluationMatrix(Map<String, EvalObjects> evaluationMatrix) {
		this.evaluationMatrix = evaluationMatrix;
	}

	public double getAllTP() {
		return allTP;
	}

	public void setAllTP(double allTP) {
		this.allTP = allTP;
	}

	public double getAllFP() {
		return allFP;
	}

	public void setAllFP(double allFP) {
		this.allFP = allFP;
	}

	public double getAllTN() {
		return allTN;
	}

	public void setAllTN(double allTN) {
		this.allTN = allTN;
	}

	public double getAllFN() {
		return allFN;
	}

	public void setAllFN(double allFN) {
		this.allFN = allFN;
	}

	public double getAllFScore() {
		return allFScore;
	}

	public void setAllFScore(double allFScore) {
		this.allFScore = allFScore;
	}

	public double getMicrofscore() {
		return microfscore;
	}

	public void setMicrofscore(double microfscore) {
		this.microfscore = microfscore;
	}

	public double getMacrofscore() {
		return macrofscore;
	}

	public void setMacrofscore(double macrofscore) {
		this.macrofscore = macrofscore;
	}

	public static ResultObjects getInstance() {
		ResultObjects eval = new ResultObjects();

		eval.setAllFN(0);
		eval.setAllFP(0);
		eval.setAllTN(0);
		eval.setAllTP(0);
		eval.setAllFScore(0.0);
		return eval;

	}

}
