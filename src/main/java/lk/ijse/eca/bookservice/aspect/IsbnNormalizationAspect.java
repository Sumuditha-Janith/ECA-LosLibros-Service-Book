package lk.ijse.eca.bookservice.aspect;

import lk.ijse.eca.bookservice.dto.BookRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class IsbnNormalizationAspect {

    @Around("execution(* lk.ijse.eca.bookservice.service.BookService.*(..))")
    public Object normalizeIsbnArguments(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String str && str.matches("^[\\d-]+$")) {
                args[i] = normalize(str);
                log.debug("Normalized ISBN argument: {} -> {}", str, args[i]);
            } else if (args[i] instanceof BookRequestDTO dto && dto.getIsbn() != null) {
                dto.setIsbn(normalize(dto.getIsbn()));
                log.debug("Normalized ISBN in DTO: {}", dto.getIsbn());
            }
        }

        return joinPoint.proceed(args);
    }

    private String normalize(String isbn) {
        // Remove hyphens and any non-digit characters, then return as-is (or could enforce length)
        return isbn.replaceAll("[^\\d]", "");
    }
}
