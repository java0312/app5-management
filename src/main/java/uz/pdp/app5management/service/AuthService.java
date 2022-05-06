package uz.pdp.app5management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.app5management.entity.Role;
import uz.pdp.app5management.entity.Turnstile;
import uz.pdp.app5management.entity.User;
import uz.pdp.app5management.entity.enums.RoleName;
import uz.pdp.app5management.payload.ApiResponse;
import uz.pdp.app5management.payload.LoginDto;
import uz.pdp.app5management.payload.RegisterDto;
import uz.pdp.app5management.repository.RoleRepository;
import uz.pdp.app5management.repository.TurnstileRepository;
import uz.pdp.app5management.repository.UserRepository;
import uz.pdp.app5management.security.JwtProvider;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    TurnstileRepository turnstileRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(username);
        return optionalUser.orElse(null);
    }

    /*
     * Register account
     * */
    public ApiResponse registerUser(RegisterDto registerDto) {
        boolean existsByEmail = userRepository.existsByEmail(registerDto.getEmail());
        if (existsByEmail)
            return new ApiResponse("User already registered!", false);

        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        user.setEmailCode(UUID.randomUUID().toString());



        /*
         * authentication da hechkim yo'a bo'lsa, yani qo'shilayaotgan user 1 - user bo'lsa uning ro'li Director bo'ladi
         * Agar authentication da Director bo'lsa, qo'shilayotgan user ro'li HR_MANAGER bo'ladi
         * agar athenticationda HR_MANAGER bo'lsa, qo'shilayotgan user WORKER bo'ladi
         *
         * **************************
         * */
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        RoleName roleName = RoleName.WORKER;
        if (authentication.getPrincipal().toString().equals("anonymousUser"))
            roleName = RoleName.DIRECTOR;
        else {
            User principal = (User) authentication.getPrincipal();
            Set<Role> roles = principal.getRoles();
            for (Role role : roles) {
                if (role.getRoleName().equals(RoleName.DIRECTOR)) {
                    roleName = RoleName.HR_MANAGER;
                    break;
                }
            }
        }
        user.setRoles(Collections.singleton(roleRepository.findByRoleName(roleName)));
        /*
         * ***************************
         * */

        User savedUser = userRepository.save(user);

        boolean sendEmail = sendEmail(user.getEmail(), user.getEmailCode());
        if (sendEmail)
            return new ApiResponse("User successfully registered!", true);

        return new ApiResponse("Error in register user", false);
    }

    private boolean sendEmail(String sendingEmail, String emailCode) {

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("Test@gmail.com");
            mailMessage.setTo(sendingEmail);
            mailMessage.setSubject("Confirm Account");
            mailMessage.setText(
                    "http://localhost:8080/api/auth/verifyEmail?email=" + sendingEmail + "&emailCode=" + emailCode
            );
            javaMailSender.send(mailMessage);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    /*
     * Confirm account
     * */
    public ApiResponse verifyEmail(String email, String emailCode) {
        Optional<User> optionalUser = userRepository.findByEmailAndEmailCode(email, emailCode);
        if (optionalUser.isEmpty())
            return new ApiResponse("User not found!", false);
        User user = optionalUser.get();
        user.setEmailCode(null);
        user.setEnabled(true);
        userRepository.save(user);
        return new ApiResponse("User confirmed!", true);
    }


    /**
     * Enter to system
     *
     * @param loginDto
     * @return
     */
    public ApiResponse login(LoginDto loginDto) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(),
                            loginDto.getPassword()
                    )
            );

            User user = (User) authenticate.getPrincipal();

            String token = jwtProvider.generateToken(loginDto.getEmail(), user.getRoles());


            Turnstile turnstile = new Turnstile();
            turnstile.setEnter(true);
            turnstile.setUser(user);
            turnstileRepository.save(turnstile);


            return new ApiResponse("You entered to system", true, token);
        } catch (Exception e) {
            return new ApiResponse("Email or password is wrong!", false);
        }
    }

    public void logout() {

        User user = null;
        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Turnstile turnstile = new Turnstile();
            turnstile.setUser(user);
            turnstileRepository.save(turnstile);
        }

        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
