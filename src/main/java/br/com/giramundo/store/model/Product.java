package br.com.giramundo.store.model;

public class Product {
    private String id;
    private String name;
    private String description;
    private Double price;
    private String sku;
    private String image;

    public Product() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}
