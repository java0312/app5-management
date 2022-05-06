package uz.pdp.app5management.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.app5management.entity.Task;
import uz.pdp.app5management.entity.Turnstile;
import uz.pdp.app5management.entity.User;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AboutUser {
    private User user;
    private List<Task> tasks;
    private List<Turnstile> turnstiles;
}
