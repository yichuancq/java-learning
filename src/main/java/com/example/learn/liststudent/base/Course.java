package com.example.learn.liststudent.base;

/**
 * 课程
 */
public class Course {
    /**
     * 课程编号
     */
    public String curseNumber;
    /**
     * 课程名称
     */
    public String curseName;
    /**
     * 总分数
     */
    public float totalScore;
    /**
     * 平均分
     */
    public float avgScore;
    /**
     * 参加课程人数
     */
    public int studentAmount;

    public Course() {
    }

    /**
     * @param curseNumber
     * @param curseName
     */
    public Course(String curseNumber, String curseName) {
        this.curseNumber = curseNumber;
        this.curseName = curseName;
    }

    @Override
    public String toString() {
        return "Course{" +
                "curseNumber='" + curseNumber + '\'' +
                ", curseName='" + curseName + '\'' +
                ", totalScore=" + totalScore +
                ", avgScore=" + avgScore +
                ", studentAmount=" + studentAmount +
                '}';
    }
}