package accountserviceapp.presentation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@AllArgsConstructor
@Getter
@JsonPropertyOrder({"name", "lastname", "period", "salary"})
public class EmployeeSalaryDTO {
    private String name;

    @JsonProperty("lastname")
    private String lastName;

    private String period;
    private String salary;
}
