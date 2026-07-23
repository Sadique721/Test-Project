package com.diameter.config;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Cross-cutting method tracing for the Spring-managed layers (controllers, service
 * implementations, repositories and kafka components).
 *
 * <p>For every advised method it logs:
 * <ul>
 *   <li>method entry (with truncated arguments)</li>
 *   <li>method exit together with the elapsed time in milliseconds</li>
 *   <li>any uncaught exception, with the full stack trace (class / file / line numbers)</li>
 * </ul>
 *
 * <p>Entry/exit/timing are emitted at DEBUG so production (INFO) stays quiet; enable with
 * {@code logging.level.com.diameter=DEBUG} (env {@code ENV_DIAMETER_LOG_LEVEL=DEBUG}).
 * Uncaught exceptions are always logged at ERROR.
 *
 * <p>NOTE: this only intercepts Spring beans. The Diameter protocol handlers and the
 * vendored {@code com.diameter.commons} stack are not Spring beans and are instrumented
 * separately.
 */
@Aspect
@Component
public class MethodLoggingAspect {

    /** Maximum rendered length of the argument list, to keep log lines bounded. */
    private static final int MAX_ARGS_LENGTH = 500;

    @Pointcut(
            "execution(* com.diameter.controller..*(..)) || " +
            "execution(* com.diameter.serviceImpl..*(..)) || " +
            "execution(* com.diameter.repository..*(..)) || " +
            "execution(* com.diameter.kafka..*(..))"
    )
    public void applicationLayer() {
        // pointcut marker
    }

    @Around("applicationLayer()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        final Logger log = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());
        final String method = joinPoint.getSignature().getName();
        final boolean trace = log.isDebugEnabled();
        final long start = System.currentTimeMillis();

        if (trace) {
            log.debug(">> ENTRY {}() args={}", method, renderArgs(joinPoint.getArgs()));
        }

        try {
            Object result = joinPoint.proceed();
            if (trace) {
                log.debug("<< EXIT  {}() tookMs={}", method, System.currentTimeMillis() - start);
            }
            return result;
        } catch (Throwable ex) {
            // Trailing throwable -> SLF4J prints the full stack trace incl. line numbers.
            log.error("!! EXCEPTION in {}() after {}ms : {}",
                    method, System.currentTimeMillis() - start, ex.getMessage(), ex);
            throw ex;
        }
    }

    private static String renderArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        String rendered;
        try {
            rendered = Arrays.toString(args);
        } catch (Exception e) {
            return "[unrenderable args]";
        }
        if (rendered.length() > MAX_ARGS_LENGTH) {
            return rendered.substring(0, MAX_ARGS_LENGTH) + "...(truncated)";
        }
        return rendered;
    }
}
