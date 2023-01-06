package ru.practicum.ewm.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.model.Request;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "email", unique = true)
    String email;
    @Column(name = "name")
    String name;
    @JsonIgnore
    @OneToMany(mappedBy = "initiator")
    private List<Event> events = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "requestor")
    private List<Request> requests = new ArrayList<>();
}
