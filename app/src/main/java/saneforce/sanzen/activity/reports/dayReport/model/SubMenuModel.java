package saneforce.sanzen.activity.reports.dayReport.model;

public class SubMenuModel {

    String MenuId, MenuName, MenuLink;

    public SubMenuModel(String menuId, String menuName, String menuLink) {
        this.MenuId = menuId;
        this.MenuName = menuName;
        this.MenuLink = menuLink;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }

    public String getMenuName() {
        return MenuName;
    }

    public void setMenuName(String menuName) {
        MenuName = menuName;
    }

    public String getMenuLink() {
        return MenuLink;
    }

    public void setMenuLink(String menuLink) {
        MenuLink = menuLink;
    }
}
