package athena.timetable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import athena.Forecast;
import athena.Importance;
import athena.TaskList;
import athena.common.utils.DateUtils;
import athena.exceptions.CommandException;
import athena.task.Task;

/**
 * Test methods of Timetable.
 */
class TimetableTest {

    /**
     * Tests that the timetable from 8am to 12pm is drawn correctly.
     */
    @Test
    void drawTimetable_start8End12_returnsCorrectlyDrawnTimetable() throws CommandException {
        TaskList taskList = new TaskList();
        taskList.addTask("Assignment 1", "0800", "2", "6pm", "19-10-2020",
                Importance.HIGH, "Tough assignment", false);
        taskList.addTask("Tutorial 2", "0900", "2", "6pm", "20-10-2020",
                Importance.HIGH, "Tough assignment", false);
        taskList.addTask("OP 3", "1000", "2", "6pm", "21-10-2020",
                Importance.HIGH, "Tough assignment", false);
        taskList.addTask("TP 4", "1100", "2", "6pm", "22-10-2020",
                Importance.HIGH, "Tough assignment", false);

        ArrayList<LocalDate> dates = new ArrayList<LocalDate>();
        LocalDate date = LocalDate.parse("18-10-2020", DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        for (int i = 0; i < 7; i++) {
            dates.add(date);
            date = date.plusDays(1);
        }

        Timetable timetable = new Timetable(taskList, 8, 12);
        String drawnTimetable = timetable.drawTimetable(dates);
        String expectedDrawnTimetable =
                "+-------08---------09---------10---------11---------+\n"
                        + "|  SUN  |          |          |          |          |\n"
                        + "| 18/10 |          |          |          |          |\n"
                        + "+-------+----------+----------+----------+----------+\n"
                        + "|  MON  | Assignment 1        |          |          |\n"
                        + "| 19/10 | [0]                 |          |          |\n"
                        + "+-------+----------+----------+----------+----------+\n"
                        + "|  TUE  |          | Tutorial 2          |          |\n"
                        + "| 20/10 |          | [1]                 |          |\n"
                        + "+-------+----------+----------+----------+----------+\n"
                        + "|  WED  |          |          | OP 3                |\n"
                        + "| 21/10 |          |          | [2]                 |\n"
                        + "+-------+----------+----------+----------+----------+\n"
                        + "|  THU  |          |          |          | TP 4     |\n"
                        + "| 22/10 |          |          |          | [3]      |\n"
                        + "+-------+----------+----------+----------+----------+\n"
                        + "|  FRI  |          |          |          |          |\n"
                        + "| 23/10 |          |          |          |          |\n"
                        + "+-------+----------+----------+----------+----------+\n"
                        + "|  SAT  |          |          |          |          |\n"
                        + "| 24/10 |          |          |          |          |\n"
                        + "+-------+----------+----------+----------+----------+\n";

        assertEquals(drawnTimetable, expectedDrawnTimetable);
    }

    /**
     * Checks if the timetable returns all tasks with no filter.
     */
    @Test
    void getTimetable_noFilter_returnsAllTasks() throws CommandException {
        final ArrayList<TimetableDay> days = new ArrayList<TimetableDay>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date;
        TimetableDay day;

        date = LocalDate.parse("12-12-2020", formatter);
        day = new TimetableDay(date);
        day.addTask(new Task("Assignment 1", "1600", "2", "6pm",
                "12-12-2020", Importance.HIGH, "Tough assignment", 0, false));
        days.add(day);

        date = LocalDate.parse("13-12-2020", formatter);
        day = new TimetableDay(date);
        day.addTask(new Task("Assignment 2", "1600", "2", "6pm",
                "13-12-2020", Importance.MEDIUM, "Tough assignment", 1, false));
        days.add(day);

        date = LocalDate.parse("14-12-2020", formatter);
        day = new TimetableDay(date);

        day.addTask(new Task("Assignment 3", "1000", "2", "6pm",
                "14-12-2020", Importance.LOW, "Tough assignment", 2, false));
        day.addTask(new Task("Assignment 4", "1300", "2", "6pm",
                "14-12-2020", Importance.MEDIUM, "Tough assignment", 3, false));
        day.addTask(new Task("Assignment 5", "1600", "2", "6pm",
                "14-12-2020", Importance.HIGH, "Tough assignment", 4, false));
        days.add(day);

        date = LocalDate.parse("15-12-2020", formatter);
        day = new TimetableDay(date);
        day.addTask(new Task("Assignment 6", "1600", "2", "6pm",
                "15-12-2020", Importance.MEDIUM, "Tough assignment", 5, false));
        day.addTask(new Task("Assignment 7", "1900", "2", "6pm",
                "15-12-2020", Importance.HIGH, "Tough assignment", 6, false));
        day.addTask(new Task("Assignment 8", "2100", "2", "6pm",
                "15-12-2020", Importance.MEDIUM, "Tough assignment", 7, false));
        days.add(day);

        date = LocalDate.parse("16-12-2020", formatter);
        day = new TimetableDay(date);
        day.addTask(new Task("Assignment 9", "1600", "2", "6pm",
                "16-12-2020", Importance.LOW, "Tough assignment", 8, false));
        day.addTask(new Task("Assignment 10", "1300", "2", "6pm",
                "16-12-2020", Importance.MEDIUM, "Tough assignment", 9, false));
        days.add(day);

        TaskList taskList = getTestTaskList();
        Timetable timetable = new Timetable(taskList);

        assertTrue(areTimetablesSame(timetable, days));
    }

    /**
     * Checks if the timetable returns only high importance tasks with the high importance filter.
     */
    @Test
    void getTimetable_highImportanceFilter_returnsHighImportanceTasks() throws CommandException {
        final ArrayList<TimetableDay> days = new ArrayList<TimetableDay>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date;
        TimetableDay day;

        date = LocalDate.parse("12-12-2020", formatter);
        day = new TimetableDay(date);
        day.addTask(new Task("Assignment 1", "1600", "2", "6pm",
                "12-12-2020", Importance.HIGH, "Tough assignment", 0, false));
        days.add(day);

        date = LocalDate.parse("14-12-2020", formatter);
        day = new TimetableDay(date);
        day.addTask(new Task("Assignment 5", "1600", "2", "6pm",
                "14-12-2020", Importance.HIGH, "Tough assignment", 4, false));
        days.add(day);

        date = LocalDate.parse("15-12-2020", formatter);
        day = new TimetableDay(date);
        day.addTask(new Task("Assignment 7", "1900", "2", "6pm",
                "15-12-2020", Importance.HIGH, "Tough assignment", 6, false));
        days.add(day);

        TaskList taskList = getTestTaskList();
        Timetable timetable = new Timetable(taskList, Importance.HIGH, Forecast.ALL);

        assertTrue(areTimetablesSame(timetable, days));
    }

    /**
     * Checks if two timetables are the same.
     *
     * @param timetable Pre-created timetable
     * @param days      ArrayList of days
     * @return True or false depending if the two timetables are the same
     */
    private boolean areTimetablesSame(Timetable timetable, ArrayList<TimetableDay> days) {
        ArrayList<TimetableDay> timetableDays = timetable.getTimetableDays();

        if (timetableDays.size() != days.size()) {
            return false;
        }

        for (int i = 0; i < timetableDays.size(); ++i) {
            ArrayList<Task> tasks1 = timetableDays.get(i).getTaskList().getTasks();
            ArrayList<Task> tasks2 = days.get(i).getTaskList().getTasks();

            if (tasks1.size() != tasks2.size()) {
                return false;
            }

            for (int j = 0; j < tasks1.size(); ++j) {
                if (!tasks1.get(j).equals(tasks2.get(j))) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Gets a task list filled with default tasks.
     *
     * @return Task list of default tasks
     */
    public static TaskList getTestTaskList() throws CommandException {
        TaskList taskList = new TaskList();
        taskList.addTask(0, "Assignment 1", "1600", "2", "6pm", "12-12-2020",
                Importance.HIGH, "Tough assignment", false);
        taskList.addTask(1, "Assignment 2", "1600", "2", "6pm", "13-12-2020",
                Importance.MEDIUM, "Tough assignment", false);
        taskList.addTask(2, "Assignment 3", "1000", "2", "6pm", "14-12-2020",
                Importance.LOW, "Tough assignment", false);
        taskList.addTask(3, "Assignment 4", "1300", "2", "6pm", "14-12-2020",
                Importance.MEDIUM, "Tough assignment", false);
        taskList.addTask(4, "Assignment 5", "1600", "2", "6pm", "14-12-2020",
                Importance.HIGH, "Tough assignment", false);
        taskList.addTask(5, "Assignment 6", "1600", "2", "6pm", "15-12-2020",
                Importance.MEDIUM, "Tough assignment", false);
        taskList.addTask(6, "Assignment 7", "1900", "2", "6pm", "15-12-2020",
                Importance.HIGH, "Tough assignment", false);
        taskList.addTask(7, "Assignment 8", "2100", "2", "6pm", "15-12-2020",
                Importance.MEDIUM, "Tough assignment", false);
        taskList.addTask(8, "Assignment 9", "1600", "2", "6pm", "16-12-2020",
                Importance.LOW, "Tough assignment", false);
        taskList.addTask(9, "Assignment 10", "1300", "2", "6pm", "16-12-2020",
                Importance.MEDIUM, "Tough assignment", false);
        return taskList;
    }
}
