package athena.task;

import athena.Importance;
import athena.common.utils.DateUtils;
import athena.exceptions.InvalidDeadlineException;
import athena.exceptions.InvalidRecurrenceException;
import athena.exceptions.TaskDuringSleepTimeException;
import athena.exceptions.TaskIsDoneException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;


/**
 * Handles task objects.
 */
public class Task {
    public static final String YES = "Y";
    public static final String NO = "N";
    public static final int DATE_TIME_FORMAT = 5;
    public static final int FIRST_INDEX = 0;
    private String name;

    private boolean isFlexible;

    private boolean isDone = false;
    private Importance importance;
    private String notes;
    private int number;

    private Time timeInfo;


    //TODO: add dependencies between Tasks

    /**
     * Determines if the task is done.
     *
     * @return string representing if the task is done
     */
    private String getStatus() {
        return (isDone ? YES : NO);
    }

    /**
     * Constructor for the task class.
     *
     * @param name       name of the task
     * @param startTime  starting time of the task
     * @param duration   how long the task is scheduled to last for
     * @param deadline   when the task is due
     * @param recurrence when the task occurs/repeats
     * @param importance importance of the task
     * @param notes      additional notes for the task
     * @param number     task number
     * @param isFlexible time flexibility
     * @throws TaskDuringSleepTimeException Exception thrown when task clashes with sleep time
     * @throws InvalidRecurrenceException   Exception thrown when user mistypes recurrence
     * @throws InvalidDeadlineException     Exception thrown when user mistypes deadline
     */
    public Task(String name, String startTime, String duration, String deadline,
                String recurrence, Importance importance, String notes, int number, Boolean isFlexible)
            throws TaskDuringSleepTimeException, InvalidRecurrenceException, InvalidDeadlineException {
        setAttributes(name, importance, notes, number, isFlexible);
        recurrence = getDefaultDate(recurrence);
        setTime(startTime, duration, deadline, recurrence, isFlexible);
    }

    private void setTime(String startTime, String duration, String deadline, String recurrence, Boolean isFlexible) throws TaskDuringSleepTimeException, InvalidRecurrenceException, InvalidDeadlineException {
        this.timeInfo = new Time(isFlexible, startTime, duration, deadline, recurrence);
    }

    private String getDefaultDate(String recurrence) {
        if (recurrence.toUpperCase().equals("TODAY")) {
            recurrence = DateUtils.formatDate(LocalDate.now());
        }
        return recurrence;
    }

    private void setAttributes(String name, Importance importance, String notes, int number, Boolean isFlexible) {
        this.name = name;
        assert !this.name.equals("");
        this.importance = importance;
        this.notes = notes;
        this.number = number;
        this.isFlexible = isFlexible;
    }


