package com.cheny.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.cheny.domain.dto.StudentDto;
import com.cheny.domain.entity.Result;
import com.cheny.domain.entity.Student;
import com.cheny.domain.query.StudentQuery;
import com.cheny.domain.vo.PageVo;
import com.cheny.domain.vo.StudentVo;
import com.cheny.service.StudentService;
import com.cheny.mapper.StudentMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
* @author Mlpnk
* @description 针对表【student(学员表)】的数据库操作Service实现
* @createDate 2026-08-08 15:15:22
*/
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student>
    implements StudentService{

    @Override
    public PageVo<StudentVo> getStudentPage(StudentQuery studentQuery) {

        int pageNo = studentQuery.getPageNo();
        int pageSize = studentQuery.getPageSize();
        String sortBy = studentQuery.getSortBy();
        //Boolean包装类可以是 null，直接赋值给基础类型boolean会自动拆箱，出现 NPE；
        // 如果为null，默认false；不为null就取传入的值
        boolean isAsc = studentQuery.getAsc() != null && studentQuery.getAsc();

        Page<StudentVo> page = new Page<>(pageNo, pageSize);

        if(StringUtils.hasText(sortBy)){
            page.addOrder(isAsc ? OrderItem.asc(sortBy) : OrderItem.desc(sortBy));
        }else {
            // 联表查询时 student/clazz 都有 id 列，显式加表别名避免歧义
            page.addOrder(OrderItem.asc("s.id"));
        }
        //条件链式构造：XML 里 student 起别名 s、clazz 起别名 c，name 两表都有，
        // 所以条件列必须带 s. 前缀，否则 MySQL 报 Column 'name' in where clause is ambiguous
        Integer degreeCode = studentQuery.getDegree() == null ? null : studentQuery.getDegree().getValue();
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(studentQuery.getName() != null, "s.name", studentQuery.getName())
                .eq(studentQuery.getClazzId() != null, "s.clazz_id", studentQuery.getClazzId())
                .eq(degreeCode != null, "s.degree", degreeCode);
        Page<StudentVo> studentPage = baseMapper.selectStudentPage(page, queryWrapper);


        PageVo<StudentVo> vo = new PageVo<>();
        vo.setTotal((int) studentPage.getTotal());
        vo.setPages((int) studentPage.getPages());
        vo.setList(studentPage.getRecords());
        return vo;

    }

    @Override
    public Result deleteStudents(List<Integer> ids) {
        for (Integer id : ids) {
            Student student = baseMapper.selectById(id);
            if (student == null) {
                return Result.error("学生不存在");
            }
        }
        baseMapper.deleteByIds(ids);
       return Result.success();
    }

    @Override
    public Result addStudent(StudentDto studentDto) {
        Student student = new Student();
        student.setName(studentDto.getName());
        student.setNo(studentDto.getNo());
        student.setGender(studentDto.getGender().getCode());
        student.setPhone(studentDto .getPhone());
        student.setIdCard(studentDto.getIdCard());
        student.setIsCollege(studentDto.getIsCollege() != null ? 1 : 0);//是否来自于大学 是1 否0
        student.setAddress(studentDto.getAddress());
        student.setDegree(studentDto.getDegree());
        student.setGraduationDate(studentDto.getGraduationDate());
        student.setClazzId(studentDto.getClazzId());
        student.setViolationCount(studentDto.getViolationCount());
        student.setViolationScore(studentDto.getViolationScore());
        baseMapper.insert(student);
        return Result.success();
    }

    @Override
    public Result getStudentById(Integer id) {
        return Result.success(baseMapper.selectById(id));
    }

    @Override
    public Result updateStudentById(StudentDto studentDto) {

        //先查库
        Student student = baseMapper.selectById(studentDto.getId());
        if (student == null) {
            return Result.error("学生不存在");
        }
        if(studentDto.getViolationCount()==null){
            return Result.error("违规次数不能为空");
        }
        if(studentDto.getViolationScore()==null){
            return Result.error("违规积分不能为空");
        }
        student.setName(studentDto.getName());
        student.setNo(studentDto.getNo());
        student.setGender(studentDto.getGender().getCode());
        student.setPhone(studentDto .getPhone());
        student.setIdCard(studentDto.getIdCard());
        student.setIsCollege(studentDto.getIsCollege() != null ? 1 : 0);//是否来自于大学 是1 否0
        student.setAddress(studentDto.getAddress());
        student.setDegree(studentDto.getDegree());
        student.setGraduationDate(studentDto.getGraduationDate());
        student.setClazzId(studentDto.getClazzId());
        student.setViolationCount(studentDto.getViolationCount());
        student.setViolationScore(studentDto.getViolationScore());
        baseMapper.updateById(student);
        return Result.success();
    }
}




