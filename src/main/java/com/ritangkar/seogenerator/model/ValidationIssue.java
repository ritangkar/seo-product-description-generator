package com.ritangkar.seogenerator.model;

import java.util.List;

public record ValidationIssue(String field, String severity, String message) {
}
