package mingzuozhibi.common.spider;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mingzuozhibi.common.jms.JmsMessage;
import mingzuozhibi.common.model.Result;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
@Getter
public class SpiderRecorder {

    private JmsMessage jmsMessage;
    private String dataName;

    private int fetchCount;
    private int errorCount;
    private int breakCount;

    private int taskCount;
    private int doneCount;
    private int dataCount;

    public SpiderRecorder(String dataName, int taskCount, JmsMessage jmsMessage) {
        this.dataName = dataName;
        this.taskCount = taskCount;
        this.jmsMessage = jmsMessage;
    }

    public void jmsStartUpdate() {
        jmsMessage.notify("更新%s：开始", this.dataName);
        jmsMessage.notify("更新%s：共%d个任务", this.dataName, this.taskCount);
    }

    public void jmsEndUpdate() {
        jmsMessage.notify("更新%s：结束", this.dataName);
    }

    public boolean checkBreakCount(int maxBreakCount) {
        if (this.breakCount >= maxBreakCount) {
            jmsMessage.warning("更新%s：连续%d次更新失败", maxBreakCount);
            return true;
        }
        return false;
    }

    public void jmsStartUpdateRow(String origin) {
        this.fetchCount++;
        jmsMessage.info("正在更新：[%s](%s/%d)", origin, this.fetchCount, this.taskCount);
    }

    public boolean checkUnfinished(Result<?> result) {
        if (result.isUnfinished()) {
            jmsMessage.warning("抓取失败：%s", result.formatError());
            this.breakCount++;
            this.errorCount++;
            return true;
        }
        return false;
    }

    public void jmsSuccessRow(String origin, String message) {
        this.breakCount = 0;
        this.doneCount++;
        jmsMessage.info("成功更新：[%s][%s]", origin, message);
    }

    public void jmsFoundData(String message) {
        this.dataCount++;
        jmsMessage.success(message);
    }

    public void jmsFailedRow(String message) {
        this.breakCount++;
        this.errorCount++;
        jmsMessage.warning("更新失败：%s", message);
    }

    public void jmsErrorRow(Exception e) {
        this.breakCount++;
        this.errorCount++;
        jmsMessage.danger("捕获异常：%s", Result.formatErrors(e));
    }

    public void jmsSummary() {
        int skipCount = this.taskCount - this.fetchCount;
        jmsMessage.notify("There are %d tasks, %d updates, %d successes, %d failures, and %d skips.",
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
