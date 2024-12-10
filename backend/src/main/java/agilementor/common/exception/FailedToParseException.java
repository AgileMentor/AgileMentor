package agilementor.common.exception;

public class FailedToParseException extends RuntimeException {

    private static final String MESSAGE = "ai api 응답 파싱에 실패했습니다.";

    public FailedToParseException() {
        super(MESSAGE);
    }
}
