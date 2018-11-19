package no.nav.foreldrepenger.errorhandling;

import static com.fasterxml.jackson.annotation.JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.lookup.Constants.NAV_CALL_ID;
import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ImmutableList;

@JsonInclude(JsonInclude.Include.NON_NULL)
class ApiError {

    private final HttpStatus status;
    @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private final LocalDateTime timestamp;
    @JsonFormat(with = WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
    private final List<String> messages;
    private final String uuid;

    ApiError(HttpStatus status, Throwable t) {
        this(status, t, emptyList());
    }

    ApiError(HttpStatus status, Throwable t, List<Object> objects) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.messages = new ImmutableList.Builder<String>()
                .add(getRootCauseMessage(t))
                .addAll(objects.stream()
                        .filter(s -> s != null)
                        .map(Object::toString)
                        .collect(toList()))
                .build();
        this.uuid = MDC.get(NAV_CALL_ID);
    }

    public String getUuid() {
        return uuid;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<String> getMessages() {
        return messages;
    }

    private static String getRootCauseMessage(Throwable e) {
        return getMostSpecificCause(e).getMessage();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[status=" + status + ", timestamp=" + timestamp + ", messages=" + messages
                + ", uuid=" + uuid + "]";
    }
}