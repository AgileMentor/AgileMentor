package agilementor.common.exception;

public class AlreadyJoinedMemberException extends RuntimeException {

    private static final String MESSAGE = "이미 프로젝트에 참가한 회원입니다.";

    public AlreadyJoinedMemberException() {
        super(MESSAGE);
    }
}
