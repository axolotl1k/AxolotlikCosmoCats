package org.axolotlik.axolotlikcosmocats.web.exception;

public record ValidationError(String field, String reason) {}
