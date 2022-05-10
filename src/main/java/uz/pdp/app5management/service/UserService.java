package uz.pdp.app5management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.app5management.entity.Role;
import uz.pdp.app5management.entity.Task;
import uz.pdp.app5management.entity.Turnstile;
import uz.pdp.app5management.entity.User;
import uz.pdp.app5management.entity.enums.RoleName;
import uz.pdp.app5management.payload.AboutUser;
import uz.pdp.app5management.payload.ApiResponse;
import uz.pdp.app5management.repository.TaskRepository;
import uz.pdp.app5management.repository.TurnstileRepository;
import uz.pdp.app5management.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TurnstileRepository turnstileRepository;

    public List<User> getAllUsersForDirectorAndManager() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Role role = (Role) principal.getRoles().toArray()[0];
        if (role.getRoleName().equals(RoleName.DIRECTOR))
            return userRepository.findAll();
        if (role.getRoleName().equals(RoleName.HR_MANAGER))
            return userRepository.findAllByIdNot(principal.getId());
        return null;
    }

    public AboutUser getAllInfoAboutUser(UUID id) {

        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Role role = (Role) principal.getRoles().toArray()[0];
        if (!role.getRoleName().equals(RoleName.DIRECTOR) || !role.getRoleName().equals(RoleName.HR_MANAGER))
            return null;

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty())
            return null;

        User user = optionalUser.get();
        List<Task> tasks = taskRepository.findAllByToUser(user);
        List<Turnstile> turnstiles = turnstileRepository.findAllByUser(user);

        return new AboutUser(user, tasks, turnstiles);
    }

    public ApiResponse deleteUser(UUID id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()){

            User deletingUser = optionalUser.get();
            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            Role principalRole = (Role) principal.getRoles().toArray()[0];
            Role deletingUserRole = (Role) deletingUser.getRoles().toArray()[0];
            if (principalRole.getRoleName().equals(RoleName.DIRECTOR) ||
                    (principalRole.getRoleName().equals(RoleName.HR_MANAGER) && deletingUserRole.getRoleName().equals(RoleName.WORKER))
            || principalRole.getRoleName().equals(deletingUserRole.getRoleName())) {
                userRepository.delete(deletingUser);
                return new ApiResponse("User deleted!", true);
            }
        }
        return new ApiResponse("User not found!", false);
    }
}
