package uz.pdp.app5management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.app5management.entity.Role;
import uz.pdp.app5management.entity.User;
import uz.pdp.app5management.entity.Wages;
import uz.pdp.app5management.entity.enums.RoleName;
import uz.pdp.app5management.payload.ApiResponse;
import uz.pdp.app5management.payload.WagesDto;
import uz.pdp.app5management.repository.UserRepository;
import uz.pdp.app5management.repository.WagesRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WagesService {

    @Autowired
    WagesRepository wagesRepository;

    @Autowired
    UserRepository userRepository;

    public ApiResponse addWages(WagesDto wagesDto) {

        Optional<User> optionalUser = userRepository.findById(wagesDto.getUserId());
        if (optionalUser.isEmpty())
            return new ApiResponse("User not found!", false);

        Wages wages = new Wages();
        wages.setDate(new Date());
        wages.setUser(optionalUser.get());
        wagesRepository.save(wages);
        return new ApiResponse("Wages added!", true);
    }

    public List<Wages> getWagesByUserId(UUID userId) {
        List<Wages> wages = wagesRepository.findAllByUser_Id(userId);
        return wages;
    }

    public List<Wages> getWagesByPrice(double price) {
        return wagesRepository.findAllByPrice(price);
    }

    public ApiResponse editWages(UUID id, WagesDto wagesDto) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Role role = (Role) principal.getRoles().toArray()[0];
        if (role.getRoleName().equals(RoleName.DIRECTOR)) {
            Optional<Wages> optionalWages = wagesRepository.findById(id);
            if (optionalWages.isPresent()) {
                Wages editingWages = optionalWages.get();
                editingWages.setDate(new Date());
                editingWages.setPrice(wagesDto.getPrice());

                Optional<User> optionalUser = userRepository.findById(wagesDto.getUserId());
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    editingWages.setUser(user);
                    wagesRepository.save(editingWages);
                    return new ApiResponse("Wages edited!", true);
                }
            }
        }
        return new ApiResponse("Wages not edited!", false);
    }

    public ApiResponse deleteWages(UUID id) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Role role = (Role) principal.getRoles().toArray()[0];
        if (role.getRoleName().equals(RoleName.DIRECTOR)) {
            Optional<Wages> optionalWages = wagesRepository.findById(id);
            if (optionalWages.isPresent()) {
                wagesRepository.deleteById(id);
            }
        }
        return new ApiResponse("Wages not deleted!", false);
    }
}









