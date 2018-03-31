package com.example.pranshu.yummyrestaurant;

/**
 * Created by PRanshu on 13-06-2017.
 */

public class RecyclerItemMenu {
    private String menuNumber;
    private String menuItem;
    private String menuPrice;
    private String menuPiece;
    private String menuCategory;
    private String menuType;
    private String menuBlock;

    public RecyclerItemMenu(String menuNumber, String menuItem, String menuPrice, String menuPiece, String menuCategory, String menuType, String menuBlock) {
        this.menuNumber = menuNumber;
        this.menuItem = menuItem;
        this.menuPrice = menuPrice;
        this.menuPiece = menuPiece;
        this.menuCategory = menuCategory;
        this.menuType = menuType;
        this.menuBlock = menuBlock;
    }

    public String getMenuNumber() {
        return menuNumber;
    }

    public void setMenuNumber(String menuNumber) {
        this.menuNumber = menuNumber;
    }

    public String getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(String menuItem) {
        this.menuItem = menuItem;
    }

    public String getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPrice(String menuPrice) {
        this.menuPrice = menuPrice;
    }

    public String getMenuPiece() {
        return menuPiece;
    }

    public void setMenuPiece(String menuPiece) {
        this.menuPiece = menuPiece;
    }

    public String getMenuCategory() {
        return menuCategory;
    }

    public void setMenuCategory(String menuCategory) {
        this.menuCategory = menuCategory;
    }

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    public String getMenuBlock() {
        return menuBlock;
    }

    public void setMenuBlock(String menuBlock) {
        this.menuBlock = menuBlock;
    }
}
