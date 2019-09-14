package mingzuozhibi.common.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
@Setter
public class Result<T> {

    private List<Exception> errors = new LinkedList<>();
    private String errorMessage;
    private T content;

    public void pushError(Exception e) {
        errors.add(e);
    }

    public String formatError() {
        return Optional.ofNullable(errorMessage).orElseGet(() -> formatErrors(errors));
    }

    @Deprecated
    public boolean notDone() {
        return content == null;
    }

    public boolean isUnfinished() {
        return content == null;
    }

    public void syncResult(Result<T> result) {
        if (result.isUnfinished()) {
            this.setErrorMessage(result.formatError());
        } else {
            this.setContent(result.getContent());
        }
    }

    public static <T> Result<T> ofContent(T content) {
        Result<T> result = new Result<>();
        result.setContent(content);
        return result;
    }

    public static Result ofExceptions(Exception... exceptions) {
        Result result = new Result();
        for (Exception e : exceptions) {
            result.pushError(e);
        }
        return result;
    }

    public static Result ofErrorMessage(String errorMessage) {
        Result result = new Result();
        result.setErrorMessage(errorMessage);
        return result;
    }

    public static String formatErrors(Exception... exceptions) {
        return formatErrors(Arrays.asList(exceptions));
    }

    public static String formatErrors(List<Exception> errors) {
        if (errors == null || errors.isEmpty()) {
            return "No Error";
        }
        AtomicInteger count = new AtomicInteger(0);
        return errors.stream()
                .map(e -> e.getClass().getSimpleName() + ": " + e.getMessage())
                .distinct()
                .map(str -> String.format("(%d)[%s]", count.incrementAndGet(), str))
                .collect(Collectors.joining(" "));
    }

}
