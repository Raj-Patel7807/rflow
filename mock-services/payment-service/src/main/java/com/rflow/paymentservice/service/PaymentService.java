package com.rflow.paymentservice.service;

import com.rflow.paymentservice.exception.PaymentNotFoundException;
import com.rflow.paymentservice.model.Payment;
import com.rflow.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElseThrow(() -> new PaymentNotFoundException("Payment Not Found with Id: " + id));
    }

    public Payment createPayment(Payment payment) {
        payment.setCreatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    public Payment updatePayment(Long id, Payment payment) {
        Payment oldPayment = paymentRepository.findById(id).orElseThrow(() -> new PaymentNotFoundException("Payment Not Found with Id: " + id));

        oldPayment.setAmount(payment.getAmount());
        oldPayment.setStatus(payment.getStatus());
        oldPayment.setPaymentMethod(payment.getPaymentMethod());

        return paymentRepository.save(oldPayment);
    }

    public Payment updatePaymentStatus(Long id, Payment payment) {
        Payment oldPayment = paymentRepository.findById(id).orElseThrow(() -> new PaymentNotFoundException("Payment Not Found with Id: " + id));

        oldPayment.setStatus(payment.getStatus());

        return paymentRepository.save(oldPayment);
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    public List<Payment> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId);
    }
}
