package com.atguigu.gulimall.product.service.impl;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 1. 查出所有分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        // 2. 组装成父子的树形结构
        // 2.1 找到所有的一级分类
        List<CategoryEntity> level1Menus = categoryEntities.stream()
                .filter(a -> a.getParentCid() == 0)
                // 找到一级目录的子目录
                .peek(menu -> menu.setChildren(getChildren(menu, categoryEntities)))
                .sorted(Comparator.comparingInt(menu -> ((menu.getSort() == null) ? 0 : menu.getSort())))
                .collect(Collectors.toList());

        // 2.2 找到所有的
        return level1Menus;
    }

    /**
     * 找出所有菜单的子菜单
     *
     * @param categoryRoot    当前菜单
     * @param allCategoryList 所有菜单的集合
     * @return
     */
    public List<CategoryEntity> getChildren(CategoryEntity categoryRoot, List<CategoryEntity> allCategoryList) {
        List<CategoryEntity> childrenList = allCategoryList.stream()
                .filter(entity -> Objects.equals(entity.getParentCid(), categoryRoot.getCatId()))
                .peek(menu -> menu.setChildren(getChildren(menu, allCategoryList)))
                .sorted(Comparator.comparingInt(menu -> ((menu.getSort() == null) ? 0 : menu.getSort())))
                .collect(Collectors.toList());
        return childrenList;
    }


    @Override
    public void removeMenuByIds(List<Long> list) {
        // TODO 检查当前待删除的菜单是否被别的地方引用

        // 这是物理删除，现在常用的是mybatis-plus的逻辑视图
        // 即 用一个字段来表示这个字段是否删除，比如1就是没删，0是删了
        baseMapper.deleteBatchIds(list);
    }
}