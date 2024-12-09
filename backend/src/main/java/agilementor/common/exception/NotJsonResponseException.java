package agilementor.common.exception;

public class NotJsonResponseException extends RuntimeException {

    private static final String MESSAGE = "파싱할 데이터가 json 구조가 아닙니다.";

    public NotJsonResponseException() {
        super(MESSAGE);
    }
}
