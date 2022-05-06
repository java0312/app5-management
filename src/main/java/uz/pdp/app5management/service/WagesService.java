package uz.pdp.app5management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.app5management.entity.User;
import uz.pdp.app5management.entity.Wages;
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

        Optional<User> optionalUser = userRepository.findById(wagesDto.getId());
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
}









