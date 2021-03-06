package com.example.algorithm.liststudent.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.algorithm.liststudent.base.Course;
import com.example.algorithm.liststudent.base.LinkNode;
import com.example.algorithm.liststudent.base.Student;
import com.example.algorithm.liststudent.base.Teacher;
import com.example.algorithm.liststudent.repository.CourseRepository;
import com.example.algorithm.liststudent.utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * 课程服务类
 */
public class CourseService {
    /**
     * 基础服务类
     */
    public BaseService baseService;

    public CourseService(BaseService baseService) {
        this.baseService = baseService;
    }

    /**
     * 删除课程信息
     *
     * @throws Exception
     */
    private void delCourse() throws Exception {
        System.out.println("=====显示系统菜单====");
        System.out.println("删除课程信息");
        System.out.println("输入课程的编号");
        System.out.println("输入0返回");
        System.out.println("===================");
        //显示信息
        showCourseInto();
        //用户输入
        System.out.println("录入课程编号:");
        Scanner scanner = new Scanner(System.in);
        String courseNumber = "";
        if (scanner.hasNext()) {
            courseNumber = scanner.nextLine();
        }
        if ("0".equals(courseNumber)) {
            //返回上一步
            return;
        }
        CourseRepository courseRepository = this.baseService.getCourseRepository();
        if (!courseNumber.isEmpty() && courseRepository != null && courseRepository.size() > 0) {

            //执行删除动作
            courseRepository.delete(new Course(courseNumber, "", 0.0f));
            // 把内存数据删除的写入文件
            //显示学生信息
            this.showCourseInto();
            //删除本行文件内容
            Utils.removeLineForLineContent(this.baseService.courseFilePath, "courseNumber",
                    courseNumber, true);
        }

    }

    /**
     * 添加课程信息
     *
     * @throws Exception
     */
    private void addCourse() throws Exception {
        //添加课程信息
        loadData();
        //
        System.out.println("=====显示信息====");
        String curseNumber = Utils.getInnerId("CRS");
        System.out.println("系统生成的课程编号:" + curseNumber);
        String curseName = "";
        //用户输入
        System.out.println("=====显示信息====");
        System.out.println("输入课程名（字符如:Java程序设计）");
        System.out.println("");
        //用户输入
        Scanner scanner = new Scanner(System.in);
        curseName = scanner.nextLine();
        System.out.println("输入：" + curseName);
        if (curseName.isEmpty()) {
            System.out.println("录入不合法");
            return;
        }
        float point = 4.0f;
        Course course = new Course(curseNumber, curseName, point);
        baseService.getCourseRepository().add(course);
        System.out.println("集合长度：" + baseService.getCourseRepository().size());
        this.saveCourseInfoToDisk(course);
    }

