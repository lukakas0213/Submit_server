package spss;

import java.util.List;

/*
 * This class represents a submission of test results by a student.
 * The class calculates and manages the total score based on the test results.
 */
public class Submission {

	// List of test results for the submission
	private List<Integer> testResults;
	// Total score calculated from the test results
	private int totalScore;
 
    /*
     * Constructs a submission with the given test results.
     *
     * parameter testResults The list of test results for the submission.
     */
	public Submission(List<Integer> testResults) {
		this.testResults = testResults;
		this.totalScore = calculateTotalScore();
	}



	
	// Setter for testResults
	public void setTestResults(List<Integer> testResults) {
		this.testResults = testResults;
	}

	// Getter for testResults
	public List<Integer> getTestResults() {
		return testResults;
	}

	// Calculate total score of the testResults
	private int calculateTotalScore() {
		int sum = 0;
		for (int score : testResults) {
			sum += score;
		}
		return sum;
	}
}
