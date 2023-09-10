package accountserviceapp.business;

import accountserviceapp.exceptions.CustomExceptions;
import accountserviceapp.presentation.PaymentDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class AccountantService {
    private final SalaryService salaryService;
    private final UserService userService;

    public AccountantService(SalaryService salaryService, UserService userService) {
        this.salaryService = salaryService;
        this.userService = userService;
    }

    public ResponseEntity<?> uploadPayrolls(ArrayList<PaymentDTO> paymentDTOList) {
        for (PaymentDTO currPaymentDTO : paymentDTOList) {
            validatePaymentDTO(currPaymentDTO);
        }

        if (!salaryService.hasNoDuplicateAllocationsInList(paymentDTOList)) {
            throw new CustomExceptions.DuplicatePaymentException();
        }

        salaryService.upsertEmployeeSalaryTableByList(paymentDTOList);
        return new ResponseEntity<>(Map.of("status", "Added successfully!"), HttpStatus.OK);
    }

    public ResponseEntity<?> uploadPayroll(PaymentDTO paymentDTO) {
        validatePaymentDTO(paymentDTO);

        salaryService.upsertEmployeeSalaryTable(paymentDTO);
        return new ResponseEntity<>(Map.of("status", "Updated successfully!"), HttpStatus.OK);
    }

    private void validatePaymentDTO(PaymentDTO paymentDTO) {
        if (!userService.emailExists(paymentDTO.getEmployeeEmail())) {
            throw new CustomExceptions.InvalidEmployeeEmailException();
        }

        if (!salaryService.isValidPaymentDTO(paymentDTO)) {
            throw new CustomExceptions.InvalidPaymentFormatException();
        }
    }
}