    /**
     * @throws Exception
     */
    private void saveCourseInfoToDisk(final Course course) throws Exception {
        if (course == null) {
            System.out.println("无写入内容到磁盘!");
            return;
        }
        FileWriter fileWriter = new FileWriter(baseService.courseFilePath, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        String fileContent = JSON.toJSONString(course);
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
    private Course[] readInfoFromDisk() throws Exception {
        boolean flag = Utils.checkFile(baseService.courseFilePath);
        if (!flag) {
            return null;
        }
        FileReader fileReader = new FileReader(baseService.courseFilePath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String content = null;
        //按行读取
        List<Course> courseArrayList = new ArrayList<>();
        while ((content = bufferedReader.readLine()) != null) {
            //判断为空
            if (!content.isEmpty()) {
                JSONObject jsonObject = JSONObject.parseObject(content);
                //将JSONObject对象转为Bean实体对象
                Course temp = JSON.toJavaObject(jsonObject, Course.class);
                courseArrayList.add(temp);
            }

        }
        fileReader.close();
        Course[] courses = new Course[courseArrayList.size()];
        for (int i = 0; i < courseArrayList.size(); i++) {
            courses[i] = courseArrayList.get(i);
        }
        return courses;

    }


    /**
     * 显示课程信息
     *
     * @throws Exception
     */
    private void showCourseInto() throws Exception {

        Course[] courses = this.readInfoFromDisk();
        if (courses == null || courses.length == 0) {
            System.out.println("无信息，返回上一级");
            return;
        }
        //如果存在记录同时加载到内存里面，给链表赋值
        baseService.setCourseRepository(new CourseRepository(courses));
        //print
        System.out.println("=====课程数如下=====");
        System.out.println("课程数目：" + courses.length);
        System.out.println("=====课程信息=====");
        for (Course course : courses) {
            String createTime = Utils.getStringFormatDate(course.getCreateTime());
            String updateTime = Utils.getStringFormatDate(course.getUpdateTime());
            System.out.println("课程编号：" + course.curseNumber + "\t课程名称：" + course.curseName + "\t"
                    + "学分数:" + course.gradePoint +
                    "\t添加日期：" + createTime + "\t修改日期：" + updateTime);
        }
        System.out.println("======end=====");
    }

    /**
     * @throws Exception
     */
    public void loadData() throws Exception {
        Course[] courses = this.readInfoFromDisk();
        if (courses == null || courses.length == 0) {
            System.out.println("无信息，返回上一级");
            return;
        }
        //如果存在记录同时加载到内存里面，给链表赋值
        baseService.setCourseRepository(new CourseRepository(courses));
    }

    /**
     * 查询单个信息
     *
     * @param queryCourse
     * @return
     */
    public Course searchByKey(Course queryCourse) throws Exception {
        Course course = null;
        LinkNode<Course> courseLinkNode = baseService.getCourseRepository().search(queryCourse);
        if (courseLinkNode == null) {
            return course;
        } else {
            return courseLinkNode.data;
        }
    }


    /**
     * 查看教师选课信息
     *
     * @throws Exception
     */
    private void showTeacherChooseCourse() throws Exception {
//        打印教师信息
        new TeacherService(baseService).showTeacherCourseInto();
    }


    /**
     * 查看学生选课信息
     */
    private void showStudentChooseCourse() throws Exception {
        new StudentService(baseService).showStudentCourseInto();
    }


    /**
     * 添加学生选课
     * 1、显示学生信息
     * 2、显示课程信息
     * 3、保存学生选课记录到文件
     */
    private void addStudentChooseCourse() throws Exception {
        new StudentService(baseService).showStudentInto();
        //2、显示课程信息
        this.showCourseInto();
        //请学生输入自己编号
        System.out.println("请学生输入自己编号");
        //教师编码
        String studentNumber = "";
        //课程编码
        String courseNumber = "";
        //用户输入
        Scanner scanner = new Scanner(System.in);
        studentNumber = scanner.nextLine();
        System.out.println("输入：" + studentNumber);
        System.out.println("输入选择课程编码");
        scanner = new Scanner(System.in);
        courseNumber = scanner.nextLine();
        //输入合法性判断
        if (studentNumber.isEmpty() || courseNumber.isEmpty()) {
            System.out.println("录入不合法,请完善录入信息！");
            return;
        }
        this.saveStudentCourseToDisk(studentNumber, courseNumber);
    }

    /**
     * @param studentNumber
     * @param courseNumber
     * @throws Exception
     */
    private void saveStudentCourseToDisk(String studentNumber, String courseNumber) throws Exception {
        // 加载教师信息
        StudentService studentService = new StudentService(baseService);
        //
        studentService.loadStudentInfo();
        //文件系统内的教师信息是否存在
        Student studentDb = new StudentService(baseService).findStudentByNumber(studentNumber);
        if (studentDb == null) {
            System.out.println("学生信息不存在");
            return;
        }
        //保存信息到磁盘存档
        Student.StudentCourse studentCourseDb = new Student.StudentCourse(studentNumber, courseNumber);
        //get and add to list
        if (studentDb.getStudentCourses() != null) {
            //保留历史数据
            if (!studentDb.getStudentCourses().contains(studentCourseDb)) {
                studentDb.getStudentCourses().add(studentCourseDb);
                //修改最后时间
                studentDb.setUpdateTime(new Date());
            }
        }
        //修改学生选课信息
        this.modStudentInfo(studentDb);
        //remove
        Utils.removeLineForLineContent(baseService.studentFilePath, "number", studentDb.getNumber(), true);
        //保存到磁盘
        studentService.saveStudentInfoToDisk(studentDb);
    }


    /**
     * @param student
     */
    private void modStudentInfo(Student student) throws Exception {
        baseService.getPersonRepository().update(student);
    }


    /**
     * 添加教师选课
     * 1、显示教师信息
     * 2、显示课程信息
     * 3、教师录入选课信息，保存到文件
     *
     * @throws Exception
     */
    private void addTeacherChooseCourse() throws Exception {
        //添加教师选课
        //1、显示教师信息
        new TeacherService(baseService).showTeacherInto();
        //2、显示课程信息
        this.showCourseInto();
        //输入教师和课程编号完成选择课程
        System.out.println("请教师输入自己教师编号");
        //教师编码
        String teacherNumber = "";
        //课程编码
        String courseNumber = "";
        //用户输入
        Scanner scanner = new Scanner(System.in);
        teacherNumber = scanner.nextLine();
        System.out.println("输入：" + teacherNumber);
        System.out.println("输入选择上课的课程编码");
        scanner = new Scanner(System.in);
        courseNumber = scanner.nextLine();
        //输入合法性判断
        if (teacherNumber.isEmpty() || courseNumber.isEmpty()) {
            System.out.println("录入不合法,请完善录入信息！");
            return;
        }
        this.saveTeacherCourseToDisk(teacherNumber, courseNumber);

    }

    /**
     * @param teacherNumber
     * @param courseNumber
     * @throws Exception
     */
    private void saveTeacherCourseToDisk(String teacherNumber, String courseNumber) throws Exception {
        // 加载教师信息
        new TeacherService(baseService).LoadTeacherInfo();
        //文件系统内的教师信息是否存在
        Teacher teacherDb = this.findTeacherByNumber(teacherNumber);
        if (teacherDb == null) {
            System.out.println("教师信息不存在");
            return;
        }
        //是否已经存在选课记录？
        //保存信息到磁盘存档
        Teacher.TeacherCourse teacherCourse = new Teacher.TeacherCourse(teacherNumber, courseNumber);
        //get and add to list
        if (teacherDb.getTeacherCourses() != null) {
            if (!teacherDb.getTeacherCourses().contains(teacherCourse)) {
                teacherDb.getTeacherCourses().add(teacherCourse);
                //修改最后时间
                teacherDb.setUpdateTime(new Date());
            }

        }
        //修改教师选课信息
        this.modTeacherInfo(teacherDb);
        //先删除再添加
        Utils.removeLineForLineContent(baseService.teacherFilePath, "number", teacherDb.getNumber(), true);
        //保存到磁盘
        new TeacherService(baseService).saveTeacherInfoToDisk(teacherDb);

    }

    /**
     * 修改教师信息
     *
     * @param teacher
     * @throws Exception
     */
    public void modTeacherInfo(Teacher teacher) throws Exception {
        baseService.getTeacherRepository().update(teacher);
    }

    /**
     * 查询教师信息
     *
     * @param teacherNumber
     * @return
     */
    Teacher findTeacherByNumber(String teacherNumber) {
        Teacher teacher = null;
        if (teacherNumber == null || teacherNumber.isEmpty()) {
            return teacher;
        }
        Teacher teacherQuery = new Teacher();
        teacherQuery.setNumber(teacherNumber);
        return baseService.getTeacherRepository().search(teacherQuery);
    }


    /**
     * 显示课程信息菜单
     */
    public void showCourseMenu() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\r\n=====显示课程信息菜单====\r\n");
        stringBuilder.append("1.添加课程信息\r\n");
        stringBuilder.append("2.删除课程信息\r\n");
        stringBuilder.append("3.查看课程信息\r\n");
        stringBuilder.append("4.修改课程信息\r\n");
        stringBuilder.append("5.添加教师选课\r\n");
        stringBuilder.append("6.查看教师选课\r\n");
        stringBuilder.append("7.添加学生选课\r\n");
        stringBuilder.append("8.查看学生选课\r\n");
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
                    System.out.println("添加课程信息.");
                    //显示信息
                    this.addCourse();
                    //show menu
                    System.out.println(disInfo);
                    break;
                case 2:
                    System.out.println("删除课程信息");
                    delCourse();
                    System.out.println(disInfo);
                    break;
                case 3:
                    System.out.println("查看课程信息.");
                    //显示信息
                    this.showCourseInto();
                    System.out.println(disInfo);
                    break;
                case 4:
                    System.out.println("修改课程信息");
                    break;
                case 5:
                    System.out.println("添加教师选课");
                    this.addTeacherChooseCourse();
                    System.out.println(disInfo);
                    break;
                case 6:
                    System.out.println("查看教师选课");
                    this.showTeacherChooseCourse();
                    System.out.println(disInfo);
                    break;
                case 7:
                    System.out.println("添加学生选课");
                    this.addStudentChooseCourse();
                    System.out.println(disInfo);
                    break;
                case 8:
                    System.out.println("查看学生选课");
                    this.showStudentChooseCourse();
                    System.out.println(disInfo);
                    break;
                default:
                    return;
            }
        }
    }


}
