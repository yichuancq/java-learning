package com.example.algorithm.liststudent.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.algorithm.liststudent.base.Course;
import com.example.algorithm.liststudent.base.Teacher;
import com.example.algorithm.liststudent.repository.TeacherRepository;
import com.example.algorithm.liststudent.utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 教师服务类
 */
public class TeacherService {

    /**
     * 基础服务类
     */
    public BaseService baseService;

    public TeacherService(BaseService baseService) {
        this.baseService = baseService;
    }

    /**
     * 添加教师信息
     */
    private void addTeacher() throws Exception {
        System.out.println("=====显示信息====");
        String teacherNumber = Utils.getInnerId("TEA");
        System.out.println("系统生成的教师编号:" + teacherNumber);
        String teacherName = "";
        //用户输入
        System.out.println("=====显示信息====");
        System.out.println("输入教师名（字符如:王明）");
        System.out.println("");
        //用户输入
        Scanner scanner = new Scanner(System.in);
        teacherName = scanner.nextLine();
        System.out.println("输入：" + teacherName);
        if (teacherName.isEmpty()) {
            System.out.println("录入不合法");
            return;
        }
        Teacher teacher = new Teacher(teacherNumber, teacherName);
        this.baseService.getTeacherRepository().add(teacher);
        System.out.println("集合长度：" + this.baseService.getTeacherRepository().size());
        this.saveTeacherInfoToDisk(teacher);

    }

