package accountserviceapp.presentation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "employee_salary_db")
public class PaymentDTO {
    @Id
    @GeneratedValue
    private int id;

    @JsonProperty("employee")
    @NotBlank
    private String employeeEmail;

    @NotBlank
    @Pattern(regexp = "^(0[1-9]|1[0-2])-\\d{4}$")
    private String period;

    @Min(value = 1)
    private long salary;
}
