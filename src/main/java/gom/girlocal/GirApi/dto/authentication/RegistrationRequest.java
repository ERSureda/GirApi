package gom.girlocal.GirApi.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

@Data
public class RegistrationRequest {
    @NotBlank(message = "FirstName is mandatory")
    private String firstName;

    @NotBlank(message = "LastName is mandatory")
    private String lastName;

    @NotBlank(message = "Email is mandatory")
    private String email;

    private String phoneNumber;

    @NotBlank(message = "Password is mandatory")
    private String password;

    @NotBlank(message = "ConfirmPassword is mandatory")
    private String confirmPassword;
}
