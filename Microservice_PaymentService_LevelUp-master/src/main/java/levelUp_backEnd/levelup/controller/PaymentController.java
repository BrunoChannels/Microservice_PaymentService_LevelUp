package levelUp_backEnd.levelup.controller;

import levelUp_backEnd.levelup.model.Payment;
import levelUp_backEnd.levelup.service.PaymentService;
import levelUp_backEnd.levelup.repository.PaymentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;
    private final PaymentRepository repo;

    public PaymentController(PaymentService service, PaymentRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    public static class CheckoutItem {
        public Long productId;
        public Integer cantidad;
        public Integer price;
    }

    public static class CheckoutRequest {
        public java.util.List<CheckoutItem> items;
        public Long userId;
    }

    @GetMapping
    public ResponseEntity<List<Payment>> list() {
        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping
    public ResponseEntity<Payment> create(@RequestBody Payment body) {
        Payment saved = repo.save(body);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> get(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payment> update(@PathVariable Long id, @RequestBody Payment body) {
        return repo.findById(id).map(p -> {
            p.setCantidad(body.getCantidad());
            p.setTotal(body.getTotal());
            p.setEstado(body.getEstado());
            p.setRawPayload(body.getRawPayload());
            return ResponseEntity.ok(repo.save(p));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest req) {
        try {
            java.util.List<PaymentService.Item> items =
                (req.items == null ? java.util.Collections.<CheckoutItem>emptyList() : req.items)
                .stream()
                .map(i -> new PaymentService.Item(i.productId, i.cantidad, i.price))
                .toList();
            Payment p = service.checkout(items);
            return ResponseEntity.status(201).body(p);
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Error interno en checkout");
        }
    }
}
