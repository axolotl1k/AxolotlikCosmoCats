package org.axolotlik.axolotlikcosmocats.web;

import org.axolotlik.axolotlikcosmocats.featuretoggle.exception.FeatureNotAvailableException;
import org.axolotlik.axolotlikcosmocats.service.exception.NotFoundException;
import org.axolotlik.axolotlikcosmocats.web.exception.ValidationError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  // 404 - entity not found
  @ExceptionHandler(NotFoundException.class)
  ProblemDetail handleNotFound(NotFoundException ex) {
    ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    detail.setTitle("Resource Not Found");
    detail.setType(URI.create("not-found"));
    return detail;
  }

  // 503 - feature disabled
  @ExceptionHandler(FeatureNotAvailableException.class)
  ProblemDetail handleFeatureNotAvailable(FeatureNotAvailableException ex) {
    ProblemDetail detail =
        ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    detail.setTitle("Feature Unavailable");
    detail.setType(URI.create("feature-unavailable"));
    return detail;
  }

  // 400 - Bean Validation errors (@NotNull, @Size, etc.)
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    List<ValidationError> fieldErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(err -> new ValidationError(err.getField(), err.getDefaultMessage()))
            .toList();

    List<ValidationError> globalErrors =
        ex.getBindingResult().getGlobalErrors().stream()
            .map(err -> new ValidationError("object", err.getDefaultMessage()))
            .toList();

    List<ValidationError> allErrors =
        Stream.concat(fieldErrors.stream(), globalErrors.stream()).toList();

    ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    detail.setTitle("Validation Failed");
    detail.setType(URI.create("validation-error"));
    detail.setProperty("errors", allErrors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(detail);
  }
}
