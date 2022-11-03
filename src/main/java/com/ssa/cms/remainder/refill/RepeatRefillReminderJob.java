package com.ssa.cms.remainder.refill;

import com.ssa.cms.service.RefillReminderService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class RepeatRefillReminderJob {

    @Autowired
    RefillReminderService refillReminderService;

    private final Log logger = LogFactory.getLog(getClass());

//    @Scheduled(cron = "${GENERIC_RPEAT_REFILL_REMINDERS_CRON}")
    protected void executeInternal() {
        try {

            PMSGenericRepeatRefillReminder repeatRefillReminder = new PMSGenericRepeatRefillReminder();
            repeatRefillReminder.sendRepeatRefillReminder(refillReminderService);
            
        } catch (Exception e) {
            logger.error("Exception: GenericRepeatRefillReminderJob -> executeInternal", e);

        }
    }
}
