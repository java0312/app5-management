package uz.pdp.app5management.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {

    @Size(max = 50, min = 5)
    private String firstName;

    @Size(max = 50, min = 5)
    private String lastName;

    @Email
    private String email;

    @NotNull
    private String password;
}
