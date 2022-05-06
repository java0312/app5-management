package uz.pdp.app5management.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Turnstile {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private User user;

    @CreationTimestamp
    private Timestamp timestamp;

    private boolean enter; //default false

}
