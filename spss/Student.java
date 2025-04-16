package spss;

import java.util.ArrayList;
import java.util.List;

/*
 * Represents a student in the SPSS system with their name, submissions, and 
 * test results.
 */
public class Student {
	private String name;
	private int numSubmissions;
	private Submission submissions;
	private List<Integer> testResults = new ArrayList<>();

    /*
     * Constructs a student with the given name.
     *
     * parameter name The name of the student.
     */
	public Student(String name) {
		this.name = name;
		this.numSubmissions = 0;
		this.submissions = new Submission(testResults);
	}
 
    /*
     * Calculates the total score of the student based on their submissions.
     *
     * return The total score of the student.
     */
	public int calculateTotalScore() {
		int sum = 0;
        // Calculate the sum of test results from submissions to get the total 
		// score
		for (int score : submissions.getTestResults()) {
			sum += score;
		}
		
		// Return the calculated total score
		return sum;
	}

	// Getter for testResults
	public List<Integer> getTestResults() {
		return testResults;
	}
	
	// Setter for testResults
	public void setTestResults(List<Integer> testResults) {
		this.testResults = testResults;
	}

	// Setter for name
	public void setName(String name) {
		this.name = name;
	}
	
	// Getter for name
	public String getName() {
		return name;
	}
	
	// Setter for numSubmissions
	public void setNumSubmissions(int numSubmissions) {
		this.numSubmissions = numSubmissions;
	}
	
	// Getter for numSubmissions
	public int getNumSubmissions() {
		return numSubmissions;
	}

	// Getter for submissions
	public Submission getSubmissions() {
		return submissions;
	}

	// Setter for submissions
	public void setSubmissions(Submission submissions) {
		this.submissions = submissions;
	}
}
