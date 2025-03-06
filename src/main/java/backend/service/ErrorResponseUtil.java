package backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ErrorResponseUtil {

    public Mono<Throwable> handleErrorResponse(ClientResponse response) {
        if (response.statusCode() == HttpStatus.UNAUTHORIZED) {

            return formErrorBody(
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                    response.statusCode(),
                    Constants.INVALID_TOKEN,
                    Arrays.asList(Constants.AUTH_FAILED),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase()
            );

        } else if (response.statusCode() == HttpStatus.FORBIDDEN) {

            return formErrorBody(
                    HttpStatus.FORBIDDEN.getReasonPhrase(),
                    response.statusCode(),
                    Constants.INVALID_TOKEN,
                    Arrays.asList(Constants.INVALID_ROLE),
                    HttpStatus.FORBIDDEN.getReasonPhrase()
            );

        } else if (response.statusCode() == HttpStatus.SERVICE_UNAVAILABLE || response.statusCode() == HttpStatus.GATEWAY_TIMEOUT) {

            return response.bodyToMono(String.class)
                    .flatMap(errorBody ->
                            formErrorBody(
                                    HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase() + " OR " + HttpStatus.GATEWAY_TIMEOUT.getReasonPhrase(),
                                    response.statusCode(),
                                    Constants.SERVICE_UNREACHABLE,
                                    Arrays.asList(errorBody),
                                    HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase() + " OR " + HttpStatus.GATEWAY_TIMEOUT.getReasonPhrase()
                            )
                    );

        } else {
            return response.bodyToMono(CustomError.class)
                    .flatMap(errorBody -> handleCustomError(errorBody, response));
        }
    }

    public Mono<Throwable> formErrorBody(String code, HttpStatusCode httpStatusCode, String description, List<String> additionalErrorDescription, String cause) {
        Set<String> uniqueErrorDescriptions = new HashSet<>(additionalErrorDescription);
        // Create the GraphqlCustomError with unique descriptions
        GraphqlCustomError error = new GraphqlCustomError(code, description, List.copyOf(uniqueErrorDescriptions), cause);
        return Mono.error(new GraphqlException(error, httpStatusCode));
    }

    private Mono<Throwable> handleCustomError(CustomError errorBody, ClientResponse clientResponse) {
        List<String> additionalErrorDescription = null;
        if (errorBody.getAdditionalError() != null && !errorBody.getAdditionalError().isEmpty()) {
            if (errorBody.getAdditionalError().get(0).getDescription() != null && errorBody.getAdditionalError().get(0).getDescription().contains(Constants.SIZE_ERROR)) {
                additionalErrorDescription = List.of(Constants.LIMIT_ERROR);
            } else if (errorBody.getAdditionalError().get(0).getDescription() != null && errorBody.getAdditionalError().get(0).getDescription().contains(Constants.INDEX_NOT_FOUND)) {
                additionalErrorDescription = List.of(Constants.DATA_NOT_FOUND);
            } else if (errorBody.getAdditionalError().get(0).getDescription() != null && errorBody.getAdditionalError().get(0).getDescription().contains(Constants.INDICES_NOT_FOUND)) {
                additionalErrorDescription = List.of(Constants.ALERT_RULE_CREATION_ERROR_MESSAGE);
            } else {
                additionalErrorDescription = errorBody.getAdditionalError().stream()
                        .map(additionalError -> additionalError.getDescription())
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
        } else {
            if (StringUtils.isNoneEmpty(errorBody.getDescription())) {
                additionalErrorDescription = List.of(errorBody.getDescription());
            }
        }
        return formErrorBody(
                HttpStatus.valueOf(errorBody.getStatus()).getReasonPhrase(),
                clientResponse.statusCode(),
                errorBody.getDescription(),
                additionalErrorDescription,
                errorBody.getCause()
        );

    }
}
