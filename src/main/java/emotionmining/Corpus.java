package emotionmining;

import java.util.List;

public class Corpus {

	String fileName;
	String predictedFileName;
	List<Tweet> listTweetVO;


	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<Tweet> getListTweetVO() {
		return listTweetVO;
	}

	public void setListTweetVO(List<Tweet> listTweetVO) {
		this.listTweetVO = listTweetVO;
	}

	public static Corpus getInstance() {
		Corpus corpus = new Corpus();
		return corpus;

	}

}