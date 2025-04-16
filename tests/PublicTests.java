package tests;

// (c) Larry Herman, 2024.  You are allowed to use this code yourself, but
// not to provide it to anyone else.

import spss.SPSS;
import java.util.Arrays;
import org.junit.*;
import static org.junit.Assert.*;

public class PublicTests {

    // Tests that some methods are treating names that are not spelled
    // identically as different.  Note that this test does not create or use
    // any threads.
    @Test public void testPublic1() {
        SPSS server= new SPSS(4);

        assertTrue(server.addStudent("Jon"));
        assertTrue(server.addStudent("Jonathan"));

        server.addSubmission("Jon", Arrays.asList(9, 1, 1, 1));
        server.addSubmission("Jonathan", Arrays.asList(9, 0, 0, 0));
        server.addSubmission("Jonathan", Arrays.asList(0, 0, 9, 0));

        assertEquals(1, server.numSubmissions("Jon"));
        assertEquals(2, server.numSubmissions("Jonathan"));
        assertTrue(server.gotExtraCredit("Jon"));
        assertFalse(server.gotExtraCredit("jon"));
        assertFalse(server.gotExtraCredit("Jonathan"));
        assertTrue(server.satisfactory("Jon"));
        assertFalse(server.satisfactory("Jonathan"));
        assertEquals(12, server.score("Jon"));
        assertEquals(9, server.score("Jonathan"));
    }

    // Tests calling satisfactory() for a student who hasn't made any
    // submissions at all, and on a SPSS with several students, some of whom
    // have made satisfactory submissions, but some have not.  Note that
    // this test does not create or use any threads.
    @Test public void testPublic2() {
        SPSS server= new SPSS(5);

        server.addStudent("gh gh");

        assertFalse(server.satisfactory("gh gh"));

        server.addSubmission("gh gh", Arrays.asList(1, 0, 0, 0, 0));
        assertFalse(server.satisfactory("gh gh"));
        server.addSubmission("gh gh", Arrays.asList(1, 2, 0, 0, 0));
        assertFalse(server.satisfactory("gh, gh"));

        server.addStudent("iu iu");
        server.addSubmission("iu iu", Arrays.asList(0, 12, 0, 1, 1));
        assertTrue(server.satisfactory("iu iu"));
    }

    // Tests calling satisfactory() in boundary cases.  Note that this test
    // does not create or use any threads.
    @Test public void testPublic3() {
        SPSS server= new SPSS(4);

        server.addStudent("a");
        server.addSubmission("a", Arrays.asList(0, 0, 0, 0));
        assertFalse(server.satisfactory("a"));

        server.addStudent("b");
        server.addSubmission("b", Arrays.asList(1, 1, 1, 0));
        assertTrue(server.satisfactory("b"));

        server.addStudent("c");
        server.addSubmission("c", Arrays.asList(0, 2, 0, 0));
        assertFalse(server.satisfactory("c"));

        server.addStudent("d");
        server.addSubmission("d", Arrays.asList(0, 2, 3, 0));
        assertTrue(server.satisfactory("d"));

        server.addStudent("e");
        server.addSubmission("e", Arrays.asList(0, 2, 0, 0));
        assertFalse(server.satisfactory("e"));

        server.addStudent("f");
        server.addSubmission("f", Arrays.asList(0, 2, 3, 4));
        assertTrue(server.satisfactory("f"));

        server.addStudent("g");
        server.addSubmission("g", Arrays.asList(0, 2, 0, 4));
        assertTrue(server.satisfactory("g"));
    }

    // Tests calling readSubmissionsConcurrently() to create one thread to
    // read one list of submissions made by one student, to ensure that one
    // thread can be created and manipulated correctly.
    @Test public void testPublic4() {
        SPSS server= new SPSS(5);

        server.addStudent("GinnyGiraffe");

        server.readSubmissionsConcurrently(Arrays.asList("public4-input"));

        assertEquals(1, server.numStudents());
        assertEquals(7, server.numSubmissions());
        assertEquals(7, server.numSubmissions("GinnyGiraffe"));
        assertTrue(server.satisfactory("GinnyGiraffe"));
        assertEquals(75, server.score("GinnyGiraffe"));
    }

    // Tests calling readSubmissionsConcurrently() to create one thread to
    // read one list of submissions made by two students.
    @Test public void testPublic5() {
        SPSS server= new SPSS(5);

        server.addStudent("GinnyGiraffe");
        server.addStudent("WallyWalrus");

        server.readSubmissionsConcurrently(Arrays.asList("public5-input"));

        assertEquals(2, server.numStudents());
        assertEquals(13, server.numSubmissions());

        assertEquals(7, server.numSubmissions("GinnyGiraffe"));
        assertTrue(server.satisfactory("GinnyGiraffe"));
        assertEquals(75, server.score("GinnyGiraffe"));

        assertEquals(6, server.numSubmissions("WallyWalrus"));
        assertTrue(server.satisfactory("WallyWalrus"));
        assertEquals(85, server.score("WallyWalrus"));
    }

