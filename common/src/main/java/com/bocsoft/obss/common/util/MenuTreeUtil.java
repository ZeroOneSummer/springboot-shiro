package com.bocsoft.obss.common.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 菜单树工具类
 */
@Slf4j
public class MenuTreeUtil implements Serializable {
    private static List<Menu> menuList = new ArrayList<>();

    /**
     * 主树
     * @param menuList  id-pid菜单list
     * @return
     */
    public static List<Menu> buildTree(List<Menu> menuList){
        MenuTreeUtil.menuList = menuList;
        List<Menu> treeMenu = new ArrayList<>();
        if (!CollectionUtils.isEmpty(menuList)){
            for (Menu menuNode : getRootNode()) {
                menuNode = buildChildTree(menuNode);
                treeMenu.add(menuNode);
            }
        }
        return treeMenu;
    }

    /**
     * 子树
     * @param pNode
     * @return
     */
    private static Menu buildChildTree(Menu pNode){
        List<Menu> childMenu = new ArrayList<>();
        for (Menu menuNode : menuList) {
            if (menuNode.getPid().equals(pNode.getId())) {
                childMenu.add(buildChildTree(menuNode));
            }
        }
        pNode.setChildren(childMenu);
        return pNode;
    }

    /**
     * 根节点
     * @return
     */
    private static List<Menu> getRootNode(){
        List<Menu> rootMenu = new ArrayList<>();
        for (Menu menu : menuList) {
            if (menu.getPid() == 0L) {
                rootMenu.add(menu);
            }
        }
        return rootMenu;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    static class Menu{
        private Long id;
        private Long pid;
        //jackson注解必须用jackson格式化
        @JsonProperty("name")
        private String permName;
        @JsonProperty("path")
        private String url;
        private List<Menu> children;
        private Meta meta = new Meta();
        @JsonIgnore
        private String title;
        @JsonIgnore
        private Boolean keepAlive;

        public void setTitle(String title) {
            this.meta.title = title;
        }

        public void setKeepAlive(Boolean keepAlive) {
            this.meta.keepAlive = keepAlive;
        }

        //仅测试用
        public Menu(Long id, Long pid, String permName, String url) {
            this.id = id;
            this.pid = pid;
            this.permName = permName;
            this.url = url;
        }
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    static class Meta{
        private String title;
        private Boolean keepAlive;
    }

    @SneakyThrows
    public static void main(String[] args) {
        ObjectMapper JackSON = new ObjectMapper();
        List<Menu> menuList = Arrays.asList(
            new Menu(1L, 0L, "用户管理", "/user/manage"),
            new Menu(2L, 1L, "查询用户", "/user/query"),
            new Menu(3L, 1L, "新增用户", "/user/add"),
            new Menu(4L, 0L, "订单管理", "/order/manage"),
            new Menu(5L, 4L, "新增订单", "/order/add")
        );
        menuList = MenuTreeUtil.buildTree(menuList);
        log.info(JackSON.writeValueAsString(menuList));
    }
}
