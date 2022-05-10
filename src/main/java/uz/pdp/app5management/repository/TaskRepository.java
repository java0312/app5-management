package uz.pdp.app5management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.app5management.entity.Task;
import uz.pdp.app5management.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findAllByToUser(User toUser);

    List<Task> findAllByFromUser(User fromUser);


}
