package uz.pdp.app5management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.app5management.entity.Turnstile;
import uz.pdp.app5management.entity.User;

import java.util.List;
import java.util.UUID;

@Repository
public interface TurnstileRepository extends JpaRepository<Turnstile, UUID> {

    List<Turnstile> findAllByUser(User user);

}
