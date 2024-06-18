package com.aleksey.booking.hotels.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "number")
    private String number;

    @Column(name = "cost")
    private Integer cost;

    @Column(name = "max_count_of_people")
    private Integer maxCountOfPeople;

    @ManyToMany
    @JoinTable(
            name = "room2date"
            , joinColumns = @JoinColumn(name = "room_id")
            , inverseJoinColumns = @JoinColumn(name = "date_id")
    )
    @ToString.Exclude
    private List<UnavailableDate> unavailableDates;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Hotel hotel;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Room room = (Room) o;
        return getId() != null && Objects.equals(getId(), room.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}