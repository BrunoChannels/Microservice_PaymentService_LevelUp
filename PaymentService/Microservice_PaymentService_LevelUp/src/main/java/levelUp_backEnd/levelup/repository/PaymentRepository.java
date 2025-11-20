package levelUp_backEnd.levelup.repository;

import levelUp_backEnd.levelup.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    


}
