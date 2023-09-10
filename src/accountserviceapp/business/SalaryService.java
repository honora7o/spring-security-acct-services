package accountserviceapp.business;

import accountserviceapp.exceptions.CustomExceptions;
import accountserviceapp.persistence.EmployeePaymentRepository;
import accountserviceapp.presentation.EmployeeSalaryDTO;
import accountserviceapp.presentation.PaymentDTO;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SalaryService {
    private final EmployeePaymentRepository employeePaymentRepository;
    private final UserService userService;

    public SalaryService(EmployeePaymentRepository employeePaymentRepository,
                         UserService userService) {
        this.employeePaymentRepository = employeePaymentRepository;
        this.userService = userService;
    }

    public boolean isValidPaymentDTO(PaymentDTO paymentDTO) {
        return isNonNegativeSalary(paymentDTO.getSalary()) && isValidPeriod(paymentDTO.getPeriod());
    }

    public boolean hasNoDuplicateAllocationsInList(ArrayList<PaymentDTO> paymentDTOList) {
        Set<String> processedKeys = new HashSet<>();

        for (PaymentDTO paymentDTO : paymentDTOList) {
            String key = paymentDTO.getEmployeeEmail() + "-" + paymentDTO.getPeriod();

            if (processedKeys.contains(key)) {
                return false;
            } else {
                processedKeys.add(key);
            }
        }

        return true;
    }


    private boolean isNonNegativeSalary(long salary) {
        return salary >= 0;
    }

    private boolean isValidPeriod(String period) {
        String regex = "^(0[1-9]|1[0-2])-\\d{4}$";
        return period.matches(regex);
    }

    @Transactional
    public void upsertEmployeeSalaryTableByList(List<PaymentDTO> paymentDTOList) {
        for (PaymentDTO paymentDTO : paymentDTOList) {
            upsertEmployeeSalaryTable(paymentDTO);
        }
    }

    @Transactional
    public void upsertEmployeeSalaryTable(PaymentDTO paymentDTO) {
        String email = paymentDTO.getEmployeeEmail();
        String period = paymentDTO.getPeriod();

        PaymentDTO existingPayment = employeePaymentRepository.findByEmployeeEmailAndPeriod(email, period);

        if (existingPayment != null) {
            existingPayment.setSalary(paymentDTO.getSalary());
            employeePaymentRepository.save(existingPayment);
        } else {
            employeePaymentRepository.save(paymentDTO);
        }
    }

    private Long findSalaryByEmailAndPeriod(String email, String period) {
        return employeePaymentRepository.findSalaryByEmployeeEmailAndPeriod(email, period);
    }

    private List<PaymentDTO> findPaymentsByEmail(String email) {
        return employeePaymentRepository.findAllByEmployeeEmail(email);
    }

    public ResponseEntity<?> getEmplPayment(String period, UserDetails userDetails) {
        User currUser = userService.getUserByEmail(userDetails.getUsername());

        if (period != null) {
            validatePeriod(period);

            Long salary = this.findSalaryByEmailAndPeriod(currUser.getEmail().toLowerCase(), period);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(createEmployeeSalaryResponse(currUser, period, salary));
        } else {
            List<PaymentDTO> userPayments = this.findPaymentsByEmail(currUser.getEmail().toLowerCase());
            List<EmployeeSalaryDTO> salaryList = createEmployeeSalaryList(currUser, userPayments);

            Collections.reverse(salaryList);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(salaryList);
        }
    }

    private void validatePeriod(String period) {
        if (Integer.valueOf(period.substring(0, 2)) > 12 || Integer.valueOf(period.substring(0, 2)) < 1) {
            throw new CustomExceptions.InvalidPeriodException();
        }
    }

    private EmployeeSalaryDTO createEmployeeSalaryResponse(User user, String period, Long salary) {
        YearMonth yearMonth = YearMonth.parse(period, DateTimeFormatter.ofPattern("MM-yyyy"));
        String formattedPeriod = yearMonth.format(DateTimeFormatter.ofPattern("MMMM-yyyy").localizedBy(Locale.ENGLISH));
        String formattedSalary = formatSalary(salary);

        return new EmployeeSalaryDTO(user.getFirstName(), user.getLastName(), formattedPeriod, formattedSalary);
    }

    private List<EmployeeSalaryDTO> createEmployeeSalaryList(User user, List<PaymentDTO> payments) {
        List<EmployeeSalaryDTO> salaryList = new ArrayList<>();

        for (PaymentDTO payment : payments) {
            YearMonth yearMonth = YearMonth.parse(payment.getPeriod(), DateTimeFormatter.ofPattern("MM-yyyy"));
            String formattedPeriod = yearMonth.format(DateTimeFormatter.ofPattern("MMMM-yyyy").localizedBy(Locale.ENGLISH));
            String formattedSalary = formatSalary(Long.valueOf(payment.getSalary()));

            EmployeeSalaryDTO salaryDTO = new EmployeeSalaryDTO(user.getFirstName(), user.getLastName(),
                    formattedPeriod, formattedSalary);

            salaryList.add(salaryDTO);
        }

        return salaryList;
    }

    private String formatSalary(Long salary) {
        return String.format("%d dollar(s) %02d cent(s)", salary / 100, salary % 100);
    }
}
