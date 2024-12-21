package com.shop.projet_shop.Home;

public enum Product {

    APPLE("Apple.jpg",0.55f), MILK("Milk.jpg", 0.78f),
    JUICE("Juice.jpg",0.56f), LETTUCE("Lettuce",0.56f);

    //prince and image
    private String imageFile;
    private float price;

    Product(String imageFile, float price){
        this.imageFile = imageFile;
        this.price = price;
    }

    public String getImageFile(){
        return imageFile;
    }

    public float getPrice(){
        return price;
    }

}
