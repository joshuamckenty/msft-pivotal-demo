package org.pivotalecosystem;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Cover {
    @Id
    @GeneratedValue
    private Long id;

    private String coverName;

    public Cover(String coverName) {
        this.coverName = coverName;
    }

    public Cover() {
    }

    public Long getId() {
        return id;
    }

    public String getCoverName() {
        return coverName;
    }
}
