package com.aleksey.booking.hotels.aspect;

import brave.ScopedSpan;
import brave.Tracer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class TracingAspect {

    private final Tracer tracer;

    @Around("execution(* com.aleksey.booking.hotels.service.BookingServiceImpl.createBooking(..))")
    public Object traceCreateBooking(ProceedingJoinPoint joinPoint) throws Throwable {
        ScopedSpan span = tracer.startScopedSpan("createBooking");
        Object proceed = joinPoint.proceed();
        span.tag("peer.service", "createBooking");
        span.annotate("Client received");
        span.finish();
        return proceed;
    }
}