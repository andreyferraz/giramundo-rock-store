package br.com.giramundo.store.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("event")
public class Event implements Persistable<String> {

    @Id
    @Column("id")
    private String id;

    @Column("title")
    private String title;

    @Column("description")
    private String description;

    @Column("image")
    private String image;

    @Column("published_at")
    private String publishedAt;

    @Transient
    private boolean isNew = false;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}