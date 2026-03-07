package org.acme;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Talk {

    @Id
    public Long id;

    public String day;

    @Column(name = "start_time")
    public String time;

    @Column(name = "end_time")
    public String endTime;

    public String title;
    public String speaker;
    public String room;
    public String category;
}
