package test;


/**
 * 
 * Stores TP,FN,FP (by default set by zero) and Recall, Precision, FScore measures for each Label.
 *
 */
public class EvalObjects {

	
	private double tp ;
	private double fp;
	private double tn ;
	private double fn ;
	private double recall;
	private double precision;
	private double fscore;
	
	
	public double getRecall() {
		return recall;
	}
	public void setRecall(double recall) {
		this.recall = recall;
	}
	public double getPrecision() {
		return precision;
	}
	public void setPrecision(double precision) {
		this.precision = precision;
	}
	public double getFscore() {
		return fscore;
	}
	public void setFscore(double fscore) {
		this.fscore = fscore;
	}
	
	
	
	
	
	public double getTp() {
		return tp;
	}
	public void setTp(double tp) {
		this.tp = tp;
	}
	public double getFp() {
		return fp;
	}
	public void setFp(double fp) {
		this.fp = fp;
	}
	public double getTn() {
		return tn;
	}
	public void setTn(double tn) {
		this.tn = tn;
	}
	public double getFn() {
		return fn;
	}
	public void setFn(double fn) {
		this.fn = fn;
	}
	public static EvalObjects getInstance(){
		
		
		EvalObjects confMatrixDO = new EvalObjects();
		confMatrixDO.setFn(0.0);
		confMatrixDO.setFp(0.0);
		confMatrixDO.setTn(0.0);
		confMatrixDO.setTp(0.0);
		return confMatrixDO;
		
			
	}
	
	

	
}