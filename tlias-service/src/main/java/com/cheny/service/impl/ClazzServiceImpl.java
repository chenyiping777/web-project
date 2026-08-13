package com.cheny.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.cheny.domain.dto.ClazzDto;
import com.cheny.domain.entity.Clazz;
import com.cheny.domain.entity.Result;
import com.cheny.domain.query.ClazzQuery;
import com.cheny.domain.vo.ClazzVo;
import com.cheny.domain.vo.PageVo;
import com.cheny.service.ClazzService;
import com.cheny.mapper.ClazzMapper;
import org.apache.ibatis.executor.result.ResultMapException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
* @author Mlpnk
* @description 针对表【clazz(班级表)】的数据库操作Service实现
* @createDate 2026-08-08 15:15:16
*/
@Service
public class ClazzServiceImpl extends ServiceImpl<ClazzMapper, Clazz>
    implements ClazzService{

    @Override
    public PageVo<ClazzVo> getClazz(ClazzQuery clazzQuery) {
        // 直接取值，无需判空，实体自带默认值
        Page<ClazzVo> pageParam = new Page<>(clazzQuery.getPageNo(), clazzQuery.getPageSize());

        // 排序逻辑不变
        String sortBy = clazzQuery.getSortBy();
        boolean asc = clazzQuery.getAsc() != null && clazzQuery.getAsc();
        if (StringUtils.hasText(sortBy)) {
            pageParam.addOrder(asc ? OrderItem.asc(sortBy) : OrderItem.desc(sortBy));
        } else {
            //默认排序
            pageParam.addOrder(OrderItem.desc("create_time"), OrderItem.asc("begin_date"));
        }

        // 条件链式构造
        QueryWrapper<Clazz> wrapper = new QueryWrapper<Clazz>()
                .eq(clazzQuery.getSubject() != null, "subject", clazzQuery.getSubject())
                .between(clazzQuery.getBeginDate()!=null && clazzQuery.getEndDate()!=null,
                        "begin_date", clazzQuery.getBeginDate(), clazzQuery.getEndDate());


        IPage<ClazzVo> pageResult = baseMapper.selectClazzVoPage(pageParam, wrapper);


        // 封装返回
        PageVo<ClazzVo> vo = new PageVo<>();
        vo.setTotal((int) pageResult.getTotal());
        vo.setPages((int) pageResult.getPages());
        vo.setList(pageResult.getRecords());
        return vo;

    }

    @Override
    public Result deleteClazz(Integer id) {
        if(id==null){
            throw new RuntimeException("参数为空");
        }
        baseMapper.deleteById(id);
        return Result.success();
    }

    @Override
    public Result addClazz(ClazzDto clazzDto) {
        //转换成实体类
        if(clazzDto==null){
            throw new RuntimeException("新增班级不能为空");
        }
        if(clazzDto.getName() == null) throw new RuntimeException("新增班级名称不能为空");
        if(clazzDto.getSubject() == null) throw new RuntimeException("新增学科不能为空");


        Clazz clazz = new Clazz();
        clazz.setName(clazzDto.getName());
        clazz.setRoom(clazzDto.getRoom());
        clazz.setBeginDate(clazzDto.getBeginDate());
        clazz.setEndDate(clazzDto.getEndDate());
        clazz.setMasterId(clazzDto.getMasterId());
        clazz.setSubject(clazzDto.getSubject());
        baseMapper.insert(clazz);
        return Result.success();
    }

    @Override
    public Result getClazzOne(Integer id) {
        if(id==null){
            throw new RuntimeException("参数为空");
        }
        Clazz clazz = baseMapper.selectById(id);
        return Result.success(clazz);
    }

    @Override
    public Result updateClazz(ClazzDto clazzDto) {
        if (clazzDto == null){
            throw new RuntimeException("更新班级不能为空");
        }
        if(clazzDto.getMasterId() == null)
            throw new RuntimeException("更新的班主任编号不能为空");
        if(clazzDto.getId() == null)
            throw new RuntimeException("更新的班级编号不能为空");
        Clazz clazz = new Clazz();
        clazz.setId(clazzDto.getId());
        clazz.setName(clazzDto.getName());
        clazz.setRoom(clazzDto.getRoom());
        clazz.setBeginDate(clazzDto.getBeginDate());
        clazz.setEndDate(clazzDto.getEndDate());
        clazz.setMasterId(clazzDto.getMasterId());
        clazz.setSubject(clazzDto.getSubject());
        baseMapper.updateById(clazz);
        return Result.success();
    }

    @Override
    public Result getAllClazz() {
        return Result.success(baseMapper.selectList(null));
    }

}