    public LocalDate getRecurrenceDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(date, formatter);
    }

    public Task(String name, boolean isFlexible, boolean isDone, Importance importance,
                String notes, int number, Time timeInfo)
            throws TaskDuringSleepTimeException, InvalidRecurrenceException, InvalidDeadlineException {
        this.name = name;
        this.isFlexible = isFlexible;
        this.isDone = isDone;
        this.importance = importance;
        this.notes = notes;
        this.number = number;
        this.timeInfo = timeInfo.getClone();
    }

    public Task getClone() {
        Task copy = null;
        try {
            copy = new Task(name, isFlexible, isDone, importance, notes, number, timeInfo);
        } catch (TaskDuringSleepTimeException e) {
            assert false;   // a task that can be cloned should have been blocked from being assigned the sleep time
        } catch (InvalidRecurrenceException e) {
            assert false;
        } catch (InvalidDeadlineException e) {
            assert false;
        }

        return copy;
    }


    /**
     * Edits the features of the task.
     *
     * @param name       New task name
     * @param startTime  New task start time
     * @param duration   New task duration
     * @param deadline   New task deadline
     * @param recurrence New task recurrence
     * @param importance New task importance
     * @param notes      New task notes
     * @throws InvalidRecurrenceException   Exception thrown when user mistypes recurrence
     * @throws InvalidDeadlineException     Exception thrown when user mistypes deadline
     */
    public void edit(String name, String startTime, String duration, String deadline,
                     String recurrence, Importance importance, String notes)
            throws InvalidRecurrenceException, InvalidDeadlineException {
        setAttributes(name, importance, notes, number, isFlexible);
        assertAll(startTime, duration, deadline);

        recurrence = getDefaultDate(recurrence);
        timeInfo.edit(startTime, duration, deadline, recurrence);
    }

    private void assertAll(String startTime, String duration, String deadline) {
        assert !this.name.equals("");
        assert !startTime.equals("");
        assert !duration.equals("");
        assert !deadline.equals("");
        assert this.importance != null;
    }


    /**
     * Return the importance of the task.
     *
     * @return Importance of task
     */
    public Importance getImportance() {
        return importance;
    }

    /**
     * Marks the task as done.
     *
     * @throws TaskIsDoneException Exception thrown when user tries to mark a task as done which is done.
     */
    public void setDone() throws TaskIsDoneException {
        if (isDone) {
            throw new TaskIsDoneException();
        }
        isDone = true;
        isFlexible = false;
    }

    /**
     * Returns the description of the task.
     *
     * @return Description of task
     */
    public String getName() {
        return name;
    }

    /**
     * Returns if the task is done.
     *
     * @return Status of task completion
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns task notes.
     *
     * @return Task notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Returns when the task repeats as a LocalDate object.
     *
     * @return When the task repeats as a LocalDate object
     */
    public ArrayList<LocalDate> getDates() {
        return timeInfo.getRecurrenceDates();
    }

    /**
     * Deletes the specified date from recurrenceDates.
     *
     * @param date Date to delete
     */
    public void removeDate(LocalDate date) {
        timeInfo.removeDate(date);
    }

    /**
     * Returns the task number.
     *
     * @return Task number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Sets the task number.
     *
     * @param number Number that the user wants to set the task to.
     */
    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isFlexible() {
        return isFlexible;
    }

    public void setFlexible(boolean isFlexible) {
        this.isFlexible = isFlexible;
    }

    /**
     * Restores a task that the user has just deleted.
     *
     * @return String representing details of the task the user wants to restore
     */

    //TODO: rework this, hard to do if dependencies are added
    public String getTaskRestore() {
        String taskRestore = "add n/" + this.getName() + " d/" + timeInfo.getDuration()
                + " D/" + this.timeInfo.getDeadline() + " r/" + timeInfo.getRecurrence() + " i/"
                + this.getImportance() + " a/" + this.getNotes();
        if (!isFlexible) {
            taskRestore += " t/" + timeInfo.getStartTime().toString().replace(":", "");
        }
        return taskRestore;
    }

    public Time getTimeInfo() {
        return timeInfo;
    }

    public ArrayList<LocalDate> makeDeepCopyDates(ArrayList<LocalDate> oldDates) {
        ArrayList<LocalDate> copy = new ArrayList<LocalDate>();
        for (LocalDate date : oldDates) {
            LocalDate dateCopy = getRecurrenceDate(date.toString());
            copy.add(dateCopy);
        }
        return copy;
    }


    /**
     * Converts a task object to a string.
     *
     * @return task as a string
     */
    @Override
    public String toString() {
        String deadlinePreText;
        if (timeInfo.getDeadline().toLowerCase().equals("no deadline")) {
            deadlinePreText = " and has ";
        } else {
            deadlinePreText = " which should be finished by ";
        }
        return "[ID: " + number + "] " + name + " at " + timeInfo.getStartTime() + deadlinePreText
                + timeInfo.getDeadline().toLowerCase() + ". Done? " + getStatus()
                + " Importance: " + importance.toString();
    }

    /**
     * Converts a task object to a string of details.
     *
     * @return task as a string
     */
    public String getDetailsAsString() {
        return "\n Done? " + getStatus() + "\n Name: " + name + "\n Start time: " + timeInfo.getStartTime()
                + "\n Deadline: " + timeInfo.getDeadline() + "\n Duration: " + timeInfo.getDuration()
                + "\n Recurrence: " + timeInfo.getRecurrence()
                + "\n Importance: " + importance + "\n Notes: " + notes;
    }


    /**
     * Compare this task with another object.
     *
     * @param o Object to compare with.
     * @return Whether the object compared with is also a task and has the exact same properties.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;

        return isDone == task.isDone
                && number == task.number
                && Objects.equals(name, task.name)
                && Objects.equals(timeInfo, task.timeInfo)
                && importance == task.importance
                && Objects.equals(notes, task.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, timeInfo, isDone, importance, notes, number);
    }
}
