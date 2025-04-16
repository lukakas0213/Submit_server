package spss;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * The SPSS class represents a system for managing student submissions and 
 * scores. It includes methods for adding students, submitting scores, reading 
 * submissions from files concurrently, calculating scores, and checking
 *  various criteria for student performance.
 */
public class SPSS extends Thread {
	private int numTests;
	private int numStudents;
	private ArrayList<Student> students;

	/*
	 * Constructor initialize numTest, numStudents, and students. 
	 */
	public SPSS(int numTests) {
		if (numTests > 0) {
			this.numTests = numTests;
		} else {
			this.numTests = 1;
		}
		this.numStudents = 0;
		students = new ArrayList<Student>();
	}



	/*
	 * Adds a new student to the system.
	 *
	 * parameter newStudent The name of the new student to be added.
	 * return true if the student is successfully added, false if the student
	 * name is null, empty, or already exists in the system.
	 */
	public boolean addStudent(String newStudent) {
	    // Check if the new student name is valid
		if (newStudent == null || newStudent.isEmpty()) {
			return false;
		}

	    // Check if the student already exists in the system
		for (int i = 0; i < students.size(); i++) {
			if (students.get(i).getName().equals(newStudent)) {
				return false;
			}
		}

	    // Create a new Student object and add it to the list of students
		Student std = new Student(newStudent);
		students.add(std);

	    // Increment the total number of students in the system
		numStudents++;

		// Return true if the student is added
		return true;
	}

	// Return the number of students.
	public int numStudents() {
		return students.size();
	}

	/* Helper method
	 * Checks if the list of test results contains any negative scores.
	 *
	 * parameter testResults The list of test results to be checked.
	 * return true if the list contains at least one negative score, false
	 *  otherwise or if the list is null.
	*/
	public boolean hasNegativeScore(List<Integer> testResults) {
	    // Check if the list of test results is null or empty
	    if (testResults == null || testResults.isEmpty()) {
	        return false;
	    }
	    
	    // Iterate through the test results to find any negative scores
		for (Integer score : testResults) {
			if (score != null && score < 0) {
				// Return true if negative score is found
				return true;
			}
		}
		// Return false if no negative score is found
		return false;
	}

	/*
	 * Helper method
	 * Calculates the total score from a list of test results.
	 *
	 * parameter testResults The list of test results for which the total score
	 * is to be calculated.
	 * return The sum of all scores in the list, or 0 if the list is null or 
	 * empty.
	 */	
	private int calculateTotalScore(List<Integer> testResults) {
		int sum = 0;
	    // Check if the list of test results is null or empty
	    if (testResults == null || testResults.isEmpty()) {
	    	// Return 0 if the list is null or empty
	        return sum; 
	    }
	    
	    // Iterate through the test results to calculate the total score
		for (int score : testResults) {
			sum += score;
		}

		return sum;
	}

	/*
	 * Adds a submission for a student.
	 * 
	 * parameter name The name of the student whose submission is being added.
	 * parameter testResults The list of test results for the submission.
	 * return True if the submission is added successfully, false otherwise.
	 */
	public synchronized boolean addSubmission(String name, 
			List<Integer> testResults) {
	    // Get the index of the student with the given name
		int index = indexOfStd(name);
		
	    // Check if the name is null or empty
		if (name == null || name.isEmpty()) {
			return false;
		}
		
	    // Check if the test results are valid
		if (testResults == null || testResults.size() != numTests || 
				testResults.contains(null)
				|| hasNegativeScore(testResults)) {
			// Return false if the test results are invalid
			return false;
		}

	    // Check if the student exists
		if (!containStd(name)) {
			// Return false if the student does not exist
			return false;
		}

	    // Check if the student's submission list is empty
		// Return true if the submission is added successfully
		if (containStd(name)) {
			if (students.get(index).getSubmissions().getTestResults()
					== null) {
				students.get(index).getSubmissions().
				setTestResults(testResults);
				students.get(index).setNumSubmissions(
						students.get(index).getNumSubmissions() + 1);
				return true;
			} else if (students.get(index).calculateTotalScore()
					<= calculateTotalScore(testResults)) {
				students.get(index).getSubmissions().setTestResults(
						testResults);
				students.get(index).setNumSubmissions(
						students.get(index).getNumSubmissions() + 1);
				return true;
			} else if (students.get(index).calculateTotalScore()
					> calculateTotalScore(testResults)) {
				students.get(index).setNumSubmissions(
						students.get(index).getNumSubmissions() + 1);
				return true;
			}
		}
		
		// Return false if the submission cannot be added
		return false;
	}