    // Tests calling readSubmissionsConcurrently() to create one thread to
    // read one list of submissions made by two students, which also
    // contains some invalid submissions made by nonexistent students.
    @Test public void testPublic6() {
        SPSS server= new SPSS(5);

        server.addStudent("GinnyGiraffe");
        server.addStudent("WallyWalrus");

        server.readSubmissionsConcurrently(Arrays.asList("public6-input"));

        assertEquals(2, server.numStudents());
        assertEquals(13, server.numSubmissions());

        assertEquals(7, server.numSubmissions("GinnyGiraffe"));
        assertTrue(server.satisfactory("GinnyGiraffe"));
        assertEquals(75, server.score("GinnyGiraffe"));

        assertEquals(6, server.numSubmissions("WallyWalrus"));
        assertTrue(server.satisfactory("WallyWalrus"));
        assertEquals(85, server.score("WallyWalrus"));
    }

    // Tests calling readSubmissionsConcurrently() to create two threads to
    // read two lists of submissions made by two students (each file
    // contains the submissions of one student).
    @Test public void testPublic7() {
        SPSS server= new SPSS(5);

        server.addStudent("GinnyGiraffe");
        server.addStudent("WallyWalrus");

        server.readSubmissionsConcurrently(Arrays.asList("public7a-input",
                                                         "public7b-input"));

        assertEquals(2, server.numStudents());
        assertEquals(13, server.numSubmissions());

        assertEquals(7, server.numSubmissions("GinnyGiraffe"));
        assertTrue(server.satisfactory("GinnyGiraffe"));
        assertEquals(75, server.score("GinnyGiraffe"));

        assertEquals(6, server.numSubmissions("WallyWalrus"));
        assertTrue(server.satisfactory("WallyWalrus"));
        assertEquals(85, server.score("WallyWalrus"));
    }

    // Tests calling readSubmissionsConcurrently() to create two threads to
    // read two lists of submissions made by two students (both files
    // contain submissions made by both students).
    @Test public void testPublic8() {
        SPSS server= new SPSS(5);

        server.addStudent("GinnyGiraffe");
        server.addStudent("WallyWalrus");

        server.readSubmissionsConcurrently(Arrays.asList("public8a-input",
                                                         "public8b-input"));

        assertEquals(2, server.numStudents());
        assertEquals(13, server.numSubmissions());

        assertEquals(7, server.numSubmissions("GinnyGiraffe"));
        assertTrue(server.satisfactory("GinnyGiraffe"));
        assertEquals(75, server.score("GinnyGiraffe"));

        assertEquals(6, server.numSubmissions("WallyWalrus"));
        assertTrue(server.satisfactory("WallyWalrus"));
        assertEquals(85, server.score("WallyWalrus"));
    }

    // Tests calling readSubmissionsConcurrently() to create two threads,
    // which independently read the same file of submissions for the same
    // student.  Note that, unlike Project #1, all submissions are now
    // counted, so even though the same submissions are processed twice, the
    // students' number of submissions increase.
    @Test public void testPublic9() {
        SPSS server= new SPSS(5);

        server.addStudent("GinnyGiraffe");
        server.addStudent("WallyWalrus");

        server.readSubmissionsConcurrently(Arrays.asList("public9-input",
                                                         "public9-input"));

        assertEquals(2, server.numStudents());
        assertEquals(26, server.numSubmissions());

        assertEquals(14, server.numSubmissions("GinnyGiraffe"));
        assertTrue(server.satisfactory("GinnyGiraffe"));
        assertEquals(75, server.score("GinnyGiraffe"));

        assertEquals(12, server.numSubmissions("WallyWalrus"));
        assertTrue(server.satisfactory("WallyWalrus"));
        assertEquals(85, server.score("WallyWalrus"));
    }

    // Tests calling readSubmissionsConcurrently() with some invalid
    // filenames in its argument list.
    @Test public void testPublic10() {
        SPSS server= new SPSS(5);

        server.addStudent("GinnyGiraffe");
        server.addStudent("WallyWalrus");

        server.readSubmissionsConcurrently(Arrays.asList("nonexistent-file",
                                                         "public10-input",
                                                         "also-nonexistent"));

        assertEquals(2, server.numStudents());
        assertEquals(13, server.numSubmissions());

        assertEquals(7, server.numSubmissions("GinnyGiraffe"));
        assertTrue(server.satisfactory("GinnyGiraffe"));
        assertEquals(75, server.score("GinnyGiraffe"));

        assertEquals(6, server.numSubmissions("WallyWalrus"));
        assertTrue(server.satisfactory("WallyWalrus"));
        assertEquals(85, server.score("WallyWalrus"));
    }

}
