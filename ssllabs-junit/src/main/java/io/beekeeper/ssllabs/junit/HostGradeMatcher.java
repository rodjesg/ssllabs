package io.beekeeper.ssllabs.junit;

import io.beekeeper.ssllabs.api.dto.Endpoint;
import io.beekeeper.ssllabs.api.dto.Host;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Matcher for an {@link Endpoint} class. Verified that a given grade has a
 * minimum grade
 */
public class HostGradeMatcher extends BaseMatcher<Host> {

    private final static List<String> GRADES = Arrays.asList("A+", "A", "A-", "B", "C", "D", "E", "F");
    
    private final Set<String> acceptableGrades = new HashSet<String>();

    /**
     * Instantiates a new host grade matcher.
     *
     * @param minimumGrade
     *            the minimum grade. The matcher will pass if the endpoint grade
     *            is equal to the minumum grade or if the grade is better. Must
     *            not be <code>null</code>
     */
    public HostGradeMatcher(String minimumGrade) {
        super();
        if (minimumGrade == null || !GRADES.contains(minimumGrade)) {
            throw new IllegalArgumentException("minimumGrade must be one of " + GRADES);
        }

        for (String grade : GRADES) {
            acceptableGrades.add(grade);
            if (grade.equals(minimumGrade)) {
                break;
            }
        }
    }

    @Override
    public boolean matches(Object item) {
        if (item instanceof Host) {
            return getFailingEndpoints((Host)item).isEmpty();
        } else {
            throw new IllegalArgumentException("This matcher only applies to Host Objects");
        }
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        if (item instanceof Host) {
            List<Endpoint> endpoints = getFailingEndpoints((Host)item);
            List<String> out = new ArrayList<>();
            for (Endpoint e : endpoints) {
                out.add(e.ipAddress + ":" + getEndpointGrade(e));
            }
            description.appendText("was ").appendValueList("[", ",", "]", out);
        } else {
            throw new IllegalArgumentException("This matcher only applies to Host Objects");
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(" expected all host endpoint grades to be in ")
                   .appendValueList("[", ",", "]", acceptableGrades);
    }

    private List<Endpoint> getFailingEndpoints(Host host) {
        List<Endpoint> result = new LinkedList<Endpoint>();
        for (Endpoint endpoint : host.endpoints) {
            if (!acceptableGrades.contains(getEndpointGrade(endpoint))) {
                result.add(endpoint);
            }
        }
        return result;
    }

    private String getEndpointGrade(Endpoint e) {
        return e.grade;
    }

}