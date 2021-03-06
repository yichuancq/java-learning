package com.example.algorithm.liststudent;

import com.alibaba.fastjson.JSON;
import com.example.algorithm.liststudent.base.LinkNode;
import com.example.algorithm.liststudent.base.Person;
import com.example.algorithm.liststudent.base.Student;
import com.example.algorithm.liststudent.repository.StudentRepository;
import com.example.algorithm.liststudent.utils.SnowflakeUtil;
import org.junit.Test;

public class StudentTest {

    /**
     * 测试主键生成
     */
    @Test
    public void testKey() {
        for (int i = 0; i < 10; i++) {
            long id = SnowflakeUtil.getInstance().nextId();
            System.out.println(id);
        }
    }

    @Test
    public void tetSearch() {
        int stuSize = 5;
        Student[] students = new Student[stuSize];
        for (int i = 0; i < stuSize; i++) {
            students[i] = new Student("stuNo" + i, "stuName" + i);
        }
        String person = JSON.toJSONString(students);
        //
        System.out.println("json->" + person);
        StudentRepository studentRepository = new StudentRepository(students);
        LinkNode linkNode = studentRepository.search(new Person("stuNo4", "stuName0"));
        if (linkNode != null) {
            System.out.println(linkNode.data);
        }

    }

    @Test
    public void testPerson() {

        int stuSize = 5;
        Student[] students = new Student[stuSize];
        for (int i = 0; i < stuSize; i++) {
            students[i] = new Student("stuNo" + i, "stuName" + i);
        }
        String person = JSON.toJSONString(students);
        //
        System.out.println("json->" + person);
        StudentRepository studentRepository = new StudentRepository(students);
        //new LinkList(students);
        studentRepository.printNode();
        int len = studentRepository.size();
        System.out.println("len ->" + len);
        //查找结点
        LinkNode linkNode = studentRepository.search(4);

        if (linkNode != null) {
            System.out.println(linkNode.data);
        }
        len = studentRepository.size();
        System.out.println("len ->" + len);
        studentRepository.delete(new Person("stuNo0", "stuName0"));
        len = studentRepository.size();
        System.out.println("len ->" + len);
    }

}
