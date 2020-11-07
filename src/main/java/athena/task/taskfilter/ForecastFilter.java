package athena.task.taskfilter;

import athena.Forecast;
import athena.task.Task;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class ForecastFilter extends TaskFilter {

    private final Forecast forecast;
    private LocalDate filterDate = LocalDate.now();

    public ForecastFilter(Forecast forecast) {
        this.forecast = forecast;
    }

    public ForecastFilter(LocalDate date) {
        this.forecast = Forecast.DAY;
        this.filterDate = date;
    }

    public void setDate(LocalDate filterDate) {
        this.filterDate = filterDate;
    }

    /**
     * Checks whether to include a task based on its date.
     *
     * @param task Task to check.
     * @return Whether the task should be included.
     */
    @Override
    public boolean isTaskIncluded(Task task) {
        for (LocalDate date : task.getDates()) {
            if (isDateIncluded(date)) {
                return true;
            }
        }
        return false;
    }

    private static int getWeekNumber(LocalDate taskDate) {
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        return taskDate.get(woy);
    }

    private boolean isDateIncluded(LocalDate taskDate) {
        boolean isDateIncluded;
        if (forecast == Forecast.ALL) {
            isDateIncluded = true;
        } else if (forecast == Forecast.WEEK) {
            int currentWeekNumber = getWeekNumber(filterDate);
            int taskWeekNumber = getWeekNumber(taskDate);
            isDateIncluded = (currentWeekNumber == taskWeekNumber && taskDate.getYear() == LocalDate.now().getYear());
        } else {
            isDateIncluded = taskDate.equals(filterDate);
        }
        return isDateIncluded;
    }
}
