package uz.pdp.app5management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.app5management.entity.Wages;

import java.util.List;
import java.util.UUID;

@Repository
public interface WagesRepository extends JpaRepository<Wages, UUID> {

    List<Wages> findAllByUser_Id(UUID user_id);

    List<Wages> findAllByPrice(double price);

}
