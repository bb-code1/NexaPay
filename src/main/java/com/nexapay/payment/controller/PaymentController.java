package com.nexapay.payment.controller;

import com.nexapay.payment.entity.PaymentEntity;
import com.nexapay.payment.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public record CaptureRequest(
            @NotNull String transactionRef,
            @NotNull @Positive BigDecimal amount
    ) {}

    public record CaptureResponse(
            String paymentRef,
            String transactionRef,
            BigDecimal capturedAmount,
            String status
    ) {}

    @PostMapping("/capture")
    public ResponseEntity<CaptureResponse> capture(
            @RequestHeader(value = "Idempotency-Key", defaultValue = "default-key") String idempotencyKey,
            @Valid @RequestBody CaptureRequest request
    ) {
        PaymentEntity payment = paymentService.capturePayment(request.transactionRef(), idempotencyKey, request.amount());
        return ResponseEntity.ok(new CaptureResponse(
                payment.getPaymentRef(),
                payment.getTransaction().getTransactionRef(),
                payment.getCapturedAmount(),
                payment.getStatus().name()
        ));
    }
}
