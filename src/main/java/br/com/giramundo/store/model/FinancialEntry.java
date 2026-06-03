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
@Table("financial_entry")
public class FinancialEntry implements Persistable<String> {

    @Id
    @Column("id")
    private String id;

    @Column("type")
    private String type;

    @Column("price")
    private Double price;

    @Column("occurred_at")
    private String occurredAt;

    @Column("description")
    private String description;

    @Transient 
    private boolean isNew = false;

    @Override
    public String getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }

}