    /**
     * 保存信息到文件
     */
    public void saveTeacherInfoToDisk(final Teacher teacher) throws Exception {
        if (teacher == null) {
            System.out.println("无写入内容到磁盘!");
            return;
        }
        FileWriter fileWriter = new FileWriter(baseService.teacherFilePath, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        String fileContent = JSON.toJSONString(teacher);
        bufferedWriter.write(fileContent);
        bufferedWriter.newLine();
        bufferedWriter.flush(); //将数据更新至文件
        bufferedWriter.close();
        fileWriter.close();
    }


    /**
     * 读取磁盘数据
     *
     * @return
     */
    private Teacher[] readInfoFromDisk() throws Exception {
        boolean flag = Utils.checkFile(baseService.teacherFilePath);
        if (!flag) {
            return null;
        }
        FileReader fileReader = new FileReader(baseService.teacherFilePath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String content = null;
        //按行读取
        List<Teacher> teacherList = new ArrayList<>();
        while ((content = bufferedReader.readLine()) != null) {
            //判断为空
            if (!content.isEmpty()) {
                JSONObject jsonObject = JSONObject.parseObject(content);
                //将JSONObject对象转为Bean实体对象
                Teacher temp = JSON.toJavaObject(jsonObject, Teacher.class);
                teacherList.add(temp);
            }

        }
        fileReader.close();
        Teacher[] teachers = new Teacher[teacherList.size()];
        for (int i = 0; i < teacherList.size(); i++) {
            teachers[i] = teacherList.get(i);
        }
        return teachers;

    }


    /**
     * 1、先加载磁盘数据
     * 2、如果存在记录同时加载到内存里面，给链表赋值
     *
     * @throws Exception
     */
    public Teacher[] LoadTeacherInfo() throws Exception {
        Teacher[] teachers = this.readInfoFromDisk();
        if (teachers == null || teachers.length == 0) {
            System.out.println("无信息，返回上一级");
            System.out.println("");
            return teachers;
        }
        //如果存在记录同时加载到内存里面，给链表赋值
        baseService.setTeacherRepository(new TeacherRepository(teachers));
        return teachers;
    }

    /**
     * 1、先加载磁盘数据
     * 2、如果存在记录同时加载到内存里面，给链表赋值
     * 3、显示到用户界面
     */
    public void showTeacherInto() throws Exception {
        Teacher[] teachers = LoadTeacherInfo();
        //print
        if (teachers == null || teachers.length == 0) {
            System.out.println("无信息，返回上一级");
            return;
        }
        System.out.println("=====教师人数如下=====");
        System.out.println("教师数目：" + teachers.length);
        System.out.println("=====教师信息如下=====");
        for (Teacher t : teachers) {
            String createTime = Utils.getStringFormatDate(t.getCreateTime());
            String updateTime = Utils.getStringFormatDate(t.getUpdateTime());
            System.out.println("教师编号：" + t.getNumber() + "\t教师姓名：" + t.getName() + "\t"
                    + "添加日期：" + createTime + "\t修改日期：" + updateTime);
        }
        System.out.println("======end=====");
    }

    /**
     * 教师选课信息
     *
     * @throws Exception
     */
    public void showTeacherCourseInto() throws Exception {
        Teacher[] teachers = LoadTeacherInfo();
        //print
        if (teachers == null || teachers.length == 0) {
            System.out.println("无信息，返回上一级");
            return;
        }
        System.out.println("=====教师人数如下=====");
        System.out.println("教师数目：" + teachers.length);
        System.out.println("=====教师信息如下=====");
        CourseService courseService = new CourseService(baseService);
        //加载信息
        courseService.loadData();
        int i = 0;
        for (Teacher t : teachers) {
            String createTime = Utils.getStringFormatDate(t.getCreateTime());
            String updateTime = Utils.getStringFormatDate(t.getUpdateTime());
            System.out.println("教师姓名：" + t.getName());
            System.out.println("");
            //本教师选课
            for (Teacher.TeacherCourse teacherCourse : t.getTeacherCourses()) {
                ++i;
                System.out.println("===row :" + i + "===");
                System.out.println("教师编号：" + t.getNumber() + "\t教师姓名：" + t.getName() + "\t" + "添加日期：" + createTime + "\t修改日期：" + updateTime);
                System.out.println("本教师选课信息如下：");
                System.out.println("课程编码:" + teacherCourse.courseNumber);
                Course queryKey = new Course();
                //查询课程服务类
                queryKey.curseNumber = teacherCourse.courseNumber;
                //find course info
                Course course = courseService.searchByKey(queryKey);
                if (course != null) {
                    System.out.println("课程名称:" + course.curseName + "\t 课程学分:" + course.gradePoint);
                }

            }
        }
        System.out.println("======end=====");
    }

    /**
     * 删除教师信息（内存+磁盘文件）
     */
    private void delTeacher() throws Exception {
        System.out.println("=====显示系统菜单====");
        System.out.println("删除教师信息");
        System.out.println("输入教师的编号");
        System.out.println("输入0返回");
        System.out.println("===================");
        //显示教师信息
        showTeacherInto();
        //用户输入
        System.out.println("录入教师编号:");
        Scanner scanner = new Scanner(System.in);
        String teacherNumber = "";
        if (scanner.hasNext()) {
            teacherNumber = scanner.nextLine();
        }
        if ("0".equals(teacherNumber)) {
            //返回上一步
            return;
        }
        if (teacherNumber.isEmpty()) {
            System.out.println("教师编号信息不存在");
            return;
        }
        //执行删除动作
        Utils.removeLineForLineContent(this.baseService.teacherFilePath, "number", teacherNumber, true);
        //显示信息
        this.showTeacherInto();
    }


    /**
     * @param teacherNumber
     * @return
     */
    public Teacher findTeacherByNumber(String teacherNumber) {
        Teacher teacher = null;
        if (teacherNumber == null || teacherNumber.isEmpty()) {
            return teacher;
        }
        Teacher teacherQuery = new Teacher();
        teacherQuery.setNumber(teacherNumber);
        teacher = baseService.getTeacherRepository().search(teacherQuery);
        return teacher;
    }


    /**
     * 显示教师信息菜单
     */
    public void showTeacherMenu() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\r\n=====显示教师信息菜单====\r\n");
        stringBuilder.append("1.添加教师信息\r\n");
        stringBuilder.append("2.删除教师信息\r\n");
        stringBuilder.append("3.查看教师信息\r\n");
        stringBuilder.append("4.修改教师信息\r\n");
        stringBuilder.append("0.返回上一层\r\n");
        stringBuilder.append("请选择?(0-4)\r\n");
        stringBuilder.append("===================\r\n");
        String disInfo = stringBuilder.toString();
        System.out.println(disInfo);
        //用户输入信息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextInt()) {
            int orderNumber = scanner.nextInt();
            switch (orderNumber) {
                case 0:
                    System.out.println("返回上一层.");
                    new StudentService(baseService).initMenu();
                    break;
                case 1:
                    System.out.println("添加教师信息.");
                    //显示信息
                    this.showTeacherInto();
                    //
                    this.addTeacher();
                    //show menu
                    System.out.println(disInfo);
                    break;
                case 2:
                    System.out.println("删除教师信息");
                    delTeacher();
                    System.out.println(disInfo);
                    break;
                case 3:
                    System.out.println("查看教师信息.");
                    //显示信息
                    this.showTeacherInto();
                    System.out.println(disInfo);
                    break;
                case 4:
                    System.out.println("修改教师信息");
                    break;
                default:
                    System.out.println("录入非法,exit...");
                    System.out.println(disInfo);
                    return;
            }
        }
    }
}
