package iut.nantes.project.gateway

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleContactIsInAStoreException(ex: AuthorizationDeniedException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<String> {
        return ResponseEntity("Internal server error: ${ex.message}", HttpStatus.INTERNAL_SERVER_ERROR)
    }
}