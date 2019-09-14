package mingzuozhibi.common.spider;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mingzuozhibi.common.jms.JmsMessage;
import mingzuozhibi.common.model.Result;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
@Getter
public class SpiderRecorder {

    @Autowired
    private JmsMessage jmsMessage;

    private String dataName;

    private int fetchCount;
    private int errorCount;
    private int breakCount;

    private int taskCount;
    private int doneCount;
    private int rowCount;

    public SpiderRecorder(String dataName, int taskCount, JmsMessage jmsMessage) {
        this.dataName = dataName;
        this.taskCount = taskCount;
        this.jmsMessage = jmsMessage;
    }

    public void jmsStartUpdate() {
        jmsMessage.warning("Now update the %s, a total of %d", this.dataName, this.taskCount);
    }

    public void jmsEndUpdate() {
        jmsMessage.info("Update %s is complete", this.dataName);
    }

    public boolean checkBreakCount(int maxBreakCount) {
        if (this.breakCount >= maxBreakCount) {
            jmsMessage.warning("Continuous error reached %d times, the task stopped", maxBreakCount);
            return true;
        }
        return false;
    }

    public void jmsStartUpdateRow(String origin) {
        this.fetchCount++;
        jmsMessage.info("Start updating %s (%s/%d)[%s]", this.dataName, this.fetchCount, this.taskCount, origin);
    }

    public boolean checkUnfinished(Result<?> result, String origin) {
        if (result.isUnfinished()) {
            jmsMessage.warning("An error occurred while updating the content: [%s]", origin);
            this.breakCount++;
            this.errorCount++;
            return true;
        }
        return false;
    }

    public void jmsSuccessRow(String message) {
        this.breakCount = 0;
        this.doneCount++;
        jmsMessage.info("Successfully updated: %s", message);
    }

    public void jmsFailedRow(String message) {
        this.breakCount++;
        this.errorCount++;
        jmsMessage.warning("Failed updated: %s", message);
    }

    public void jmsErrorRow(Exception e) {
        this.breakCount++;
        this.errorCount++;
        jmsMessage.danger("An error occurred while parsing the content: %s", Result.formatErrors(e));
    }

    public void jmsSummary() {
        int skipCount = this.taskCount - this.fetchCount;
        jmsMessage.info("Task summary：There are %d tasks, %d updates, %d successes, %d failures, and %d skips.",
                this.taskCount, this.fetchCount, this.doneCount, this.errorCount, skipCount);
    }

    public static void writeContent(String content, String origin) {
        try {
            File file = File.createTempFile("spider", ".html");
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
                bufferedWriter.write(content);
                bufferedWriter.flush();
                log.warn("An error occurred while parsing the content: [{}][file={}]", origin, file.getAbsolutePath());
            }
        } catch (IOException e) {
            log.warn("An error occurred while recording the error content", e);
        }
    }

}
