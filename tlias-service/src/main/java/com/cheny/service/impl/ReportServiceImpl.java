package com.cheny.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cheny.domain.entity.Clazz;
import com.cheny.domain.entity.Emp;
import com.cheny.domain.entity.Student;
import com.cheny.domain.enums.DegreeEnum;
import com.cheny.domain.enums.GenderEnum;
import com.cheny.domain.enums.JobEnum;
import com.cheny.mapper.ClazzMapper;
import com.cheny.mapper.EmpMapper;
import com.cheny.mapper.StudentMapper;
import com.cheny.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


//extends ServiceImpl<EmpMapper, Emp>不继承这个，就用mapper层中 mybatis注解
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private EmpMapper empMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ClazzMapper clazzMapper;

    //员工性别统计
    @Override
    public List<Map<String, Object>> empGenderData() {

        //count 聚合函数固定返回 Long；select的字段别名都是map里的key，
        // （没有就用原始表达式名字，
        // 像count(*)不写别名就是count(*)带星号，取数据很麻烦，所以一定要给聚合函数取名）
        //list下标1：{"gender": 1, "value": 2}
        //gender由于在Emp类里是GenderEnum类型，所以这里会自动转换成对应的枚举实例，key“gender”对应的value也就是MALE或者FEMALE
        //所以别名都是String类型，value有Long,有GenderEnum，有Integer，最后统一是Object
        QueryWrapper<Emp> wrapper = new QueryWrapper<>();
        wrapper.select("gender", "COUNT(*) AS value");
        wrapper.groupBy("gender");

        wrapper.orderByAsc("gender");//最后list共有2个元素，下标0和1
        List<Map<String, Object>> rawList = empMapper.selectMaps(wrapper);
//数据库存的是 int，但是经过 MP 类型处理器，Java Map 里面拿到的是枚举实例，不是 Integer。
        List<Map<String, Object>> result = new ArrayList<>();
        //这个for循环的主要目的不是把从数据库取出的数据进行格式转换吗
        for (Map<String, Object> row : rawList) {
            GenderEnum gender = GenderEnum.of((Integer) row.get("gender"));
            //通过gender这个key拿到对应的value，由于是用object接收的，所以需要强制转换类型
            String name = gender.getDesc() + "性员工";
            //这里为什么还要再声明一个linkedHashMap,不能直接用row的原因是row的key是"gender"，value是1或者2，不符合要求
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", name);//前面是key，后面是value  ---> "name": "_male性员工"
            //这里为什么会有两个put ？？？哪个放的是key，哪个放的是value，答案是：第一个put是放key和value，第二个put是放value
            item.put("value", row.get("value"));//前面是key，后面是value
            result.add(item);
            //row对应的                                item对应的
            //list下标1：{"gender": 1, "value": 2}变为了{"name": "_male性员工", "value": 2}
        }
        return result;
    }

    // 员工职位人数统计
    @Override
    public List<Map<String, Object>> empJobData() {

        List<Map<String, Object>> rawList = empMapper.countByEmp();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> row : rawList) {
            Map<String, Object> item = new LinkedHashMap<>();

            JobEnum job = JobEnum.of((Integer) row.get("job"));
            item.put("job", job.getDesc());
            item.put("value", row.get("cnt"));
            result.add(item);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> studentDegreeData() {

        List<Map<String, Object>> rawList = studentMapper.countByDegree();

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rawList) {

            Map<String, Object> item = new LinkedHashMap<>();
            DegreeEnum degree = DegreeEnum.of((Integer) row.get("degree"));
            item.put("degree", degree.getDesc());
            item.put("value", row.get("cnt"));
            result.add(item);
        }
        return result;
    }

    @Override
    public Map<String, Object> studentCountData() {
        List<Map<String, Object>> list = studentMapper.countByClazz();

        List<String> clazzNameList = list.stream()
                .map(m -> (String) m.get("clazz_name"))
                .toList();//m就是每一个 Map 对象
        List<Integer> countList = list.stream()
                .map(m -> ((Number) m.get("cnt")).intValue())
                .toList();
        return Map.of("clazzNameList", clazzNameList, "studentCountList", countList);
    }
}