	/*
	 * Reads submissions concurrently from multiple files.
	 * 
	 * parameter fileNames The list of file names containing submissions.
	 * return True if submissions are read successfully, false otherwise.
	 */
	public boolean readSubmissionsConcurrently(List<String> fileNames) {
	    // Check if the fileNames list is null
		if (fileNames == null) {
	        return false;
	    }
	    // Create a list to store threads
	    List<Thread> threads = new ArrayList<>();

	    // Iterate through each file name in the list
	    for (String fileName : fileNames) {
	        // Create a new thread for each file
	        Thread thread = new Thread(new Runnable() {
	            @Override
	            public void run() {
	                try {
	                    // Create a BufferedReader to read the file
	                    BufferedReader reader = new BufferedReader(
	                    		new FileReader("./" + fileName));
	                    String line;

	                    // Read each line from the file
	                    while ((line = reader.readLine()) != null) {
	                        // Split the line into parts using whitespace
	                        String[] parts = line.split("\\s+");
	                        List<Integer> testResults = new ArrayList<>();
	                        // Convert the test results to integers and add to
	                        //the list
	                        for (int i = 1; i < parts.length; i++) {
	                            testResults.add(Integer.parseInt(parts[i]));
	                        }
	                        // Synchronize the addition of submission
	                        synchronized(this) {
	                        addSubmission(parts[0], testResults);
	                        }
	                    }
	                    reader.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                    System.err.println("Error reading file: " + fileName);
	                }
	            }
	        });
	        // Add the thread to the list
	        threads.add(thread);
	    }
	    // Start all the threads
	    for (Thread thread : threads) {
	        thread.start();
	    }

	    // Wait for all threads to finish
	    for (Thread thread : threads) {
	        try {
	            thread.join();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    }
	    // Return true if submissions are read successfully
	    return true;
	}
	

	/*
	 * Checks if a student with the given name exists in the students list.
	 * 
	 * parameter name The name of the student to check.
	 * return True if a student with the given name exists, false otherwise.
	 */	public boolean containStd(String name) {
		// Iterate through the students list
		for (int i = 0; i < students.size(); i++) {
	        // Check if the name of the student at index i matches the given
			// name
			if (students.get(i).getName().equals(name)) {
				// Return true if a match is found
				return true;
			}
		}
		// Return false if no match is found
		return false;
	}

	/*
	 * Helper method
	 * Finds the index of a student with the given name in the students list.
	 * 
	 * parameter name The name of the student to find.
	 * return The index of the student in the list, or -1 if not found.
	 */	public int indexOfStd(String name) {
		 // Initialize the result to -1 (not found)
		int result = -1;
	    // Iterate through the students list
		for (int i = 0; i < students.size(); i++) {
	        // Check if the name of the student at index i matches the given 
			// name
			if (students.get(i).getName().equals(name)) {
	            // Update the result to the index i if found
				result = i;
			}
		}
		// Return the index of the student or -1 if not found
		return result;
	}

	/*
	 * Calculates the total score for a student with the given name.
	 * 
	 * parameter name The name of the student to calculate the score for.
	 * return The total score of the student, or -1 if the name is null, empty,
	 *  or the student does not exist.
	 */
	public int score(String name) {
	    // Get the index of the student with the given name
		int index = indexOfStd(name);

	    // Check if the name is null, empty, or the student does not exist
		if (name == null || name.isEmpty() || !containStd(name)) {
			// Return -1 if any of the conditions are true
			return -1;
		}
	    // Calculate and return the total score of the student at the determined
		// index
		return students.get(index).calculateTotalScore();
	}

	/*
	 * Retrieves the number of submissions for a student with the given name.
	 * 
	 * parameter name The name of the student to retrieve the number of 
	 * submissions for.
	 * return The number of submissions of the student, 0 if they have no 
	 * submissions, or -1 if the name is null, empty, or the student does not 
	 * exist.
	 */
	public int numSubmissions(String name) {
	    // Get the index of the student with the given name
		int index = indexOfStd(name);
	    // Check if the name is null, empty, or the student does not exist
		if (name == null || name.isEmpty() || !containStd(name)) {
			// Return -1 if any of the conditions are true
			return -1;
		}
		
	    // Check if the student has no submissions
		if (students.get(index).getNumSubmissions() == 0) {
			// Return 0 if the student has no submissions
			return 0;
		}
	    // Return the number of submissions of the student
		return students.get(index).getNumSubmissions();
	}

	/*
	 * Retrieves the total number of submissions across all students.
	 * 
	 * return The total number of submissions across all students.
	 */
	public int numSubmissions() {
		int totalSubmissions = 0;
		
	    // Iterate through all students and sum up their number of submissions
		for (Student student : students) {
			totalSubmissions += student.getNumSubmissions();
		}
	    // Return the total number of submissions
		return totalSubmissions;
	}

	/*
	 * Checks if a student's performance is satisfactory based on the number of
	 * passed tests.
	 * 
	 * parameter name The name of the student.
	 * return True if the student's performance is satisfactory, otherwise
	 * false.
	 */
	public boolean satisfactory(String name) {
	    // Check if the name is null or empty
		if (name == null || name.isEmpty()) {
			return false;
		}
		
	    // Check if the student exists
		if (!containStd(name)) {
			return false;
		}
	    // Get the index of the student
		int index = indexOfStd(name);
	    // Get the number of tests
		int numOfTest = students.get(index).getSubmissions().
				getTestResults().size();
	    // Count the number of passed tests
		int numOfPassedTest = 0;
		for (int i = 0; i < numOfTest; i++) {
			if (students.get(index).getSubmissions().getTestResults().get(i)
					> 0) {
				numOfPassedTest++;
			}
		}
	    // Check if the number of passed tests is at least half of the total 
		// tests and greater than 0
		if (numOfPassedTest >= numOfTest / 2 && numOfPassedTest > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/*
	* This method checks if a student with the given name has received extra
	* credit for all tests.
	* 
	* return true if the student has received extra credit for all tests, 
	* false otherwise.
	*/
	public boolean gotExtraCredit(String name) {
		// Check for invalid input
		if (name == null || name.isEmpty()) {
			return false;
		}
		
		if (!containStd(name)) {
			// Student not found
			return false;
		}

		int index = indexOfStd(name);
	    // Check if the student has received extra credit for all tests
		for (int i = 0; i < students.get(index).getSubmissions().
				getTestResults().size(); i++) {
			if (students.get(index).getSubmissions().getTestResults().
					get(i).equals(0)) {
				// Student has not received extra credit for this test
				return false;
			}
		}
		// Student has received extra credit for all tests
		return true;
	}
}
