package levelUp_backEnd.levelup.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private int cantidad;
    @Column(nullable = false)
    private int total;
    @Column(nullable = false)
    private String estado;   // PAGADO / ERROR

    @Lob
    private String rawPayload;
}
