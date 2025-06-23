package br.com.rafaelaranda.task_manager.reminder.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.com.rafaelaranda.task_manager.task.entity.TaskEntity;
import br.com.rafaelaranda.task_manager.task.service.TaskService;

@EnableScheduling
@Service
public class ReminderScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReminderScheduler.class);

    private final TaskService taskService;

    public ReminderScheduler(TaskService taskService) {
        this.taskService = taskService;
    }

    @Scheduled(fixedRate = 300000)
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LOGGER.info("# Checking for tasks with active reminders at {}", now);
        List<TaskEntity> tasks = taskService.findTasksWithActiveReminders();
        if (!tasks.isEmpty()) {
            LOGGER.info("Tasks with reminders found {}", tasks.size());
            for (TaskEntity task : tasks) {
                LocalDateTime lastSent = task.getLastReminderSent();
                int intervalMinutes = task.getReminderInterval().getMinutes();
                if (lastSent == null || lastSent.plusMinutes(intervalMinutes).isBefore(now)) {
                    // Simula envio de lembrete (log por enquanto)
                    LOGGER.info("# Sending reminder for task {} (Title: {}, User: {})",
                            task.getTaskId(), task.getTitle(), task.getUser().getEmail());
                    task.setLastReminderSent(now);
                    taskService.saveTask(task);
                }
            }
        } else {
            LOGGER.info("# No tasks with pending reminders were found");
        }
    }
}
