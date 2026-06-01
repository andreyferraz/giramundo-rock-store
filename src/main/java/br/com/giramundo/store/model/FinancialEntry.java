package br.com.giramundo.store.model;

public class FinancialEntry {
    private String id;
    private String type; // IN or OUT
    private Double amount;
    private String description;
    private String occurredAt; // ISO datetime or date

    public FinancialEntry() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOccurredAt() { return occurredAt; }
    public void setOccurredAt(String occurredAt) { this.occurredAt = occurredAt; }
}
