package levelUp_backEnd.levelup.service;

import levelUp_backEnd.levelup.model.Payment;
import levelUp_backEnd.levelup.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Service
public class PaymentService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PaymentRepository paymentRepository;

    // endpoint base del product-service (configurable por env var)
    private final String PRODUCT_BASE_URL =
            System.getenv().getOrDefault("PRODUCT_BASEURL", "http://localhost:8085/api/v1/products");


    public static class Item {
        public Long productId;
        public Integer cantidad;
        public Integer price;
        public Item(Long productId, Integer cantidad, Integer price) {
            this.productId = productId;
            this.cantidad = cantidad;
            this.price = price;
        }
    }

    public Payment checkout(java.util.List<Item> items) {
        int total = 0;
        int totalCantidad = 0;
        Payment p = new Payment();
        try {
            com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
            String payload = om.writeValueAsString(items);
            p.setRawPayload(payload);
            for (Item it : items) {
                total += (it.price != null ? it.price : 0) * (it.cantidad != null ? it.cantidad : 0);
                totalCantidad += (it.cantidad != null ? it.cantidad : 0);
            }
            p.setTotal(total);
            p.setCantidad(totalCantidad);
            p.setEstado("PAGADO");
            p = paymentRepository.save(p);
            for (Item it : items) {
                try {
                    String getUrl = PRODUCT_BASE_URL + "/" + it.productId;
                    ResponseEntity<ProductDto> pr = restTemplate.getForEntity(getUrl, ProductDto.class);
                    ProductDto dto = pr.getBody();
                    if (dto == null) continue;
                    int current = dto.stock != null ? dto.stock : 0;
                    int qty = it.cantidad != null ? it.cantidad : 0;
                    dto.stock = Math.max(current - qty, 0);
                    String putUrl = PRODUCT_BASE_URL + "/" + it.productId;
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<ProductDto> update = new HttpEntity<>(dto, headers);
                    restTemplate.exchange(putUrl, HttpMethod.PUT, update, ProductDto.class);
                } catch (Exception ignore) {
                }
            }
            p.setEstado("PAGADO");
            return paymentRepository.save(p);
        } catch (Exception ex) {
            p.setTotal(total);
            p.setCantidad(totalCantidad);
            p.setEstado("PAGADO");
            return paymentRepository.save(p);
        }
    }

    public static class ProductDto {
        public Long id;
        public String name;
        public String description;
        public Integer price;
        public String img;
        public String category;
        public Integer stock;
    }

    public Payment updateStatus(Long id, String estado) {
        Payment p = paymentRepository.findById(id).orElseThrow();
        p.setEstado(estado);
        return paymentRepository.save(p);
    }
}
