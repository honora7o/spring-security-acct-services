package accountserviceapp.persistence;

import accountserviceapp.presentation.PaymentDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface EmployeePaymentRepository extends CrudRepository<PaymentDTO, Integer> {
    PaymentDTO findByEmployeeEmailAndPeriod(String employeeEmail, String period);

    @Query("SELECT p.salary FROM PaymentDTO p WHERE p.employeeEmail = :email AND p.period = :period")
    Long findSalaryByEmployeeEmailAndPeriod(@Param("email") String email, @Param("period") String period);

    List<PaymentDTO> findAllByEmployeeEmail(String employeeEmail);
}
