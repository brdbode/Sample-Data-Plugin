package com.example.mockapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class IssueController {

    @GetMapping("/issues")
    public List<Issue> getIssues() {
        return List.of(
            new Issue(1, "Outdated dependency", "medium", "2025-08-20T10:15:00Z"),
            new Issue(2, "SQL injection risk", "high", "2025-08-21T08:03:00Z"),
            new Issue(3, "Weak TLS settings", "low", "2025-08-22T12:45:00Z")
        );
    }
}
