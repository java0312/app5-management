package uz.pdp.app5management.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Wages {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    private User user;

    private double price;

    private Date date;

}
