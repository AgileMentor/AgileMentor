package agilementor.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleSocialLoginFailException(
        SocialLoginFailException exception) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleExternalServerException(
        ExternalServerErrorException exception) {
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleMemberNotFoundException(
        MemberNotFoundException exception) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleProjectNotFoundException(
        ProjectNotFoundException exception) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleNotProjectAdminException(
        NotProjectAdminException exception) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleKickOneselfException(
        KickOneselfException exception) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleAlreadyJoinedMemberException(
        AlreadyJoinedMemberException exception) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleInvitationNotFoundException(
        InvalidInvitationException exception) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleInvalidSessionException(
        InvalidSessionException exception) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleSprintNotFoundException(
        SprintNotFoundException exception) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleEndDateNullException(
        EndDateNullException exception) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleTitleNullException(
        TitleNullException exception) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleStoryNotFoundException(
        StoryNotFoundException exception) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleBacklogNotFoundException(
        BacklogNotFoundException exception) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleNotJsonResponseException(
        NotJsonResponseException exception) {
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleFailedToParseException(
        FailedToParseException exception) {
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(new ExceptionResponse(exception.getMessage()));
    }

}
