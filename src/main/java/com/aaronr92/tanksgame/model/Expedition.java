package com.aaronr92.tanksgame.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Entity
public class Expedition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private User user;
    @OneToOne
    private Tank tank;
    @JsonIgnore
    @CreationTimestamp
    private LocalDateTime startTime;
    @ReadOnlyProperty
    transient private String remainingTime;
    @Enumerated(EnumType.STRING)
    private Period period;
    @Basic
    private boolean finished;

    public Expedition() { }

    public Expedition(long id, User user, Tank tank, LocalDateTime startTime, Period period) {
        this.id = id;
        this.user = user;
        this.tank = tank;
        this.startTime = startTime;
        this.period = period;
        this.finished = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Tank getTank() {
        return tank;
    }

    public void setTank(Tank tank) {
        this.tank = tank;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getRemainingTime() {
        LocalDateTime finishTime = startTime.plusHours(period.getHours());
        long minutesRem = Duration.between(finishTime, LocalDateTime.now()).toMinutes();
        long hoursRem = TimeUnit.MINUTES.toHours(minutesRem);

        return String.format(
                "%02d:%02d",
                Math.abs(hoursRem),
                Math.abs(minutesRem - TimeUnit.HOURS.toMinutes(hoursRem))
        );
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @JsonIgnore
    public Instant getFinishTime() {
        return Instant.parse(startTime + "Z")
                .plus(period.getHours(), ChronoUnit.HOURS);
    }

    public enum Period {
        ONE(1),
        FOUR(4),
        EIGHT(8),
        TWELVE(12),
        TWENTY_FOUR(24);

        private static final Random random = new Random();
        private final int hours;

        Period(int hours) {
            this.hours = hours;
        }

        public int getHours() {
            return hours;
        }

        public static Period random() {
            return values()[random.nextInt(values().length)];
        }
    }
}